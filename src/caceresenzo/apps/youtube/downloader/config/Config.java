package caceresenzo.apps.youtube.downloader.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import caceresenzo.libs.internationalization.i18n;
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
			
			if (!(new File(PATH_FFMPEG_EXECUTABLE).exists())) {
				JOptionPane.showMessageDialog(new JFrame(), i18n.string("ui.dialog.error.ffmpeg-not-found", PATH_FFMPEG_EXECUTABLE), i18n.string("ui.dialog.error.title"), JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
				throw new Exception("FFMPEG target don't exist.");
			}
		} catch (Exception exception) {
			handleConfigException(exception, "paths");
		}
		
		/* Worker */
		PROPERTIES_WORKER = new Properties();
		try {
			PROPERTIES_WORKER.load(new FileInputStream(new File(CONFIG_WORKER)));
			
			WORKER_COUNT_MAX = Integer.parseInt(PROPERTIES_WORKER.getProperty("worker.count.max"));
			WORKER_BATCH_MAX_RETRY_COUNT = Integer.parseInt(PROPERTIES_WORKER.getProperty("worker.batch.max_retry_count"));
		} catch (Exception exception) {
			handleConfigException(exception, "worker");
		}
	}
	
	/**
	 * Update and save {@link #PROPERTIES_PATHS} and {@link #PROPERTIES_WORKER}<br>
	 * <b>WARNING: This function will kill the program if any exception append when saving file</b><br>
	 */
	public static void save() {
		try { /* Paths */
			PROPERTIES_PATHS.setProperty("path.dowload.directory", String.valueOf(PATH_DOWNLOAD_DIRECTORY));
			PROPERTIES_PATHS.setProperty("path.ffmpeg.executable", String.valueOf(PATH_FFMPEG_EXECUTABLE));
			
			PROPERTIES_PATHS.store(new FileOutputStream(CONFIG_PATHS), null);
		} catch (Exception exception) {
			handleConfigException(exception, "paths");
		}
		
		try { /* Worker */
			PROPERTIES_WORKER.setProperty("worker.count.max", String.valueOf(WORKER_COUNT_MAX));
			PROPERTIES_WORKER.setProperty("worker.batch.max_retry_count", String.valueOf(WORKER_BATCH_MAX_RETRY_COUNT));
			
			PROPERTIES_WORKER.store(new FileOutputStream(CONFIG_WORKER), null);
		} catch (Exception exception) {
			handleConfigException(exception, "worker");
		}
	}
	
	/**
	 * Handle the exception that has been throw when a config save or load is called.<br>
	 * This function will log the exception in the console, will show a {@link JOptionPane} and exit the program.
	 * 
	 * @param exception
	 *            Throw exception
	 * @param config
	 *            Target config name
	 */
	private static void handleConfigException(Exception exception, String config) {
		Logger.exception(exception, "Failed to save/load \"%s\" config.", config);
		JOptionPane.showMessageDialog(new JFrame(), i18n.string("ui.dialog.error.config-failed", exception.getLocalizedMessage()), i18n.string("ui.dialog.error.title"), JOptionPane.ERROR_MESSAGE);
		System.exit(-1);
	}
	
}