package sajad.wazin.mcgill.ca.facebook;

import sajad.wazin.mcgill.ca.scraper.SuggestionsScraper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class SuggestionNode {

    private final String link;
    private final int depth;

    private List<SuggestionNode> children;

    public SuggestionNode(String link, int depth) {
        this.link = link;
        this.depth = depth;

        children = new ArrayList<>();
    }

    public void add(SuggestionNode node) {
        children.add(node);
    }

    public String getLink(){
        return link;
    }

    public List<SuggestionNode> getChildren(){
        return children;
    }

    public int getDepth() {
        return depth;
    }
}
