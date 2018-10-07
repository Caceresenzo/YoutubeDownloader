package caceresenzo.apps.youtube.downloader.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;

import caceresenzo.apps.youtube.downloader.config.Config;
import caceresenzo.apps.youtube.downloader.manager.VideoManager;
import caceresenzo.apps.youtube.downloader.worker.PlaylistExtractionWorker;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.math.MathUtils;
import caceresenzo.libs.youtube.playlist.YoutubePlaylist;
import caceresenzo.libs.youtube.playlist.YoutubePlaylistItem;

public class DownloaderFrame {
	
	private JFrame frame;
	private JTextField urlTextField;
	private JScrollPane listScrollPane;
	private JButton startButton;
	private JProgressBar mainProgressBar;
	private JPanel panel;
	private JPanel listPanel;
	private JPanel etaPanel;
	private JButton outputDirectoryButton;
	private JButton downloadAllButton;
	private JLabel etaLabel;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Config.initialize();
		
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
		
		initializeListeners();
		
		urlTextField.setText("https://www.youtube.com/playlist?list=PLw-VjHDlEOgvtnnnqWlTqByAtC7tXBg6D");
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 720, 480);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Downloader");
		
		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		
		urlTextField = new JTextField();
		urlTextField.setText("");
		urlTextField.setColumns(10);
		
		mainProgressBar = new JProgressBar();
		mainProgressBar.setStringPainted(true);
		mainProgressBar.setMaximum(100);
		
		startButton = new JButton(i18n.string("ui.button.start"));
		
		listScrollPane = new JScrollPane();
		listScrollPane.getVerticalScrollBar().setUnitIncrement(10);
		listScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		listScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		outputDirectoryButton = new JButton(i18n.string("ui.button.output.directory"));
		
		downloadAllButton = new JButton(i18n.string("ui.button.download.all"));
		
		etaPanel = new JPanel();
		etaPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(etaPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
						.addComponent(listScrollPane, GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
						.addComponent(mainProgressBar, GroupLayout.DEFAULT_SIZE, 684, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(urlTextField, GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
								.addComponent(downloadAllButton, GroupLayout.PREFERRED_SIZE, 142, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false)
								.addComponent(outputDirectoryButton, 0, 0, Short.MAX_VALUE)
								.addComponent(startButton, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(urlTextField, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addComponent(startButton))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(outputDirectoryButton)
						.addComponent(downloadAllButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(listScrollPane, GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(etaPanel, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(mainProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(6))
		);
		
		etaLabel = new JLabel(i18n.string("worker.eta.waiting"));
		GroupLayout gl_etaPanel = new GroupLayout(etaPanel);
		gl_etaPanel.setHorizontalGroup(gl_etaPanel.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING, gl_etaPanel.createSequentialGroup().addContainerGap().addComponent(etaLabel, GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE).addContainerGap()));
		gl_etaPanel.setVerticalGroup(gl_etaPanel.createParallelGroup(Alignment.LEADING).addGroup(gl_etaPanel.createSequentialGroup().addComponent(etaLabel, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE).addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		etaPanel.setLayout(gl_etaPanel);
		
		listPanel = new JPanel();
		listScrollPane.setViewportView(listPanel);
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
		panel.setLayout(gl_panel);
	}
	
	private void initializeListeners() {
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				listPanel.removeAll();
				
				etaLabel.setText(i18n.string("worker.extractor.eta.working"));
				mainProgressBar.setIndeterminate(true);
				
				try {
					PlaylistExtractionWorker.fromUrl(urlTextField.getText()).callback(new PlaylistExtractionWorker.WorkerCallback() {
						private int count;
						
						@Override
						public void onStarted() {
							state(true);
						}
						
						@Override
						public void onPageFetched(YoutubePlaylist youtubePlaylist) {
							List<YoutubePlaylistItem> items = youtubePlaylist.getItems();
							
							count += items.size();
							etaLabel.setText(i18n.string("worker.extractor.eta.working.count", count, count > 1 ? "s" : "", youtubePlaylist.getTotalResults()));
							if (mainProgressBar.isIndeterminate()) {
								mainProgressBar.setIndeterminate(false);
							}
							mainProgressBar.setValue((int) MathUtils.pourcent(count, youtubePlaylist.getTotalResults()));
							
							for (YoutubePlaylistItem item : items) {
								listPanel.add(new VideoPanel(item).enableDownloadButton(false));
							}
						}
						
						@Override
						public void onException(Exception exception, boolean critical) {
							etaLabel.setText(i18n.string("worker.extractor.eta.error.common", exception.getMessage()));
							if (mainProgressBar.isIndeterminate()) {
								mainProgressBar.setIndeterminate(false);
							}
							mainProgressBar.setValue(0);
							
							state(false);
						}
						
						@Override
						public void onFinished(List<YoutubePlaylistItem> allItems) {
							etaLabel.setText(i18n.string("worker.eta.waiting"));
							
							state(false);
						}
						
						public void state(boolean running) {
							urlTextField.setEditable(!running);
							startButton.setEnabled(!running);
							
							if (!running) {
								changeItemsDownloadButtonState(true);
							}
						}
					}).start();
				} catch (MalformedURLException exception) {
					etaLabel.setText(i18n.string("worker.extractor.eta.working"));
				}
			}
		});
		
		downloadAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				changeItemsDownloadButtonState(false);
				
				List<VideoPanel> panels = new ArrayList<>();
				
				for (VideoPanel videoPanel : getListedVideoPanels()) {
					if (!videoPanel.isFileAlreadyDownloaded()) {
						panels.add(videoPanel);
					}
				}
				
				changeButtonState(false);
				VideoManager.getVideoManager().download(panels, new VideoManager.BatchDownloadCallback() {
					private int batchSize, videoCountLeft, actualRetryCount, actualPanelIndex;
					
					@Override
					public void onDownloadsStarted(List<VideoPanel> panels, int actualBatchRetryCount) {
						batchSize = videoCountLeft = panels.size();
						actualRetryCount = actualBatchRetryCount;
						
						updateProgress();
						
						mainProgressBar.setIndeterminate(true);
						mainProgressBar.setValue(0);
					}
					
					@Override
					public void onVideoDownloaded(VideoPanel videoPanel) {
						videoCountLeft--;
						
						updateProgress();
						
						if (mainProgressBar.isIndeterminate()) {
							mainProgressBar.setIndeterminate(false);
						}
						mainProgressBar.setValue((int) MathUtils.pourcent(++actualPanelIndex, batchSize));
					}
					
					@Override
					public void onRetryCalled(int newBatchRetryCount) {
						etaLabel.setText(i18n.string("worker.extractor.eta.downloader.retry-called", newBatchRetryCount));
						mainProgressBar.setIndeterminate(true);
						mainProgressBar.setValue(0);
					}
					
					@Override
					public void onBatchEnd(boolean retryLimitReached) {
						etaLabel.setText(i18n.string(retryLimitReached ? "worker.extractor.eta.error.retry-limit-reached" : "worker.eta.waiting"));
						mainProgressBar.setIndeterminate(false);
						mainProgressBar.setValue(0);
						
						changeButtonState(true);
					}
					
					private void updateProgress() {
						String multiple = batchSize > 1 ? "s" : "";
						if (actualRetryCount == 0) {
							etaLabel.setText(i18n.string("worker.extractor.eta.downloader.started", videoCountLeft, multiple));
						} else {
							etaLabel.setText(i18n.string("worker.extractor.eta.downloader.started.retry", videoCountLeft, multiple, actualRetryCount));
						}
					}
				});
			}
		});
	}
	
	public void changeItemsDownloadButtonState(boolean newState) {
		for (VideoPanel videoPanel : getListedVideoPanels()) {
			videoPanel.enableDownloadButton(newState);
		}
	}
	
	public List<VideoPanel> getListedVideoPanels() {
		List<VideoPanel> panels = new ArrayList<>();
		
		for (int i = 0; i < listPanel.getComponentCount(); i++) {
			Component component = listPanel.getComponent(i);
			
			if (component instanceof VideoPanel) {
				panels.add((VideoPanel) component);
			}
		}
		
		return panels;
	}
	
	public void changeButtonState(boolean enabled) {
		JButton[] buttons = new JButton[] {startButton, outputDirectoryButton, downloadAllButton};
		
		for (JButton button : buttons) {
			button.setEnabled(enabled);
		}
		
	}
	
	public JPanel getEtaPanel() {
		return etaPanel;
	}
	
	public JPanel getPanel() {
		return panel;
	}
	
	public JButton getOutputDirectoryButton() {
		return outputDirectoryButton;
	}
	
	public JButton getDownloadAllButton() {
		return downloadAllButton;
	}
	
	public JButton getStartButton() {
		return startButton;
	}
	
	public JLabel getEtaLabel() {
		return etaLabel;
	}
	
	public JScrollPane getListScrollPane() {
		return listScrollPane;
	}
	
	public JTextField getUrlTextField() {
		return urlTextField;
	}
	
	public JProgressBar getMainProgressBar() {
		return mainProgressBar;
	}
}