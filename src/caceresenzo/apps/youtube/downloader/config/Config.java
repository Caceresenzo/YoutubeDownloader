package caceresenzo.apps.youtube.downloader.config;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import caceresenzo.libs.config.Configuration;
import caceresenzo.libs.config.annotations.ConfigFile;
import caceresenzo.libs.config.annotations.ConfigProperty;
import caceresenzo.libs.config.annotations.ConfigProperty.PropertyType;
import caceresenzo.libs.config.processor.implementations.PropertiesConfigProcessor;
import caceresenzo.libs.internationalization.i18n;
import caceresenzo.libs.logger.Logger;

/**
 * External .properties config handler
 * 
 * @author Enzo CACERES
 */
public class Config extends Configuration {
	
	/* Instance */
	private static Configuration CONFIGURATION;
	
	/* Base config directory */
	public static String CONFIG_DIRECTORY = "./config/";
	
	/* Configs directories */
	@ConfigFile(name = "paths", processor = PropertiesConfigProcessor.class)
	public static String CONFIG_PATHS = CONFIG_DIRECTORY + "paths.properties";
	
	@ConfigFile(name = "worker", processor = PropertiesConfigProcessor.class)
	public static String CONFIG_WORKER = CONFIG_DIRECTORY + "worker.properties";
	
	/* Config -> Paths */
	@ConfigProperty(file = "paths", key = "path.dowload.directory", defaultValue = ".\\music\\")
	public static String PATH_DOWNLOAD_DIRECTORY;
	
	@ConfigProperty(file = "paths", key = "path.ffmpeg.executable", defaultValue = ".\\bin\\ffmpeg\\ffmpeg.exe")
	public static String PATH_FFMPEG_EXECUTABLE;
	
	/* Config -> Worker */
	@ConfigProperty(file = "worker", key = "worker.count.max", defaultValue = "5", type = PropertyType.INTEGER)
	public static int WORKER_COUNT_MAX;
	
	@ConfigProperty(file = "worker", key = "worker.batch.max_retry_count", defaultValue = "4", type = PropertyType.INTEGER)
	public static int WORKER_BATCH_MAX_RETRY_COUNT;
	
	/* Initializer */
	public static void initialize() {
		try {
			CONFIGURATION = initialize(Config.class);
			
			if (!(new File(PATH_FFMPEG_EXECUTABLE).exists())) {
				JOptionPane.showMessageDialog(new JFrame(), i18n.string("ui.dialog.error.ffmpeg-not-found", PATH_FFMPEG_EXECUTABLE), i18n.string("ui.dialog.error.title"), JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
				throw new Exception("FFMPEG target don't exist.");
			}
		} catch (Exception exception) {
			handleConfigException(exception);
		}
	}
	
	/**
	 * Update and save {@link #CONFIG_PATHS} and {@link #CONFIG_WORKER} properties<br>
	 * <b>WARNING: This function will kill the program if any exception append when saving file</b><br>
	 */
	public static void save() {
		try { /* Paths */
			CONFIGURATION.saveAll();
		} catch (Exception exception) {
			handleConfigException(exception);
		}
	}
	
	/**
	 * Handle the exception that has been throw when a config save or load is called.<br>
	 * This function will log the exception in the console, will show a {@link JOptionPane} and exit the program.
	 * 
	 * @param exception
	 *            Throw exception
	 */
	private static void handleConfigException(Exception exception) {
		Logger.exception(exception, "Failed to save/load config.");
		JOptionPane.showMessageDialog(new JFrame(), i18n.string("ui.dialog.error.config-failed", exception.getLocalizedMessage()), i18n.string("ui.dialog.error.title"), JOptionPane.ERROR_MESSAGE);
		System.exit(-1);
	}
	
}