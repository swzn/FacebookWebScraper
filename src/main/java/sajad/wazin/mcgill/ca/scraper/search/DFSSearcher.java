package sajad.wazin.mcgill.ca.scraper.search;

import org.openqa.selenium.WebElement;
import sajad.wazin.mcgill.ca.FacebookWebScraper;
import sajad.wazin.mcgill.ca.chrome.BrowserController;
import sajad.wazin.mcgill.ca.facebook.SuggestionNode;


import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import static sajad.wazin.mcgill.ca.FacebookWebScraper.CANCELLED_TASK;
import static sajad.wazin.mcgill.ca.FacebookWebScraper.LOGGER;
import static sajad.wazin.mcgill.ca.utils.SeleniumUtils.*;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class DFSSearcher implements Searcher {

    private int maxDepth;
    private int maxGrowth;
    private HashMap<String, SuggestionNode> suggestionsMap;
    private Stack<String> linkStack;

    private BrowserController controller;
    private int unknownErrorsInARow = 0;

    /*
    * A DFS searcher is a recursive depth-first-search algorithm that will use a stack to search through facebook suggestions
    * */
    public DFSSearcher(int maxDepth, int maxGrowth, BrowserController controller, HashMap<String, SuggestionNode> suggestionsMap) {
        if(controller != null) this.controller = controller;
        this.suggestionsMap = suggestionsMap;
        this.maxGrowth = maxGrowth;
        this.maxDepth = maxDepth;
        linkStack = new Stack<>();
    }

    public void add(String link) {
        linkStack.add(link);
    }

    public void add(List<String> linksToAdd) {
        linkStack.addAll(linksToAdd);
    }

    public void add(List<String> linksToAdd, int amount) {
        for (int i = 0; i < linksToAdd.size() && i < amount; i++) {
            linkStack.add(linksToAdd.get(i));
        }
    }



    @Override
    public int search() {
        // If search was called, then a link has been added to the stack prior
        String link = linkStack.pop();

        // Retrieve the corresponding suggestion node from the hashmap
        SuggestionNode node = suggestionsMap.get(link);
        int currentDepth = node.getDepth();

        // Skip this page if the link is unreadable
        if (link == null) return currentDepth;

        // If this searcher has been killed, then stop searching
        if (CANCELLED_TASK) return currentDepth;

        // Open the link
        controller.getDriver().get(link);

        //If this level is maximum depth, then this is a leaf
        if(currentDepth == maxDepth) return currentDepth;

        LOGGER.log("Opened " + link + " [" + currentDepth + "/" + maxDepth +"]");

        // Sleep a random amount of time between 45 and 100 seconds to slow down the rate limitations
        long waitTime = 45000 + new Random().nextInt(55000);

        LOGGER.log("Waiting " + waitTime);
        controller.sleep(waitTime);

        int amountAdded = 0;
        try {
            // Find the feed of buttons
            List<WebElement> feeds = controller.getDriver().findElements(getSelector("containers.div.role.main"));

            if (feeds.size() == 0) return currentDepth;

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
            if (!hasClicked) return currentDepth;

            List<WebElement> suggestionsContainer = mainFeed.findElements(getSelector("containers.div.class.suggestions_feed"));

            if (suggestionsContainer.size() == 0) return currentDepth;

            highlightWebElement(suggestionsContainer.get(0), controller);
            List<WebElement> suggestions = suggestionsContainer.get(0).findElements(getSelector("buttons.a.suggestion"));

            //If no suggestions then is a leaf
            if (suggestions.size() == 0) {
                canLike();
                return currentDepth;
            }

            // Always skip the last suggestion because it may not be a real suggestion
            for (int i = 0; i < suggestions.size() - 1; i++) {
                String newLink = suggestions.get(i).getAttribute("href");

                // If the current suggestion has already been seen in this scrape, skip it
                if (suggestionsMap.containsKey(newLink)) continue;


                // Add the suggestion to the hashmap and to its parent
                SuggestionNode child = new SuggestionNode(newLink, currentDepth + 1);
                node.add(child);
                suggestionsMap.put(newLink, child);

                // Add it to the stack
                LOGGER.log("Added " + newLink);
                linkStack.add(newLink);

                amountAdded++;
                if (amountAdded == maxGrowth) break;
            }
        } catch (Exception e) {
            // Check if task needs to be stopped
            unknownErrorsInARow++;
            if(unknownErrorsInARow >= FacebookWebScraper.MAX_ERRORS_BEFORE_STOP) CANCELLED_TASK = true;
            LOGGER.log("Exception occurred while scraping: " + link);

            if(canLike()) LOGGER.log("[" + unknownErrorsInARow + "/" + FacebookWebScraper.MAX_ERRORS_BEFORE_STOP +"] errors in a row before stopping");
        }

        // Recurse once for every child (subtree completion property of DFS trees)
        for(int i = 0; i < amountAdded; i++) {
            if(CANCELLED_TASK) return currentDepth;
            search();
        }

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
