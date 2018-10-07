package caceresenzo.apps.youtube.downloader.worker;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import caceresenzo.apps.youtube.downloader.config.Config;
import caceresenzo.apps.youtube.downloader.exception.ExtractionFailedException;
import caceresenzo.apps.youtube.downloader.thread.StreamReaderThread;
import caceresenzo.libs.array.SparseArray;
import caceresenzo.libs.cryptography.MD5;
import caceresenzo.libs.databridge.ObjectWrapper;
import caceresenzo.libs.filesystem.FileUtils;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.math.MathUtils;
import caceresenzo.libs.network.Downloader;
import caceresenzo.libs.thread.implementations.WorkerThread;
import caceresenzo.libs.youtube.extractor.YouTubeExtractor;
import caceresenzo.libs.youtube.format.VideoCodec;
import caceresenzo.libs.youtube.format.YoutubeFormat;
import caceresenzo.libs.youtube.playlist.YoutubePlaylistItem;
import caceresenzo.libs.youtube.video.VideoMeta;
import caceresenzo.libs.youtube.video.YoutubeVideo;

/**
 * Extractor/Downloader/Converter work handler
 * 
 * @author Enzo CACERES
 */
public class VideoDownloadWorker extends WorkerThread {
	
	/* Variables */
	private List<YoutubePlaylistItem> videos;
	private WorkerCallback callback;
	
	/* Constructor */
	private VideoDownloadWorker(List<YoutubePlaylistItem> videos) {
		this.videos = videos;
	}
	
	/**
	 * @param workerCallback
	 *            Attach this callback to this worker
	 * @return Itself
	 */
	public VideoDownloadWorker callback(WorkerCallback callback) {
		this.callback = callback;
		
		return this;
	}
	
	@Override
	protected void execute() {
		final File downloadDirectory = new File(Config.PATH_DOWNLOAD_DIRECTORY);
		final File processingDirectory = new File(downloadDirectory, ".downloader");
		
		try {
			FileUtils.forceFolderCreation(downloadDirectory);
			FileUtils.forceFolderCreation(processingDirectory);
		} catch (Exception exception) {
			if (callback != null) {
				callback.onException(exception, true);
			}
			
			cancel();
			return;
		}
		
		if (callback != null) {
			callback.onStarted();
		}
		
		for (final YoutubePlaylistItem item : videos) {
			try {
				final File videoFile = new File(downloadDirectory, FileUtils.replaceIllegalChar(item.getVideoMeta().getTitle()) + ".mp3");
				final File md5VideoFile = new File(processingDirectory, MD5.silentMd5(videoFile.getName()) + "." + YoutubeFormat.WEBM);
				final ObjectWrapper<YoutubeVideo> youtubeVideoWrapper = new ObjectWrapper<>(null);
				
				if (videoFile.exists()) {
					continue;
				}
				
				/* Extraction */
				if (callback != null) {
					callback.onVideoExtraction(item);
				}
				
				new YouTubeExtractor() {
					@Override
					protected void onExtractionComplete(SparseArray<YoutubeVideo> youtubeFiles, VideoMeta videoMeta) {
						if (youtubeFiles == null) {
							throw new ExtractionFailedException(videoMeta);
						}
						
						YoutubeVideo highestAudioVideo = null;
						for (YoutubeVideo video : youtubeFiles.values()) {
							YoutubeFormat format = video.getFormat();
							
							if (format.getExtension().equals(YoutubeFormat.WEBM) && format.getVideoCodec().equals(VideoCodec.NONE)) {
								if (highestAudioVideo != null) {
									if (highestAudioVideo.getFormat().getAudioBitrate() < format.getAudioBitrate()) {
										highestAudioVideo = video;
									}
								} else {
									highestAudioVideo = video;
								}
							}
						}
						
						if (highestAudioVideo == null) {
							throw new ExtractionFailedException(videoMeta);
						}
						
						youtubeVideoWrapper.setValue(highestAudioVideo);
					}
				}.extract(item.getVideoUrl(), true, true);
				
				/* Download */
				if (callback != null) {
					callback.onVideoDownload(item);
				}
				
				String directVideoUrl = youtubeVideoWrapper.getValue().getUrl();
				
				final long fileLength = Downloader.getFileSize(directVideoUrl);
				Downloader.downloadFile(md5VideoFile, directVideoUrl, new Downloader.OnDownloadProgress() {
					@Override
					public void onProgress(int length) {
						if (callback != null) {
							callback.onVideoDownloadProgressUpdate(item, MathUtils.pourcent(length, fileLength));
						}
					}
				});
				
				/* Conversion */
				if (callback != null) {
					callback.onVideoConversion(item);
				}
				
				StringBuilder builder = new StringBuilder(Config.PATH_FFMPEG_EXECUTABLE);
				builder.append(" -i ");
				builder.append("\"").append(md5VideoFile.getAbsolutePath()).append("\"");
				builder.append(" ");
				builder.append("\"").append(videoFile.getAbsolutePath()).append("\"");
				Logger.debug(builder);
				
				Process ffmpegProcess = Runtime.getRuntime().exec(builder.toString());
				
				if (callback != null) {
					callback.onVideoConversionProgressUpdate(item, 0);
				}
				
				final long fileSize = md5VideoFile.length();
				final Pattern processedSizePattern = Pattern.compile("^size\\=[\\s]*(\\d*).*?$");
				
				StreamReaderThread streamReader = new StreamReaderThread(ffmpegProcess.getErrorStream(), new StreamReaderThread.StreamListener() {
					@Override
					public void onLineRead(String line) {
						if (callback != null) {
							long processed = 0;
							
							Matcher matcher = processedSizePattern.matcher(line);
							if (matcher.find()) { /* Exemple: size= 768kB time=00:00:57.62 bitrate= 109.2kbits/s speed=8.39x */
								processed = Long.parseLong(matcher.group(1));
								
								callback.onVideoConversionProgressUpdate(item, MathUtils.pourcent(processed * 1000, fileSize));
							}
						}
					}
					
					@Override
					public void onException(Exception exception) {
						exception.printStackTrace();
					}
				});
				
				streamReader.start();
				streamReader.join();
				
				if (callback != null) {
					callback.onVideoConversionProgressUpdate(item, 100);
				}
				
				/* Saving */
				if (callback != null) {
					callback.onVideoSaving(item, videoFile);
				}
				
				ffmpegProcess.destroy();
				
				if (!md5VideoFile.delete()) {
					throw new IOException("Failed to delete processing file.");
				}
			} catch (Exception exception) {
				if (callback != null) {
					callback.onVideoException(item, exception, true);
				}
				
				continue;
			}
		}
		
		if (callback != null) {
			callback.onFinished();
		}
	}
	
