package sajad.wazin.mcgill.ca.gui;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sajad.wazin.mcgill.ca.FacebookWebScraper;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class DialogBox {

    String message;

    public DialogBox(String message) {
        /*
        * Pop-up dialog box that displays "message"
        * */
        this.message = message;
    }

    public void show() {
        // Create pop-up
        Stage dialogStage = new Stage();
        dialogStage.setTitle("FacebookWebScraper");
        dialogStage.getIcons().add(new Image(FacebookWebScraper.ICON_PATH));
        Pane dialogPane = new Pane();

        // Create container for scene elements
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30,30,30,30));
        grid.setVgap(40);
        grid.add(new Text(message), 0,0);

        //Create the button to close the pop-up
        Button ok = new Button("OK");
        ok.setOnAction(e->{
            dialogStage.hide();
        });
        grid.add(ok, 0,1);

        // Display pop-up
        dialogPane.getChildren().add(grid);
        dialogStage.setScene(new Scene(dialogPane,400,150));
        dialogStage.show();
    }
}
