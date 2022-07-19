package sajad.wazin.mcgill.ca.persistence;

import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sajad Wazin @ https://github.com/swzn
 * @project FacebookWebScraper
 * @mail sajad.wazin@mail.mcgill.ca
 */

public class PersistenceService {

    private static PersistenceService PERSISTENCE_SERVICE = new PersistenceService();


    private PersistenceService() {}

    public void saveJSONFile(JSONObject completedTask, Path outputFolder) {
        if(!Files.exists(outputFolder)) {
            try {
                Files.createDirectory(outputFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File outputFile = new File((outputFolder.toAbsolutePath().toString() + "\\output_" + System.currentTimeMillis() + ".json"));

        try {
            outputFile.createNewFile();
            FileWriter outputWriter = new FileWriter(outputFile);

            outputWriter.write(completedTask.toString(4));
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveListFile(List<String> strings, Path outputFolder) {
        if(!Files.exists(outputFolder)) {
            try {
                Files.createDirectory(outputFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File outputFile = new File((outputFolder.toAbsolutePath().toString() + "\\raw_" + System.currentTimeMillis() + ".txt"));

        try {
            outputFile.createNewFile();
            FileWriter outputWriter = new FileWriter(outputFile);

            for(String link : strings) {
                outputWriter.write(link + "\n");
            }
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                e.printStackTrace();
            }
        }
        return outputStrings;
    }

    public static PersistenceService getPersistenceService() {
        return PERSISTENCE_SERVICE;
    }

}
