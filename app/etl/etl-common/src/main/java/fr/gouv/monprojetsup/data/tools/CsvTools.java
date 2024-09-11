package fr.gouv.monprojetsup.data.tools;

import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import lombok.val;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CsvTools implements Closeable {

    private final ICSVWriter csvWriter;
    //
    public CsvTools(String filename, char separator) throws IOException {
        Path filePath = Paths.get(filename);
        Path parentDir = filePath.getParent();
        // Create the parent directories if they do not exist
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        val writer = new BufferedWriter(new FileWriter(filename, StandardCharsets.UTF_8));
        this.csvWriter = new CSVWriterBuilder(writer).withSeparator(separator).build();
    }

    private CsvTools(String filename, char separator, Charset charset) throws IOException {
        Path filePath = Paths.get(filename);
        Path parentDir = filePath.getParent();
        // Create the parent directories if they do not exist
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        val writer = new BufferedWriter(new FileWriter(filename, charset));
        this.csvWriter = new CSVWriterBuilder(writer).withSeparator(separator).build();
    }

    public static List<Map<String,String>> readCSV(String path, char separator) {
        return readCSV(path, separator, List.of());
    }
    public static List<Map<String,String>> readCSV(String path, char separator, List<String> requiredFields) {
        List<Map<String,String>> data = new ArrayList<>();
        val parser = new CSVParserBuilder().withSeparator(separator).build();
        try (BufferedReader br = new BufferedReader(new FileReader(path));
             CSVReader csvReader = new CSVReaderBuilder(br).withCSVParser(parser).build()
        ) {
            String[] values = csvReader.readNext();

            if(values == null) {
                throw new RuntimeException("Cannot read csv / empty csv '" + path );
            }
            int headerSize = values.length;
            String[] header = values;

            // we are going to read data line by line
            while ((values = csvReader.readNext()) != null) {
                if(values.length == headerSize) {
                    Map<String,String> entry = new HashMap<>();
                    for(int i = 0; i < headerSize; i++) {
                        entry.put(header[i],values[i]);
                    }
                    data.add(entry);
                } else {
                    throw new RuntimeException(" csv line with a number of items inconsistent with the header: " + Arrays.toString(values));
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }

        data.removeIf(Map::isEmpty);
        if(data.isEmpty()) {
            throw new RuntimeException("Pas de ligne dans le fichier " + path);
        }
        requiredFields.forEach(field -> {
            if(data.stream().anyMatch(l -> !l.containsKey(field))) {
                throw new RuntimeException("Echec de chargement de `" + path + " champ manquant '" + field + "'");
            }
        });

        return data;
    }

    public static CsvTools getWriter(String filename) throws IOException {
        return new CsvTools(filename, ';', StandardCharsets.ISO_8859_1);
    }


    @Override
    public void close() throws IOException {
        csvWriter.close();
    }

    public void appendHeaders(List<String> headers) throws IOException {
        append(headers);
    }
    public void append(List<String> items) throws IOException {
        csvWriter.writeNext(items.toArray(new String[0]));
    }


}