	/**
	 * Create a {@link VideoDownloadWorker} from a {@link List} of {@link YoutubePlaylistItem}
	 * 
	 * @param videos
	 *            Targetted {@link List}
	 * @return A new {@link VideoDownloadWorker} instance
	 */
	public static VideoDownloadWorker fromVideoList(List<YoutubePlaylistItem> videos) {
		return new VideoDownloadWorker(videos);
	}
	
	/**
	 * Create a {@link VideoDownloadWorker} from a single {@link YoutubePlaylistItem}
	 * 
	 * @param item
	 *            Targetted {@link YoutubePlaylistItem}
	 * @return A new {@link VideoDownloadWorker} instance
	 */
	public static VideoDownloadWorker fromVideoItem(YoutubePlaylistItem item) {
		return fromVideoList(Arrays.asList(item));
	}
	
	/**
	 * Worker callback used to tell progress about the whole process
	 * 
	 * @author Enzo CACERES
	 */
	public interface WorkerCallback {
		
		/**
		 * Called when the {@link VideoDownloadWorker} is ready to start
		 */
		public void onStarted();
		
		/**
		 * Called when the {@link YouTubeExtractor} is ready to be called
		 * 
		 * @param item
		 *            Target item to extract url
		 */
		public void onVideoExtraction(YoutubePlaylistItem item);
		
		/**
		 * Called when an {@link YoutubePlaylistItem} is starting downloading
		 * 
		 * @param item
		 *            Downloading item
		 */
		public void onVideoDownload(YoutubePlaylistItem item);
		
		/**
		 * Called when the downloader update his progress
		 * 
		 * @param item
		 *            Downloading item
		 * @param progress
		 *            New progress (out of 100%)
		 */
		public void onVideoDownloadProgressUpdate(YoutubePlaylistItem item, float progress);
		
		/**
		 * Called when an {@link YoutubePlaylistItem} is starting converting
		 * 
		 * @param item
		 *            Converting item
		 */
		public void onVideoConversion(YoutubePlaylistItem item);
		
		/**
		 * Called when the converter update his progress
		 * 
		 * @param item
		 *            Converting item
		 * @param progress
		 *            New progress (out of 100%)
		 */
		public void onVideoConversionProgressUpdate(YoutubePlaylistItem item, float progress);
		
		/**
		 * Called when the {@link VideoDownloadWorker} need to kill FFMPEG instance and delete old processing file
		 * 
		 * @param item
		 *            Actual item
		 * @param targetFile
		 *            Target file where file has been downloaded
		 */
		public void onVideoSaving(YoutubePlaylistItem item, File targetFile);
		
		/**
		 * Called when an exception has been throw when processing a {@link YoutubePlaylistItem}
		 * 
		 * @param item
		 *            Actual item
		 * @param exception
		 *            Throw {@link Exception}
		 * @param critical
		 *            If the {@link Exception} is critical and the process can't continue
		 */
		public void onVideoException(YoutubePlaylistItem item, Exception exception, boolean critical);
		
		/**
		 * Called when an exception has been throw without processing a {@link YoutubePlaylistItem}
		 * 
		 * @param exception
		 *            Throw {@link Exception}
		 * @param critical
		 *            If the {@link Exception} is critical and the process can't continue
		 */
		public void onException(Exception exception, boolean critical);
		
		/**
		 * Called when the {@link VideoDownloadWorker} has totally finished
		 */
		public void onFinished();
		
	}
	
}