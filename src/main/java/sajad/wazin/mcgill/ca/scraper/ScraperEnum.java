package sajad.wazin.mcgill.ca.scraper;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public enum ScraperEnum {
    CONTENT_SCRAPER, SUGGESTIONS_SCRAPER, NULL_SCRAPER;

    public static ScraperEnum getScraper(String s) {
        return switch (s) {
            case "Content Scraper" -> CONTENT_SCRAPER;
            case "Suggestions Scraper" -> SUGGESTIONS_SCRAPER;
            default -> NULL_SCRAPER;
        };
    }
}
