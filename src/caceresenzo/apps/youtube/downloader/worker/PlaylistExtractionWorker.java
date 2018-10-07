package caceresenzo.apps.youtube.downloader.worker;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import caceresenzo.libs.thread.implementations.WorkerThread;
import caceresenzo.libs.youtube.api.implementations.YoutubePlaylistApi;
import caceresenzo.libs.youtube.playlist.YoutubePlaylist;
import caceresenzo.libs.youtube.playlist.YoutubePlaylistItem;

/**
 * Worker to extract data and {@link YoutubePlaylistItem} from a Youtube's playlist
 * 
 * @author Enzo CACERES
 */
public class PlaylistExtractionWorker extends WorkerThread {
	
	/* Variables */
	private String playlistId;
	private WorkerCallback callback;
	
	/* Constructor */
	private PlaylistExtractionWorker(String playlistId) {
		this.playlistId = playlistId;
	}
	
	/**
	 * @param workerCallback
	 *            Attach this callback to this worker
	 * @return Itself
	 */
	public PlaylistExtractionWorker callback(WorkerCallback workerCallback) {
		this.callback = workerCallback;
		
		return this;
	}
	
	@Override
	protected void execute() {
		List<YoutubePlaylistItem> items = new ArrayList<>();
		
		if (callback != null) {
			callback.onStarted();
		}
		
		YoutubePlaylistApi playlistApi = new YoutubePlaylistApi(playlistId);
		while (true) {
			try {
				YoutubePlaylist youtubePlaylist = playlistApi.execute();
				
				List<YoutubePlaylistItem> pageItems = youtubePlaylist.getItems();
				
				if (pageItems != null) {
					items.addAll(pageItems);
					
					if (callback != null) {
						callback.onPageFetched(youtubePlaylist);
					}
				}
				
				if (youtubePlaylist.hasMoreItemOnNextPage(items.size())) {
					playlistApi = new YoutubePlaylistApi(playlistId, youtubePlaylist.getNextPageToken());
				} else {
					break;
				}
			} catch (Exception exception) {
				if (callback != null) {
					callback.onException(exception, true);
				}
				break;
			}
		}
		
		if (callback != null) {
			callback.onFinished(items);
		}
	}
	
	/**
	 * Get a {@link PlaylistExtractionWorker} instance from a Youtube url
	 * 
	 * @param url
	 *            Targetted url to be extracted
	 * @return A new {@link PlaylistExtractionWorker} instance
	 * @throws MalformedURLException
	 *             If the <code>list=playlistId</code> hasn't been found
	 */
	public static PlaylistExtractionWorker fromUrl(String url) throws MalformedURLException {
		Matcher matcher = YoutubePlaylist.PLAYLIST_ID_MATCHER.matcher(url);
		
		if (matcher.find()) {
			return new PlaylistExtractionWorker(matcher.group(1));
		}
		
		throw new MalformedURLException();
	}
	
	/**
	 * Worker callback used to tell progress about the extraction
	 * 
	 * @author Enzo CACERES
	 */
	public interface WorkerCallback {
		
		/**
		 * Called when the extractor has started
		 */
		public void onStarted();
		
		/**
		 * Called when a page has been fetched
		 * 
		 * @param youtubePlaylist
		 *            {@link List} of {@link YoutubePlaylist} found on the page
		 */
		public void onPageFetched(YoutubePlaylist youtubePlaylist);
		
		/**
		 * Called if an exception has been throw when processing data
		 * 
		 * @param exception
		 *            The exception in question
		 * @param critical
		 *            If the extraction can continue or not
		 */
		public void onException(Exception exception, boolean critical);
		
		/**
		 * Called when the {@link PlaylistExtractionWorker} has totally finish
		 * 
		 * @param allItems
		 *            All found item on all pages
		 */
		public void onFinished(List<YoutubePlaylistItem> allItems);
		
	}
	
}