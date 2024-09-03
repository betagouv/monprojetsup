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

    private static InputStream getRemoteFile(String urlString, boolean useCache) throws IOException, InterruptedException {
        String cacheName = urlString.hashCode() + ".cache";
        if (useCache) {
            if (Files.exists(Path.of(cacheName))) {
                LOGGER.warning("Utilisation du cache pour " + urlString + " depuis " + cacheName);
                return new FileInputStream(cacheName);
            }
        }
        LOGGER.info("Téléchargement depuis " + urlString);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString))
                .timeout(Duration.ofSeconds(10)) // optional timeout setting
                .header("Content-Type", "application/json")
                .GET() // or use .POST(), .PUT(), etc.
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() == HTTP_OK) {
            val stream = response.body();
            if (useCache) {
                LOGGER.warning("Sauvegarde de " + urlString + " dans le cache " + cacheName);
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

    public static <T> @NotNull T fromRemoteJson(String urlString, Type type, boolean useCache) throws IOException, InterruptedException {
        try (InputStreamReader reader = new InputStreamReader(getRemoteFile(urlString, useCache), StandardCharsets.UTF_8)) {
            return new Gson().fromJson( reader, type);
        }
    }

    public static <T> @NotNull T fromRemoteZippedXml(String urlString, JavaType type, boolean useCache) throws IOException, InterruptedException {
        try (InputStream stream = getRemoteFile(urlString, useCache)) {
            ZipInputStream zip = new ZipInputStream(stream);
            ZipEntry entry = zip.getNextEntry();
            if (entry == null)
                throw new RuntimeException("No data in zip file " + urlString);
            try (BufferedReader r = new BufferedReader(new InputStreamReader(zip, StandardCharsets.UTF_8))) {
                XmlMapper xmlMapper = new XmlMapper();
                xmlMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return xmlMapper.readValue(r, type);
            }
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
        try(ZipOutputStream out = new ZipOutputStream(
                new BufferedOutputStream(
                        Files.newOutputStream(Path.of(path))
                )
        )) {
            out.setMethod(8);
            out.setLevel(7);
            out.putNextEntry(new ZipEntry(Path.of(path).getFileName().toString() + ".json"));
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
