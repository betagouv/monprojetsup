package fr.gouv.monprojetsup.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.gouv.monprojetsup.data.update.psup.OrientationConfig;
import fr.parcoursup.carte.exceptions.AccesDonneesException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.apache.commons.lang3.NotImplementedException;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Serialisation {

    private static final Logger LOGGER = Logger.getLogger(Serialisation.class.getSimpleName());

    public static <T> T fromJsonFile(String path, Class<T> type) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(
                new BufferedInputStream(
                        Files.newInputStream(Path.of(path))
                ), StandardCharsets.UTF_8)
        ) {
            return new Gson().fromJson(reader, type);
        }
    }
    public static <T> T fromJsonFile(String path, Type type) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(
                new BufferedInputStream(
                        Files.newInputStream(Path.of(path))
                ), StandardCharsets.UTF_8)
        ) {
            return new Gson().fromJson(reader, type);
        }
    }
    public static <T> T fromZippedJson(String path, Class<T> type) throws IOException {
        try (BufferedInputStream s = new BufferedInputStream(
                Files.newInputStream(Path.of(path)))
        ) {
            ZipInputStream zip = new ZipInputStream(s);
            ZipEntry entry = zip.getNextEntry();
            if (entry == null) throw new RuntimeException("No data in " + path);
            try (BufferedReader r = new BufferedReader(new InputStreamReader(zip, StandardCharsets.UTF_8))) {
                return new Gson().fromJson(r, type);
            }
        }
    }

    public static <T> T fromZippedObject(String filename, Class<T> classe) throws IOException, ClassNotFoundException {
        try (BufferedInputStream s = new BufferedInputStream(
                Files.newInputStream(Path.of(filename + ".obj.zip")))
        ) {
            ZipInputStream in = new ZipInputStream(s);
            in.getNextEntry();
            ObjectInputStream ois = new ObjectInputStream(in);
            Object obj = ois.readObject();
            if(classe.isInstance(obj)) {
                ois.close();
                return classe.cast (obj);
            } else {
                throw new ClassCastException();
            }
        }
    }

    public static <T> T load(OrientationConfig config, Class<T> classe, String suffix) throws IOException, ClassNotFoundException, JAXBException {
        String prefix = classe.getTypeName() + suffix;
        String filename = config.getInputFilename(prefix);
        if(config.inputJson) {
            return fromZippedJson(filename, classe);
        } else if(config.inputObj) {
            return fromZippedObject(filename, classe);
        } else {
            throw new NotImplementedException();
        }
    }

    public static void toZippedJson(String path, Object object, boolean prettyPrint) throws IOException {
        ZipOutputStream out = new ZipOutputStream(
                new BufferedOutputStream(
                        Files.newOutputStream(Path.of(path))
                )
        );
        out.setMethod(8);
        out.setLevel(7);
        out.putNextEntry(new ZipEntry(Path.of(path).getFileName().toString() + ".json"));
        try(OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            if(prettyPrint) {
                new GsonBuilder().setPrettyPrinting()./*disableHtmlEscaping().*/create().toJson(object, writer);
            } else {
                new GsonBuilder().create().toJson(object, writer);
            }
        }
        out.close();
    }

    public static void toJsonFile(String filename, Object object, boolean prettyPrint) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new BufferedOutputStream(
                        Files.newOutputStream(Path.of(filename))
                ),
                StandardCharsets.UTF_8)
        ) {
            if (prettyPrint) {
                new GsonBuilder().setPrettyPrinting()./*disableHtmlEscaping().*/create().toJson(object, writer);
            } else {
                new GsonBuilder().create().toJson(object, writer);
            }
        }
    }

    public static void toZippedObject(String filename, Object obj) throws IOException{
        ZipOutputStream out = new ZipOutputStream(
                new BufferedOutputStream(
                        Files.newOutputStream(
                                Path.of(filename + ".obj.zip")
                        )
                )
        );
        out.setMethod(8);
        out.setLevel(7);
        out.putNextEntry(new ZipEntry(filename + ".obj"));
        ObjectOutputStream oos = new ObjectOutputStream(out);
        oos.writeObject(obj);
        oos.close();
        out.close();
    }


    public static <T> void save(OrientationConfig config, Class<T> classe, T object, String suffix) throws IOException, AccesDonneesException {
        String prefix = classe.getTypeName() + suffix;
        String filename = config.getOutputFilename(prefix);
        if(config.outputJson) {
            toZippedJson(filename  + ".json.zip", object, true);
        } else if(config.outputObj) {
            toZippedObject(filename, object);
        } else {
            throw new NotImplementedException();
        }
    }

    public static Map<String,List<Map<String, String>>> exportSelectToObject(Connection conn, Map<String, String> sqls) throws AccesDonneesException, IOException, SQLException {
        Map<String,List<Map<String, String>>> m = new HashMap<>();
        for (Map.Entry<String, String> entry : sqls.entrySet()) {
            String prefix = entry.getKey();
            String sql = entry.getValue();
            List<Map<String, String>> result = new ArrayList<>();
            try (Statement stmt = conn.createStatement()) {
                /* récupère la liste des candidats ayant des voeux confirmés à l'année n-1 */
                stmt.setFetchSize(1_000_000);
                LOGGER.info(sql);

                try (ResultSet resultSet = stmt.executeQuery(sql)) {

                    ResultSetMetaData rsmd = resultSet.getMetaData();
                    int nb = rsmd.getColumnCount();
                    int cnt = 0;
                    while (resultSet.next()) {
                        Map<String, String> o = new HashMap<>();
                        for (int i = 1; i <= nb; i++) {
                            String st = resultSet.getString(i);
                            String columnName = rsmd.getColumnName(i);
                            o.put(columnName, st);
                        }
                        if (++cnt % 100000 == 0) {
                            LOGGER.info("Got " + cnt + " entries");
                        }
                        result.add(o);
                    }
                }
            }
            m.put(prefix, result);
        }
        return m;
    }

    public static <T> T fromXML(String path, Class<T> type) throws IOException, JAXBException {
        try (InputStreamReader reader =
                     new InputStreamReader(
                             new BufferedInputStream(
                                     Files.newInputStream(
                                             Path.of(path)
                                     )
                             ),
                             StandardCharsets.UTF_8
                     )
        ) {
            JAXBContext jaxbContext = JAXBContext.newInstance(type );
            Unmarshaller m = jaxbContext.createUnmarshaller();
            return (T) m.unmarshal(reader);
        }
    }
}
