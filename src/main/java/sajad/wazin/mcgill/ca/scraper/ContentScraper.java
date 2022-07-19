package sajad.wazin.mcgill.ca.scraper;

import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import sajad.wazin.mcgill.ca.FacebookWebScraper;
import sajad.wazin.mcgill.ca.utils.ResourcesManager;
import sajad.wazin.mcgill.ca.chrome.BrowserController;
import sajad.wazin.mcgill.ca.facebook.FacebookPost;
import sajad.wazin.mcgill.ca.facebook.PostData;
import sajad.wazin.mcgill.ca.persistence.Encoder;
import sajad.wazin.mcgill.ca.persistence.PersistenceService;
import sajad.wazin.mcgill.ca.scraper.settings.ContentScraperSettings;
import sajad.wazin.mcgill.ca.scraper.settings.ScraperSettings;
import sajad.wazin.mcgill.ca.utils.SeleniumUtils;

import java.net.SocketException;
import java.util.*;

import static sajad.wazin.mcgill.ca.FacebookWebScraper.LOGGER;
import static sajad.wazin.mcgill.ca.FacebookWebScraper.RESOURCES;


/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class ContentScraper implements Scraper {

    private ContentScraperSettings settings;

    public ContentScraper(ScraperSettings settings) {

        if(!(settings instanceof ContentScraperSettings)) {
            throw new IllegalArgumentException("Scraper settings are invalid");
        }
        this.settings = (ContentScraperSettings) settings;
        validateInput();
    }

    private void validateInput() {

    }

    @Override
    public void runScraper() {

        List<String> links = PersistenceService.readLines(settings.getInput());
        List<Thread> threadPool = new ArrayList<>();

        if(settings.getInstanceCount() > 1) {

            int instanceCount = settings.getInstanceCount();

            if(settings.getInstanceCount() > links.size()) instanceCount = links.size();

            float linksPerThread = (float) links.size() / (float) instanceCount;

            float startIndex = 0;
            float endIndex = linksPerThread - 1;



            for(int i = 0; i < instanceCount; i++) {


                if(startIndex > links.size()) break;
                if(endIndex > links.size()) endIndex = links.size();

                //To conform to finality requirements of multithreading
                int threadStartIndex = (int) Math.floor(startIndex);
                int threadEndIndex = (int) Math.floor(endIndex);


                Thread newThread = new Thread(() -> runScraper(links.subList(threadStartIndex, threadEndIndex + 1)));
                threadPool.add(newThread);
                newThread.setUncaughtExceptionHandler((t, e) -> {
                    if(!(e instanceof SocketException)) {
                        LOGGER.log("(Thread " + Thread.currentThread().getId() + ") Error");
                        LOGGER.log(e.getMessage());
                    }
                });
                newThread.start();

                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    LOGGER.log("(Thread " + Thread.currentThread().getId() + ") Error");
                    LOGGER.log(e.getMessage());
                }
                startIndex += linksPerThread;
                endIndex += linksPerThread;
            }
            for(Thread thread : threadPool) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    RESOURCES.deleteTemp();
                }
            }
        }
        else {
            Thread newThread = new Thread(() -> runScraper(links));
            newThread.setUncaughtExceptionHandler((t, e) -> {
                if(!(e instanceof Exception)) {
                    LOGGER.log(e.getMessage());
                }
            });
            newThread.start();
        }
    }



    public void runScraper(List<String> links) {
        String threadId = String.valueOf(Thread.currentThread().getId());

        BrowserController controller = new BrowserController(settings.isHeadless());
        int realTotal = 0;

        LOGGER.log("(Thread "+ threadId +"): Scraping for the following links: " + links);

        controller.initialize();
        controller.wait(5);

        HashMap<String, List<PostData>> allData = new HashMap<>();

        for(String link : links) {
            int count = 0;
            int total = settings.getAmountOfPosts();

            String[] subsectionOfLink = link.split("/");
            String pageName = subsectionOfLink[subsectionOfLink.length - 1];
            if(pageName.equals("")) pageName = subsectionOfLink[subsectionOfLink.length-2];

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
                controller.runJavaScript("arguments[0].style.border='1px solid red'", post);
                controller.wait(30);
                List<WebElement> linkRedirect = post.findElements(By.cssSelector(SeleniumUtils.getCSSAsString("a", "role", "link")));
                if (linkRedirect.isEmpty()) {
                    LOGGER.log("(Thread "+ threadId+") " + pageName + ": post cannot be scraped [" + count + "/" + total + "]");
                    continue;
                }

                String redirect = null;

                for(WebElement we :linkRedirect) {
                    SeleniumUtils.highlightWebElement(we, controller);
                    if(we.getAttribute("href") != null && we.getAttribute("href").contains("/posts/")) {
                        redirect = we.getAttribute("href");
                        break;
                    }
                }

                if(redirect == null) {
                    total--;
                    LOGGER.log("(Thread "+ threadId+") " + pageName + ": post cannot be scraped [" + count + "/" + total + "]");
                    continue;
                }

                //Remove the comment id
                controller.openNewTab(redirect.split("\\?", 2)[0]);

                try {
                    FacebookPost postContainer = new FacebookPost(SeleniumUtils.getCSSAsString("div", "class", ResourcesManager.RESOURCES.getResource("containers.div.class.post")), controller);
                    if (postContainer.isNull()) {
                        LOGGER.log("(Thread "+ threadId+") " + pageName + ": post cannot be scraped [" + count + "/" + total + "]");
                    }
                    PostData postData = new PostData(controller.getDriver().getCurrentUrl());

                    if (settings.isSavingContent()) postData.setContent(postContainer.getContent());
                    if (settings.isSavingReactions()) postData.setReactions(postContainer.getReactions());
                    if (settings.isSavingComments())
                        postData.setComments(postContainer.getComments(settings.getAmountOfComments()));
                    if (settings.isSavingShares()) postData.setShares(postContainer.getShares());
                    count++;
                    LOGGER.log("(Thread "+ threadId+") " + pageName + ": " + "post scraped successfully [" + count + "/" + total + "]");
                    //Everytime we read a post, we add it to the list of posts read
                    pageData.add(postData);
                } catch (Exception e) {
                    total--;
                    LOGGER.log("(Thread "+ threadId+") Error has been found");
                    LOGGER.log(e.getMessage());
                }
                controller.runJavaScript("window.close()");
                controller.getDriver().switchTo().window(mainPage);
            }
            realTotal += count;
            allData.put(link, pageData);
        }

        JSONObject encodedScrape = Encoder.encodePages(allData);

        FacebookWebScraper.PERSISTENCE_SERVICE.saveJSONFile(encodedScrape, settings.getOutput());
        LOGGER.log("(Thread " + threadId + "): Task completed with " + realTotal + " posts scraped");

        controller.getDriver().quit();

    }
}
