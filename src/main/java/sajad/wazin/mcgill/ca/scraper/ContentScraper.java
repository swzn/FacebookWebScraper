package sajad.wazin.mcgill.ca.scraper;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import sajad.wazin.mcgill.ca.ResourcesManager;
import sajad.wazin.mcgill.ca.chrome.BrowserController;
import sajad.wazin.mcgill.ca.facebook.FacebookPost;
import sajad.wazin.mcgill.ca.facebook.PostData;
import sajad.wazin.mcgill.ca.persistence.Encoder;
import sajad.wazin.mcgill.ca.persistence.PersistenceService;
import sajad.wazin.mcgill.ca.utils.SeleniumUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class ContentScraper implements Scraper {

    private ContentScraperSettings settings;
    private BrowserController controller;

    public ContentScraper(ScraperSettings settings, BrowserController controller) {

        if(!(settings instanceof ContentScraperSettings)) {
            throw new IllegalArgumentException("Scraper settings are invalid");
        }
        this.settings = (ContentScraperSettings) settings;
        this.controller = controller;
        validateInput();
    }

    private void validateInput() {

    }

    @Override
    public void runScraper() {
        long start = System.currentTimeMillis();
        int postsScraped = 0;

        List<String> links = PersistenceService.readLines(settings.getInput());
        HashMap<String, List<PostData>> allData = new HashMap<>();

        for(String link : links){
            List<PostData> pageData = new ArrayList<>();
            controller.getDriver().get(link);

            controller.wait(10);
            WebElement mainFeed = controller.getDriver().findElement(By.cssSelector(SeleniumUtils.getCSSAsString("div", "role", "main"))).findElement(By.cssSelector(SeleniumUtils.getCSSAsString("div","class","rq0escxv l9j0dhe7 du4w35lb hpfvmrgz g5gj957u aov4n071 oi9244e8 bi6gxh9e h676nmdw aghb5jc5 gile2uim pwa15fzy fhuww2h9")));

            ArrayList<WebElement> posts = new ArrayList<>(mainFeed.findElements(By.cssSelector(SeleniumUtils.getCSSAsString("div","class","rq0escxv l9j0dhe7 du4w35lb hybvsw6c io0zqebd m5lcvass fbipl8qg nwvqtn77 k4urcfbm ni8dbmo4 stjgntxs sbcfpzgs"))));
            int lastSize = 0;
            while (posts.size() < settings.getAmountOfPosts() && posts.size() != lastSize)  {
                lastSize = posts.size();
                controller.runJavaScript("window.scroll(0,document.body.scrollHeight);");
                controller.wait(10);
                posts = new ArrayList<>(mainFeed.findElements(By.cssSelector(SeleniumUtils.getCSSAsString("div","class","rq0escxv l9j0dhe7 du4w35lb hybvsw6c io0zqebd m5lcvass fbipl8qg nwvqtn77 k4urcfbm ni8dbmo4 stjgntxs sbcfpzgs"))));
            }

            String mainPage = controller.getDriver().getWindowHandle();

            for(int i = 0; i < posts.size() && i < settings.getAmountOfPosts(); i++) {
                WebElement post = posts.get(i);
                controller.runJavaScript("arguments[0].style.border='2px solid red'", post);
                postsScraped++;
                controller.wait(2);
                List<WebElement> linkRedirect = post.findElements(By.cssSelector(SeleniumUtils.getCSSAsString("a", "class", ResourcesManager.RESOURCES.getResource("buttons.a.open_tab"))));
                if(linkRedirect.isEmpty()) {
                    continue;
                }
                controller.openNewTab(linkRedirect.get(0).getAttribute("href"));
                FacebookPost postContainer = new FacebookPost(SeleniumUtils.getCSSAsString("div", "class", ResourcesManager.RESOURCES.getResource("containers.div.class.post")), controller);
                if(postContainer.isNull()) continue;
                PostData postData = new PostData(controller.getDriver().getCurrentUrl());

                if(settings.isSavingContent()) postData.setContent(postContainer.getContent());
                if(settings.isSavingReactions()) postData.setReactions(postContainer.getReactions());
                if(settings.isSavingComments()) postData.setComments(postContainer.getComments(settings.getAmountOfComments()));
                if(settings.isSavingShares()) postData.setShares(postContainer.getShares());

                //Everytime we read a post, we add it to the list of posts read
                pageData.add(postData);

                controller.runJavaScript("window.close()");
                controller.getDriver().switchTo().window(mainPage);
            }
            allData.put(link, pageData);
        }

        System.out.println(Encoder.encodePages(allData).toString(4));
        long end = System.currentTimeMillis();

        System.out.println("Started at: " + start);
        System.out.println("end at: " + end);
        System.out.println("Scraped posts: " + postsScraped);
        System.out.println(end - start * 1000);
    }
}
