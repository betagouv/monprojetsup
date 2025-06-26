package fr.gouv.monprojetsup.data.etl

import fr.gouv.monprojetsup.data.RemoteFileAccess.Companion.writeRemoteStreamToDir
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component


fun main(args: Array<String>) {
	runApplication<UpdatePsupData>(*args)
}

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class ] )
open class UpdatePsupData


@Component
@Slf4j
class Runner : CommandLineRunner {


	@Value("\${dataRootDirectory}")
	lateinit var dataRootDirectory : String

	@Value("\${mps.data.ideo.od.formations.fiches.uri}")
	lateinit var formationsfichesUri : String
	@Value("\${mps.data.ideo.od.formations.simple.uri}")
	lateinit var formationsSimpleUri : String
	@Value("\${mps.data.ideo.od.metiers.simple.uri}")
	lateinit var metiersSimplesuri : String
	@Value("\${mps.data.ideo.od.metiers.fiches.uri}")
	lateinit var metiersFichesUri : String
	@Value("\${mps.data.ideo.od.domaines.uri}")
	lateinit var ideoOdDomainesUri : String

	override fun run(vararg args: String?) {

		listOf(metiersFichesUri, formationsfichesUri, formationsSimpleUri, metiersSimplesuri, ideoOdDomainesUri)
			.forEach { uri -> writeRemoteStreamToDir(uri, "$dataRootDirectory/data/ideo") }
	}



}

