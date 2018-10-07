package caceresenzo.apps.youtube.downloader.manager;

import java.util.ArrayList;
import java.util.List;

import caceresenzo.apps.youtube.downloader.config.Config;
import caceresenzo.apps.youtube.downloader.ui.VideoPanel;
import caceresenzo.apps.youtube.downloader.worker.VideoDownloadWorker;
import caceresenzo.libs.thread.ThreadUtils;
import caceresenzo.libs.thread.queue.WorkQueue;

public class VideoManager {
	
	/* Static */
	private static VideoManager MANAGER;
	
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
	
	public interface BatchDownloadCallback {
		
		void onDownloadsStarted(List<VideoPanel> panels, int actualBatchRetryCount);
		
		void onVideoDownloaded(VideoPanel videoPanel);
		
		void onRetryCalled(int newBatchRetryCount);
		
		void onBatchEnd(boolean retryLimitReached);
		
	}
	
}