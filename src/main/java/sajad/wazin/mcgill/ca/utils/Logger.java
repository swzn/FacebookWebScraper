package sajad.wazin.mcgill.ca.utils;

import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import sajad.wazin.mcgill.ca.FacebookWebScraper;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static sajad.wazin.mcgill.ca.FacebookWebScraper.PERSISTENCE_SERVICE;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class Logger {

    private GridPane messageLogger;
    private TextArea textContainer;
    private SimpleDateFormat dateFormatter;
    private final List<String> logAsString;

    /*
    * Simple logging system
    * */

    public Logger(){
        logAsString = new ArrayList<>();
    }

    public void setLogger(GridPane pane) {
        this.messageLogger = pane;

        textContainer = new TextArea();
        textContainer.setEditable(false);
        textContainer.setWrapText(true);

        messageLogger.getChildren().add(textContainer);

        dateFormatter = new SimpleDateFormat("[HH:mm:ss] ");
    }

    public void log(String message){
        if(textContainer != null) {
            try {
                // Add a delay to make it thread-safe
                Thread.sleep(50);
                textContainer.appendText(dateFormatter.format(new Date()) + message + "\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(dateFormatter.format(new Date()) + message);
        logAsString.add(dateFormatter.format(new Date()) + message);
    }

    public void outputLog(){
        PERSISTENCE_SERVICE.saveListFile(logAsString, null, "log");
    }

    public void dumpLog(Path output){
        ArrayList<String> dump = new ArrayList<>(logAsString);

        PERSISTENCE_SERVICE.saveListFile(dump, output, "dumped_log");
    }
}
