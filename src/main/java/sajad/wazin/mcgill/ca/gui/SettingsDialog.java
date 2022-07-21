package sajad.wazin.mcgill.ca.gui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import sajad.wazin.mcgill.ca.FacebookWebScraper;
import sajad.wazin.mcgill.ca.utils.ResourcesManager;
import sajad.wazin.mcgill.ca.scraper.interactions.InteractionModeEnum;
import sajad.wazin.mcgill.ca.scraper.search.SearcherEnum;
import sajad.wazin.mcgill.ca.scraper.settings.ContentScraperSettings;
import sajad.wazin.mcgill.ca.scraper.ScraperEnum;
import sajad.wazin.mcgill.ca.scraper.settings.ScraperSettings;
import sajad.wazin.mcgill.ca.scraper.settings.SuggestionsScraperSettings;
import sajad.wazin.mcgill.ca.utils.FXUtils;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class SettingsDialog extends Stage {

    private ScraperEnum scraperType;
    private GridPane settingsPane;
    private Pane root;
    private ScraperSettings scraperSettings;

    /*
    * A settings dialog can only be instantiated once the scraper has already been selected
    * */
    public SettingsDialog(ScraperEnum scraperType){
        this.scraperType = scraperType;

        this.root = new Pane();
        this.settingsPane = new GridPane();

        // Set the preferred size of the dialog
        this.root.setPrefSize(600,350);

        // Make it look readable
        this.settingsPane.setPadding(new Insets(30, 30, 30, 30));
        this.settingsPane.setHgap(10);
        this.settingsPane.setVgap(5);

        // Initialize the settings dialog appropriate for the given scraper enum
        if(scraperType == ScraperEnum.CONTENT_SCRAPER) initializeContentsSettingsGrid();
        else initializeSuggestionsSettingsGrid();

        // Show the dialog
        this.root.getChildren().add(settingsPane);
        this.getIcons().add(new Image(ResourcesManager.ICON_PATH));
        this.setTitle("Choose scraping settings");
        this.initModality(Modality.APPLICATION_MODAL);
        this.setScene(new Scene(root));
    }

    private void initializeSuggestionsSettingsGrid() {

        // Create a dropdown that allows the user to choose between DFS and BFS only
        StringDropdown searchMode = new StringDropdown("Depth-First Search (DFS)", "Breadth-First Search (BFS)");
        searchMode.setPromptText("Choose search mode");

        /* For contributors: Interaction modes will alter how a user interacts with a page suggestion. As of now, only
        "Follow-only" is implemented (NULL_INTERACTION). If one chooses to implement other forms of interactions
        uncomment this section.

        StringDropdown interactionMode = new StringDropdown("Follow-only", "Follow + Post Interactions", "Experimental");
        interactionMode.setPromptText("Choose interaction mode");*/


        // Show it on the dialog
        settingsPane.add(searchMode, 0, 0);

        // Create integer-only text fields for depth and child count
        TextField childCount = new TextField();
        childCount.setMaxWidth(120);
        childCount.setPromptText("Max children");


        TextField depthField = new TextField();
        depthField.setMaxWidth(120);
        depthField.setPromptText("Max depth");

        FXUtils.setIntegerListener(childCount);
        FXUtils.setIntegerListener(depthField);

        settingsPane.add(childCount, 0, 1);
        settingsPane.add(depthField, 1, 1);


        /*
         * Create a Save button that will validate scraper settings and instantiate a ScraperSettings object if all
         * settings are valid
         */

        Button saveSettings = new Button("Save");
        saveSettings.setOnAction(actionEvent -> {

            // Check to see if the integer text fields are empty
            if(depthField.getText().isEmpty() || childCount.getText().isEmpty()) {
                new DialogBox("Please enter valid settings!").show();
                return;
            }

            int maxDepth = Integer.parseInt(depthField.getText());
            int maxGrowth = Integer.parseInt(childCount.getText());

            // Make sure the scraper settings are good enough to actually scrape something
            if(maxDepth < 1 || maxGrowth < 1) {
                new DialogBox("Please enter valid settings!").show();
                return;
            }

            /* Suggestions Settings Constructor takes the following input
            * (InteractionModeEnum, SearcherEnum, int (depth), int (growth)) */
            this.scraperSettings = new SuggestionsScraperSettings(
                    InteractionModeEnum.NULL_INTERACTION,
                    SearcherEnum.getSearcher(searchMode.getSelectionModel().getSelectedItem()),
                    maxDepth,
                    maxGrowth
            );

            this.hide();
        });

        settingsPane.add(saveSettings, 0, 5);

        // Create a simple "Reset" button that will reset all chosen settings

        Button cancel = new Button("Reset");
        cancel.setOnAction(actionEvent -> {
            childCount.setText("");
            depthField.setText("");
            searchMode.valueProperty().set(null);
            this.hide();
        });

        settingsPane.add(cancel, 1, 5);

    }

    private void initializeContentsSettingsGrid() {

        // Create scraping content checkbox
        CheckBox content = new CheckBox("Scrape post contents");

        // Create scraping comments checkbox & integer textfield
        CheckBox comments = new CheckBox("Scrape post comments");
        TextField commentsAmount = new TextField();
        commentsAmount.setPromptText("Max # comments");
        FXUtils.setIntegerListener(commentsAmount);

        // If the user wants to scrape comments, ask for a maximum number of comments
        comments.setOnAction(actionEvent -> {
            if(!comments.isSelected()) {
                settingsPane.getChildren().remove(commentsAmount);
            }
            else {
                commentsAmount.setText("");
                settingsPane.add(commentsAmount, 1,1);
            }
        });

        // Create a shares checkbox & integer textfield
        CheckBox shares = new CheckBox("Scrape post shares");
        TextField sharesAmount = new TextField();
        sharesAmount.setPromptText("Max # shares");
        FXUtils.setIntegerListener(sharesAmount);

        // If the user wants to scrape shares, ask for a maximum number of shares
        shares.setOnAction(actionEvent -> {
            if(!shares.isSelected()) {
                settingsPane.getChildren().remove(sharesAmount);
            }
            else {
                sharesAmount.setText("");
                settingsPane.add(sharesAmount, 1, 2);
            }
        });

        // Create a checkbox to scrape reactions
        CheckBox reactions = new CheckBox("Scrape post reactions");

        // Ask for a maximum number of posts
        TextField posts = new TextField();
        posts.setPromptText("Max # posts");
        FXUtils.setIntegerListener(posts);

        // Ask for the number of instances (optional)
        TextField instanceCount = new TextField();
        instanceCount.setPromptText("# instances");
        FXUtils.setIntegerListener(instanceCount);

        settingsPane.add(content, 0, 0);
        settingsPane.add(comments, 0, 1);
        settingsPane.add(shares, 0, 2);
        settingsPane.add(reactions, 0, 3);
        settingsPane.add(posts, 0, 4);
        settingsPane.add(instanceCount, 0, 5);


        // Create a save button that will validate settings before continuing
        Button saveSettings = new Button("Save");
        saveSettings.setOnAction(actionEvent -> {

            // Make sure all mandatory integer textfields have an input
            if(posts.getText().isEmpty() ||
                    sharesAmount.getText().isEmpty() && shares.isSelected() ||
                    commentsAmount.getText().isEmpty() && comments.isSelected()) {
                new DialogBox("Please enter valid settings!").show();
                return;
            }


            // For the GUI version of the app, the app builder can set a minimum number of posts (recommended >=3)
            int postsVal = Integer.parseInt(posts.getText());

            if(postsVal < FacebookWebScraper.MIN_POSTS) {
                new DialogBox("You have to scrape at least " + FacebookWebScraper.MIN_POSTS + " posts!").show();
                return;
            }

            // If all settings are valid, then instantiate a ContentScraperSettings object
            this.scraperSettings = new ContentScraperSettings(content.isSelected(),
                    comments.isSelected(),
                    shares.isSelected(),
                    reactions.isSelected(),
                    postsVal);

            if(shares.isSelected()) ((ContentScraperSettings) this.scraperSettings).setAmountOfShares(Integer.parseInt(sharesAmount.getText()));
            if(comments.isSelected()) ((ContentScraperSettings) this.scraperSettings).setAmountOfComments(Integer.parseInt(commentsAmount.getText()));
            if(!instanceCount.getText().isEmpty()) ((ContentScraperSettings) this.scraperSettings).setInstanceCount(Integer.parseInt(instanceCount.getText()));

            this.hide();
        });

        settingsPane.add(saveSettings, 0, 7);

        // Reset button that clears all inputs
        Button cancel = new Button("Reset");
        cancel.setOnAction(actionEvent -> {
            for (Node node : settingsPane.getChildren()) {
                if(node instanceof TextField) ((TextField) node).setText("");
                else if (node instanceof CheckBox) ((CheckBox) node).setSelected(false);
            }
            settingsPane.getChildren().remove(sharesAmount);
            settingsPane.getChildren().remove(commentsAmount);
        });
        settingsPane.add(cancel, 1, 7);
    }


    public ScraperSettings getScraperSettings(){
        return scraperSettings;
    }
}
