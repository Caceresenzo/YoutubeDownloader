package caceresenzo.apps.youtube.downloader.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import caceresenzo.apps.youtube.downloader.manager.VideoManager;
import caceresenzo.libs.logger.Logger;

/**
 * External .properties config handler
 * 
 * @author Enzo CACERES
 */
public class Config {
	
	/* Base config directory */
	public static String CONFIG_DIRECTORY = "./config/";
	
	/* Configs directories */
	public static String CONFIG_PATHS = CONFIG_DIRECTORY + "paths.properties";
	public static String CONFIG_WORKER = CONFIG_DIRECTORY + "worker.properties";
	
	/* Config -> Paths */
	public static String PATH_DOWNLOAD_DIRECTORY;
	public static String PATH_FFMPEG_EXECUTABLE;
	
	/* Config -> Worker */
	public static int WORKER_COUNT_MAX;
	public static int WORKER_BATCH_MAX_RETRY_COUNT;
	
	/* Initializer */
	public static void initialize() {
		/* Paths */
		Properties pathsProperties = new Properties();
		try {
			pathsProperties.load(new FileInputStream(new File(CONFIG_PATHS)));
			
			PATH_DOWNLOAD_DIRECTORY = pathsProperties.getProperty("path.dowload.directory");
			PATH_FFMPEG_EXECUTABLE = pathsProperties.getProperty("path.ffmpeg.executable");
		} catch (Exception exception) {
			Logger.exception(exception, "Failed to load \"paths\" config.");
			System.exit(-1);
		}
		
		/* Worker */
		Properties workerProperties = new Properties();
		try {
			workerProperties.load(new FileInputStream(new File(CONFIG_WORKER)));
			
			WORKER_COUNT_MAX = Integer.parseInt(workerProperties.getProperty("worker.count.max"));
			WORKER_BATCH_MAX_RETRY_COUNT = Integer.parseInt(workerProperties.getProperty("worker.batch.max_retry_count"));
		} catch (Exception exception) {
			Logger.exception(exception, "Failed to load \"worker\" config.");
			System.exit(-1);
		}
		
		Language.getLanguage().initialize();
		VideoManager.getVideoManager();
	}
	
}