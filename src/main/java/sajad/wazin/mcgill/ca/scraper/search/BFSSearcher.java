package sajad.wazin.mcgill.ca.scraper.search;


import org.openqa.selenium.WebElement;
import sajad.wazin.mcgill.ca.FacebookWebScraper;
import sajad.wazin.mcgill.ca.chrome.BrowserController;
import sajad.wazin.mcgill.ca.facebook.SuggestionNode;


import java.util.*;

import static sajad.wazin.mcgill.ca.FacebookWebScraper.CANCELLED_TASK;
import static sajad.wazin.mcgill.ca.FacebookWebScraper.LOGGER;
import static sajad.wazin.mcgill.ca.utils.SeleniumUtils.*;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class BFSSearcher implements Searcher{

    private int  maxGrowth;
    private final int maxDepth;
    private int unknownErrorsInARow = 0;

    private HashMap<String, SuggestionNode> suggestionsMap;
    private LinkedList<String> linkQueue;
    private BrowserController controller;

    /*
    * A BFS searcher is a recursive breadth-first-search algorithm that uses queues to recursively search through facebook suggestions
    * */

    public BFSSearcher(int maxDepth, int maxGrowth, BrowserController controller, HashMap<String, SuggestionNode> suggestionsMap) {
        this.maxGrowth = maxGrowth;
        this.maxDepth = maxDepth;
        if(controller != null) {
            this.controller = controller;
        }
        linkQueue = new LinkedList<>();
        this.suggestionsMap = suggestionsMap;
    }

    @Override
    public void add(String link) {
        linkQueue.add(link);
    }

    public void add(List<String> linksToAdd) {
        linkQueue.addAll(linksToAdd);
    }

    public void add(List<String> linksToAdd, int amount) {
        for (int i = 0; i < linksToAdd.size() && i < amount; i++) {
            linkQueue.add(linksToAdd.get(i));
        }
    }

    @Override
    public int search() {

        // Create a queue that will be passed on to the next depth
        LinkedList<String> nextDepth = new LinkedList<>();

        int currentDepth = 0;

        // Recursively loop through the queue at each depth
        while(!linkQueue.isEmpty()) {

            String link = linkQueue.pollFirst();

            // Skip this page if the link is unreadable
            if (link == null) continue;

            // Retrieve the corresponding suggestion node from the hashmap
            SuggestionNode node = suggestionsMap.get(link);

            currentDepth = node.getDepth();

            // If this searcher has been killed, then stop searching
            if (CANCELLED_TASK) return currentDepth;

            // Remove the link from the queue if it hasn't been removed
            if (currentDepth >= maxDepth) {
                linkQueue.remove(link);
            }

            //If this level is maximum depth, then skip this page
            if(currentDepth == maxDepth) continue;

            LOGGER.log("Opened " + link + " [" + currentDepth + "/" + maxDepth +"]");
            controller.getDriver().get(link);

            // Sleep a random amount of time between 45 and 100 seconds to slow down the rate limitations
            long waitTime = 45000 + new Random().nextInt(55000);

            LOGGER.log("Waiting " + waitTime);
            controller.sleep(waitTime);

            try {

                // Find the feed of buttons
                List<WebElement> feeds = controller.getDriver().findElements(getSelector("containers.div.role.main"));

                if (feeds.size() == 0) continue;

                // Find the toolbar containing the like buttons
                WebElement mainFeed = feeds.get(0);
                highlightWebElement(mainFeed, controller);

                controller.wait(10);
                WebElement toolBar = mainFeed.findElement(getSelector("menus.div.class.like_toolbar"));
                highlightWebElement(toolBar, controller);

                List<WebElement> buttons = toolBar.findElements(getSelector("buttons.div.role"));

                // Keep track of whether or not we have clicked the like button
                boolean hasClicked = false;

                for (WebElement button : buttons) {
                    highlightWebElement(button, controller);
                    if (button.getText().contains("Liked")) {
                        LOGGER.log(link + " was already liked");
                        button.click();
                        canLike();
                        List<WebElement> newButtons = toolBar.findElements(getSelector("buttons.div.role"));
                        for (WebElement we : newButtons) {
                            if (we.getText().contains("Follow") || we.getText().contains("Like")) {
                                LOGGER.log(link + " has been re-liked");
                                we.click();
                                canLike();
                                hasClicked = true;
                            }
                        }
                        break;
                    } else if (button.getText().contains("Follow") || button.getText().contains("Like")) {
                        LOGGER.log(link + " has been liked");
                        button.click();
                        canLike();
                        hasClicked = true;
                    }
                }
                if (!hasClicked) continue;

                List<WebElement> suggestionsContainer = mainFeed.findElements(getSelector("containers.div.class.suggestions_feed"));

                if (suggestionsContainer.size() == 0) continue;

                highlightWebElement(suggestionsContainer.get(0), controller);
                List<WebElement> suggestions = suggestionsContainer.get(0).findElements(getSelector("buttons.a.suggestion"));

                //If no suggestions then is a leaf
                if (suggestions.size() == 0) {
                    canLike();
                    continue;
                }

                int amountAdded = 0;

                unknownErrorsInARow = 0;
                for (int i = 0; i < suggestions.size() - 1; i++) {
                    String newLink = suggestions.get(i).getAttribute("href");

                    // If the current suggestion has already been seen in this scrape, skip it
                    if (suggestionsMap.containsKey(newLink)) continue;


                    // Add the suggestion to the hashmap and to its parent
                    SuggestionNode child = new SuggestionNode(newLink, currentDepth + 1);
                    node.add(child);
                    suggestionsMap.put(newLink, child);

                    // Add it to the next queue
                    LOGGER.log("Added " + newLink);
                    nextDepth.add(newLink);

                    amountAdded++;
                    if (amountAdded == maxGrowth) break;
                }
            }
            catch (Exception e) {

                // Check if task needs to be stopped
                unknownErrorsInARow++;
                if(unknownErrorsInARow >= FacebookWebScraper.MAX_ERRORS_BEFORE_STOP) CANCELLED_TASK = true;
                LOGGER.log("Exception occurred while scraping: " + link);

                if(canLike()) LOGGER.log("[" + unknownErrorsInARow + "/" + FacebookWebScraper.MAX_ERRORS_BEFORE_STOP +"] errors in a row before stopping");

            }
        }

        // Start scraping at the next depth
        if(CANCELLED_TASK) return currentDepth;
        linkQueue = nextDepth;

        if(!linkQueue.isEmpty()) search();

        return currentDepth;
    }

    // Check if the user can still like pages, if not then set task to cancelled and return false
    private boolean canLike(){
        controller.wait(5);
        List<WebElement> permissionLost = controller.getDriver().findElements(getSelector("containers.div.class.error"));
        if(!permissionLost.isEmpty()) {
            CANCELLED_TASK = true;
            LOGGER.log("Permissions to liking pages have been temporarily revoked by Facebook.");
            return false;
        }
        return true;
    }
}
