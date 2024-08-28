package fr.gouv.monprojetsup.data.etl

import fr.gouv.monprojetsup.data.psup.ConnecteurBackendSQL
import fr.gouv.monprojetsup.data.tools.Serialisation
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.sql.DriverManager


fun main(args: Array<String>) {
	runApplication<UpdatePsupData>(*args)
}

@SpringBootApplication
class UpdatePsupData

@Value("\${dataRootDirectory}")
private val dataRootDirectory = "./"

@Value("\${psup.url}")
private val psupUrl : String? = null

@Value("\${psup.username}")
private val psupUsername : String? = null

@Value("\${psup.password}")
private val psupPassword : String? = null

fun getSourceDataFilePath(filename: String): String {
	val pathWithSpace = dataRootDirectory + "data/" + filename
	val path = Path.of(pathWithSpace)
	return path.toString()
}

@Component
@Slf4j
@Profile("!test")
class Runner : CommandLineRunner {


	private val logger = LoggerFactory.getLogger(UpdatePsupData::class.java)

	companion object {
		const val STATS_BACK_SRC_FILENAME = "parcoursup/statistiques.zip"
		const val FRONT_MID_SRC_PATH = "parcoursup/data_mid.zip"
		const val BACK_PSUP_DATA_FILENAME = "parcoursup/backPsupData.json.zip"
	}

	override fun run(vararg args: String?) {

		DriverManager.getConnection(psupUrl, psupUsername, psupPassword)
		.use { co ->
			val conn = ConnecteurBackendSQL(co)
			logger.info("Récupération des données")
			val stats = conn.recupererStatistiques()

			logger.info("Export du full data set")
			Serialisation.toZippedJson(
				getSourceDataFilePath(STATS_BACK_SRC_FILENAME),
				stats,
				true
			)

			logger.info("Export du legacy front data set")
			stats.minimize()
			Serialisation.toZippedJson(
				getSourceDataFilePath(FRONT_MID_SRC_PATH),
				stats,
				true
			)

			logger.info("Récupération des données backend autres que les stats")
			val data = conn.recupererData()
			logger.info("Export du backend data au format json  ")
			Serialisation.toZippedJson(
				getSourceDataFilePath(BACK_PSUP_DATA_FILENAME),
				data,
				true
			)
		}


	}



}


@Component
@Profile("test")
class TestRunner : CommandLineRunner {
	override fun run(vararg args: String?) {
	}
}
