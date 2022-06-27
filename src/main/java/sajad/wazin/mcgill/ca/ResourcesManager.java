package sajad.wazin.mcgill.ca;


import java.io.IOException;
import java.util.Properties;


/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class ResourcesManager {

    public static final ResourcesManager RESOURCES = new ResourcesManager();

    private Properties properties;

    private ResourcesManager(){
        properties = new Properties();
        try {
            properties.load(FacebookWebScraper.PROPERTIES_STREAM);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getResource(String resourceKey){
        return properties.getProperty(resourceKey);
    }
}
