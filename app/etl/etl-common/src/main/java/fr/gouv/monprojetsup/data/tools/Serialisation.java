package fr.gouv.monprojetsup.data.tools;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static java.net.HttpURLConnection.HTTP_OK;

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

    public static InputStream getRemoteFile(String urlString, String dataDir) throws IOException, InterruptedException {
        String dirName = dataDir;
        int i = urlString.lastIndexOf('/') + 1;
        String cacheName = dirName + "/" + urlString.substring(i);
        if (dataDir != null && Files.exists(Path.of(cacheName))) {
            LOGGER.warning("Utilisation du cache pour " + urlString + " depuis " + cacheName);
            return new FileInputStream(cacheName);
        }

        LOGGER.info("Téléchargement depuis " + urlString);
        val uri = URI.create(urlString);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(10)) // optional timeout setting
                .header("Content-Type", "application/json")
                .GET() // or use .POST(), .PUT(), etc.
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() == HTTP_OK) {
            val stream = response.body();
            if (dataDir != null) {
                LOGGER.warning("Sauvegarde de " + urlString + " dans le cache " + cacheName);
                if(!Files.exists(Path.of(dirName))) Files.createDirectories(Path.of(dirName));
                try (OutputStream out = new FileOutputStream(cacheName)) {
                    stream.transferTo(out);
                }
                return new FileInputStream(cacheName);
            } else {
                return stream;
            }
        } else {
            throw new RuntimeException("Echec de la requête GET à " + urlString + " statut: " + response.statusCode());
        }
    }

    public static <T> @NotNull T fromRemoteJson(String urlString, Type type, String dataDir) throws IOException, InterruptedException {
        try (InputStreamReader reader = new InputStreamReader(getRemoteFile(urlString, dataDir), StandardCharsets.UTF_8)) {
            return new Gson().fromJson( reader, type);
        }
    }

    public static <T> @NotNull List<T> fromLocalJson(String path, Type type) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(
                new BufferedInputStream(
                        Files.newInputStream(Path.of(path))
                ), StandardCharsets.UTF_8)
        ) {
            return new Gson().fromJson(reader, type);
        }
    }


    @SuppressWarnings("unused")
    public static <T> @NotNull T fromRemoteZippedXml(String urlString, JavaType type, String useCache) throws IOException, InterruptedException {
        try (InputStream stream = getRemoteFile(urlString, useCache)) {
            return fromZippedXmlStream(stream, type);
        }
    }

    public static <T> @NotNull List<T> fromZippedXml(String path, JavaType listType) throws IOException {
        try (BufferedInputStream s = new BufferedInputStream(
                Files.newInputStream(Path.of(path)))
        ) {
            return fromZippedXmlStream(s, listType);
        }
    }

    private static <T> T fromZippedXmlStream(InputStream stream, JavaType type) throws IOException {
        ZipInputStream zip = new ZipInputStream(stream);
        ZipEntry entry = zip.getNextEntry();
        if (entry == null)
            throw new RuntimeException("No data in zip file");
        try (BufferedReader r = new BufferedReader(new InputStreamReader(zip, StandardCharsets.UTF_8))) {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return xmlMapper.readValue(r, type);
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



    public static void toZippedJson(String path, Object object, boolean prettyPrint) throws IOException {
        Path path1 = Path.of(path);
        try(ZipOutputStream out = new ZipOutputStream(
                new BufferedOutputStream(
                        Files.newOutputStream(path1)
                )
        )) {
            out.setMethod(8);
            out.setLevel(7);
            out.putNextEntry(new ZipEntry(path1.getFileName().toString() + ".json"));
            try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
                if (prettyPrint) {
                    new GsonBuilder().setPrettyPrinting()./*disableHtmlEscaping().*/create().toJson(object, writer);
                } else {
                    new GsonBuilder().create().toJson(object, writer);
                }
            }
        }
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

    private Serialisation() {}


}
