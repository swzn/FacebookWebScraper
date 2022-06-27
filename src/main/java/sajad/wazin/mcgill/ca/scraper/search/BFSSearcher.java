package sajad.wazin.mcgill.ca.scraper.search;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class BFSSearcher implements Searcher{

    private int maxDepth;

    public BFSSearcher(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    @Override
    public void next(int currentDepth) {

    }
}
