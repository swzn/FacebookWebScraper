package sajad.wazin.mcgill.ca.utils;

import org.json.JSONObject;
import sajad.wazin.mcgill.ca.FacebookWebScraper;
import sajad.wazin.mcgill.ca.facebook.PostData;
import sajad.wazin.mcgill.ca.facebook.SuggestionNode;
import sajad.wazin.mcgill.ca.persistence.Encoder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static sajad.wazin.mcgill.ca.FacebookWebScraper.LOGGER;
import static sajad.wazin.mcgill.ca.FacebookWebScraper.PERSISTENCE_SERVICE;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class DumpManager {

    private final static DumpManager dumpManager = new DumpManager();

    private List<HashMap<String, List<PostData>>> listOfAllContentScrapes;

    private List<List<SuggestionNode>> listOfAllTreeSuggestionScrapes;

    private List<HashMap<String, SuggestionNode>> listOfAllRawSuggestionScrapes;

    private DumpManager() {
        listOfAllContentScrapes = new ArrayList<>();
        listOfAllTreeSuggestionScrapes = new ArrayList<>();
        listOfAllRawSuggestionScrapes = new ArrayList<>();
    }

    public static DumpManager getDumpManager() {
        return dumpManager;
    }

    public void dump(Path output) {

        for(HashMap<String, List<PostData>> data : listOfAllContentScrapes) {
            JSONObject encodedScrape = Encoder.encodePages(data);
            FacebookWebScraper.PERSISTENCE_SERVICE.saveJSONFile(encodedScrape, output, "dumped_content");
        }

        for (List<SuggestionNode> roots : listOfAllTreeSuggestionScrapes) {
            PERSISTENCE_SERVICE.saveJSONFile(Encoder.encodeRoots(roots), output, "dumped_suggestions");
        }

        for (HashMap<String, SuggestionNode> suggestionMap : listOfAllRawSuggestionScrapes) {
            PERSISTENCE_SERVICE.saveListFile(new ArrayList<>(suggestionMap.keySet()), output, "dumped_raw");
        }

        LOGGER.dumpLog(output);

    }

    public void addContent(HashMap<String, List<PostData>> content) {
        listOfAllContentScrapes.add(content);
    }

    public void addRaw(HashMap<String, SuggestionNode> suggestionMap) {
        listOfAllRawSuggestionScrapes.add(suggestionMap);
    }

    public void addRoots(List<SuggestionNode> roots) {
        listOfAllTreeSuggestionScrapes.add(roots);
    }
}
