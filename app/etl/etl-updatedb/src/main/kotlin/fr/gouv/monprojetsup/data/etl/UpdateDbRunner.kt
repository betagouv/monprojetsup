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
@EnableJpaRepositories(basePackages = [
	"fr.gouv.monprojetsup.data.app.infrastructure",
	"fr.gouv.monprojetsup.data.suggestions.infrastructure"]
)
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
	override fun run(vararg args: String?) {
		updateFormationRepositories.updateFormationDbs()
		updateMetierDbs.updateMetierDbs()
		updateReferentielDbs.updateReferentielDbs()
		updateSuggestionsDbs.updateSuggestionDbs()
	}
}

@Component
@Profile("test")
class TestRunner : CommandLineRunner {
	override fun run(vararg args: String?) {
	}
}

