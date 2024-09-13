package fr.gouv.monprojetsup.data.etl

import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration


fun main(args: Array<String>) {
	runApplication<UpdatePsupData>(*args)
}

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class ] )
open class UpdatePsupData


@Component
@Slf4j
class Runner : CommandLineRunner {


	private val logger = LoggerFactory.getLogger(UpdatePsupData::class.java)

	@Value("\${dataRootDirectory}")
	lateinit var dataRootDirectory : String

	companion object {
		const val IDEO_OD_FORMATIONS_FICHES_URI: String = "https://api.opendata.onisep.fr/downloads/5fe07a9ecc960/5fe07a9ecc960.zip"
		const val IDEO_OD_FORMATIONS_SIMPLE_URI: String = "https://api.opendata.onisep.fr/downloads/5fa591127f501/5fa591127f501.json"
		const val IDEO_OD_METIERS_SIMPLE_URI: String = "https://api.opendata.onisep.fr/downloads/5fa5949243f97/5fa5949243f97.json"
		const val IDEO_OD_METIERS_FICHES_URI: String = "https://api.opendata.onisep.fr/downloads/5fe0808a2da6f/5fe0808a2da6f.zip"
		const val IDEO_OD_DOMAINES_URI: String = "https://api.opendata.onisep.fr/downloads/5fa58d750a60c/5fa58d750a60c.json"
	}

	override fun run(vararg args: String?) {

		listOf(IDEO_OD_METIERS_FICHES_URI, IDEO_OD_FORMATIONS_FICHES_URI, IDEO_OD_FORMATIONS_SIMPLE_URI, IDEO_OD_METIERS_SIMPLE_URI, IDEO_OD_DOMAINES_URI)
			.forEach { uri -> writeRemoteStreamToDir(uri, "$dataRootDirectory/data/ideo") }
	}

	private fun writeRemoteStreamToDir(urlString: String, dataDir : String) {
		val uri = URI.create(urlString)
		val i = urlString.lastIndexOf('/') + 1
		val cacheName: String = dataDir + "/" + urlString.substring(i)
		val dir = Path.of(dataDir)
		val request = HttpRequest.newBuilder()
			.uri(uri)
			.timeout(Duration.ofSeconds(10)) // optional timeout setting
			.header("Content-Type", "application/json")
			.GET() // or use .POST(), .PUT(), etc.
			.build()
		val client = HttpClient.newHttpClient()
		val response = client.send(request, HttpResponse.BodyHandlers.ofInputStream())
		if (response.statusCode() == HttpURLConnection.HTTP_OK) {
			val stream = response.body()
			if (!Files.exists(dir)) Files.createDirectories(dir)
			FileOutputStream(cacheName).use { out ->
				stream.transferTo(out)
			}
		} else {
			throw RuntimeException("Echec de la requête GET à " + urlString + " statut: " + response.statusCode())
		}

	}


}

