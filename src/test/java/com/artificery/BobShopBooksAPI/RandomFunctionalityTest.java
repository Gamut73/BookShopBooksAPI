package com.artificery.BobShopBooksAPI;

import com.artificery.BobShopBooksAPI.model.google.VolumeInfo;
import com.artificery.BobShopBooksAPI.model.google.VolumeSearchResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RandomFunctionalityTest {

    private Logger LOGGER = LoggerFactory.getLogger(RandomFunctionalityTest.class);

    @Test
    void shouldMapJsonFileToVolumeInfoList() throws IOException {
        List<VolumeInfo> volumeInfoList = mapJsonFileToVolumeInfoList();


        List<VolumeInfo> hasRatingGreaterThan4 = volumeInfoList.stream()
                .filter(volumeInfo -> volumeInfo.getAverageRating() != null)
                .filter(volumeInfo -> Double.parseDouble(volumeInfo.getAverageRating()) > 4)
                .filter(volumeInfo -> volumeInfo.getRatingsCount() != null && Integer.parseInt(volumeInfo.getRatingsCount()) > 5)
                .collect(Collectors.toList());

        prettyPrintVolumeInfo(hasRatingGreaterThan4);

        List<VolumeInfo> hasCategory = volumeInfoList.stream()
                .filter(volumeInfo -> volumeInfo.getCategories() != null && volumeInfo.getCategories().size() > 0)
                .filter(volumeInfo -> volumeInfo.getCategories().stream().anyMatch(category -> category.equalsIgnoreCase("fiction")))
                .collect(Collectors.toList());
    }

    @Test
    void shouldGetCategory() throws IOException {
        List<VolumeInfo> volumeInfoList = mapJsonFileToVolumeInfoList();

        String categoryToSearch = "history";

        List<VolumeInfo> hasCategory = volumeInfoList.stream()
                .filter(volumeInfo -> volumeInfo.getCategories() != null && volumeInfo.getCategories().size() > 0)
                .filter(volumeInfo -> volumeInfo.getCategories().stream().anyMatch(category -> category.contains(categoryToSearch)))
                .collect(Collectors.toList());

        prettyPrintVolumeInfo(hasCategory);
    }

    @Test
    void shouldGetAuthorsInList() throws IOException {
        List<VolumeInfo> volumeInfoList = mapJsonFileToVolumeInfoList();

        List<String> authorsToSearch = List.of("George rr marting", "Tolkien", "Plath", "David Forster", "Vonnegut", "Bukowski", "Kafka", "Dostoyevsky", "Orwell", "Camus", "Sartre", "Nietzsche", "Kierkegaard", "Aristotle", "Plato", "Seneca", "Marcus Aurelius", "Epictetus", "Descartes", "Kant");

        List<VolumeInfo> authorSearch = volumeInfoList
                .stream()
                .filter(volumeInfo -> volumeInfo.getAuthors() != null && volumeInfo.getAuthors().size() > 0)
                .filter(volumeInfo -> volumeInfo.getAuthors().stream().anyMatch(author -> authorsToSearch.stream().anyMatch(authorToSearch -> author.contains(authorToSearch))))
                .collect(Collectors.toList());

        prettyPrintVolumeInfo(authorSearch);
    }

    private void prettyPrintVolumeInfo(List<VolumeInfo> volumeInfos) {
        volumeInfos.stream().forEach(volumeInfo -> {
            StringBuilder logStringBuilder = new StringBuilder()
                    .append("\t\nTitle: ").append(volumeInfo.getTitle());

            Optional.ofNullable(volumeInfo.getAuthors()).ifPresent(authors -> {
                logStringBuilder.append("\t\nAuthors: ");
                authors.stream().forEach(author -> logStringBuilder.append(author).append(", "));
            });

            logStringBuilder.append("\t\nRating: ").append(volumeInfo.getAverageRating())
                    .append("\t\nRatings Count: ").append(volumeInfo.getRatingsCount())
                    .append("\t\nDescription: ").append(volumeInfo.getDescription());

            Optional.ofNullable(volumeInfo.getCategories()).ifPresent(categories -> {
                logStringBuilder.append("\t\nCategories: ");
                categories.stream().forEach(category -> logStringBuilder.append(category).append(", "));
            });
            Optional.ofNullable(volumeInfo.getGoodReadsPreviewLink()).ifPresent(logStringBuilder.append("\t\nGoodReads: ")::append);
            Optional.ofNullable(volumeInfo.getStorygraphSearchLink()).ifPresent(logStringBuilder.append("\t\nStorygraph: ")::append);

            LOGGER.info(logStringBuilder.toString());
            LOGGER.info("---------------------------------------------------------------------------------------------");
        });
    }


    /**
     * A java method that reads a file and maps to a list of VolumeInfo objects
     */
    private List<VolumeInfo> mapJsonFileToVolumeInfoList() throws IOException {
        File file = ResourceUtils.getFile("classpath:savedVolumeInfo.json");
        InputStream in = new FileInputStream(file);

        return mapJsonToVolumeInfoListFromInputStream(in);
    }

    private List<VolumeInfo> mapJsonToVolumeInfoListFromInputStream(InputStream json) {
        //convert the json string to a list of VolumeInfo objects
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, new TypeReference<List<VolumeInfo>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static String readJsonFileAsString(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }

        return content.toString();
    }
}
