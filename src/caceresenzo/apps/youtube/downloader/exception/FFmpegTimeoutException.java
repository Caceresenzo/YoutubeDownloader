package caceresenzo.apps.youtube.downloader.exception;

import caceresenzo.apps.youtube.downloader.worker.VideoDownloadWorker;

public class FFmpegTimeoutException extends IllegalStateException {
	
	public FFmpegTimeoutException() {
		super("Ffmpeg reached his timeout time, value: " + VideoDownloadWorker.FFMPEG_THREAD_TIMEOUT + " ms.");
	}
	
}