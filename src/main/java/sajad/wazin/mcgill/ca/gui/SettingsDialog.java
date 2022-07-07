package sajad.wazin.mcgill.ca.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sajad.wazin.mcgill.ca.FacebookWebScraper;
import sajad.wazin.mcgill.ca.ResourcesManager;
import sajad.wazin.mcgill.ca.scraper.settings.ContentScraperSettings;
import sajad.wazin.mcgill.ca.scraper.ScraperEnum;
import sajad.wazin.mcgill.ca.scraper.settings.ScraperSettings;
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

    public SettingsDialog(ScraperEnum scraperType){
        this.scraperType = scraperType;

        this.root = new Pane();
        this.settingsPane = new GridPane();

        this.root.setPrefSize(400,200);

        this.settingsPane.setPadding(new Insets(30, 30, 30, 30));
        this.settingsPane.setHgap(10);
        this.settingsPane.setVgap(5);

        if(scraperType == ScraperEnum.POST_CONTENT || scraperType == ScraperEnum.GROUPS_CONTENT) initializeContentsSettingsGrid();
        else initializeSuggestionsSettingsGrid();

        this.root.getChildren().add(settingsPane);
        this.getIcons().add(new Image(ResourcesManager.ICON_PATH));
        this.setTitle("Choose scraping settings");
        this.setScene(new Scene(root));
    }

    private void initializeSuggestionsSettingsGrid() {
        StringDropdown searchMode = new StringDropdown("Depth-First Search (DFS)", "Breadth-First Search (BFS)");
        searchMode.setPromptText("Choose search mode");

        StringDropdown interactionMode = new StringDropdown("Follow-only", "Follow + Post Interactions", "Experimental");
        interactionMode.setPromptText("Choose interaction mode");
        settingsPane.add(searchMode, 0, 0);
        StringDropdown childCount = new StringDropdown("1", "2", "3", "4", "5");
        childCount.setMaxWidth(120);
        childCount.setPromptText("# children");

        TextField depthField = new TextField();
        depthField.setMaxWidth(120);
        depthField.setPromptText("Max depth");
        FXUtils.setIntegerListener(depthField);

        settingsPane.add(childCount, 0, 1);
        settingsPane.add(depthField, 1, 1);
        settingsPane.add(interactionMode, 0,2);

        Button saveSettings = new Button("Save");
        saveSettings.setOnAction(actionEvent -> {
            /*this.scraperSettings = new SuggestionsScraperSettings(
                    InteractionModeEnum.getInteractionMode(interactionMode.getSelectionModel().getSelectedItem()),
                    SearcherEnum.getSearcher(searchMode.getSelectionModel().getSelectedItem()));*/
            this.hide();
        });
        settingsPane.add(saveSettings, 0, 5);

        Button cancel = new Button("Cancel");
        cancel.setOnAction(actionEvent -> {
            this.hide();
        });
        settingsPane.add(cancel, 1, 5);
    }

    private void initializeContentsSettingsGrid() {
        CheckBox content = new CheckBox("Scrape post contents");
        CheckBox comments = new CheckBox("Scrape post comments");
        CheckBox shares = new CheckBox("Scrape post shares");
        CheckBox reactions = new CheckBox("Scrape post reactions");

        TextField posts = new TextField();
        posts.setPromptText("Amount of posts to scrape");
        FXUtils.setIntegerListener(posts);

        settingsPane.add(content, 0, 0);
        settingsPane.add(comments, 0, 1);
        settingsPane.add(shares, 0, 2);
        settingsPane.add(reactions, 0, 3);
        settingsPane.add(posts, 0, 4);

        Button saveSettings = new Button("Save");
        saveSettings.setOnAction(actionEvent -> {
            int postsVal = Integer.parseInt(posts.getText());
            if(postsVal < FacebookWebScraper.MIN_POSTS) {
                new DialogBox("You have to scrape at least 5 posts!").show();
                return;
            }
            this.scraperSettings = new ContentScraperSettings(content.isSelected(), comments.isSelected(), shares.isSelected(), reactions.isSelected(), postsVal, 0 /*Change this*/);
            System.out.println(scraperSettings.toString());
            this.hide();
        });
        settingsPane.add(saveSettings, 0, 5);

        Button cancel = new Button("Cancel");
        cancel.setOnAction(actionEvent -> {
            this.hide();
        });
        settingsPane.add(cancel, 1, 5);
    }


    public ScraperSettings getScraperSettings(){
        return scraperSettings;
    }
}
