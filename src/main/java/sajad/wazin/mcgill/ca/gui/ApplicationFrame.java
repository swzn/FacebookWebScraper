package sajad.wazin.mcgill.ca.gui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import sajad.wazin.mcgill.ca.FacebookWebScraper;
import sajad.wazin.mcgill.ca.scraper.ContentScraper;
import sajad.wazin.mcgill.ca.scraper.ScraperEnum;
import sajad.wazin.mcgill.ca.scraper.SuggestionsScraper;
import sajad.wazin.mcgill.ca.scraper.settings.ContentScraperSettings;
import sajad.wazin.mcgill.ca.scraper.settings.ScraperSettings;
import sajad.wazin.mcgill.ca.scraper.settings.SuggestionsScraperSettings;

import java.io.File;
import java.nio.file.Path;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class ApplicationFrame {

    private GridPane applicationGrid;
    private TextField userField;
    private PasswordField passField;
    private LoginButton loginButton;
    private SettingsDialog settingsDialog;

    private GridPane loggerMessages;

    private File selectedInput;
    private File selectedOutput;

    public ApplicationFrame(int insets, int hGap, int vGap){
        /*
         * The ApplicationFrame is the main container for the FacebookWebScraper
         */
        applicationGrid = new GridPane();
        applicationGrid.setPadding(new Insets(insets, insets, insets, insets));
        applicationGrid.setHgap(hGap);
        applicationGrid.setVgap(vGap);
    }

    private void generateLoginPrompt() {
        // Creating the username and password fields
        userField = new TextField();
        userField.setPromptText("Username");
        passField = new PasswordField();
        passField.setPromptText("Password");

        // Creating the login button
        loginButton = new LoginButton(userField, passField);

        // Appending them to the grid
        applicationGrid.add(userField, 0, 0);
        applicationGrid.add(passField, 0, 1);
        applicationGrid.add(loginButton, 1,1);
    }

    private void generateScrapingDropdown() {
        // Create the dropdown that allows users to choose the scrapers they want
        StringDropdown scrapingModes = new StringDropdown(
                "Posts Content Scraper",
                "Posts Suggestions Scraper"
        );
        scrapingModes.setPromptText("Choose a scraping mode");

        // Prevent the users from using it if there are no credentials
        scrapingModes.setOnShown(actionEvent -> {
            if(!loginButton.isLocked()) {
                new DialogBox("Please lock your credentials first").show();
                scrapingModes.hide();
            }
        });

        // Creating a button that shows the current settings
        Button viewSettingsButton = new Button("View Settings");
        viewSettingsButton.setOnAction(actionEvent -> {
            settingsDialog.show();
        });

        // Showing the settings pop-up
        scrapingModes.setOnAction(actionEvent -> {
            if(!applicationGrid.getChildren().contains(viewSettingsButton)) {
                applicationGrid.add(viewSettingsButton, 1, 2);
            }
            generateFileChoosers();
            generateScrapeButton();
            settingsDialog = new SettingsDialog(ScraperEnum.getScraper(scrapingModes.getSelectionModel().getSelectedItem()));
            settingsDialog.show();
        });

        // Appending the dropdown to the grid
        applicationGrid.add(scrapingModes, 0, 2);

    }

    private void generateFileChoosers() {
        // Get root directory from the system properties
        String rootPath = System.getProperty("user.home") + File.separator + "Desktop";

        // Create an input file chooser ".txt" files
        FileChooser inputChooser = new FileChooser();
        inputChooser.setTitle("Choose input");
        inputChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text format", "*.txt"));
        inputChooser.setInitialDirectory(new File(rootPath));

        // Create an output file chooser for ".json" Files
        DirectoryChooser outputChooser = new DirectoryChooser();
        outputChooser.setTitle("Choose output");
        outputChooser.setInitialDirectory(new File(rootPath));

        // Create a field that shows the input path
        TextField inputPath = new TextField("Select an input file...");
        inputPath.setEditable(false);
        inputPath.setPrefSize(500, inputPath.getHeight());

        // Create a text field that shows the output path
        TextField outputPath = new TextField("Select an output directory...");
        outputPath.setEditable(false);
        outputPath.setPrefSize(500, outputPath.getHeight());

        // This button triggers the input file chooser
        Button inputSelector = new Button("Select input");
        inputSelector.setOnAction(actionEvent -> {
            selectedInput = inputChooser.showOpenDialog(this.getApplicationGrid().getScene().getWindow());
            if(selectedInput != null) inputPath.setText(selectedInput.getAbsolutePath());
        });

        // This button triggers the output file chooser
        Button outputSelector = new Button("Select output");
        outputSelector.setOnAction(actionEvent -> {
            selectedOutput = outputChooser.showDialog(this.getApplicationGrid().getScene().getWindow());
            if(selectedOutput != null) outputPath.setText(selectedOutput.getAbsolutePath());
        });

        // Clear the current file paths and elements (if they exist)
        applicationGrid.getChildren().remove(inputPath);
        applicationGrid.getChildren().remove(inputSelector);
        applicationGrid.getChildren().remove(outputPath);
        applicationGrid.getChildren().remove(outputSelector);

        // Add new file paths and elements
        applicationGrid.add(inputPath, 0, 3);
        applicationGrid.add(inputSelector, 1, 3);
        applicationGrid.add(outputPath, 0, 4);
        applicationGrid.add(outputSelector, 1, 4);
    }

    private void generateScrapeButton(){
        Button scrapeButton = new Button("Start Scraping");

        ScrollPane loggingPane = new ScrollPane();
        loggingPane.setPrefSize(300, 200);
        loggingPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        loggerMessages = new GridPane();
        loggingPane.setContent(loggerMessages);

        CheckBox headless = new CheckBox("GUI-less Chrome");

        scrapeButton.setOnAction(actionEvent -> {
            if(!getLoginButton().isLocked()) {
                new DialogBox("Please lock your credentials!").show();
            }
            else if(settingsDialog.getScraperSettings() == null) {
                new DialogBox("Please choose valid settings!").show();
            }
            else if(selectedInput == null) {
                new DialogBox("Please choose an input file!").show();
            }
            else if(selectedOutput == null) {
                new DialogBox("Please choose an output file!").show();
            }
            else {
                for(Node node : applicationGrid.getChildren()) {
                    node.setDisable(true);
                }
                applicationGrid.add(loggingPane,0 ,7);
                FacebookWebScraper.LOGGER.setLogger(loggerMessages);
                FacebookWebScraper.LOGGER.log("GUI disabled");
                FacebookWebScraper.LOGGER.log("Scraping will begin shortly...");

                ScraperSettings currentSettings = settingsDialog.getScraperSettings();
                currentSettings.setHeadless(headless.isSelected());
                currentSettings.setInput(selectedInput);
                currentSettings.setOutput(Path.of(selectedOutput.toURI()));

                Thread scraperExecutor = new Thread (() -> {
                    if (currentSettings instanceof ContentScraperSettings) {
                        ContentScraper scraper = new ContentScraper(currentSettings);
                        scraper.runScraper();
                    } else if (currentSettings instanceof SuggestionsScraperSettings) {
                        SuggestionsScraper scraper = new SuggestionsScraper(currentSettings);
                        scraper.runScraper();
                    }
                });
                scraperExecutor.start();
            }
        });
        applicationGrid.add(headless, 0, 5);
        applicationGrid.add(scrapeButton, 0, 6);
    }

    public void initialize(){
        generateLoginPrompt();
        generateScrapingDropdown();
    }

    public GridPane getApplicationGrid() {
        return applicationGrid;
    }

    public LoginButton getLoginButton() {
        return loginButton;
    }

}
