package caceresenzo.apps.youtube.downloader.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;

import caceresenzo.apps.youtube.downloader.manager.VideoManager;
import caceresenzo.apps.youtube.downloader.worker.ImageDownloaderWorker;
import caceresenzo.apps.youtube.downloader.worker.VideoDownloadWorker;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.string.StringUtils;
import caceresenzo.libs.youtube.playlist.YoutubePlaylistItem;

/**
 * Displayer components that extends {@link JPanel} to showcase a single {@link YoutubePlaylistItem}
 * 
 * @author Enzo CACERES
 */
public class VideoPanel extends JPanel {
	
	/* Components */
	private JLabel imageLabel, titleLabel, etaLabel;
	private JButton downloadButton;
	private JProgressBar itemProgressBar;
	
	/* Variables */
	private final YoutubePlaylistItem youtubePlaylistItem;
	private final File videoFile;
	private final VideoDownloadWorker.WorkerCallback workerCallback;
	
	/**
	 * Create a new {@link VideoPanel} that will handle his own download button and progress
	 * 
	 * @param youtubePlaylistItem
	 *            Attached {@link YoutubePlaylistItem}
	 */
	public VideoPanel(final YoutubePlaylistItem youtubePlaylistItem) {
		this.youtubePlaylistItem = youtubePlaylistItem;
		this.videoFile = VideoDownloadWorker.createTargetVideoFile(youtubePlaylistItem.getVideoMeta().getTitle());
		this.workerCallback = new VideoDownloadWorker.WorkerCallback() {
			private Exception error;
			
			@Override
			public void onStarted() {
				etaLabel.setText(i18n.string("worker.eta.waiting"));
				itemProgressBar.setIndeterminate(true);
				itemProgressBar.setValue(0);
				
				error = null;
				buttonState(true);
			}
			
			@Override
			public void onVideoExtraction(YoutubePlaylistItem item) {
				etaLabel.setText(i18n.string("worker.downloader.eta.extraction"));
				itemProgressBar.setIndeterminate(true);
				itemProgressBar.setValue(0);
			}
			
			@Override
			public void onVideoDownload(YoutubePlaylistItem item) {
				etaLabel.setText(i18n.string("worker.downloader.eta.download"));
				itemProgressBar.setIndeterminate(true);
				itemProgressBar.setValue(0);
			}
			
			@Override
			public void onVideoDownloadProgressUpdate(YoutubePlaylistItem item, float progress) {
				if (itemProgressBar.isIndeterminate()) {
					itemProgressBar.setIndeterminate(false);
				}
				itemProgressBar.setValue((int) progress);
			}
			
			@Override
			public void onVideoConversion(YoutubePlaylistItem item) {
				etaLabel.setText(i18n.string("worker.downloader.eta.conversion"));
				itemProgressBar.setIndeterminate(true);
				itemProgressBar.setValue(0);
			}
			
			@Override
			public void onVideoConversionProgressUpdate(YoutubePlaylistItem item, float progress) {
				if (itemProgressBar.isIndeterminate()) {
					itemProgressBar.setIndeterminate(false);
				}
				itemProgressBar.setValue((int) progress);
			}
			
			@Override
			public void onVideoSaving(YoutubePlaylistItem item, File targetFile) {
				etaLabel.setText(i18n.string("worker.downloader.eta.saving"));
				itemProgressBar.setIndeterminate(true);
			}
			
			@Override
			public void onVideoException(YoutubePlaylistItem item, Exception exception, boolean critical) {
				onException(exception, critical);
			}
			
			@Override
			public void onException(Exception exception, boolean critical) {
				itemProgressBar.setIndeterminate(false);
				itemProgressBar.setValue(0);
				
				error = exception;
				buttonState(false);
				
				Logger.exception(exception, "Failed to extract/download/convert video: " + youtubePlaylistItem.getVideoMeta().getTitle());
			}
			
			@Override
			public void onFinished() {
				etaLabel.setText(i18n.string("worker.eta.waiting"));
				itemProgressBar.setIndeterminate(false);
				itemProgressBar.setValue(0);
				
				buttonState(false);
				
				if (error != null) {
					VideoManager.getVideoManager().addFailedPanel(VideoPanel.this);
				}
			}
			
			private void buttonState(boolean working) {
				downloadButton.setEnabled(!working);
				downloadButton.setText(i18n.string(working ? "ui.button.download.downloading" : "ui.button.download.selected"));
				
				if (error == null) {
					etaLabel.setVisible(working);
				} else {
					etaLabel.setVisible(true);
					etaLabel.setText(i18n.string("worker.downloader.eta.error", StringUtils.cutIfTooLong(error.getLocalizedMessage(), 150)));
				}
				itemProgressBar.setVisible(working);
				
				if (!working) {
					updateDownloadButton();
					
					if (VideoManager.getVideoManager().isInBatch()) {
						downloadButton.setEnabled(false);
					}
				}
			}
		};
		
		initialize();
		
		initializeListeners();
	}
	
