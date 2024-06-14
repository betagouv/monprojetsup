package fr.gouv.monprojetsup.data.tools.csv;

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
        List<Map<String,String>> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            if(line == null) {
                throw new RuntimeException("Cannot read csv");
            }
            String[] header = line.split(String.valueOf(separator), -1);
            int headerSize = header.length;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(String.valueOf(separator), -1);
                if(values.length == headerSize) {
                    Map<String,String> entry = new HashMap<>();
                    for(int i = 0; i < headerSize; i++) {
                        entry.put(header[i],values[i]);
                    }
                    data.add(entry);
                } else {
                    throw new RuntimeException(" csv line with a number of items inconsistent with the header: " + line);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
        for(String h : headers) {
            append(h);
        }
        newLine();
    }

    public void append(boolean b) throws IOException {
        if(!this.skipNextSeparator) writer.append(separator);
        writer.append(String.valueOf(b));
        skipNextSeparator = false;
    }

    public void append(Stream<Map.Entry<String, Integer>> entryStream, String s) {
    }

}
