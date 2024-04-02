package fr.gouv.monprojetsup.common.server;

import com.google.gson.Gson;
import com.google.json.JsonSanitizer;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;


public class Helpers {

    public static final Logger LOGGER = Logger.getLogger(Helpers.class.getName());

    public final static String NULL_DATA = "Requête mal formée";
    public static String getSanitizedBuffer(HttpExchange t) throws IOException {
        try (InputStream in = t.getRequestBody()) {
            String buffer = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            String wellFormedBuffer = JsonSanitizer.sanitize(buffer);
            return wellFormedBuffer;
        }
    }

    public static <T> void write(T obj, HttpExchange t) throws IOException {
        String response = new Gson().toJson(obj);
        //DB.addTrace(" --> " + response);
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        t.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(bytes);
        }
    }

}
