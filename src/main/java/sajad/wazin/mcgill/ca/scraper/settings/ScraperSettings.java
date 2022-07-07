package sajad.wazin.mcgill.ca.scraper.settings;

import java.io.File;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public interface ScraperSettings {
    void setInput(File input);
    void setOutput(File output);
    File getInput();
    File getOutput();
    String toString();
}
