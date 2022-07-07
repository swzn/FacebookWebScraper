package sajad.wazin.mcgill.ca.scraper.search;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import sajad.wazin.mcgill.ca.chrome.BrowserController;


import java.util.List;
import java.util.Random;
import java.util.Stack;

import static sajad.wazin.mcgill.ca.utils.SeleniumUtils.*;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class DFSSearcher implements Searcher {

    private int maxDepth;
    private int maxGrowth;
    private Stack<String> linkStack;

    private BrowserController controller;

    public DFSSearcher(int maxDepth, int maxGrowth, BrowserController controller) {
        if(controller != null) this.controller = controller;
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
    public int search(int currentDepth) {


        String link = linkStack.pop();
        if (link == null) return currentDepth;
        System.out.println("Opened " + link);

        //If this level is maximum depth, then this is a leaf
        if(currentDepth == maxDepth) return currentDepth;


        controller.getDriver().get(link);
        List<WebElement> feeds = controller.getDriver().findElements(By.cssSelector(getCSSAsString("div","role","main")));

        if(feeds.size() == 0) return currentDepth;

        WebElement mainFeed = feeds.get(0);
        highlightWebElement(mainFeed, controller);

        controller.wait(10);
        WebElement toolBar = mainFeed.findElement(By.cssSelector(getCSSAsString("div","class", "rq0escxv l9j0dhe7 du4w35lb j83agx80 taijpn5t gs1a9yip owycx6da btwxx1t3 cddn0xzi dsne8k7f pfnyh3mw")));
        highlightWebElement(toolBar, controller);

        List<WebElement> buttons = toolBar.findElements(By.cssSelector(getCSSAsString("div", "role", "button")));

        boolean hasClicked = false;

        for(WebElement button : buttons) {
            highlightWebElement(button, controller);
            if(button.getText().contains("Follow") || button.getText().contains("Like")) {
                button.click();
                hasClicked = true;
            }
        }
        if (!hasClicked) return currentDepth;

        List<WebElement> suggestionsContainer = mainFeed.findElements(By.cssSelector(getCSSAsString("div", "class", "rq0escxv d2edcug0 ecyo15nh k387qaup r24q5c3a hv4rvrfc dati1w0a aov4n071")));

        if(suggestionsContainer.size() == 0) return currentDepth;

        highlightWebElement(suggestionsContainer.get(0), controller);
        List<WebElement> suggestions = suggestionsContainer.get(0).findElements(By.cssSelector(getCSSAsString("a","class","oajrlxb2 g5ia77u1 qu0x051f esr5mh6w e9989ue4 r7d6kgcz nhd2j8a9 p7hjln8o kvgmc6g5 cxmmr5t8 oygrvhab hcukyx3x jb3vyjys rz4wbd8a qt6c0cv9 a8nywdso i1ao9s8h esuyzwwr f1sip0of lzcic4wl gmql0nx0 gpro0wi8 i09qtzwb rq0escxv a8c37x1j n7fi1qx3 pmk7jnqg j9ispegn kr520xx4")));

        //If no suggestions then is a leaf
        if(suggestions.size() == 0) return currentDepth;

        int startingBound = maxGrowth - 1;
        if(startingBound >= suggestions.size()) maxGrowth = suggestions.size() - 1;
        int amountAdded = 0;
        for (int i = startingBound; i >= 0; i--) {
            amountAdded++;
            System.out.println("Added " + suggestions.get(i).getAttribute("href"));
            linkStack.add(suggestions.get(i).getAttribute("href"));
        }

        long waitTime = 35000 + new Random().nextInt(25000);
        controller.sleep(waitTime);

        for(int i = 0; i < amountAdded; i++) {
            search(currentDepth + 1);
        }

        return currentDepth;
    }


}
