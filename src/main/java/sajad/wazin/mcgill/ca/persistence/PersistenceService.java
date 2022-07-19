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
    private Path output;

    private PersistenceService() {}

    /*
    * Save a JSONObject as a file given an output folder
    * */
    public void saveJSONFile(JSONObject completedTask, Path outputFolder, String prefix) {
        if(!Files.exists(outputFolder)) {
            try {
                Files.createDirectory(outputFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File outputFile = new File((outputFolder.toAbsolutePath().toString() + "\\" + prefix + "_" + System.currentTimeMillis() + ".json"));

        try {
            outputFile.createNewFile();
            FileWriter outputWriter = new FileWriter(outputFile);

            outputWriter.write(completedTask.toString(4));
            outputWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
    * Save a list of strings as a file to an output folder
    * */
    public void saveListFile(List<String> strings, Path outputFolder, String prefix) {
        if(outputFolder == null) {
            outputFolder = this.output;
        }
        if(!Files.exists(outputFolder)) {
            try {
                Files.createDirectory(outputFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File outputFile = new File((outputFolder.toAbsolutePath().toString() + "\\" + prefix + "_" + System.currentTimeMillis() + ".txt"));

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


    /*
    * Read a file and output a list of strings that will contain each line in the file
    * */
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

    public void setOutput(Path path) {
        this.output = path;
    }

}
