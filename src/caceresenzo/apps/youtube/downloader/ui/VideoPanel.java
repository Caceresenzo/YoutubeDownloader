package caceresenzo.apps.youtube.downloader.ui;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;

import caceresenzo.apps.youtube.downloader.worker.ImageDownloaderWorker;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.youtube.video.VideoMeta;

public class VideoPanel extends JPanel {
	
	private JLabel imageLabel;
	private JLabel titleLabel;
	private JButton downloadButton;
	
	public VideoPanel(VideoMeta videoMeta) {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		imageLabel = new JLabel();
		new ImageDownloaderWorker(imageLabel, videoMeta.getThumbnails().getDefaultThumbnailImageUrl()).queue();
		
		titleLabel = new JLabel(videoMeta.getTitle());
		
		downloadButton = new JButton(i18n.string("ui.button.download.selected"));
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(imageLabel, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addComponent(titleLabel, GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE).addComponent(downloadButton)).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(titleLabel).addPreferredGap(ComponentPlacement.RELATED, 62, Short.MAX_VALUE).addComponent(downloadButton)).addGroup(groupLayout.createSequentialGroup().addGap(7).addComponent(imageLabel, GroupLayout.PREFERRED_SIZE, 99, Short.MAX_VALUE))).addContainerGap()));
		setLayout(groupLayout);
	}
	
	public VideoPanel enableDownloadButton(boolean enable) {
		this.downloadButton.setEnabled(enable);
		
		return this;
	}
	
}