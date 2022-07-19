package sajad.wazin.mcgill.ca.facebook;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import sajad.wazin.mcgill.ca.chrome.BrowserController;
import sajad.wazin.mcgill.ca.utils.SeleniumUtils;

import java.util.ArrayList;
import java.util.List;

import static sajad.wazin.mcgill.ca.utils.SeleniumUtils.*;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class FacebookPost {

    private final BrowserController globalController;
    private final WebElement postContainer;
    private boolean nullBoolean = false;


    /*
    * This constructor will create an Object that can manipulate a post-identified Facebook page, given that the
    * String in the container leads to the HTML element in which the post lives.
    * */

    public FacebookPost(String pathToContainer, BrowserController globalController) {
        this.globalController = globalController;
        List<WebElement> elements = globalController.getDriver().findElements(By.cssSelector(pathToContainer));

        // Determine if the post's HTML element is null
        if(elements.size() == 0) {
            nullBoolean = true;
            globalController.runJavaScript("window.close()");
            globalController.getDriver().switchTo().window((String) globalController.getDriver().getWindowHandles().toArray()[0]);
            postContainer = null;
        }
        else {
            // If not, then instantiate the WebElement to the post's HTML element
            this.postContainer = elements.get(0);
        }
    }

    public List<String> getComments(int maxComments){


        ArrayList<String> comments = new ArrayList<>();

        globalController.wait(25);

        List<WebElement> commentsSection = postContainer.findElements(getSelector("containers.div.class.comments_section"));

        if(commentsSection.isEmpty()) {
            return comments;
        }

        // Finds the container for all comments from the comments section
        WebElement commentsContainer = commentsSection.get(0);

        // Locates the buttons that allows the user to choose which comments to show
        List<WebElement> commentsLoaderTab = commentsContainer.findElements(getSelector("containers.div.class.comments_loader_tab"));
        List<WebElement> commentsLoader = commentsLoaderTab.get(0).findElements(getSelector("buttons.div.role"));

        // If the button has been found, choose to show all comments
        if(commentsLoader.size() > 0) {
            commentsLoader.get(0).click();
            List<WebElement> commentsOptionsMenu = globalController.getDriver().findElements(getSelector("menus.div.class.comments_options"));
            if(commentsOptionsMenu.size() != 0) {
                for (WebElement option : commentsOptionsMenu.get(0).findElements(getSelector("menus.div.role.comments_menu_item"))) {
                    if(option.getText().contains("All")) {
                        option.click();
                    }
                }
            }

        }

        // Find the button that loads more comments at the bottom of the comment section
        List<WebElement> loadComments = commentsSection.get(0).findElements(getSelector("buttons.a.load_comments"));

        // Keep pressing it until you reached the maximum
        while (loadComments.size() > 1) {
            if (!loadComments.get(1).getText().startsWith("View")) break;
            globalController.wait(10);
            if (!commentsContainer.findElements(getSelector("containers.div.class.comment_count")).isEmpty()) {
                String[] text = commentsContainer.findElement(getSelector("containers.div.class.comment_count")).getText().split(" ");

                // Find the "X of Y" text area and check if X surpasses the maximum comments to scrape
                if(parseFormattedNumber(text[0]) > maxComments) break;
            }
            globalController.wait(5);

            // If we have not surpassed the limit, press the button
            try {
                loadComments.get(1).click();
            }

            // If an error occurs, stop pressing the button
            catch (Exception e) {
                break;
            }

            globalController.wait(3);
            loadComments = commentsContainer.findElements(getSelector("buttons.a.load_comments"));
        }

        globalController.wait(2);


        // Load all comments and add it to the ArrayList
        List<WebElement> commentsList = commentsContainer.findElements(getSelector("containers.div.class.comment"));
        for(int i = 0; i < commentsList.size(); i++) {
            if(i == maxComments) break;
            comments.add(commentsList.get(i).getText());
        }

        return comments;
    }

    public FacebookReactions getReactions() {

        // Create a new Reactions object to store the output
        FacebookReactions reactions = new FacebookReactions();
        globalController.wait(10);

        // Find the button that will open the reactions tab
        List<WebElement> reactionsButton = postContainer.findElements(getSelector("buttons.div.class.open_reactions_tab"));
        if(reactionsButton.size() == 0) {
            return reactions;
        }

        globalController.runJavaScript("arguments[0].click();", reactionsButton.get(0));

        // Find the container for the reactions pop-up
        WebElement reactionContainer = globalController.getFirstElementByCSS("div", "aria-label", "Reactions");

        // List all the reaction tabs
        List<WebElement> reactionTabs = reactionContainer.findElements(By.cssSelector(getCSSAsString("div", "role", "tab")));

        for(int i = 0; i < reactionTabs.size(); i++) {
            if(reactionTabs.get(i) != null) {
                globalController.wait(5);
                String textField = reactionTabs.get(i).getText();

                // Skip empty or "All" tab
                if(textField.equals("All") || textField.equals("")) {
                    continue;
                }
                if(textField.equals("More") && reactionTabs.get(i).getAttribute("aria-hidden").equals("false")) {

                    // For the "more" tab, click on it and read the reactions individually
                    reactionTabs.get(i).click();
                    globalController.wait(5);

                    // Read from the reactions dropdown
                    WebElement moreReactionsContainer = globalController.getDriver().findElement(getSelector("menus.div.class.more_reactions"));
                    List<WebElement> allMoreTabs = moreReactionsContainer.findElements(getSelector("menus.div.role.reactions_menu_item"));
                    for (int j = 0; j < allMoreTabs.size(); j++) {
                        SeleniumUtils.highlightWebElement(allMoreTabs.get(j), globalController);

                        // Use the image url to parse through the reactions and add them to the FacebookReactionsObject
                        String imageURL = allMoreTabs.get(j).findElement(getSelector("menus.img.class.reaction")).getAttribute("src");
                        reactions.setReaction(imageURL, allMoreTabs.get(j).getText());
                    }
                }
                else {
                    globalController.wait(5);

                    // Look at the rest of the main tabs and parse them using their image
                    List<WebElement> images = reactionTabs.get(i).findElements(getSelector("menus.img.class.reaction"));
                    if (images.size() == 0) continue;
                    String imageURL = images.get(0).getAttribute("src");
                    reactions.setReaction(imageURL, textField);
                }
            }
        }
        globalController.wait(5);

        // Close the pop-up
        reactionContainer.findElement(getSelector("menus.div.aria-label.close_reactions")).click();

        return reactions;
    }

    public List<String> getShares(int maxShares){
        List<String> shares = new ArrayList<>();

        globalController.wait(20);

        // Find all buttons on the page
        List<WebElement> shareButton = postContainer.findElements(getSelector("buttons.div.class.open_shares"));

        boolean buttonFound = false;

        for(WebElement we : shareButton) {

            // Click on the button that will open the shares pop-up
            if(we.getText().contains("hares")) {
                List<WebElement> a = we.findElements(getSelector("buttons.div.role"));
                if(a.size() != 1) {
                    break;
                }
                buttonFound = true;
                globalController.runJavaScript("arguments[0].click();", a.get(0));
            }
        }

        if(!buttonFound) return shares;

        WebElement sharesContainer = globalController.getDriver().findElement(getSelector("containers.div.aria-label.shares"));

        WebElement textContainer = sharesContainer.findElement(getSelector("containers.div.shares_move_to_text"));
        /*highlightWebElement(textContainer, globalController);
        new Actions(globalController.getDriver()).moveToElement(textContainer).build().perform();*/

        globalController.sleep(5000);
        globalController.wait(10);

        // If there are more shares to load, then scroll to the bottom of the shares pop-up
        int lastSize = 0;
        while(textContainer.findElements(By.cssSelector(getCSSAsString("div", "aria-label", "Show Attachment"))).size() != lastSize) {
            List<WebElement> allSharesBoxes = textContainer.findElements(By.cssSelector(getCSSAsString("div", "aria-label", "Show Attachment")));

            if(allSharesBoxes.size() >= maxShares) {
                break;
            }
            lastSize = allSharesBoxes.size();

            WebElement bottomOfContainer = textContainer.findElement(getSelector("containers.div.scrollable_bottom_of_shares"));
            globalController.runJavaScript("arguments[0].scrollIntoView(true);", bottomOfContainer);
            globalController.sleep(1500);

        }

        // Once it has loaded enough shares, then stop scrolling and read everything that can be read
        List<WebElement> sharesCaptionContainers = textContainer.findElements(getSelector("containers.div.shares_box"));

        for(WebElement container: sharesCaptionContainers) {
            if(!container.getText().isEmpty()) shares.add(container.getText());
        }
        return shares;
    }


    public String getContent(){
        // If the post has readable content, then scrape it
        List<WebElement> contentContainer = postContainer.findElements(getSelector("containers.div.class.post_content"));
        if(contentContainer.size() == 1) {
            return contentContainer.get(0).getText();
        }
        return "";
    }

    public boolean isNull() {
        return nullBoolean;
    }
}
