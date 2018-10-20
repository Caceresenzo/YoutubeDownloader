package caceresenzo.apps.youtube.downloader.thread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread capable of reading outputs of {@link Process}
 * 
 * @author Enzo CACERES
 */
public class StreamReaderThread extends Thread {
	
	/* Static */
	protected static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
	
	/* Variables */
	private final int incrementedId;
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
		this.incrementedId = ATOMIC_INTEGER.incrementAndGet();
		this.inputStream = inputStream;
		this.listener = listener;
		
		if (inputStream == null) {
			throw new IllegalArgumentException("The stream can't be null.");
		}
	}
	
	@Override
	public void run() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			String line = null;
			
			while ((line = bufferedReader.readLine()) != null) {
				listener.onLineRead(this, line);
			}
		} catch (IOException exception) {
			listener.onException(exception);
		}
	}
	
	/**
	 * @return The unique id incremented everytime an instance of a {@link StreamReaderThread} is created
	 */
	public int getIncrementedId() {
		return incrementedId;
	}
	
	/**
	 * @return Attached {@link InputStream}
	 */
	public InputStream getInputStream() {
		return inputStream;
	}
	
	/**
	 * @return Attached {@link StreamListener}
	 */
	public StreamListener getListener() {
		return listener;
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
		 * @param streamReader
		 *            Parent {@link StreamReaderThread}
		 * @param line
		 *            The new line
		 */
		public void onLineRead(StreamReaderThread streamReader, String line);
		
		/**
		 * Called when the {@link StreamReaderThread} has encounter an exception while reading lines
		 * 
		 * @param exception
		 *            Throws exceptions
		 */
		public void onException(Exception exception);
		
	}
	
	/* To String */
	@Override
	public String toString() {
		return "StreamReaderThread[incrementedId=" + incrementedId + ", inputStream=" + inputStream + ", listener=" + listener + "]";
	}
	
}