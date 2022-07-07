package sajad.wazin.mcgill.ca.scraper;

import sajad.wazin.mcgill.ca.chrome.BrowserController;
import sajad.wazin.mcgill.ca.persistence.PersistenceService;
import sajad.wazin.mcgill.ca.scraper.search.BFSSearcher;
import sajad.wazin.mcgill.ca.scraper.search.DFSSearcher;
import sajad.wazin.mcgill.ca.scraper.search.Searcher;
import sajad.wazin.mcgill.ca.scraper.search.SearcherEnum;
import sajad.wazin.mcgill.ca.scraper.settings.ScraperSettings;
import sajad.wazin.mcgill.ca.scraper.settings.SuggestionsScraperSettings;

import java.util.List;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class SuggestionsScraper implements Scraper{

    private SuggestionsScraperSettings settings;
    private int maxGrowth;
    private int maxDepth;

    public SuggestionsScraper(ScraperSettings scraperSettings) {
        if(!(scraperSettings instanceof SuggestionsScraperSettings)) {
            throw new IllegalArgumentException("Scraper settings are invalid!");
        }
        this.settings = (SuggestionsScraperSettings) scraperSettings;

        maxGrowth = settings.getMaxGrowth();
        maxDepth = settings.getMaxDepth();
        validateInput();
    }

    private void validateInput(){

    }

    @Override
    public void runScraper() {
        List<String> links = PersistenceService.readLines(settings.getInput());

        BrowserController controller = new BrowserController();
        Searcher currentSearcher;
        if(settings.getSearcher() == SearcherEnum.BFS) currentSearcher = new BFSSearcher(maxDepth, maxGrowth, controller);
        else if(settings.getSearcher() == SearcherEnum.DFS) currentSearcher = new DFSSearcher(maxDepth, maxGrowth, controller);
        else return;


        controller.initialize();
        controller.wait(5);

        long start = System.currentTimeMillis();
        int postsScraped = 0;

        for(String link : links) {
            currentSearcher.add(link);
            currentSearcher.search(0);
        }
    }
}
