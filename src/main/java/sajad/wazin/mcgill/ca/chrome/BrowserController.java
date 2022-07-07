package sajad.wazin.mcgill.ca.chrome;


import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import sajad.wazin.mcgill.ca.FacebookWebScraper;
import sajad.wazin.mcgill.ca.utils.SeleniumUtils;

import java.time.Duration;
import java.util.ArrayList;


/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca*/


public class BrowserController {

    private final WebDriver driver;
    private final ChromeOptions options;
    private final int WAIT_TIME = 5;
    private WebDriverWait waiter;

    private ArrayList<String> tabs;

    public BrowserController(){
        System.setProperty("webdriver.chrome.driver", FacebookWebScraper.RESOURCES.getChromeDriverPath());
        options = new ChromeOptions();
        options.addArguments("--disable-notifications", "--disable-gpu", "--disable-extensions", "--disable-logging", "--log-level=3", "--disable-logging-redirect");
        driver = new ChromeDriver(options);
        waiter = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void initialize(){
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.get("https://facebook.com");

        getFirstElementById("email").sendKeys(FacebookWebScraper.EMAIL);
        getFirstElementById("pass").sendKeys(FacebookWebScraper.PASS);
        getFirstElementByName("login").click();
    }

    public WebElement getFirstElementById(String id){
        return driver.findElements(By.id(id)).get(0);
    }

    public WebElement getFirstElementByName(String name) {
        return driver.findElements(By.name(name)).get(0);
    }

    public WebElement getFirstElementByClass(String aClass) {
        this.wait(WAIT_TIME);
        return driver.findElements(By.className(aClass)).get(0);
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

    public void returnToHome(){
        getFirstElementByCSS("a", "aria-label", "Home").click();
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

    public WebDriverWait getWaiter() {
        return waiter;
    }
}
