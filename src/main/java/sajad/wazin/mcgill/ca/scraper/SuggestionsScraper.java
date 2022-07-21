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
    public int unknownErrorsInARow = 0;

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
        DUMP_MANAGER.addRaw(suggestionsMap);
    }


    @Override
    public void runScraper() {
        // Read the input file
        List<String> links = PersistenceService.readLines(settings.getInput());

        // Start chrome
        BrowserController controller = new BrowserController(settings.isHeadless());

        // Initialize the searcher
        Searcher currentSearcher;
        if(settings.getSearcher() == SearcherEnum.BFS) currentSearcher = new BFSSearcher(maxDepth, maxGrowth, controller, suggestionsMap);
        else if(settings.getSearcher() == SearcherEnum.DFS) currentSearcher = new DFSSearcher(maxDepth, maxGrowth, controller, suggestionsMap);
        else {
            LOGGER.log("FATAL ERROR: SearcherEnum cannot be parsed.");
            return;
        }


        if(!controller.initialize()) return;

        // Start scraping from each root
        List<SuggestionNode> roots = new ArrayList<>();

        // add root to the dump manager
        DUMP_MANAGER.addRoots(roots);

        for(String link : links) {
            SuggestionNode root = new SuggestionNode(link, 0);
            suggestionsMap.put(link, root);
            roots.add(root);

            currentSearcher.add(link);
            try {
                currentSearcher.search();
            } catch (Exception e) {
                LOGGER.log("Unknown Error found");
                unknownErrorsInARow++;
                if(unknownErrorsInARow >= MAX_ERRORS_BEFORE_STOP) CANCELLED_TASK = true;
            }
        }

        CONTROLLER_POOL.kill(controller);
        RESOURCES.deleteTemp();

        if(CANCELLED_TASK) LOGGER.log("Task has been cancelled");
        LOGGER.log("Task has completed with " + suggestionsMap.size() + " unique suggestions");

        // Encode the output in JSON and in a txt file
        PERSISTENCE_SERVICE.saveJSONFile(Encoder.encodeRoots(roots), settings.getOutput(), "suggestions");
        PERSISTENCE_SERVICE.saveListFile(new ArrayList<>(suggestionsMap.keySet()), settings.getOutput(), "raw");
        LOGGER.outputLog();
    }
}
