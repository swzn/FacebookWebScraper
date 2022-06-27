package sajad.wazin.mcgill.ca;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import sajad.wazin.mcgill.ca.chrome.BrowserController;
import sajad.wazin.mcgill.ca.gui.ApplicationFrame;
import sajad.wazin.mcgill.ca.gui.LoginButton;
import sajad.wazin.mcgill.ca.scraper.ContentScraper;
import sajad.wazin.mcgill.ca.scraper.ContentScraperSettings;
import sajad.wazin.mcgill.ca.utils.SeleniumUtils;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import static sajad.wazin.mcgill.ca.utils.SeleniumUtils.*;


public class FacebookWebScraper extends Application {

    public static int MIN_POSTS = 5;
    public static String EMAIL = "davidodson69@gmail.com";
    public static String PASS = "Saji@wazin2001";
    public static String ICON_PATH = FacebookWebScraper.class.getClassLoader().getResource("fws_icon64x64.png").toString();
    public static InputStream PROPERTIES_STREAM = FacebookWebScraper.class.getClassLoader().getResourceAsStream("FacebookWebScraper.properties");
    private static BrowserController controller;

    public static void main(String[] args) {
        controller = new BrowserController();
        controller.initialize();
        controller.wait(5);
        controller.getDriver().get("https://www.facebook.com/instagram/");

        WebElement toolbar = controller.getDriver().findElement(By.cssSelector(getCSSAsString("div","class","rq0escxv l9j0dhe7 du4w35lb j83agx80 taijpn5t gs1a9yip owycx6da btwxx1t3 cddn0xzi dsne8k7f pfnyh3mw")));
        highlightWebElement(toolbar, controller);

        WebElement button = toolbar.findElements(By.cssSelector(getCSSAsString("div","aria-label","Like"))).get(0);
        highlightWebElement(button, controller);
        button.click();

        WebElement relatedPages = controller.getDriver().findElement(By.cssSelector(getCSSAsString("div","class","sjgh65i0 iwynql29 s3d9ay3k rq0escxv dflh9lhu dati1w0a k4urcfbm")));
        highlightWebElement(relatedPages,controller);

        List<WebElement> allSuggestions = controller.getDriver().findElements(By.cssSelector(getCSSAsString("div","class","kb5gq1qc pfnyh3mw hpfvmrgz qdtcsgvi oi9244e8 t7l9tvuc")));
        allSuggestions.forEach(object -> highlightWebElement(object, controller));

        String mainPage = controller.getDriver().getWindowHandle();
        for(WebElement we : allSuggestions) {
            String link = we.findElement(By.cssSelector(getCSSAsString("a","role","link"))).getAttribute("href");

            controller.openNewTab(link);
            WebElement toolbar1 = controller.getDriver().findElement(By.cssSelector(getCSSAsString("div","class","rq0escxv l9j0dhe7 du4w35lb j83agx80 taijpn5t gs1a9yip owycx6da btwxx1t3 cddn0xzi dsne8k7f pfnyh3mw")));
            highlightWebElement(toolbar1, controller);

            WebElement button1 = toolbar1.findElements(By.cssSelector(getCSSAsString("div","aria-label","Like"))).get(0);
            highlightWebElement(button1, controller);
            button1.click();

            controller.runJavaScript("window.close()");
            controller.getDriver().switchTo().window(mainPage);
        }

        /*ContentScraperSettings settings = new ContentScraperSettings(true, true, false, true, 20, 25);
        settings.setInput(new File("C:\\Users\\Sajad\\Desktop\\inpout.txt"));
        ContentScraper scraper = new ContentScraper(settings, controller);
        scraper.runScraper();*/



        /*controller.getDriver().get("https://www.facebook.com/nba/");

        WebElement mainFeed = controller.getDriver().findElement(By.cssSelector(SeleniumUtils.getCSSAsString("div", "role", "main"))).findElement(By.cssSelector(SeleniumUtils.getCSSAsString("div","role","main")));

        ArrayList<WebElement> posts = new ArrayList<>(mainFeed.findElements(By.cssSelector(SeleniumUtils.getCSSAsString("div","role","article"))));
        while (posts.size() < 12)  {
            controller.runJavaScript("window.scroll(0,document.body.scrollHeight);");
            controller.wait(10);
            posts = new ArrayList<>(mainFeed.findElements(By.cssSelector(SeleniumUtils.getCSSAsString("div","role","article"))));
            System.out.println(posts.size());
        }

        String mainPage = controller.getDriver().getWindowHandle();
        for(WebElement post : posts) {
            controller.wait(2);
            List<WebElement> linkRedirect = post.findElements(By.cssSelector(SeleniumUtils.getCSSAsString("a", "class", ResourcesManager.RESOURCES.getResource("buttons.a.open_tab"))));
            if(linkRedirect.isEmpty()) {
                System.out.println("This post cannot be opened.");
                continue;
            }
            controller.openNewTab(linkRedirect.get(0).getAttribute("href"));
            FacebookPost postContainer = new FacebookPost(SeleniumUtils.getCSSAsString("div", "class", ResourcesManager.RESOURCES.getResource("containers.div.class.post")), controller);
            PostData currentStats = new PostData("https://www.facebook.com/nba/");

            currentStats.setContent(postContainer.getContent());
            currentStats.setReactions(postContainer.getReactions());
            currentStats.setComments(postContainer.getComments(3));
            currentStats.setShares(postContainer.getShares());

            controller.runJavaScript("window.close()");
            controller.getDriver().switchTo().window(mainPage);
        }
        controller.getDriver().findElement(By.cssSelector(SeleniumUtils.getCSSAsString("div", "role", "main")));*/

        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("FacebookWebScraper");
        stage.getIcons().add(new Image(ICON_PATH));

        // Instantiating the scene elements
        ApplicationFrame application = new ApplicationFrame(30, 10, 5);
        application.initialize();

        //Loading the scene container
        Pane root = new Pane();
        root.setPrefSize(960,600);


        //Appending the application frame
        root.getChildren().add(application.getApplicationGrid());


        //Initializing Event Handlers
        Scene fbScene = new Scene(root);
        fbScene.setOnKeyPressed(LoginButton.getEnterKeyEvent(application.getLoginButton()));

        //Launching the scene
        stage.setScene(fbScene);
        stage.show();
    }
}