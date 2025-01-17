package fr.gouv.monprojetsup.data.etl

import fr.gouv.monprojetsup.data.etl.DefaultRunner.Companion.BACK_PSUP_DATA_FILENAME
import fr.gouv.monprojetsup.data.etl.DefaultRunner.Companion.FULL_BACK_PSUP_DATA_FILENAME
import fr.gouv.monprojetsup.data.etl.DefaultRunner.Companion.PSUP_STATS_FILENAME
import fr.gouv.monprojetsup.data.model.psup.PsupData
import fr.gouv.monprojetsup.data.model.specialites.Specialites
import fr.gouv.monprojetsup.data.psup.ConnecteurBackendSQL
import fr.gouv.monprojetsup.data.tools.Serialisation
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.nio.file.Path
import java.sql.DriverManager


fun main(args: Array<String>) {
	runApplication<UpdatePsupData>(*args)
}

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class ] )
open class UpdatePsupData



@Component
@Slf4j
@Profile("default")
class DefaultRunner : CommandLineRunner {


	private val logger = LoggerFactory.getLogger(UpdatePsupData::class.java)

	@Value("\${dataRootDirectory}")
	lateinit var dataRootDirectory : String

	@Value("\${psup.url}")
	lateinit var psupUrl : String

	@Value("\${psup.username}")
	lateinit var psupUsername : String

	@Value("\${psup.password}")
	lateinit var psupPassword : String

	companion object {
		const val FULL_BACK_PSUP_DATA_FILENAME = "parcoursup/psupDataBack.zip"
		const val PSUP_STATS_FILENAME = "parcoursup/psupStats.zip"
		const val BACK_PSUP_DATA_FILENAME = "parcoursup/psupDataBackNoStats.zip"
	}


	override fun run(vararg args: String?) {

		DriverManager.getConnection(psupUrl, psupUsername, psupPassword)
		.use { co ->
			val conn = ConnecteurBackendSQL(co)

			val specialites: Specialites =
				Serialisation.fromJsonFile(
					getSourceDataFilePath("parcoursup/specialites.json"),
					Specialites::class.java
				)

			logger.info("Récupération des données backend autres que les stats")
			val psupData = conn.recupererData(specialites.eds.keys)

			logger.info("Export du backend data au format json  ")
			val fullBackDataFilename = getSourceDataFilePath(FULL_BACK_PSUP_DATA_FILENAME)
			val statsFilename = getSourceDataFilePath(PSUP_STATS_FILENAME)
			val backDataFilename = getSourceDataFilePath(BACK_PSUP_DATA_FILENAME)

			Serialisation.toZippedJson(
				fullBackDataFilename,
				psupData,
				true
			)

			logger.info("Export des stats au format json  ")
			Serialisation.toZippedJson(
				statsFilename,
				psupData.stats,
				true
			)

			logger.info("Minimisation des données")
			psupData.keepOnlyBackData();

			logger.info("Export des données back au format json  ")
			Serialisation.toZippedJson(
				backDataFilename,
				psupData,
				true
			)

		}

	}


	fun getSourceDataFilePath(filename: String): String {
		val path = Path.of(dataRootDirectory, filename)
		return path.toString()
	}

}


@Component
@Profile("split", "!test", )
class SplitRunner : CommandLineRunner {

	private val logger = LoggerFactory.getLogger(UpdatePsupData::class.java)

	@Value("\${dataRootDirectory}")
	lateinit var dataRootDirectory : String

	override fun run(vararg args: String?) {

		val fullBackDataFilename = getSourceDataFilePath(FULL_BACK_PSUP_DATA_FILENAME)
		val statsFilename = getSourceDataFilePath(PSUP_STATS_FILENAME)
		val backDataFilename = getSourceDataFilePath(BACK_PSUP_DATA_FILENAME)

		logger.info("Chargement de " + getSourceDataFilePath(FULL_BACK_PSUP_DATA_FILENAME))
		val psupData = Serialisation.fromLargeZippedJson(
			Path.of(fullBackDataFilename),
			PsupData::class.java
		)

		logger.info("Export des stats au format json  ")
		Serialisation.toZippedJson(
			statsFilename,
			psupData.stats,
			true
		)

		logger.info("Minimisation des données")
		psupData.keepOnlyBackData();

		logger.info("Export des données back au format json  ")
		Serialisation.toZippedJson(
			backDataFilename,
			psupData,
			true
		)


	}


	fun getSourceDataFilePath(filename: String): String {
		val path = Path.of(dataRootDirectory, filename)
		return path.toString()
	}

}

@Component
@Profile("test", "!split")
class TestRunner : CommandLineRunner {
	override fun run(vararg args: String?) {
	}
}
