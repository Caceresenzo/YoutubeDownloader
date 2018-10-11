package caceresenzo.apps.youtube.downloader.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

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
	protected static Properties PROPERTIES_PATHS;
	public static String PATH_DOWNLOAD_DIRECTORY;
	public static String PATH_FFMPEG_EXECUTABLE;
	
	/* Config -> Worker */
	protected static Properties PROPERTIES_WORKER;
	public static int WORKER_COUNT_MAX;
	public static int WORKER_BATCH_MAX_RETRY_COUNT;
	
	/* Initializer */
	public static void initialize() {
		/* Paths */
		PROPERTIES_PATHS = new Properties();
		try {
			PROPERTIES_PATHS.load(new FileInputStream(new File(CONFIG_PATHS)));
			
			PATH_DOWNLOAD_DIRECTORY = PROPERTIES_PATHS.getProperty("path.dowload.directory");
			PATH_FFMPEG_EXECUTABLE = PROPERTIES_PATHS.getProperty("path.ffmpeg.executable");
		} catch (Exception exception) {
			Logger.exception(exception, "Failed to load \"paths\" config.");
			System.exit(-1);
		}
		
		/* Worker */
		PROPERTIES_WORKER = new Properties();
		try {
			PROPERTIES_WORKER.load(new FileInputStream(new File(CONFIG_WORKER)));
			
			WORKER_COUNT_MAX = Integer.parseInt(PROPERTIES_WORKER.getProperty("worker.count.max"));
			WORKER_BATCH_MAX_RETRY_COUNT = Integer.parseInt(PROPERTIES_WORKER.getProperty("worker.batch.max_retry_count"));
		} catch (Exception exception) {
			Logger.exception(exception, "Failed to load \"worker\" config.");
			System.exit(-1);
		}
	}
	
	public static void save() {
		try {
			PROPERTIES_PATHS.setProperty("path.dowload.directory", String.valueOf(PATH_DOWNLOAD_DIRECTORY));
			PROPERTIES_PATHS.setProperty("path.ffmpeg.executable", String.valueOf(PATH_FFMPEG_EXECUTABLE));
			
			PROPERTIES_PATHS.store(new FileOutputStream(CONFIG_PATHS), null);
		} catch (Exception exception) {
			Logger.exception(exception, "Failed to save \"paths\" config.");
			System.exit(-1);
		}
		
		try {
			PROPERTIES_WORKER.setProperty("worker.count.max", String.valueOf(WORKER_COUNT_MAX));
			PROPERTIES_WORKER.setProperty("worker.batch.max_retry_count", String.valueOf(WORKER_BATCH_MAX_RETRY_COUNT));
			
			PROPERTIES_WORKER.store(new FileOutputStream(CONFIG_WORKER), null);
		} catch (Exception exception) {
			Logger.exception(exception, "Failed to save \"worker\" config.");
			System.exit(-1);
		}
	}
	
}