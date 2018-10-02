package caceresenzo.apps.youtube.downloader.units;

import caceresenzo.libs.array.SparseArray;
import caceresenzo.libs.logger.Logger;
import caceresenzo.libs.test.SimpleTestUnits;
import caceresenzo.libs.youtube.extractor.YouTubeExtractor;
import caceresenzo.libs.youtube.video.VideoMeta;
import caceresenzo.libs.youtube.video.YoutubeVideo;

public class ExtractorTestUnits extends SimpleTestUnits {
	
	public static class SimpleExtractorTest {
		
		public static void main(String[] args) {
			initializeUnit(false);
			
			new YouTubeExtractor("./cache/") {
				@Override
				protected void onExtractionComplete(SparseArray<YoutubeVideo> ytFiles, VideoMeta videoMeta) {
					Logger.info("Meta: " + videoMeta.getTitle());
					
					for (int i = 0; i < ytFiles.size(); i++) {
						int key = ytFiles.keyAt(i);
						YoutubeVideo file = ytFiles.get(key);
						Logger.info("VIDEO:: %s \n%s\n", file.getFormat().getExtension(), file.getUrl());
						
					}
					
					// for (YoutubeFile youtubeFile : ytFiles) {
					// Logger.info(youtubeFile.getUrl());
					// }
				}
			}.extract("https://www.youtube.com/watch?v=QTSEHkgxs6E", true, true);
		}
		
	}
	
}
