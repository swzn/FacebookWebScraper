package sajad.wazin.mcgill.ca.scraper.search;

import sajad.wazin.mcgill.ca.chrome.BrowserController;

import java.util.Stack;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class DFSSearcher {

    private int maxDepth;
    private Stack<String> links;
    private BrowserController browserController;

    public DFSSearcher(int maxDepth, BrowserController browserController) {
        this.maxDepth = maxDepth;
        this.links = new Stack<>();
        this.browserController = browserController;
    }

}
