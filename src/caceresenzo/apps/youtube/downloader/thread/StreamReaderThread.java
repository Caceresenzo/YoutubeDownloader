package caceresenzo.apps.youtube.downloader.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Thread capable of reading outputs of {@link Process}
 * 
 * @author Enzo CACERES
 */
public class StreamReaderThread extends Thread {
	
	/* Variables */
	private InputStream inputStream;
	private StreamListener listener;
	
	/**
	 * Constructor<br>
	 * Read {@link Process#getOutputStream()} or {@link Process#getErrorStream()} and call the listener if a new line has been read
	 * 
	 * @param inputStream
	 *            {@link Process} output stream
	 * @param listener
	 *            Listener for callback
	 */
	public StreamReaderThread(InputStream inputStream, StreamListener listener) {
		this.inputStream = inputStream;
		this.listener = listener;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			
			while ((line = bufferedReader.readLine()) != null) {
				listener.onLineRead(line);
			}
		} catch (IOException exception) {
			listener.onException(exception);
		}
	}
	
	/**
	 * Simple Listener that need to be used with the {@link StreamReaderThread}
	 * 
	 * @author Enzo CACERES
	 */
	public interface StreamListener {
		
		/**
		 * Called when the program has print a new line
		 * 
		 * @param line
		 *            The new line
		 */
		public void onLineRead(String line);
		
		/**
		 * Called when the {@link StreamReaderThread} has encounter an exception while reading lines
		 * 
		 * @param exception
		 *            Throws exceptions
		 */
		public void onException(Exception exception);
		
	}
	
}