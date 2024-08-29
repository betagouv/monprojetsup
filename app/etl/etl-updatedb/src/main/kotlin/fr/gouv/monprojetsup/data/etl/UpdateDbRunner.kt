package fr.gouv.monprojetsup.data.etl

import fr.gouv.monprojetsup.data.etl.formation.UpdateFormationDbs
import fr.gouv.monprojetsup.data.etl.metier.UpdateMetierDbs
import fr.gouv.monprojetsup.data.etl.referentiel.UpdateReferentielDbs
import fr.gouv.monprojetsup.data.etl.suggestions.UpdateSuggestionsDbs
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Component


fun main(args: Array<String>) {
	runApplication<UpdateDbRunner>(*args)
}

@ComponentScan(basePackages = ["fr.gouv.monprojetsup"])
@SpringBootApplication
@EntityScan(basePackages = [
	"fr.gouv.monprojetsup.data"]
)
@EnableJpaRepositories
class UpdateDbRunner  {

}

@Component
@Profile("!test")
class Runner(
	private val updateFormationRepositories: UpdateFormationDbs,
	private val updateReferentielDbs: UpdateReferentielDbs,
	private val updateMetierDbs: UpdateMetierDbs,
	private val updateSuggestionsDbs: UpdateSuggestionsDbs
) : CommandLineRunner {

	private val logger = java.util.logging.Logger.getLogger(Runner::class.java.simpleName)

	override fun run(vararg args: String?) {

		//clearAll in this order to avoid foreign key constraint errors
		updateFormationRepositories.clearAll()
		updateMetierDbs.clearAll()
		updateSuggestionsDbs.clearAll()
		updateReferentielDbs.clearAll()

		logger.info("Updating referentiel dbs")
		updateReferentielDbs.updateReferentielDbs()

		logger.info("Updating formations dbs")
		updateFormationRepositories.updateFormationDbs()

		logger.info("Updating metiers dbs")
		updateMetierDbs.updateMetierDbs()

		logger.info("Updating suggestions dbs")
		updateSuggestionsDbs.updateSuggestionDbs()
	}
}

@Component
@Profile("test")
class TestRunner : CommandLineRunner {
	override fun run(vararg args: String?) {
	}
}

