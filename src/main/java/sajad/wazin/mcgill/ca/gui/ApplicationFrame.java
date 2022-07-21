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

import static sajad.wazin.mcgill.ca.FacebookWebScraper.*;

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
                "Content Scraper",
                "Suggestions Scraper"
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
            //Retrieve the scraper enum through the dropdown
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
        // Generate the button
        Button scrapeButton = new Button("Start Scraping");

        // Instantiate the log panel
        ScrollPane loggingPane = new ScrollPane();
        loggingPane.setPrefSize(300, 200);
        loggingPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Create the underlying grid structure to add each log message
        loggerMessages = new GridPane();
        loggingPane.setContent(loggerMessages);

        // Generate the "headless" button to allow chrome to run invisibly
        CheckBox headless = new CheckBox("GUI-less Chrome");

        // Show the button and the checkbox
        applicationGrid.add(headless, 0, 5);
        applicationGrid.add(scrapeButton, 0, 6);

        // Create a button to stop the task
        Button cancelTask = new Button("Cancel task as soon as possible");
        cancelTask.setOnAction(actionEvent -> {
            CANCELLED_TASK = true;
            LOGGER.log("Task will be cancelled after the current scrape!");
            cancelTask.setDisable(true);
        });

        // Create a button to dump current memory
        Button dumpMemory = new Button("Dump current memory");

        // Validating input and adding logic to the scrape button
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
                // If all input is valid, disable the GUI
                for(Node node : applicationGrid.getChildren()) {
                    node.setDisable(true);
                }

                // Show the logging pane
                applicationGrid.add(loggingPane,0 ,7);
                LOGGER.setLogger(loggerMessages);

                LOGGER.log("GUI disabled");
                LOGGER.log("Scraping will begin shortly...");

                // Update the settings
                ScraperSettings currentSettings = settingsDialog.getScraperSettings();
                currentSettings.setHeadless(headless.isSelected());
                currentSettings.setInput(selectedInput);
                currentSettings.setOutput(Path.of(selectedOutput.toURI()));

                // Run the scraper on a different thread to not kill the GUI
                Thread scraperExecutor = new Thread (() -> {
                    if (currentSettings instanceof ContentScraperSettings) {
                         ContentScraper scraper = new ContentScraper(currentSettings);
                        scraper.runScraper();
                    } else if (currentSettings instanceof SuggestionsScraperSettings) {
                        SuggestionsScraper scraper = new SuggestionsScraper(currentSettings);
                        scraper.runScraper();
                    }
                });

                FacebookWebScraper.PERSISTENCE_SERVICE.setOutput(Path.of(selectedOutput.toURI()));
                scraperExecutor.start();

                GridPane controlPanel = new GridPane();
                controlPanel.setVgap(10);
                // Instantiate the dump button
                dumpMemory.setOnAction(dumpEvent -> {
                    DUMP_MANAGER.dump(Path.of(currentSettings.getOutput().toString() + "\\dump"));
                    LOGGER.log("Attempting to dump memory...");
                });

                controlPanel.add(cancelTask, 0, 0);
                controlPanel.add(dumpMemory, 0, 1);

                // Create a "Kill Process" button
                Button kill = new Button("Kill current process");
                kill.setOnAction(killAction -> {
                    CANCELLED_TASK = true;
                    CONTROLLER_POOL.kill();
                    RESOURCES.deleteTemp();
                    LOGGER.log("Process has been killed");
                    DUMP_MANAGER.dump(Path.of(currentSettings.getOutput().toString() + "\\killed_process"));
                    System.exit(0);
                });

                controlPanel.add(kill, 0, 2);
                applicationGrid.add(controlPanel, 1,7);
            }
        });

    }

    // When first booted, all that should be seen is the login prompt and the scraper dropdown
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
