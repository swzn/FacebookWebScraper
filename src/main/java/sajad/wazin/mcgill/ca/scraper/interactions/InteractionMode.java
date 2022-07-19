package sajad.wazin.mcgill.ca.scraper.interactions;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public interface InteractionMode {
    /*
    * This has not been implemented. It is used for suggestions scraping.
    * Interaction modes should have a "interact" method that will interact with a page suggestion and make a decision
    * on what to do on the page. Currently "follow-only" is the default interaction mode.
    * */
    void interact();
}
