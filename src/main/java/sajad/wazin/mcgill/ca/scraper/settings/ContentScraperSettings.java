package sajad.wazin.mcgill.ca.scraper.settings;

import java.io.File;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class ContentScraperSettings implements ScraperSettings {

    private File inputPath;
    private File outputPath;

    //Mandatory booleans
    private final boolean saveContent;
    private final boolean saveComments;
    private final boolean saveShares;
    private final boolean saveReactions;

    //Optional boolean
    private boolean isAsynchronous = false;

    //Default parameters
    private int amountOfPosts = 2;
    private int amountOfComments = 10;
    private int instanceCount = 1;

    public ContentScraperSettings(boolean saveContent, boolean saveComments, boolean saveShares, boolean saveReactions, int amountOfPosts, int amountOfComments) {
        this.saveContent = saveContent;
        this.saveComments = saveComments;
        this.saveShares = saveShares;
        this.saveReactions = saveReactions;
        this.amountOfPosts = amountOfPosts;
        this.amountOfComments = amountOfComments;
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

    public void setAsynchronous(boolean isAsynchronous) {
        this.isAsynchronous = isAsynchronous;
    }

    public boolean isAsynchronous() {
        return isAsynchronous;
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
        if(saveComments) output.append("true\n");
        else output.append("false\n");

        output.append("Scraping reactions: ");
        if(saveReactions) output.append("true\n");
        else output.append("false\n");

        output.append("Scraping shares: ");
        if(saveShares) output.append("true\n");
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
}
