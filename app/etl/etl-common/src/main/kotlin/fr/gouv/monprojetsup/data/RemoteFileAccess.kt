package fr.gouv.monprojetsup.data

import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration

class RemoteFileAccess {

    companion object {

        fun getRemoteStream(urlString: String): InputStream {
            val uri = URI.create(urlString)
            val request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofSeconds(10)) // optional timeout setting
                .header("Content-Type", "application/json")
                .GET()
                .build()
            val client = HttpClient.newHttpClient()
            val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                val body = response.body()
                if (body != null) {
                    return body
                } else {
                    throw RuntimeException("Le corps de la réponse est vide pour l'URL: " + urlString)
                }
            } else {
                throw RuntimeException("Echec de la requête GET à " + urlString + " statut: " + response.statusCode())
            }
        }

        fun writeRemoteStreamToDir(urlString: String, dataDir: String) {
            val stream = getRemoteStream(urlString)
            val dir = Path.of(dataDir)
            val i = urlString.lastIndexOf('/') + 1
            val cacheName: String = dataDir + "/" + urlString.substring(i)
            if (!Files.exists(dir)) Files.createDirectories(dir)
            FileOutputStream(cacheName).use { out ->
                stream.transferTo(out)
            }
        }

    }

}