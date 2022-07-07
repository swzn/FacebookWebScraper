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

import sajad.wazin.mcgill.ca.gui.ApplicationFrame;
import sajad.wazin.mcgill.ca.gui.LoginButton;
import sajad.wazin.mcgill.ca.scraper.SuggestionsScraper;
import sajad.wazin.mcgill.ca.scraper.settings.SuggestionsScraperSettings;
import sajad.wazin.mcgill.ca.scraper.interactions.InteractionModeEnum;
import sajad.wazin.mcgill.ca.scraper.search.SearcherEnum;

import java.io.File;


public class FacebookWebScraper extends Application {

    public static int MIN_POSTS = 5;
    public static String EMAIL = "davidodson69@gmail.com";
    public static String PASS = "Saji@wazin2001";


    public static ResourcesManager RESOURCES = ResourcesManager.getResourceManager();

    public static void main(String[] args) {

        SuggestionsScraperSettings settings = new SuggestionsScraperSettings(InteractionModeEnum.NULL_INTERACTION,
                SearcherEnum.BFS, 2, 2);

        SuggestionsScraper scraper = new SuggestionsScraper(settings);
        settings.setInput(new File("C:\\Users\\Sajad\\Desktop\\input.txt"));
        scraper.runScraper();

        /*ContentScraperSettings settings = new ContentScraperSettings(true, true, false, true, 2, 5);
        settings.setInput(new File("C:\\Users\\Sajad\\Desktop\\input.txt"));
        ContentScraper scraper = new ContentScraper(settings);
        scraper.runScraper();*/

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