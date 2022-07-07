package sajad.wazin.mcgill.ca.scraper.settings;

import sajad.wazin.mcgill.ca.scraper.interactions.InteractionModeEnum;
import sajad.wazin.mcgill.ca.scraper.search.SearcherEnum;

import java.io.File;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class SuggestionsScraperSettings implements ScraperSettings {

    private InteractionModeEnum interactionMode;
    private SearcherEnum searcher;
    private File inputPath;
    private File outputPath;
    private final int maxDepth;
    private final int maxGrowth;

    public SuggestionsScraperSettings(InteractionModeEnum interactionMode, SearcherEnum searcher, int maxDepth, int maxGrowth){
        this.interactionMode = interactionMode;
        this.searcher = searcher;
        this.maxDepth = maxDepth;
        this.maxGrowth = maxGrowth;
    }

    @Override
    public void setInput(File input) {
        this.inputPath = input;
    }

    @Override
    public void setOutput(File output) {
        this.outputPath = output;
    }

    @Override
    public File getInput() {
        return inputPath;
    }
    @Override
    public File getOutput() {
        return outputPath;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMaxGrowth() {
        return maxGrowth;
    }

    public SearcherEnum getSearcher() {
        return searcher;
    }
}
