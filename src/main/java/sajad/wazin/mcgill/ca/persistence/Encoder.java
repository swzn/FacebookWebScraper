package sajad.wazin.mcgill.ca.persistence;

import org.json.JSONObject;
import sajad.wazin.mcgill.ca.facebook.FacebookReactions;
import sajad.wazin.mcgill.ca.facebook.PostData;

import java.util.HashMap;
import java.util.List;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class Encoder {

    public static JSONObject encodePages(HashMap<String, List<PostData>> listOfPages){
        JSONObject pages = new JSONObject();
        for(String key : listOfPages.keySet()) {
           JSONObject pageData = encodePage(listOfPages.get(key));
           pages.put(key, pageData);
        }
        return pages;
    }

    public static JSONObject encodePage(List<PostData> statsList){
        JSONObject pageObject = new JSONObject();

        JSONObject posts = new JSONObject();
        for(int i = 0; i < statsList.size(); i++) {
            posts.put(String.valueOf(i), encodePost(statsList.get(i)));
        }
        pageObject.put("posts", posts);
        return pageObject;
    }

    public static JSONObject encodePost(PostData aPost){
        JSONObject postObject = new JSONObject();
        postObject.put("shares", aPost.getShares().toArray());
        postObject.put("comments", aPost.getComments().toArray());
        postObject.put("reactions", encodeReactions(aPost.getReactions()));
        postObject.put("content", aPost.getContent());
        postObject.put("url", aPost.getPostURL());
        return postObject;
    }

    private static JSONObject encodeReactions(FacebookReactions facebookReactions){
        JSONObject reactionsObject = new JSONObject();

        reactionsObject.put("likes", String.valueOf(facebookReactions.getLikes()));
        reactionsObject.put("mad", String.valueOf(facebookReactions.getMad()));
        reactionsObject.put("sad", String.valueOf(facebookReactions.getSad()));
        reactionsObject.put("holding_hearts", String.valueOf(facebookReactions.getHoldingHearts()));
        reactionsObject.put("hearts", String.valueOf(facebookReactions.getHearts()));
        reactionsObject.put("wow", String.valueOf(facebookReactions.getWow()));
        reactionsObject.put("laughs", String.valueOf(facebookReactions.getLaughing()));

        return reactionsObject;
    }
}
