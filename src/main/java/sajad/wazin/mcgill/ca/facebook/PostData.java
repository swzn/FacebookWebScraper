package sajad.wazin.mcgill.ca.facebook;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class PostData {

    private List<String> comments;
    private List<String> shares;
    private FacebookReactions reactions;

    private String content;

    private String postURL;

    public PostData(String postURL){
        this.postURL = postURL;

        comments = new ArrayList<>();
        shares = new ArrayList<>();
        reactions = new FacebookReactions();
    }

    public List<String> getComments() {
        return comments;
    }

    public List<String> getShares() {
        return shares;
    }

    public FacebookReactions getReactions() {
        return reactions;
    }

    public String getContent() {
        return content;
    }

    public String getPostURL() {
        return postURL;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public void setShares(List<String> shares) {
        this.shares = shares;
    }

    public void setReactions(FacebookReactions reactions) {
        this.reactions = reactions;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
