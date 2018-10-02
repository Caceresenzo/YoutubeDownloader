package caceresenzo.apps.youtube.downloader.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;

import caceresenzo.apps.youtube.downloader.worker.ImageDownloaderWorker;

public class VideoPanel extends JPanel {
	
	/**
	 * Create the panel.
	 */
	public VideoPanel() {
		setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		JLabel imageLabel = new JLabel("IMAGE");
		new ImageDownloaderWorker(imageLabel, "https://i.ytimg.com/vi/JES55S-l5RM/default.jpg").queue();
		
		JLabel titleLabel = new JLabel("New label");
		
		JButton downloadButton = new JButton("Download");
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGap(6).addComponent(imageLabel, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.UNRELATED).addGroup(groupLayout.createParallelGroup(Alignment.TRAILING).addComponent(titleLabel, GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE).addComponent(downloadButton)).addContainerGap()));
		groupLayout.setVerticalGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addGroup(groupLayout.createParallelGroup(Alignment.LEADING).addGroup(groupLayout.createSequentialGroup().addContainerGap().addComponent(titleLabel).addPreferredGap(ComponentPlacement.RELATED, 62, Short.MAX_VALUE).addComponent(downloadButton)).addGroup(groupLayout.createSequentialGroup().addGap(7).addComponent(imageLabel, GroupLayout.PREFERRED_SIZE, 99, Short.MAX_VALUE))).addContainerGap()));
		setLayout(groupLayout);
		
	}
}
