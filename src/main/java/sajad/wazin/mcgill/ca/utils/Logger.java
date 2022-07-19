package sajad.wazin.mcgill.ca.utils;

import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class Logger {

    private GridPane messageLogger;
    private TextArea textContainer;
    private SimpleDateFormat dateFormatter;

    public Logger(){}

    public void setLogger(GridPane pane) {
        this.messageLogger = pane;

        textContainer = new TextArea();
        textContainer.setEditable(false);
        textContainer.setWrapText(true);

        messageLogger.getChildren().add(textContainer);

        dateFormatter = new SimpleDateFormat("[HH:mm:ss] ");
    }

    public void log(String message){
        textContainer.appendText(dateFormatter.format(new Date()) + message + "\n");
    }
}
