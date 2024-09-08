package fr.gouv.monprojetsup.data.tools;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class CsvTools implements Closeable {

    boolean skipNextSeparator;
    final Writer writer;
    final char separator;
    final boolean ownWriter;

    public CsvTools(String filename, char separator) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(filename));
        this.skipNextSeparator = true;
        this.separator = separator;
        ownWriter = true;
    }

    public CsvTools(Writer writer, char separator) {
        this.writer = writer;
        this.skipNextSeparator = true;
        this.separator = separator;
        ownWriter = false;
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
                    throw new RuntimeException(" csv line with a number of items inconsistent with the header: " + values);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (CsvValidationException e) {
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

    public void append(@Nullable String val) throws IOException {
        if(!this.skipNextSeparator) writer.append(separator);
        writer.append("\"" + (Objects.isNull(val) ? "" : val.replace("\"","'")) + "\"");
        skipNextSeparator = false;
    }

    public void append(double val) throws IOException {
        if(!this.skipNextSeparator) writer.append(separator);
        writer.append(String.valueOf(val));
        skipNextSeparator = false;
    }

    public void append(int val) throws IOException {
        if(!this.skipNextSeparator) writer.append(separator);
        writer.append(String.valueOf(val));
        skipNextSeparator = false;
    }
    public void append(long val) throws IOException {
        if(!this.skipNextSeparator) writer.append(separator);
        writer.append(String.valueOf(val));
        skipNextSeparator = false;
    }

    public void newLine() throws IOException {
        writer.append(System.lineSeparator());
        skipNextSeparator = true;
    }

    @Override
    public void close() throws IOException {
        if(ownWriter) {
            writer.close();
        }
    }

    public void appendHeaders(List<String> headers) throws IOException {
        append(headers);
    }

    public void append(boolean b) throws IOException {
        if(!this.skipNextSeparator) writer.append(separator);
        writer.append(String.valueOf(b));
        skipNextSeparator = false;
    }

    public void append(Stream<Map.Entry<String, Integer>> entryStream, String s) {
    }

    public void append(List<String> items) throws IOException {
        for(String h : items) {
            append(h);
        }
        newLine();
    }
}
