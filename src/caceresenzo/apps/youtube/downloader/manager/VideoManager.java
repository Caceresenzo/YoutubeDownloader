package caceresenzo.apps.youtube.downloader.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import caceresenzo.apps.youtube.downloader.config.Config;
import caceresenzo.apps.youtube.downloader.ui.DownloaderFrame;
import caceresenzo.apps.youtube.downloader.ui.VideoPanel;
import caceresenzo.apps.youtube.downloader.worker.VideoDownloadWorker;
import caceresenzo.libs.filesystem.FileUtils;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.thread.ThreadUtils;
import caceresenzo.libs.thread.queue.WorkQueue;

/**
 * Batch-download handling class for {@link VideoPanel}s
 * 
 * @author Enzo CACERES
 */
public class VideoManager {
	
	/* Static */
	private static VideoManager MANAGER;
	public static File DOWNLOAD_DIRECTORY;
	public static File TEMPORARY_DIRECTORY;
	
	/* Variables */
	private WorkQueue queue;
	private Object workerTaskGroupId;
	
	private BatchDownloadCallback batchCallback;
	private List<VideoPanel> failedPanelBatch;
	private int doneWorkerCount, batchRetryCount;
	
	/* Constructor */
	private VideoManager() {
		this.queue = new WorkQueue(Config.WORKER_COUNT_MAX);
		this.workerTaskGroupId = queue.registerTaskGroup(1);
	}
	
	/* Initializer */
	public void initialize() {
		try {
			initializeDirectories();
		} catch (Exception exception) {
			Logger.exception(exception, "Failed to create targets (download and temporary) directories");
			System.exit(1);
			return;
		}
		
		emptyWorkingDirectory();
	}
	
	private void initializeDirectories() throws IOException {
		DOWNLOAD_DIRECTORY = new File(Config.PATH_DOWNLOAD_DIRECTORY);
		TEMPORARY_DIRECTORY = new File(DOWNLOAD_DIRECTORY, ".downloader");

		FileUtils.forceFolderCreation(DOWNLOAD_DIRECTORY);
		FileUtils.forceFolderCreation(TEMPORARY_DIRECTORY);
	}
	
	/**
	 * Empty, if not already, the folder used to download .webm files
	 */
	private void emptyWorkingDirectory() {
		if (TEMPORARY_DIRECTORY.list().length != 0) {
			try {
				FileUtils.deleteTree(TEMPORARY_DIRECTORY);
				FileUtils.forceFolderCreation(TEMPORARY_DIRECTORY);
			} catch (Exception exception) {
				;
			}
		}
	}
	
	public void openDownloadDirectoryChangerDialog(DownloaderFrame downloaderFrame) {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(DOWNLOAD_DIRECTORY);
		chooser.setDialogTitle(i18n.string("ui.filechooser.download-directory.title"));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		
		if (chooser.showOpenDialog(downloaderFrame.getFrame()) == JFileChooser.APPROVE_OPTION) {
			changeDownloadDirectory(chooser.getSelectedFile(), downloaderFrame);
		}
	}
	
