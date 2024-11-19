package fr.gouv.monprojetsup.data.etl

import fr.gouv.monprojetsup.data.etl.formation.UpdateFormationDbs
import fr.gouv.monprojetsup.data.etl.formationmetier.UpdateFormationMetierDbs
import fr.gouv.monprojetsup.data.etl.metier.UpdateMetierDbs
import fr.gouv.monprojetsup.data.etl.parametre.UpdateParametreDb
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
	runApplication<UpdateMpsDbRunner>(*args)
}

@ComponentScan(basePackages = ["fr.gouv.monprojetsup"])
@SpringBootApplication
@EntityScan(basePackages = [
	"fr.gouv.monprojetsup.data"]
)
@EnableJpaRepositories
open class UpdateMpsDbRunner

@Component
@Profile("!test")
open class Runner(
	private val updateFormationDbs: UpdateFormationDbs,
	private val updateReferentielDbs: UpdateReferentielDbs,
	private val updateMetierDbs: UpdateMetierDbs,
	private val updateSuggestionsDbs: UpdateSuggestionsDbs,
	private val updateFormationsMetiersDbs: UpdateFormationMetierDbs,
	private val updateParametreDb: UpdateParametreDb,
	private val mpsDataPort: MpsDataPort


) : CommandLineRunner {

	private val logger = java.util.logging.Logger.getLogger(Runner::class.java.simpleName)

	override fun run(vararg args: String?) {

		try {
			logger.info("Début de la mise à jour")
			updateParametreDb.setEtlEnCours(true)

			logger.info("Création des fichiers de diagnostic")
			mpsDataPort.exportDiagnostics()

			logger.info("Mise à jour des référentiel")
			updateReferentielDbs.updateReferentielDbs()

			logger.info("Mise à jour des formations")
			updateFormationDbs.update()

			logger.info("Mise à jour des métiers")
			updateMetierDbs.update()

			logger.info("Mise à jour des liens formations metiers")
			updateFormationsMetiersDbs.update()//after formations ert metiers

			val voeuxOntChange = updateFormationDbs.checkForcedUpdate()
			logger.info("Mise à jour des suggestions")
			updateSuggestionsDbs.updateSuggestionDbs(voeuxOntChange)
		} finally {
			updateParametreDb.setFormationUpdateForcedFlag(false)
			updateParametreDb.setEtlEnCours(false)
			logger.info("Fin de la mise à jour")
		}

	}

}

@Component
@Profile("test")
class TestRunner : CommandLineRunner {
	override fun run(vararg args: String?) {
		println("Test profile: skipping update of the database.")
	}
}

