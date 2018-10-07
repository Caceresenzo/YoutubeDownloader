package caceresenzo.apps.youtube.downloader;

import java.io.File;

import caceresenzo.apps.youtube.downloader.config.Config;
import caceresenzo.apps.youtube.downloader.config.Language;
import caceresenzo.apps.youtube.downloader.manager.VideoManager;
import caceresenzo.apps.youtube.downloader.ui.DownloaderFrame;

/**
 * Boostrap
 * 
 * @author Enzo CACERES
 */
public class Bootstrap {
	
	/* Constants */
	public static final String CACHE_FOLDER = new File("./cache/").getAbsolutePath();
	
	/* Boostrap */
	public static void main(String[] args) {
		Config.initialize();
		Language.getLanguage().initialize();
		VideoManager.getVideoManager().initialize();
		
		DownloaderFrame.display();
	}
	
}