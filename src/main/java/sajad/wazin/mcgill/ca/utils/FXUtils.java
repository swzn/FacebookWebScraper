package sajad.wazin.mcgill.ca.utils;

import javafx.scene.control.TextField;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class FXUtils {

    public static void setIntegerListener(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\d]*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
}
