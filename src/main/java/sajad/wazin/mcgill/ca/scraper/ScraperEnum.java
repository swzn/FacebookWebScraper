package sajad.wazin.mcgill.ca.scraper;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public enum ScraperEnum {
    POST_CONTENT, POST_SUGGESTIONS, GROUPS_SUGGESTIONS, GROUPS_CONTENT, NULL_SCRAPER;

    public static ScraperEnum getScraper(String s) {
        return switch (s) {
            case "Posts Content Scraper" -> POST_CONTENT;
            case "Groups Content Scraper" -> GROUPS_CONTENT;
            case "Posts Suggestions Scraper" -> POST_SUGGESTIONS;
            case "Groups Suggestions Scraper" -> GROUPS_SUGGESTIONS;
            default -> NULL_SCRAPER;
        };
    }
}
