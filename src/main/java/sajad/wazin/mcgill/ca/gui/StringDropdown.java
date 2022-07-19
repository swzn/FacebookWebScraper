package sajad.wazin.mcgill.ca.gui;

import javafx.scene.control.ComboBox;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class StringDropdown extends ComboBox<String> {
    /*
    * Constructor that inherits from a combobox and quickly creates a string dropdown node
    * */

    public StringDropdown(String... dropdownElements){
        for(String element : dropdownElements) {
            this.getItems().add(element);
        }
    }
}
