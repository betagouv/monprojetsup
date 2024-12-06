package fr.gouv.monprojetsup.data.etl

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
@Profile("!test")
class Runner : CommandLineRunner {


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
		const val BACK_PSUP_DATA_FILENAME = "parcoursup/psupDataBack.zip"
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
			val data = conn.recupererData(specialites.eds.keys)

			logger.info("Export du backend data au format json  ")
			Serialisation.toZippedJson(
				getSourceDataFilePath(BACK_PSUP_DATA_FILENAME),
				data,
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
@Profile("test")
class TestRunner : CommandLineRunner {
	override fun run(vararg args: String?) {
	}
}
