package sajad.wazin.mcgill.ca.scraper;

import sajad.wazin.mcgill.ca.FacebookWebScraper;
import sajad.wazin.mcgill.ca.chrome.BrowserController;
import sajad.wazin.mcgill.ca.facebook.SuggestionNode;
import sajad.wazin.mcgill.ca.persistence.Encoder;
import sajad.wazin.mcgill.ca.persistence.PersistenceService;
import sajad.wazin.mcgill.ca.scraper.search.BFSSearcher;
import sajad.wazin.mcgill.ca.scraper.search.DFSSearcher;
import sajad.wazin.mcgill.ca.scraper.search.Searcher;
import sajad.wazin.mcgill.ca.scraper.search.SearcherEnum;
import sajad.wazin.mcgill.ca.scraper.settings.ScraperSettings;
import sajad.wazin.mcgill.ca.scraper.settings.SuggestionsScraperSettings;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static sajad.wazin.mcgill.ca.FacebookWebScraper.*;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class SuggestionsScraper implements Scraper {

    private SuggestionsScraperSettings settings;
    public HashMap<String, SuggestionNode> suggestionsMap;
    private int maxGrowth;
    private int maxDepth;

    public SuggestionsScraper(ScraperSettings scraperSettings) {
        if(!(scraperSettings instanceof SuggestionsScraperSettings)) {
            throw new IllegalArgumentException("Scraper settings are invalid!");
        }
        this.settings = (SuggestionsScraperSettings) scraperSettings;

        maxGrowth = settings.getMaxGrowth();
        maxDepth = settings.getMaxDepth();
        suggestionsMap = new HashMap<>();
        validateInput();
    }

    private void validateInput(){

    }

    @Override
    public void runScraper() {
        List<String> links = PersistenceService.readLines(settings.getInput());

        BrowserController controller = new BrowserController(settings.isHeadless());
        Searcher currentSearcher;
        if(settings.getSearcher() == SearcherEnum.BFS) currentSearcher = new BFSSearcher(maxDepth, maxGrowth, controller, suggestionsMap);
        else if(settings.getSearcher() == SearcherEnum.DFS) currentSearcher = new DFSSearcher(maxDepth, maxGrowth, controller, suggestionsMap);
        else {
            LOGGER.log("FATAL ERROR: SearcherEnum cannot be parsed.");
            return;
        }


        controller.initialize();
        controller.sleep(10000);

        List<SuggestionNode> roots = new ArrayList<>();
        for(String link : links) {
            SuggestionNode root = new SuggestionNode(link, 0);
            suggestionsMap.put(link, root);
            roots.add(root);

            currentSearcher.add(link);
            try {
                currentSearcher.search();
            } catch (Exception e) {
                LOGGER.log("Error found");
                LOGGER.log(e.getMessage());
            }
        }

        controller.getDriver().quit();

        RESOURCES.deleteTemp();
        PERSISTENCE_SERVICE.saveJSONFile(Encoder.encodeRoots(roots), settings.getOutput());
        PERSISTENCE_SERVICE.saveListFile(new ArrayList<>(suggestionsMap.keySet()), settings.getOutput());
    }
}
