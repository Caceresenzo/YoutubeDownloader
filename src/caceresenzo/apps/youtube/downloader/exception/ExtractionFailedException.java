package caceresenzo.apps.youtube.downloader.exception;

import caceresenzo.libs.youtube.extractor.YouTubeExtractor;
import caceresenzo.libs.youtube.video.VideoMeta;
import caceresenzo.libs.youtube.video.YoutubeVideo;

/**
 * Simple exception if the {@link YouTubeExtractor} failed to extract anything
 * 
 * @author Enzo CACERES
 */
public class ExtractionFailedException extends IllegalStateException {
	
	/**
	 * @param sourceVideoMeta
	 *            Failed {@link YoutubeVideo}'s {@link VideoMeta}
	 */
	public ExtractionFailedException(VideoMeta sourceVideoMeta) {
		super("Failed to extract data from video: \"" + sourceVideoMeta.getTitle() + "\"");
	}
	
}