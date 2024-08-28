package fr.gouv.monprojetsup.data.etl

import fr.gouv.monprojetsup.data.psup.*
import fr.gouv.monprojetsup.data.tools.Serialisation
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path


fun main(args: Array<String>) {
	runApplication<UpdatePsupData>(*args)
}

@SpringBootApplication
class UpdatePsupData  {

}

@Component
@Slf4j
@Profile("!test")
class Runner(
	private val dataSources: PsupDataSources
) : CommandLineRunner {


	private val logger = LoggerFactory.getLogger(UpdatePsupData::class.java)


	override fun run(vararg args: String?) {}

	/**
	 * generates BACK_PSUP_DATA_FILENAME
	 * @throws Exception
	 */
	@Throws(Exception::class)
	private fun getBackDataFromPsupDB() {
		val config = OrientationConfig.fromFile()

		ConnecteurSQLHelper.getConnecteur(config.statsDB).use { co ->
			val conn = ConnecteurBackendSQL(co)

			logger.info("Récupération des données")
			val data = conn.recupererData()
			logger.info("Export du backend data au format json  ")
			Serialisation.toZippedJson(
				dataSources.getSourceDataFilePath(PsupDataSources.BACK_PSUP_DATA_FILENAME),
				data,
				true
			)
		}
	}


	@Throws(Exception::class)
	private fun getStatistiquesFromPsupDB() {

		val config = OrientationConfig.fromFile()

		//config.save();
		val path: String = dataSources.getSourceDataFilePath(PsupDataSources.TAGS_SOURCE_RAW_FILENAME)
		if (!Files.exists(Path.of(path))) {
			throw RuntimeException("No file $path")
		}


		ConnecteurSQLHelper.getConnecteur(config.statsDB).use { co ->
			val conn = ConnecteurBackendSQL(co)
			logger.info("Récupération des données")
			val stats = conn.recupererStatistiquesEtMotsCles()


			logger.info("Export du full data set")
			Serialisation.toZippedJson(
				dataSources.getSourceDataFilePath(PsupDataSources.STATS_BACK_SRC_FILENAME),
				stats,
				true
			)

			logger.info("Export du legacy front data set")
			stats.minimize()
			Serialisation.toZippedJson(
				dataSources.getSourceDataFilePath(PsupDataSources.FRONT_MID_SRC_PATH),
				stats,
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