	public void changeDownloadDirectory(File newDirectory, DownloaderFrame downloaderFrame) {
		try {
			Config.PATH_DOWNLOAD_DIRECTORY = newDirectory.getAbsolutePath();
			Config.save();
			
			initializeDirectories();
		} catch (Exception exception) {
			Logger.exception(exception, "Failed to save new download directory.");
			JOptionPane.showMessageDialog(downloaderFrame.getFrame(), i18n.string("ui.dialog.error.failed-to-save-download-directory", exception.getLocalizedMessage()), i18n.string("ui.dialog.error.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Same as {@link #download(List, BatchDownloadCallback)}, but with the already applied callback.<br>
	 * See {@link VideoManager#download(List, BatchDownloadCallback)} for more info.
	 */
	public void download(List<VideoPanel> panels) {
		download(panels, batchCallback);
	}
	
	/**
	 * Download multiples video at once
	 * 
	 * @param panels
	 *            Targetted {@link VideoPanel} for UI callbacks
	 * @param callback
	 *            Callback to follow global progression
	 */
	public void download(List<VideoPanel> panels, final BatchDownloadCallback callback) {
		if (callback != batchCallback) {
			this.batchCallback = callback;
		}
		failedPanelBatch = new ArrayList<>();
		
		if (batchCallback != null) {
			batchCallback.onDownloadsStarted(panels, batchRetryCount);
		}
		
		doneWorkerCount = 0;
		final int totalWorkerCount = panels.size();
		for (final VideoPanel panel : panels) {
			queue.add(workerTaskGroupId, new Runnable() {
				@Override
				public void run() {
					VideoDownloadWorker worker = panel.download();
					
					worker.start();
					try {
						worker.join();
					} catch (InterruptedException exception) {
						Thread.currentThread().interrupt();
					}
					
					if (batchCallback != null) {
						batchCallback.onVideoDownloaded(panel);
					}
					
					if (++doneWorkerCount == totalWorkerCount) {
						endBatch();
					}
				}
			});
		}
	}
	
	/**
	 * If the worker has callback an exception and the extraction has been forced to stop, please add the corresponding panel to this list.
	 * 
	 * @param panel
	 *            Failed panel
	 * @return {@link List#add(Object)}
	 */
	public boolean addFailedPanel(VideoPanel panel) {
		return failedPanelBatch.add(panel);
	}
	
	/**
	 * Called when the last video has been extracted, downloaded and converted.<br>
	 * This function will tell the ui that everything is done.<br>
	 * But if some video failed the extraction/download/conversion, this function will automatically retry them.
	 */
	public void endBatch() {
		boolean retryLimitReached = false;
		
		if (!failedPanelBatch.isEmpty()) {
			if (batchRetryCount++ < Config.WORKER_BATCH_MAX_RETRY_COUNT) {
				if (batchCallback != null) {
					batchCallback.onRetryCalled(batchRetryCount);
				}
				
				ThreadUtils.sleep(500L);
				download(failedPanelBatch);
				
				return; /* Still have retry */
			}
			
			retryLimitReached = true;
		}
		
		batchRetryCount = 0;
		failedPanelBatch = null;
		
		if (batchCallback != null) {
			batchCallback.onBatchEnd(retryLimitReached);
		}
	}
	
	/**
	 * Tell if the {@link VideoManager} has already start a batch
	 * 
	 * @return <code>failedPanelBatch != null</code>
	 */
	public boolean isInBatch() {
		return failedPanelBatch != null;
	}
	
	/**
	 * @return Video Manager singleton
	 */
	public static VideoManager getVideoManager() {
		if (MANAGER == null) {
			MANAGER = new VideoManager();
		}
		
		return MANAGER;
	}
	
	/**
	 * Batch callback used to follow global progression
	 * 
	 * @author Enzo CACERES
	 */
	public interface BatchDownloadCallback {
		
		/**
		 * Called when the whole process have started or when a retry has been started
		 * 
		 * @param panels
		 *            Actual batch panels that will be download
		 * @param actualBatchRetryCount
		 *            Actual retry count, if is equal to 0, that the first run
		 */
		void onDownloadsStarted(List<VideoPanel> panels, int actualBatchRetryCount);
		
		/**
		 * Called when a {@link VideoDownloadWorker} has just finished
		 * 
		 * @param videoPanel
		 *            Attached {@link VideoPanel} of the {@link VideoDownloadWorker}
		 */
		void onVideoDownloaded(VideoPanel videoPanel);
		
		/**
		 * Called when a retry batch is ready to begin
		 * 
		 * @param newBatchRetryCount
		 *            New barch retry count
		 */
		void onRetryCalled(int newBatchRetryCount);
		
		/**
		 * Called when the whole download process has ended
		 * 
		 * @param retryLimitReached
		 *            If the stop reason is a retry limit reached
		 */
		void onBatchEnd(boolean retryLimitReached);
		
	}
	
}