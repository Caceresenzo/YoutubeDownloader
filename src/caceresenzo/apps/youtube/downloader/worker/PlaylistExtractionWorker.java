package caceresenzo.apps.youtube.downloader.worker;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import caceresenzo.libs.thread.implementations.WorkerThread;
import caceresenzo.libs.youtube.api.implementations.YoutubePlaylistApi;
import caceresenzo.libs.youtube.playlist.YoutubePlaylist;
import caceresenzo.libs.youtube.playlist.YoutubePlaylistItem;

public class PlaylistExtractionWorker extends WorkerThread {
	
	private String playlistId;
	private WorkerCallback callback;
	
	private PlaylistExtractionWorker(String playlistId) {
		this.playlistId = playlistId;
	}
	
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
	
	public static PlaylistExtractionWorker fromUrl(String url) throws MalformedURLException {
		Matcher matcher = YoutubePlaylist.PLAYLIST_ID_MATCHER.matcher(url);
		
		if (matcher.find()) {
			return new PlaylistExtractionWorker(matcher.group(1));
		}
		
		throw new MalformedURLException();
	}
	
	public interface WorkerCallback {
		
		public void onStarted();
		
		public void onPageFetched(YoutubePlaylist youtubePlaylist);
		
		public void onException(Exception exception, boolean critical);
		
		public void onFinished(List<YoutubePlaylistItem> allItems);
		
	}
	
}