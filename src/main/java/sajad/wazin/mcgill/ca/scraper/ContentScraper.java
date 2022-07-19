package sajad.wazin.mcgill.ca.scraper;

import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import sajad.wazin.mcgill.ca.FacebookWebScraper;
import sajad.wazin.mcgill.ca.chrome.BrowserController;
import sajad.wazin.mcgill.ca.facebook.FacebookPost;
import sajad.wazin.mcgill.ca.facebook.PostData;
import sajad.wazin.mcgill.ca.persistence.Encoder;
import sajad.wazin.mcgill.ca.persistence.PersistenceService;
import sajad.wazin.mcgill.ca.scraper.settings.ContentScraperSettings;
import sajad.wazin.mcgill.ca.scraper.settings.ScraperSettings;

import java.net.SocketException;
import java.util.*;

import static sajad.wazin.mcgill.ca.FacebookWebScraper.*;
import static sajad.wazin.mcgill.ca.utils.SeleniumUtils.*;


/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class ContentScraper implements Scraper {

    private ContentScraperSettings settings;
    private int unknownErrorsInARow = 0;

    /*
    * The constructor for a ContentScraper requires only an instance of ContentScraperSettings
    * */
    public ContentScraper(ScraperSettings settings) {

        if(!(settings instanceof ContentScraperSettings)) {
            throw new IllegalArgumentException("Scraper settings are invalid");
        }
        this.settings = (ContentScraperSettings) settings;
    }


    @Override
    public void runScraper() {
        // Start by reading the input
        List<String> links = PersistenceService.readLines(settings.getInput());


        // Check if we are multithreading
        if(settings.getInstanceCount() > 1) {

            // Then by creating a thread pool to run multiple instances at once
            List<Thread> threadPool = new ArrayList<>();

            int instanceCount = settings.getInstanceCount();

            if(settings.getInstanceCount() > links.size()) instanceCount = links.size();

            float linksPerThread = (float) links.size() / (float) instanceCount;

            float startIndex = 0;
            float endIndex = linksPerThread - 1;


            // Separate the input list evenly among the threads
            for(int i = 0; i < instanceCount; i++) {


                if(startIndex > links.size()) break;
                if(endIndex > links.size()) endIndex = links.size();

                //To conform to finality requirements of multithreading
                int threadStartIndex = (int) Math.floor(startIndex);
                int threadEndIndex = (int) Math.floor(endIndex);

                // Start each thread
                Thread newThread = new Thread(() -> runScraper(links.subList(threadStartIndex, threadEndIndex + 1)));
                threadPool.add(newThread);
                newThread.setUncaughtExceptionHandler((t, e) -> {
                    if(!(e instanceof SocketException)) {
                        LOGGER.log("(Thread " + Thread.currentThread().getId() + ") FATAL Error");
                        LOGGER.log(e.getMessage());
                    }
                });
                newThread.start();

                // Wait 15 seconds before starting a new thread
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    LOGGER.log("(Thread " + Thread.currentThread().getId() + ") Error");
                    LOGGER.log(e.getMessage());
                }

                startIndex += linksPerThread;
                endIndex += linksPerThread;
            }

            // Join each threads when they're done running
            for(Thread thread : threadPool) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    LOGGER.log(e.getMessage());
                }
            }
            RESOURCES.deleteTemp();
        }

        else {
            Thread newThread = new Thread(() -> runScraper(links));
            newThread.setUncaughtExceptionHandler((t, e) -> {
                if(!(e instanceof Exception)) {
                    LOGGER.log(e.getMessage());
                }
            });
            newThread.start();
            try {
                newThread.join();
            } catch (InterruptedException e) {
                LOGGER.log(e.getMessage());
            }
            RESOURCES.deleteTemp();
        }
    }



    public void runScraper(List<String> links) {
        String threadId = String.valueOf(Thread.currentThread().getId());

        // Start chrome
        BrowserController controller = new BrowserController(settings.isHeadless());

        int realTotal = 0;

        LOGGER.log("(Thread "+ threadId +"): Scraping for the following links: " + links);

        if(!controller.initialize()) {
            return;
        }

        controller.wait(5);

        // Initialize the hashmap to keep track of each page on this thread
        HashMap<String, List<PostData>> allData = new HashMap<>();
        DUMP_MANAGER.addContent(allData);

        for(String link : links) {
            // Check if the task has been cancelled
            if(CANCELLED_TASK) {
                LOGGER.log("(Thread "+ threadId+") Task has been cancelled");
                break;
            }

            // Set lower and upper bounds for this page
            int count = 0;
            int total = settings.getAmountOfPosts();

            // Extract the page name from its link
            String[] subsectionOfLink = link.split("/");
            String pageName = subsectionOfLink[subsectionOfLink.length - 1];
            if(pageName.equals("")) pageName = subsectionOfLink[subsectionOfLink.length-2];

            // Create a list of postdata for each post on this page
            List<PostData> pageData = new ArrayList<>();
            allData.put(link, pageData);

            // Get the link to the page
            controller.getDriver().get(link);

            controller.wait(10);

            // Find the main feed of posts
            WebElement mainFeed = controller.getDriver().findElement(getSelector("containers.div.role.main")).findElement(getSelector("containers.div.class.post_feed"));

            List<WebElement> posts = mainFeed.findElements(getSelector("containers.div.class.article"));

            //Keep scrolling until you have either reached the total amount of posts on this page or the maximum from the settings
            int lastSize = 0;
            while (posts.size() < settings.getAmountOfPosts() && posts.size() != lastSize)  {
                lastSize = posts.size();
                controller.runJavaScript("window.scroll(0,document.body.scrollHeight);");
                controller.wait(10);
                posts = mainFeed.findElements(getSelector("containers.div.class.article"));
            }

            // Keep a reference to the mainpage
            String mainPage = controller.getDriver().getWindowHandle();

            // Start looping through all the loaded posts
            for(int i = 0; i < posts.size() && i < settings.getAmountOfPosts(); i++) {
                // If the task has been set to cancel, break out
                if(CANCELLED_TASK) {
                    break;
                }

                WebElement post = posts.get(i);
                controller.runJavaScript("arguments[0].style.border='1px solid red'", post);
                controller.wait(30);

                // Attempt to retrieve the post's link from the post's content
                List<WebElement> linkRedirect = post.findElements(getSelector("buttons.a.role.link"));
                if (linkRedirect.isEmpty()) {
                    total--;
                    LOGGER.log("(Thread "+ threadId+") " + pageName + ": post cannot be scraped (link not found) [" + count + "/" + total + "]");
                    continue;
                }

                String redirect = null;

                // If one of the links contains /posts/ then it is the right link
                for(WebElement we :linkRedirect) {
                    highlightWebElement(we, controller);
                    if(we.getAttribute("href") != null && we.getAttribute("href").contains("/posts/")) {
                        redirect = we.getAttribute("href");
                        break;
                    }
                }
                // If no link has been found, skip the post
                if(redirect == null) {
                    total--;
                    LOGGER.log("(Thread "+ threadId+") " + pageName + ": post cannot be scraped (link not found) [" + count + "/" + total + "]");
                    continue;
                }

                // Remove the comment id
                controller.openNewTab(redirect.split("\\?", 2)[0]);

                // Open the post in a new tab and try to scrape it
                // This only works on article style posts
                try {
                    FacebookPost postContainer = new FacebookPost(RESOURCES.getResource("containers.div.class.post"), controller);
                    if (postContainer.isNull()) {
                        total--;
                        LOGGER.log("(Thread "+ threadId+") " + pageName + ": post cannot be scraped (not article style post) [" + count + "/" + total + "]");
                        controller.runJavaScript("window.close()");
                        controller.getDriver().switchTo().window(mainPage);
                        continue;
                    }

                    // Create a postdata object for this post
                    PostData postData = new PostData(controller.getDriver().getCurrentUrl());

                    // Start scraping all the data
                    if (settings.isSavingContent()) postData.setContent(postContainer.getContent());
                    if (settings.isSavingReactions()) postData.setReactions(postContainer.getReactions());
                    if (settings.isSavingComments())
                        postData.setComments(postContainer.getComments(settings.getAmountOfComments()));
                    if (settings.isSavingShares()) postData.setShares(postContainer.getShares(settings.getAmountOfShares()));
                    count++;
                    LOGGER.log("(Thread "+ threadId+") " + pageName + ": " + "post scraped successfully [" + count + "/" + total + "]");

                    //Everytime we read a post, we add it to the list of posts read
                    unknownErrorsInARow = 0;
                    pageData.add(postData);

                } catch (Exception e) {
                    total--;
                    LOGGER.log("(Thread "+ threadId+") " + pageName + ": post cannot be scraped (unknown error) [" + count + "/" + total + "]");
                    unknownErrorsInARow++;
                    // If there have been too many unknown errors in a row, then stop scraping
                    if (unknownErrorsInARow > FacebookWebScraper.MAX_ERRORS_BEFORE_STOP) {
                        CANCELLED_TASK = true;
                    }
                }

                controller.runJavaScript("window.close()");
                controller.getDriver().switchTo().window(mainPage);
            }
            realTotal += count;
        }

        JSONObject encodedScrape = Encoder.encodePages(allData);

        FacebookWebScraper.PERSISTENCE_SERVICE.saveJSONFile(encodedScrape, settings.getOutput(), "content");
        CONTROLLER_POOL.kill(controller);

        LOGGER.log("(Thread " + threadId + "): Task completed with " + realTotal + " posts scraped");
        LOGGER.outputLog();


    }
}
