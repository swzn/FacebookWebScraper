package sajad.wazin.mcgill.ca.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class PersistenceService {

    private Encoder encoder;

    private static PersistenceService PERSISTENCE_SERVICE = new PersistenceService();


    private PersistenceService(){
        encoder = new Encoder();
    }

    public static List<String> readLines(File file) {
        List<String> outputStrings = new ArrayList<>();

        if(file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String currentLine;
                while((currentLine = bufferedReader.readLine()) != null) {
                    outputStrings.add(currentLine);
                }
            } catch (IOException e) {
                // do error handling
            }
        }
        return outputStrings;
    }

}
