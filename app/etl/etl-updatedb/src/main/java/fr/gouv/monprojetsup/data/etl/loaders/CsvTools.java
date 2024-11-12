package fr.gouv.monprojetsup.data.etl.loaders;

import com.opencsv.*;
import com.opencsv.exceptions.CsvValidationException;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CsvTools implements Closeable {

    public static final String MISSING_HEADER = "Missing header ";
    public static final String IN_LINE = " in line ";

    private final @NotNull ICSVWriter csvWriter;
    private CsvTools(String filename, char separator, Charset charset) throws IOException {
        Path filePath = Paths.get(filename);
        Path parentDir = filePath.getParent();
        // Create the parent directories if they do not exist
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        val writer = new BufferedWriter(new FileWriter(filename,charset));
        this.csvWriter = new CSVWriterBuilder(writer).withSeparator(separator).build();
    }

    public static CsvTools getWriter(String filename) throws IOException {
        return new CsvTools(filename, ';', StandardCharsets.ISO_8859_1);
    }

    public static @NotNull List<Map<@NotNull String,@NotNull String>> readCSV(String path) {
        return readCSV(path, ',');
    }
    public static @NotNull List<Map<@NotNull String,@NotNull String>> readCSV(String path, char separator) {
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
                    throw new RuntimeException(" csv line with a number of elements inconsistent with the header: " + Arrays.toString(values));
                }
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }

        return data;
    }


    @Override
    public void close() throws IOException {
        csvWriter.close();
    }

    public void appendHeaders(List<String> headers) {
        append(headers);
    }
    public void append(List<String> items) {
        csvWriter.writeNext(items.toArray(new String[0]));
    }

}
