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

    public FacebookPost(String pathToContainer, BrowserController globalController) {
        this.globalController = globalController;
        List<WebElement> elements = globalController.getDriver().findElements(By.cssSelector(pathToContainer));
        if(elements.size() == 0) {
            nullBoolean = true;
            globalController.runJavaScript("window.close()");
            globalController.getDriver().switchTo().window((String) globalController.getDriver().getWindowHandles().toArray()[0]);
            postContainer = null;
        }
        else {
            this.postContainer = elements.get(0);
        }
    }

    public List<String> getComments(int maxComments){
        ArrayList<String> comments = new ArrayList<>();
        List<WebElement> commentsSection = postContainer.findElements(By.cssSelector(getCSSAsString("div", "class", "stjgntxs ni8dbmo4 l82x9zwi uo3d90p7 h905i5nu monazrh9")));
        if(commentsSection.isEmpty()) {
            throw new IllegalArgumentException("Comments cannot be read");
        }
        WebElement commentsContainer = commentsSection.get(0);
        List<WebElement> loadComments = commentsSection.get(0).findElements(By.cssSelector(getCSSAsString("div", "class", "j83agx80 bkfpd7mw jb3vyjys hv4rvrfc qt6c0cv9 dati1w0a l9j0dhe7")));
        while (loadComments.size() > 1) {
            if (!loadComments.get(1).getText().startsWith("View")) break;
            globalController.wait(3);
            if (!commentsContainer.findElements(By.cssSelector(getCSSAsString("div", "class", "j83agx80 bp9cbjyn"))).isEmpty()) {
                String[] text = commentsContainer.findElement(By.cssSelector(getCSSAsString("div", "class", "j83agx80 bp9cbjyn"))).getText().split(" ");
                if(parseFormattedNumber(text[0]) > maxComments) break;
            }
            globalController.wait(3);
            loadComments.get(1).click();
            globalController.wait(3);
            loadComments = commentsContainer.findElements(By.cssSelector(getCSSAsString("div", "class", "j83agx80 bkfpd7mw jb3vyjys hv4rvrfc qt6c0cv9 dati1w0a l9j0dhe7")));
        }
        globalController.wait(2);


        List<WebElement> commentsList = commentsContainer.findElements(By.cssSelector(getCSSAsString("div", "class", "ecm0bbzt e5nlhep0 a8c37x1j")));
        for(int i = 0; i < commentsList.size(); i++) {
            if(i == maxComments) break;
            comments.add(commentsList.get(i).getText());
        }
        return comments;
    }

    public FacebookReactions getReactions() {
        FacebookReactions reactions = new FacebookReactions();
        globalController.wait(10);

        List<WebElement> reactionsButton = postContainer.findElements(By.cssSelector(getCSSAsString("div", "class", "oajrlxb2 g5ia77u1 qu0x051f esr5mh6w e9989ue4 r7d6kgcz rq0escxv nhd2j8a9 p7hjln8o kvgmc6g5 cxmmr5t8 oygrvhab hcukyx3x jb3vyjys rz4wbd8a qt6c0cv9 a8nywdso i1ao9s8h esuyzwwr f1sip0of n00je7tq arfg74bv qs9ysxi8 k77z8yql l9j0dhe7 abiwlrkh p8dawk7l lzcic4wl gmql0nx0 ce9h75a5 ni8dbmo4 stjgntxs tkr6xdv7 a8c37x1j")));
        if(reactionsButton.size() == 0) {
            return reactions;
        }
        globalController.runJavaScript("arguments[0].click();", reactionsButton.get(0));
        WebElement reactionContainer = globalController.getFirstElementByCSS("div", "aria-label", "Reactions");
        List<WebElement> reactionTabs = reactionContainer.findElements(By.cssSelector(getCSSAsString("div", "role", "tab")));

        for(int i = 0; i < reactionTabs.size(); i++) {
            if(reactionTabs.get(i) != null) {
                globalController.wait(5);
                String textField = reactionTabs.get(i).getText();
                if(textField.equals("All") || textField.equals("")) {
                    continue;
                }
                if(textField.equals("More") && reactionTabs.get(i).getAttribute("aria-hidden").equals("false")) {
                    reactionTabs.get(i).click();
                    globalController.wait(5);
                    WebElement moreReactionsContainer = globalController.getDriver().findElement(By.cssSelector(getCSSAsString("div", "class", "rpm2j7zs k7i0oixp gvuykj2m ni8dbmo4 du4w35lb q5bimw55 ofs802cu pohlnb88 dkue75c7 mb9wzai9 l56l04vs r57mb794 l9j0dhe7 kh7kg01d eg9m0zos c3g1iek1 gs1a9yip rq0escxv j83agx80 cbu4d94t rz4wbd8a a8nywdso smdty95z c1zf3a5g gu2zta1c k4urcfbm")));
                    List<WebElement> allMoreTabs = moreReactionsContainer.findElements(By.cssSelector(getCSSAsString("div", "role", "menuitemradio")));
                    for (int j = 0; j < allMoreTabs.size(); j++) {
                        SeleniumUtils.highlightWebElement(allMoreTabs.get(j), globalController);
                        String imageURL = allMoreTabs.get(j).findElement(By.cssSelector(getCSSAsString("img","class","hu5pjgll bixrwtb6"))).getAttribute("src");
                        reactions.setReaction(imageURL, allMoreTabs.get(j).getText());
                    }
                }
                else {
                    globalController.wait(5);
                    List<WebElement> images = reactionTabs.get(i).findElements(By.cssSelector(getCSSAsString("img","class","hu5pjgll bixrwtb6")));
                    if (images.size() == 0) continue;
                    String imageURL = images.get(0).getAttribute("src");
                    reactions.setReaction(imageURL, textField);
                }
            }
        }
        globalController.wait(5);
        reactionContainer.findElement(By.cssSelector(getCSSAsString("div", "aria-label", "Close"))).click();

        return reactions;
    }

    public List<String> getShares(){
        List<String> shares = new ArrayList<>();

        globalController.wait(20);
        List<WebElement> shareButton = postContainer.findElements(By.cssSelector(getCSSAsString("div", "class", "gtad4xkn")));

        for(WebElement we : shareButton) {
            System.out.println(we.getText());
            if(we.getText().contains("hares")) {
                List<WebElement> a = we.findElements(By.cssSelector(getCSSAsString("div","role","button")));
                if(a.size() != 1) {
                    break;
                }
                globalController.runJavaScript("arguments[0].click();", a.get(0));
            }
        }
        WebElement sharesContainer = globalController.getDriver().findElement(By.cssSelector(getCSSAsString("div", "aria-label", "People who shared this")));

        WebElement textContainer = sharesContainer.findElement(By.cssSelector(getCSSAsString("div", "class", "j83agx80 cbu4d94t buofh1pr l9j0dhe7")));

        new Actions(globalController.getDriver()).moveToElement(textContainer).build().perform();

        globalController.sleep(1000);

        while(textContainer.findElements(By.cssSelector(getCSSAsString("div", "class", "rek2kq2y"))).size() != 0) {
            WebElement bottomOfContainer = textContainer.findElement(By.cssSelector(getCSSAsString("div", "class", "ihqw7lf3")));
            globalController.runJavaScript("arguments[0].scrollIntoView(true);", bottomOfContainer);

        }

        List<WebElement> sharesCaptionContainers = textContainer.findElements(By.cssSelector(getCSSAsString("div", "class", "kvgmc6g5 cxmmr5t8 oygrvhab hcukyx3x c1et5uql ii04i59q")));
        for(WebElement container: sharesCaptionContainers) {
            shares.add(container.getText());
        }
        return shares;
    }


    public String getContent(){
        List<WebElement> contentContainer = postContainer.findElements(By.cssSelector(getCSSAsString("div", "class", "ecm0bbzt hv4rvrfc ihqw7lf3 dati1w0a")));
        if(contentContainer.size() == 1) {
            return contentContainer.get(0).getText();
        }
        return "";
    }

    public boolean isNull() {
        return nullBoolean;
    }
}
