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
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import caceresenzo.apps.youtube.downloader.config.Language;
import caceresenzo.libs.internationalization.i18n;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

public class DownloaderFrame {
	
	private JFrame frame;
	private JTextField urlTextField;
	private JScrollPane listScrollPane;
	private JButton startButton;
	private JProgressBar mainProgressBar;
	private JPanel panel;
	private JPanel listPanel;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Language.getLanguage().initialize();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
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
		
		urlTextField = new JTextField();
		urlTextField.setColumns(10);
		
		mainProgressBar = new JProgressBar();
		mainProgressBar.setStringPainted(true);
		
		startButton = new JButton(i18n.string("ui.button.start"));
		
		listScrollPane = new JScrollPane();
		listScrollPane.getVerticalScrollBar().setUnitIncrement(10);
		listScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		listScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		JButton outputDirectoryButton = new JButton(i18n.string("ui.button.output.directory"));
		
		JButton downloadAllButton = new JButton(i18n.string("ui.button.download.all"));
		
		JButton downloadButton = new JButton(i18n.string("ui.button.download.selected"));
		
		JPanel etaPanel = new JPanel();
		etaPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addGroup(gl_panel.createSequentialGroup().addContainerGap().addGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addComponent(etaPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE).addComponent(listScrollPane, GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE).addComponent(mainProgressBar, GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE).addGroup(gl_panel.createSequentialGroup().addGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel.createSequentialGroup().addComponent(downloadButton, GroupLayout.PREFERRED_SIZE, 101, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(downloadAllButton, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE)).addComponent(urlTextField, GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)).addPreferredGap(ComponentPlacement.RELATED).addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false).addComponent(outputDirectoryButton, 0, 0, Short.MAX_VALUE).addComponent(startButton, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)))).addContainerGap()));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addGroup(gl_panel.createSequentialGroup().addContainerGap().addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(urlTextField, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE).addComponent(startButton)).addPreferredGap(ComponentPlacement.RELATED).addGroup(gl_panel.createParallelGroup(Alignment.BASELINE).addComponent(outputDirectoryButton).addComponent(downloadButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(downloadAllButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addPreferredGap(ComponentPlacement.RELATED).addComponent(listScrollPane, GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE).addPreferredGap(ComponentPlacement.RELATED).addComponent(etaPanel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED).addComponent(mainProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addGap(6)));
		
		JLabel etaLabel = new JLabel(i18n.string("worker.extractor.eta.waiting"));
		GroupLayout gl_etaPanel = new GroupLayout(etaPanel);
		gl_etaPanel.setHorizontalGroup(gl_etaPanel.createParallelGroup(Alignment.TRAILING).addGroup(gl_etaPanel.createSequentialGroup().addContainerGap().addComponent(etaLabel, GroupLayout.DEFAULT_SIZE, 670, Short.MAX_VALUE)));
		gl_etaPanel.setVerticalGroup(gl_etaPanel.createParallelGroup(Alignment.LEADING).addComponent(etaLabel, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE));
		etaPanel.setLayout(gl_etaPanel);
		
		listPanel = new JPanel();
		listScrollPane.setViewportView(listPanel);
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		panel.setLayout(gl_panel);
		
		for (int i = 0; i < 15; i++) {
			listPanel.add(new VideoPanel());
		}
	}
}