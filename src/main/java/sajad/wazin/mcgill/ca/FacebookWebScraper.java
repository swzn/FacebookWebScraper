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


import sajad.wazin.mcgill.ca.chrome.ControllerPool;
import sajad.wazin.mcgill.ca.gui.ApplicationFrame;
import sajad.wazin.mcgill.ca.gui.LoginButton;
import sajad.wazin.mcgill.ca.persistence.PersistenceService;
import sajad.wazin.mcgill.ca.scraper.ContentScraper;
import sajad.wazin.mcgill.ca.scraper.SuggestionsScraper;
import sajad.wazin.mcgill.ca.scraper.interactions.InteractionModeEnum;
import sajad.wazin.mcgill.ca.scraper.search.SearcherEnum;
import sajad.wazin.mcgill.ca.scraper.settings.ContentScraperSettings;
import sajad.wazin.mcgill.ca.scraper.settings.SuggestionsScraperSettings;
import sajad.wazin.mcgill.ca.utils.DumpManager;
import sajad.wazin.mcgill.ca.utils.Logger;
import sajad.wazin.mcgill.ca.utils.Login;
import sajad.wazin.mcgill.ca.utils.ResourcesManager;



public class FacebookWebScraper extends Application {

    public static final int MAX_ERRORS_BEFORE_STOP = 10;
    public static int MIN_POSTS = 5;

    public static Logger LOGGER = new Logger();

    //Singleton instances
    public static DumpManager DUMP_MANAGER = DumpManager.getDumpManager();
    public static ResourcesManager RESOURCES = ResourcesManager.getResourceManager();
    public static PersistenceService PERSISTENCE_SERVICE = PersistenceService.getPersistenceService();
    public static ControllerPool CONTROLLER_POOL = ControllerPool.getControllerPool();

    public static Login LOGIN = new Login();
    public static boolean CANCELLED_TASK = false;

    public static void main(String[] args) {

        launch(args);

    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("FacebookWebScraper");
        stage.getIcons().add(new Image(ResourcesManager.ICON_PATH));

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