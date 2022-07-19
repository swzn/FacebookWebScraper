package sajad.wazin.mcgill.ca.chrome;

import sajad.wazin.mcgill.ca.FacebookWebScraper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class ControllerPool {

    private static ControllerPool controllerPool = new ControllerPool();
    private List<BrowserController> controllers;

    private ControllerPool(){
        controllers = new ArrayList<>();
    }

    public static ControllerPool getControllerPool(){
        return controllerPool;
    }

    public void addController(BrowserController controller){
        controllers.add(controller);
    }

    public void kill(){
        for (BrowserController controller : controllers) {
            try {
                controller.getDriver().quit();
            } catch (Exception e)
            {
                FacebookWebScraper.LOGGER.log("Could not kill a chromedriver, check current running tasks");
            }
        }
    }

    public void kill(BrowserController controller) {
        if(controllers.contains(controller)) {
            try {
                controller.getDriver().quit();
                // Setting the controller to null for garbage collection
                controller = null;
            } catch (Exception e)
            {
                FacebookWebScraper.LOGGER.log("Could not kill a chromedriver processes, check task manager");
            }
        }
    }
}