	/* Initializer */
	private void initialize() {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		imageLabel = new JLabel();
		new ImageDownloaderWorker(imageLabel, youtubePlaylistItem.getVideoMeta().getThumbnails().getDefaultThumbnailImageUrl()).queue();
		
		titleLabel = new JLabel(youtubePlaylistItem.getVideoMeta().getTitle());
		
		downloadButton = new JButton(i18n.string("ui.button.download.selected"));
		updateDownloadButton();
		
		itemProgressBar = new JProgressBar();
		itemProgressBar.setStringPainted(true);
		itemProgressBar.setVisible(false);
		
		etaLabel = new JLabel();
		etaLabel.setVisible(false);
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(imageLabel, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addComponent(titleLabel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE).addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup().addComponent(itemProgressBar, GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED).addComponent(downloadButton)).addComponent(etaLabel, GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(titleLabel).addPreferredGap(ComponentPlacement.RELATED, 28, Short.MAX_VALUE).addComponent(etaLabel).addPreferredGap(ComponentPlacement.RELATED).addGroup(groupLayout.createParallelGroup(Alignment.TRAILING, false).addComponent(itemProgressBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(downloadButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))).addGroup(groupLayout.createSequentialGroup().addGap(7).addComponent(imageLabel, GroupLayout.DEFAULT_SIZE, 89, Short.MAX_VALUE))).addContainerGap()));
		setLayout(groupLayout);
	}
	
	/* Initializer */
	private void initializeListeners() {
		downloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				download().start();
			}
		});
	}
	
	/**
	 * Change {@link JButton#setEnabled(boolean)}, but will only affect it if the file hasn't been downloaded yet! ({@link #isFileAlreadyDownloaded()})
	 * 
	 * @param enable
	 *            New button state
	 * @return Itself
	 */
	public VideoPanel enableDownloadButton(boolean enable) {
		if (!isFileAlreadyDownloaded()) {
			this.downloadButton.setEnabled(enable);
		}
		
		return this;
	}
	
	/**
	 * @return If the file has already been downloaded
	 */
	public boolean isFileAlreadyDownloaded() {
		return videoFile.exists();
	}
	
	/**
	 * Will disable the download button and set its text to "Already downloaded" (translated)
	 */
	public void updateDownloadButton() {
		if (isFileAlreadyDownloaded()) {
			downloadButton.setEnabled(false);
			downloadButton.setText(i18n.string("ui.button.download.already"));
		}
	}
	
	/**
	 * @return An instance of a {@link VideoDownloadWorker} created with the {@link VideoPanel} with his attached {@link VideoDownloadWorker.WorkerCallback}<br>
	 *         You will need to call {@link VideoDownloadWorker#start()} if you want to start extraction/download/conversion process.
	 */
	public VideoDownloadWorker download() {
		return VideoDownloadWorker.fromVideoItem(youtubePlaylistItem).callback(workerCallback);
	}
	
}