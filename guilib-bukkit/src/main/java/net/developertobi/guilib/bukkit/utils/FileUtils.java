package net.developertobi.guilib.bukkit.utils;

import com.google.gson.Gson;
import net.developertobi.guilib.bukkit.GuiLibBukkit;
import net.developertobi.guilib.bukkit.file.GuiFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class FileUtils {

    private FileUtils() {
    }

    public static <T> T read(File file, Class<T> clazz) {
        try {
            Gson gson = GuiLibBukkit.getGson();
            return gson.fromJson(new FileReader(file), clazz);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void save(File file, Object result) {
        try {
            FileWriter fileWriter = new FileWriter(file);
            GuiLibBukkit.getGson().toJson(result, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T extends GuiFile> T createOrLoadFile(Path dataFolderPath, String subFolderName, String fileName, Class<T> clazz, T content) {
        File filePath = new File(dataFolderPath + "/" + subFolderName);
        T result;

        try {
            if (!Files.exists(filePath.toPath())) {
                Files.createDirectories(filePath.toPath());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        File file = Paths.get(filePath + "/" + fileName + ".json").toFile();

        if (!Files.exists(file.toPath())) {
            result = content;
            save(file, result);
        } else {
            result = read(file, clazz);
            if (result == null) {
                throw new IllegalStateException("Failed to read file: " + file);
            }
        }

        return result;
    }

    public static File readRawFile(Path dataFolderPath, String subFolderName, String fileName) {
        File filePath = new File(dataFolderPath + "/" + subFolderName);

        if (!Files.exists(filePath.toPath())) {
            return null;
        }
        File file = Paths.get(filePath + "/" + fileName + ".json").toFile();

        return file;
    }
}
