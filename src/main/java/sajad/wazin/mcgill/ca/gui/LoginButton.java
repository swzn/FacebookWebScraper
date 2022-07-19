package sajad.wazin.mcgill.ca.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import sajad.wazin.mcgill.ca.FacebookWebScraper;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class LoginButton extends Button {

    private final TextField userField;
    private final PasswordField passField;
    private boolean locked = false;

    public LoginButton(TextField userField, PasswordField passField) {
        super("Lock credentials");

        this.userField = userField;
        this.passField = passField;

        this.setOnAction(lockingEvent);
    }

    EventHandler<ActionEvent> lockingEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent event) {
            if(userField.getText().isEmpty() || passField.getText().isEmpty()) {
                new DialogBox("Please enter valid credentials...").show();
                return;
            }

            // Lock the user/pass fields
            userField.setEditable(false);
            passField.setEditable(false);

            // Display a dialog to let the user know that the credentials have been locked
            new DialogBox("Log-in credentials have been locked in the system!").show();

            // Update the login
            FacebookWebScraper.LOGIN.setEmail(userField.getText());
            FacebookWebScraper.LOGIN.setPassword(passField.getText());


            // Morph the button into an "unlock" button in case the user needs to change credentials
            LoginButton login = ((LoginButton) event.getSource());
            login.setText("Unlock credentials");
            login.setOnAction(unlockingEvent);

            // Change the locked boolean
            locked = true;
        }
    };

    EventHandler<ActionEvent> unlockingEvent = new EventHandler<>() {
        @Override
        public void handle(ActionEvent event) {
            // Clear and unlock the user/pass
            userField.setEditable(true);
            userField.setText("");
            passField.setEditable(true);
            passField.setText("");

            // Display a dialog to let the user know that the credentials have been reset
            new DialogBox("Log-in credentials have been reset.\nPlease input new credentials.").show();

            // Update the login
            FacebookWebScraper.LOGIN.setEmail("");
            FacebookWebScraper.LOGIN.setPassword("");


            // Morph the button into an "unlock" button in case the user needs to change credentials
            LoginButton login = ((LoginButton) event.getSource());
            login.setText("Lock credentials");
            login.setOnAction(lockingEvent);

            // Change the locked boolean
            locked = false;
        }
    };

    // Add an event handler that allows to use the Enter button to LOCK your logins
    public static EventHandler<KeyEvent> getEnterKeyEvent(LoginButton loginButton) {
        return keyEvent -> {
            if(!loginButton.isLocked() && keyEvent.getCode() == KeyCode.ENTER) {
                loginButton.fire();
            }
        };
    }

    public boolean isLocked() {
        return locked;
    }
}
