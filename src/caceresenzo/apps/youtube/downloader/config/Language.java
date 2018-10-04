package caceresenzo.apps.youtube.downloader.config;

import caceresenzo.libs.internationalization.HardInternationalization;
import caceresenzo.libs.internationalization.i18n;

public class Language {
	
	public static final String LANGUAGE_FRENCH = "Français";
	
	private static Language LANGUAGE;
	private HardInternationalization selected = null;
	
	private Language() {
		selected = new French();
	}
	
	public void initialize() {
		i18n.setSelectedLanguage(LANGUAGE_FRENCH);
	}
	
	private class French extends HardInternationalization {
		
		public French() {
			super();
			register(LANGUAGE_FRENCH);
		}
		
		@Override
		public void set() {
			o("error.title", "Erreur");

			o("ui.button.start", "Démarrer");
			o("ui.button.download.selected", "Télécharger");
			o("ui.button.download.all", "Tout télécharger");
			o("ui.button.output.directory", "Dossier...");
			
			o("worker.imagedownloader.eta.downloading", "Téléchargement...");
			o("worker.imagedownloader.eta.error", "Erreur");

			o("worker.extractor.eta.waiting", "En attente");
			o("worker.extractor.eta.error", "Erreur");
		}
		
	}
	
	public HardInternationalization getSelected() {
		return selected;
	}
	
	public static Language getLanguage() {
		if (LANGUAGE == null) {
			LANGUAGE = new Language();
		}
		
		return LANGUAGE;
	}
	
	public static HardInternationalization getActual() {
		return getLanguage().getSelected();
	}
	
}