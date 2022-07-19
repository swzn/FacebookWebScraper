package sajad.wazin.mcgill.ca.utils;

import javafx.scene.control.TextField;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class FXUtils {

    // Given a TextField node, this methods adds a listener that only allows integers to be added to the field
    public static void setIntegerListener(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\d]*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
}
