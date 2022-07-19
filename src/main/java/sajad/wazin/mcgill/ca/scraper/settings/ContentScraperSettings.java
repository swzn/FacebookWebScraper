package sajad.wazin.mcgill.ca.scraper.settings;

import java.io.File;
import java.nio.file.Path;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class ContentScraperSettings implements ScraperSettings {

    private File inputPath;
    private Path outputPath;

    //Mandatory booleans
    private final boolean saveContent;
    private final boolean saveComments;
    private final boolean saveShares;
    private final boolean saveReactions;

    //Default parameters
    private int amountOfPosts = 2;
    private int amountOfComments = 10;
    private int instanceCount = 1;
    private int amountOfShares;

    //Optional boolean
    private boolean headless = false;

    public ContentScraperSettings(boolean saveContent, boolean saveComments, boolean saveShares, boolean saveReactions, int amountOfPosts) {
        this.saveContent = saveContent;
        this.saveComments = saveComments;
        this.saveShares = saveShares;
        this.saveReactions = saveReactions;
        this.amountOfPosts = amountOfPosts;
    }

    public boolean isSavingContent(){
        return saveContent;
    }
    public boolean isSavingComments() {
        return saveComments;
    }
    public boolean isSavingShares(){
        return saveShares;
    }
    public boolean isSavingReactions(){
        return saveReactions;
    }

    @Override
    public void setInput(File input) {
        this.inputPath = input;
    }

    @Override
    public void setOutput(Path output) {
        this.outputPath = output;
    }

    @Override
    public File getInput() {
        return inputPath;
    }
    @Override
    public Path getOutput() {
        return outputPath;
    }

    public void setAmountOfComments (int amountOfComments) {
        this.amountOfComments = amountOfComments;
    }

    public void setAmountOfShares (int amountOfShares) {
        this.amountOfShares = amountOfShares;
    }

    public void setInstanceCount(int instanceCount) {
        this.instanceCount = instanceCount;
    }

    public int getInstanceCount() {
        return instanceCount;
    }

    public String toString(){
        StringBuilder output = new StringBuilder("Content Scraper Settings:\n");

        output.append("Scraping content: ");
        if(saveContent) output.append("true\n");
        else output.append("false\n");

        output.append("Scraping comments: ");
        if(saveComments) output.append("true (").append(this.amountOfComments).append(") \n");
        else output.append("false\n");

        output.append("Scraping reactions: ");
        if(saveReactions) output.append("true\n");
        else output.append("false\n");

        output.append("Scraping shares: ");
        if(saveShares) output.append("true (").append(this.amountOfShares).append(")\n");
        else output.append("false\n");

        output.append("Scraping ");
        output.append(amountOfPosts);
        output.append(" posts");

        return output.toString();
    }

    public int getAmountOfPosts() {
        return amountOfPosts;
    }

    public int getAmountOfComments() {
        return amountOfComments;
    }

    public boolean isHeadless() {
        return headless;
    }

    public void setHeadless(boolean headless) {
        this.headless = headless;
    }
}
