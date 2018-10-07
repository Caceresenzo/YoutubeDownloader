package caceresenzo.apps.youtube.downloader.config;

import caceresenzo.libs.internationalization.HardInternationalization;
import caceresenzo.libs.internationalization.i18n;

/**
 * Language handler class
 * 
 * @author Enzo CACERES
 */
public class Language {
	
	/* Constants */
	public static final String LANGUAGE_FRENCH = "Français";
	
	/* Variables */
	private static Language LANGUAGE;
	private HardInternationalization selected = null;
	
	/* Constructor */
	private Language() {
		selected = new French();
	}
	
	/* Initializer */
	public void initialize() {
		i18n.setSelectedLanguage(LANGUAGE_FRENCH);
	}
	
	/**
	 * French {@link HardInternationalization}
	 * 
	 * @author Enzo CACERES
	 */
	private class French extends HardInternationalization {
		
		public French() {
			super();
			register(LANGUAGE_FRENCH);
		}
		
		@Override
		public void set() {
			o("ui.frame.title", "Youtube Downloader");
			
			o("ui.button.start", "Démarrer");
			o("ui.button.download.selected", "Télécharger");
			o("ui.button.download.all", "Tout télécharger");
			o("ui.button.download.downloading", "En cours...");
			o("ui.button.download.waiting", "En attente...");
			o("ui.button.download.already", "Déja télécharger");
			o("ui.button.output.directory", "Dossier...");
			
			o("worker.imagedownloader.eta.downloading", "Téléchargement...");
			o("worker.imagedownloader.eta.error", "Erreur");
			
			o("worker.eta.waiting", "En attente");
			o("worker.extractor.eta.working", "Travail en cours...");
			o("worker.extractor.eta.working.count", "Travail en cours... (%s vidéo%s sur %s)");
			o("worker.extractor.eta.downloader.started", "Téléchargement de %s vidéo%s...");
			o("worker.extractor.eta.downloader.started.retry", "Téléchargement de %s vidéo%s... (tentative n°%s)");
			o("worker.extractor.eta.downloader.retry-called", "Nouvelle tentative pour les vidéos qui ont échoué... (%s)");
			o("worker.extractor.eta.error.common", "Erreur: %s");
			o("worker.extractor.eta.error.bad-url", "Erreur: L'url n'est pas bonne ou ne continent pas de lien vers une playlist");
			o("worker.extractor.eta.error.retry-limit-reached", "Erreur: Le nombre de tentative à été dépassé.");
			
			o("worker.downloader.eta.extraction", "Extraction...");
			o("worker.downloader.eta.download", "Téléchargement...");
			o("worker.downloader.eta.conversion", "Conversion vers mp3...");
			o("worker.downloader.eta.saving", "Enregistrement...");
			o("worker.downloader.eta.error", "Erreur: %s");
		}
		
	}
	
	/**
	 * @return Selected {@link HardInternationalization} instance
	 */
	public HardInternationalization getSelected() {
		return selected;
	}
	
	/**
	 * @return Language singleton
	 */
	public static Language getLanguage() {
		if (LANGUAGE == null) {
			LANGUAGE = new Language();
		}
		
		return LANGUAGE;
	}
	
	/**
	 * @return Actually selected {@link HardInternationalization}
	 */
	public static HardInternationalization getActual() {
		return getLanguage().getSelected();
	}
	
}