package caceresenzo.apps.youtube.downloader.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;

import caceresenzo.apps.youtube.downloader.config.Config;
import caceresenzo.apps.youtube.downloader.worker.ImageDownloaderWorker;
import caceresenzo.apps.youtube.downloader.worker.VideoDownloadWorker;
import caceresenzo.libs.cryptography.MD5;
import caceresenzo.libs.filesystem.FileUtils;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.string.StringUtils;
import caceresenzo.libs.youtube.format.YoutubeFormat;
import caceresenzo.libs.youtube.playlist.YoutubePlaylistItem;
import javax.swing.JProgressBar;

public class VideoPanel extends JPanel {
	
	private JLabel imageLabel;
	private JLabel titleLabel;
	private JButton downloadButton;
	private JProgressBar itemProgressBar;
	private JLabel etaLabel;
	
	private final YoutubePlaylistItem youtubePlaylistItem;
	private final File videoFile;
	private final VideoDownloadWorker.WorkerCallback workerCallback;
	
	public VideoPanel(YoutubePlaylistItem youtubePlaylistItem) {
		this.youtubePlaylistItem = youtubePlaylistItem;
		this.videoFile = new File(Config.PATH_DOWNLOAD_DIRECTORY, FileUtils.replaceIllegalChar(youtubePlaylistItem.getVideoMeta().getTitle()) + ".mp3");
		this.workerCallback = new VideoDownloadWorker.WorkerCallback() {
			private Exception error;
			
			@Override
			public void onStarted() {
				etaLabel.setText(i18n.string("worker.eta.waiting"));
				itemProgressBar.setIndeterminate(true);
				itemProgressBar.setValue(0);
				
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
				etaLabel.setText(i18n.string("worker.downloader.eta.error", StringUtils.cutIfTooLong(exception.getLocalizedMessage(), 150)));
				itemProgressBar.setIndeterminate(false);
				itemProgressBar.setValue(0);
				
				error = exception;
				buttonState(false);
				
				exception.printStackTrace();
			}
			
			@Override
			public void onFinished() {
				etaLabel.setText(i18n.string("worker.eta.waiting"));
				itemProgressBar.setIndeterminate(false);
				itemProgressBar.setValue(0);
				
				buttonState(false);
			}
			
			private void buttonState(boolean working) {
				downloadButton.setEnabled(!working);
				downloadButton.setText(i18n.string(working ? "ui.button.download.downloading" : "ui.button.download.selected"));
				
				if (error == null) {
					etaLabel.setVisible(working);
				} else {
					etaLabel.setVisible(true);
					etaLabel.setText(i18n.string("worker.downloader.eta.error", error.getLocalizedMessage()));
				}
				itemProgressBar.setVisible(working);
				
				if (!working) {
					updateDownloadButton();
				}
			}
		};
		
		initialize();
		
		initializeListeners();
	}
	
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
	
	private void initializeListeners() {
		downloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				VideoDownloadWorker.fromVideoItem(youtubePlaylistItem).callback(workerCallback).start();
			}
		});
	}
	
	public VideoPanel enableDownloadButton(boolean enable) {
		if (!isFileAlreadyDownloaded()) {
			this.downloadButton.setEnabled(enable);
		}
		
		return this;
	}
	
	public boolean isFileAlreadyDownloaded() {
		return videoFile.exists();
	}
	
	public void updateDownloadButton() {
		if (isFileAlreadyDownloaded()) {
			downloadButton.setEnabled(false);
			downloadButton.setText(i18n.string("ui.button.download.already"));
		}
	}
	
	public JProgressBar getItemProgressBar() {
		return itemProgressBar;
	}
	
	public YoutubePlaylistItem getYoutubePlaylistItem() {
		return youtubePlaylistItem;
	}
	
	public JLabel getEtaLabel() {
		return etaLabel;
	}
}