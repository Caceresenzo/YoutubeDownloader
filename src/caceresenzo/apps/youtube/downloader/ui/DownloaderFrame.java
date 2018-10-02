package caceresenzo.apps.youtube.downloader.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import caceresenzo.apps.youtube.downloader.config.Language;

import javax.swing.ScrollPaneConstants;

public class DownloaderFrame {
	
	private JFrame frame;
	private JTextField textField;
	private JScrollPane scrollPane;
	private JButton btnNewButton;
	private JProgressBar progressBar;
	private JPanel panel;
	private JPanel panel_1;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Language.getLanguage().initialize();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DownloaderFrame window = new DownloaderFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the application.
	 */
	public DownloaderFrame() {
		initialize();
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 720, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		textField = new JTextField();
		textField.setColumns(10);
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		
		btnNewButton = new JButton("New button");
		
		scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addContainerGap().addGroup(gl_panel.createParallelGroup(Alignment.LEADING).addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE).addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup().addComponent(textField, GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED).addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE)).addComponent(progressBar, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)).addContainerGap()));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addGroup(gl_panel.createSequentialGroup().addContainerGap().addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(textField, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addComponent(btnNewButton)).addPreferredGap(ComponentPlacement.RELATED).addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED).addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addContainerGap()));
		
		panel_1 = new JPanel();
		scrollPane.setViewportView(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		panel.setLayout(gl_panel);
		
		for (int i = 0; i < 15; i++) {
			panel_1.add(new VideoPanel());
		}
	}
	
}