package caceresenzo.apps.youtube.downloader.exception;

import caceresenzo.libs.youtube.video.VideoMeta;

public class ExtractionFailedException extends IllegalStateException {
	
	public ExtractionFailedException(VideoMeta sourceVideoMeta) {
		super("Failed to extract data from video: " + sourceVideoMeta.getTitle());
	}
	
}