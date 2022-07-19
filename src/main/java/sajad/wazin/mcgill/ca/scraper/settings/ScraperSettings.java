package sajad.wazin.mcgill.ca.scraper.settings;

import java.io.File;
import java.nio.file.Path;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public interface ScraperSettings {
    void setInput(File input);
    void setOutput(Path output);
    void setHeadless(boolean headless);
    File getInput();
    Path getOutput();
    String toString();
}
