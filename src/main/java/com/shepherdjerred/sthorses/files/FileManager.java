package com.shepherdjerred.sthorses.files;

import com.shepherdjerred.sthorses.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public class FileManager {

    private static FileManager instance;
    public FileConfiguration messages;
    private File messagesFile;


    private FileManager() {
        instance = this;
    }

    public static FileManager getInstance() {
        if (instance == null) {
            instance = new FileManager();
        }
        return instance;
    }

    // Load/reload files
    @SuppressWarnings("deprecation")
    void loadFiles() {

        messagesFile = new File(Main.getInstance().getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {

            messagesFile.getParentFile().mkdirs();
            copy(Main.getInstance().getResource("messages.yml"), messagesFile);

        }

        messages = new YamlConfiguration();

        try {

            messages.load(messagesFile);

            messages.setDefaults(YamlConfiguration.loadConfiguration(Main.getInstance().getResource("messages.yml")));
            messages.options().copyDefaults(true);
            saveFiles(FileName.MESSAGES);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    // Save files
    private void saveFiles(FileName file) {
        try {

            switch (file) {

                case MESSAGES:
                    messages.save(messagesFile);
                    break;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Copy default files
    private void copy(InputStream in, File file) {

        try {

            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {

                out.write(buf, 0, len);

            }

            out.close();
            in.close();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public enum FileName {

        MESSAGES

    }
}
