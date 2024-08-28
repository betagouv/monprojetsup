package fr.gouv.monprojetsup.data.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Serialisation {

    private static final Logger LOGGER = Logger.getLogger(Serialisation.class.getSimpleName());

    public static <T> @NotNull T fromJsonFile(Path path, Class<T> type) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(
                new BufferedInputStream(
                        Files.newInputStream(path)
                ), StandardCharsets.UTF_8)
        ) {
            return new Gson().fromJson(reader, type);
        }
    }
    public static <T> @NotNull T fromJsonFile(String path, Class<T> type) throws IOException {
        return fromJsonFile(Path.of(path), type);
    }
    public static <T> @NotNull T fromJsonFile(Path path, Type type) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(
                new BufferedInputStream(
                        Files.newInputStream(path)
                ), StandardCharsets.UTF_8)
        ) {
            return new Gson().fromJson(reader, type);
        }
    }
    public static <T> @NotNull T fromJsonFile(String path, Type type) throws IOException {
        return fromJsonFile(Path.of(path), type);
    }

        public static <T> @NotNull T fromZippedJson(String path, Class<T> type) throws IOException {
        return fromZippedJson(Path.of(path), type);
    }

    public static <T> @NotNull T fromZippedJson(Path path, Class<T> type) throws IOException {
        try (BufferedInputStream s = new BufferedInputStream(
                Files.newInputStream(path))
        ) {
            ZipInputStream zip = new ZipInputStream(s);
            ZipEntry entry = zip.getNextEntry();
            if (entry == null) throw new RuntimeException("No data in " + path);
            try (BufferedReader r = new BufferedReader(new InputStreamReader(zip, StandardCharsets.UTF_8))) {
                return new Gson().fromJson(r, type);
            }
        }
    }

    public static <T> @NotNull T fromZippedObject(String filename, Class<T> classe) throws IOException, ClassNotFoundException {
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
