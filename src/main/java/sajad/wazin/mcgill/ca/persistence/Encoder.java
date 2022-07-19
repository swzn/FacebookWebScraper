package sajad.wazin.mcgill.ca.persistence;

import org.json.JSONArray;
import org.json.JSONObject;
import sajad.wazin.mcgill.ca.facebook.FacebookReactions;
import sajad.wazin.mcgill.ca.facebook.PostData;
import sajad.wazin.mcgill.ca.facebook.SuggestionNode;

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

        JSONArray posts = new JSONArray();

        for (PostData postData : statsList) {
            posts.put(encodePost(postData));
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

    public static JSONObject encodeRoots(List<SuggestionNode> roots) {
        JSONArray rootsArray = new JSONArray();
        for(SuggestionNode root : roots) {
            rootsArray.put(encodeNode(root));
        }
        JSONObject rootsObject = new JSONObject();
        rootsObject.put("roots", rootsArray);

        return rootsObject;
    }

    private static JSONObject encodeNode(SuggestionNode node) {
        JSONObject nodeObject = new JSONObject();
        nodeObject.put("link", node.getLink());
        nodeObject.put("depth", node.getDepth());

        JSONArray childrenArray = new JSONArray();
        if(!node.getChildren().isEmpty()) {
            for(SuggestionNode child : node.getChildren()) {
                JSONObject childNode = encodeNode(child);
                childrenArray.put(childNode);
            }
        }
        nodeObject.put("children", childrenArray);
        return nodeObject;
    }
}
