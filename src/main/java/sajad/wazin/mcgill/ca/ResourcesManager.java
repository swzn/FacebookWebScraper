package sajad.wazin.mcgill.ca;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;


/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class ResourcesManager {

    public static ResourcesManager RESOURCES = null;

    public static String ICON_PATH;

    private Properties properties;

    private Path currTmpDir;

    private ResourcesManager() {
        properties = new Properties();

        try {
            currTmpDir = Files.createTempDirectory("fws");

            InputStream exeChromeDriver = FacebookWebScraper.class.getClassLoader().getResourceAsStream("chromedriver.exe");
            ICON_PATH = FacebookWebScraper.class.getClassLoader().getResource("fws_icon64x64.png").toString();
            if(exeChromeDriver == null) {
                System.out.println("Oups");
            }

            else Files.copy(exeChromeDriver, Path.of(currTmpDir.toAbsolutePath().toString() + "\\chromedriver.exe"));

            properties.load(FacebookWebScraper.class.getClassLoader().getResourceAsStream("FacebookWebScraper.properties"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResource(String resourceKey){
        return properties.getProperty(resourceKey);
    }

    public String getChromeDriverPath(){
        return currTmpDir.toAbsolutePath().toString() + "\\chromedriver.exe";
    }

    public static ResourcesManager getResourceManager() {
        if(RESOURCES == null) {
            RESOURCES = new ResourcesManager();
        }
        return RESOURCES;
    }

    public Path getTempDir(){
        return currTmpDir;
    }

    public void deleteTemp(){
        System.out.println("Deleting!");
        if(getTempDir().toFile().exists()) {
            for (String innerFile : getTempDir().toFile().list()){
                new File(getTempDir().toFile().getPath(), innerFile).delete();
            }
            getTempDir().toFile().delete();
        }
        System.out.println("Deleted!");
    }

}
