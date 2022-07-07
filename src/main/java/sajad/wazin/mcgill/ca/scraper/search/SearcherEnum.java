package sajad.wazin.mcgill.ca.scraper.search;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public enum SearcherEnum {
    DFS, BFS, NULL_SEARCHER;

    public static SearcherEnum getSearcher(String s){
        if (s.contains("DFS")) return DFS;
        else if (s.contains("BFS")) return BFS;
        else return NULL_SEARCHER;
    }
}
