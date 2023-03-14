package com.artificery.BobShopBooksAPI.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

@Service
public class CSVService {
    public List<String[]> readCSVFile(String filePath) throws CsvValidationException, IOException {
        CSVReader csvReader = new CSVReader(new FileReader(filePath));
        List<String[]> rows = new ArrayList<>();
        String[] row;
        while ((row = csvReader.readNext()) != null) {
            rows.add(row);
        }
        csvReader.close();
        return rows;
    }

    public <T> void saveObjectsToCSV(List<T> objects, String filePath) throws IOException {
        if (objects.isEmpty()) {
            return;
        }

        FileWriter fileWriter = new FileWriter(filePath);
        CSVWriter csvWriter = new CSVWriter(fileWriter);

        // write header row using field names of object class
        Class<?> objectClass = objects.get(0).getClass();
        Field[] fields = objectClass.getDeclaredFields();

        String[] header = Optional.of(objectClass.getDeclaredFields())
                .map(Arrays::asList)
                .stream()
                .flatMap(Collection::stream)
                .map(Field::getName)
                .toArray(String[]::new);

        csvWriter.writeNext(header);

        int numberOfColumns = objectClass.getDeclaredFields().length;
        // write data rows
        for (T obj : objects) {
            String[] row = new String[numberOfColumns];
            for (int i = 0; i < numberOfColumns; i++) {
                fields[i].setAccessible(true);
                try {
                    Object value = fields[i].get(obj);
                    row[i] = value != null ? value.toString() : "";
                } catch (IllegalAccessException e) {
                    row[i] = "";
                }
            }
            csvWriter.writeNext(row);
        }

        csvWriter.close();
        fileWriter.close();
    }
}
