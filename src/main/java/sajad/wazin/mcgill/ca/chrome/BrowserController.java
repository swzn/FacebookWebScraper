package sajad.wazin.mcgill.ca.chrome;


import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import sajad.wazin.mcgill.ca.FacebookWebScraper;
import sajad.wazin.mcgill.ca.utils.SeleniumUtils;

import java.time.Duration;
import java.util.ArrayList;


/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca*/


public class BrowserController {

    /*
    * This class is a Wrapper on the ChromeDriver class from Selenium.
    * It serves as an object that has access to chains of methods from the Selenium API to make using the driver
    * easier.
    * */

    private final WebDriver driver;
    private final ChromeOptions options;
    private ArrayList<String> tabs;

    /*
    * Public constructor that will launch Chrome on instantiation.
    * The "headless" boolean will hide Chrome if true.
    * */
    public BrowserController(boolean headless) {
        System.setProperty("webdriver.chrome.driver", FacebookWebScraper.RESOURCES.getChromeDriverPath());
        options = new ChromeOptions();
        options.addArguments("--disable-notifications", "--disable-gpu", "--disable-extensions", "--disable-logging", "--log-level=3", "--disable-logging-redirect");
        if(headless) options.addArguments("--headless");
        driver = new ChromeDriver(options);
    }

    /*
    * Prepares the window to be used for scraping. This assumes that the login is a valid Facebook login and the case
    * where the credentials are invalid is not handled by this software.
    * */
    public void initialize(){
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.get("https://facebook.com");

        getFirstElementById("email").sendKeys(FacebookWebScraper.LOGIN.getEmail());
        getFirstElementById("pass").sendKeys(FacebookWebScraper.LOGIN.getPassword());

        getFirstElementByName("login").click();


    }

    public WebElement getFirstElementById(String id){
        return driver.findElements(By.id(id)).get(0);
    }

    public WebElement getFirstElementByName(String name) {
        return driver.findElements(By.name(name)).get(0);
    }


    public WebElement getFirstElementByCSS(String htmlElement, String cssTag, String value){
        this.wait(10);
        return driver.findElement(By.cssSelector(SeleniumUtils.getCSSAsString(htmlElement,cssTag,value)));
    }

    public void wait(int seconds) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(seconds));
        try {
            Thread.sleep(2 * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void sleep(long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public WebDriver getDriver() {
        return driver;
    }


    public void openNewTab(String URL) {
        ((JavascriptExecutor)driver).executeScript("window.open()");
        tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(tabs.size() - 1));
        driver.get(URL);
        wait(5);
    }

    public Object runJavaScript(String command){
        return ((JavascriptExecutor)driver).executeScript(command, "");
    }

    public Object runJavaScript(String command, WebElement element){
        return ((JavascriptExecutor)driver).executeScript(command, element);
    }

}
