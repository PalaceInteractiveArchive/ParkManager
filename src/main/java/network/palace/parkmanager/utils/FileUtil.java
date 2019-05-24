package network.palace.parkmanager.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;

public class FileUtil {
    public static final String mainPath = "plugins/ParkManager";
    private HashMap<String, FileSubsystem> subsystems = new HashMap<>();

    public FileUtil() {
        File pluginDirectory = new File(mainPath);
        if (!pluginDirectory.exists()) {
            pluginDirectory.mkdirs();
        }
    }

    /**
     * Creates (or does nothing if exists) a directory for the subsystem within the plugin directory
     * ex: 'plugins/ParkManager/outline' for OutlineManager
     *
     * @param name the name of the directory, preferably lowercase with only letters/numbers
     */
    public FileSubsystem registerSubsystem(String name) throws IllegalArgumentException {
        if (isSubsystemRegistered(name))
            throw new IllegalArgumentException("A subsystem already exists by the name '" + name + "'!");
        FileSubsystem subsystem = new FileSubsystem(name);
        File dir = subsystem.getDirectory();
        if (!dir.exists()) dir.mkdirs();
        subsystems.put(name, subsystem);
        return subsystem;
    }

    /**
     * Get a registered subsystem
     *
     * @param name the name
     * @return FileSubsystem if it exists, null if not
     */
    public FileSubsystem getSubsystem(String name) {
        return subsystems.get(name);
    }

    /**
     * Check if a subsystem is registered by name
     *
     * @param name the name
     * @return true if a subsystem has been registered by that name
     */
    public boolean isSubsystemRegistered(String name) {
        return subsystems.containsKey(name);
    }

    @AllArgsConstructor
    public class FileSubsystem {
        private String name;

        public File getDirectory() {
            return new File(mainPath + "/" + name);
        }

        /**
         * Get a file within the subsystem's directory
         * If it doesn't exist, attempt to create it
         *
         * @param name the name of the file
         * @return the File
         * @throws IOException if there was an error creating the file
         */
        public File getFile(String name) throws IOException {
            File file = new File(getDirectory().getPath() + "/" + name + ".json");
            if (!file.exists()) file.createNewFile();
            return file;
        }

        /**
         * Get the JSON contents of a subsystem file
         *
         * @param name the name of the file
         * @return JsonElement with the contents of the file
         * @throws IOException         if there was an error reading the file
         * @throws JsonSyntaxException if there was an error parsing the file as JSON
         * @see #getFile(String)
         */
        public JsonElement getFileContents(String name) throws IOException, JsonSyntaxException {
            File file = getFile(name);
            StringBuilder json = new StringBuilder();
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                json.append(line);
            }
            JsonElement element = new Gson().fromJson(json.toString(), JsonElement.class);
            if (element == null) {
                return new JsonObject();
            } else {
                return element;
            }
        }

        /**
         * Write JSON content to a subsystem file
         *
         * @param name    the name of the file
         * @param element the JSON data being written to the file
         * @throws IOException if there was an error writing to the file
         * @see #getFile(String)
         */
        public void writeFileContents(String name, JsonElement element) throws IOException {
            Files.write(Paths.get(getFile(name).toURI()), Collections.singletonList(element.toString()), Charset.forName("UTF-8"));
        }
    }
}
