package sajad.wazin.mcgill.ca.utils;

import org.openqa.selenium.WebElement;
import sajad.wazin.mcgill.ca.chrome.BrowserController;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class SeleniumUtils {
    public static String getCSSAsString(String htmlElement, String cssTag, String value) {
        return htmlElement +
                "[" +
                cssTag +
                "=\"" +
                value +
                "\"]";
    }

    public static int parseReactionNumber(String number){
        StringBuilder parsedNumber = new StringBuilder();
        for (int i = 0; i < number.length(); i++) {
            if(number.charAt(i) == '.') {
                parsedNumber.append(number.charAt(i+1));
                if(number.charAt(i+2) == 'K') {
                    parsedNumber.append("00");
                }
                else parsedNumber.append("00000");
                break;
            }
            if(number.charAt(i) == 'K') {
                parsedNumber.append("000");
                break;
            }
            else if (number.charAt(i) == 'M') {
                parsedNumber.append("000000");
                break;
            }
            parsedNumber.append(number.charAt(i));
        }
        return Integer.parseInt(parsedNumber.toString());
    }

    public static int parseFormattedNumber(String number){
        StringBuilder parsedNumber = new StringBuilder();
        for (int i = 0; i < number.length(); i++) {
            if(number.charAt(i) > 47 || number.charAt(i) < 58) parsedNumber.append(number.charAt(i));
        }
        return Integer.parseInt(parsedNumber.toString());
    }

    public static void highlightWebElement(WebElement webElement, BrowserController controller) {
        controller.runJavaScript("arguments[0].style.border='1px solid red'", webElement);
    }
}
