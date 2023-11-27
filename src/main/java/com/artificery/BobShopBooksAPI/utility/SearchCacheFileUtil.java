package com.artificery.BobShopBooksAPI.utility;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Slf4j
public class SearchCacheFileUtil {

    private static final String SEARCH_CACHE_FOLDER_PATH = System.getProperty("user.dir") + File.separator + "search_cache";

    public static String readFileFromSearchCache(String fileName) {
        try {
            Path filePath = Paths.get(SEARCH_CACHE_FOLDER_PATH, fileName);
            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                byte[] fileBytes = Files.readAllBytes(filePath);
                return new String(fileBytes);
            } else {
                System.err.println("File '" + fileName + "' not found in search_cache folder.");
            }
        } catch (IOException e) {
            System.err.println("Error reading file '" + fileName + "': " + e.getMessage());
        }
        return null;
    }


    public static List<String> listFilesInSearchCacheFolder() {

        File searchCacheFolder = new File(SEARCH_CACHE_FOLDER_PATH);
        List<String> fileNames = new ArrayList<>();

        createSearchCacheFolderIfNotExist();
        if (searchCacheFolder.exists() && searchCacheFolder.isDirectory()) {
            File[] files = searchCacheFolder.listFiles();
            if (files != null) {
                Arrays.stream(files)
                        .filter(File::isFile)
                        .map(File::getName)
                        .forEach(fileNames::add);
            }
        }

        return fileNames;
    }


    public static void saveJsonStringToFile(String jsonString, String sellerId, String category) {

        try {
            createSearchCacheFolderIfNotExist();

            String fileName = generateFileName(sellerId, category);

            // Write the JSON string to a file in the search_cache folder
            Path filePath = Paths.get(SEARCH_CACHE_FOLDER_PATH, fileName);
            FileWriter fileWriter = new FileWriter(filePath.toString());
            fileWriter.write(jsonString);
            fileWriter.close();

            System.out.println("JSON data saved to: " + filePath);
        } catch (IOException e) {
            log.error("Error saving JSON data to file: " + e.getMessage());
        }
    }


    private static String generateFileName(String sellerId, String category) {
        Date currentDate = new Date();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = dateFormat.format(currentDate);

        return sellerId + "_" + formattedDate + "_" + category + ".json";
    }

    private static void createSearchCacheFolderIfNotExist() {
        String projectRoot = System.getProperty("user.dir"); // Get the project's root directory

        File searchCacheFolder = new File(projectRoot, "search_cache");

        if (!searchCacheFolder.exists()) {
            boolean created = searchCacheFolder.mkdir();
            if (created) {
                log.info("search_cache folder created successfully.");
            } else {
                log.error("Failed to create search_cache folder.");
            }
        }
    }
}
