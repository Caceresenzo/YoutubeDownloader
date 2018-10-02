package caceresenzo.apps.youtube.downloader.worker;

import java.io.File;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import caceresenzo.apps.youtube.downloader.Bootstrap;
import caceresenzo.libs.cryptography.MD5;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.thread.queue.WorkQueue;

/**
 * Queued worker used to download and manage image cache (thumbnail for exemple)<br>
 * Limited to one worker at a time
 * 
 * @author Enzo CACERES
 */
public class ImageDownloaderWorker {
	
	/* Constants */
	public static final String IMAGE_CACHE_FOLDER = new File(Bootstrap.CACHE_FOLDER, "/images/").getAbsolutePath();
	
	static {
		try {
			new File(IMAGE_CACHE_FOLDER).mkdirs();
		} catch (Exception exception) {
			;
		}
	}
	
	/* Static */
	private static WorkQueue queue = new WorkQueue(1);
	private static Object taskGroup = queue.registerTaskGroup(1);
	
	/* Variables */
	private JLabel label;
	private String url;
	
	/* Constructor */
	public ImageDownloaderWorker(JLabel label, String url) {
		this.label = label;
		this.url = url;
		
		label.setText(i18n.string("worker.imagedownloader.eta.downloading"));
	}
	
	/**
	 * Start as soon as possible (Add to thread queue)
	 */
	public void queue() {
		queue.add(taskGroup, new Runnable() {
			public File cacheFile = new File(IMAGE_CACHE_FOLDER, MD5.silentMd5(url));
			
			@Override
			public void run() {
				if (!cacheFile.exists()) {
					try {
						if (cacheFile.createNewFile()) {
							ImageIO.write(ImageIO.read(new URL(url)), "png", cacheFile);
						} else {
							throw new Exception();
						}
					} catch (Exception exception) {
						silentDelete();
					}
				}
				
				try {
					label.setIcon(new ImageIcon(ImageIO.read(cacheFile)));
				} catch (Exception exception) {
					label.setText(i18n.string("worker.imagedownloader.eta.error"));
					silentDelete();
				}
			}
			
			public void silentDelete() {
				try {
					cacheFile.delete();
				} catch (Exception exception2) {
					;
				}
			}
		});
	}
	
}