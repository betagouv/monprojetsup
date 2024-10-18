package fr.gouv.monprojetsup.suggestions.export

import fr.gouv.monprojetsup.suggestions.export.experts.SuggestionsEvaluator
import fr.gouv.monprojetsup.suggestions.export.experts.SuggestionsGenerator
import fr.gouv.monprojetsup.suggestions.export.reference.AuditSuggestionsData
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Component


fun main(args: Array<String>) {
    runApplication<RunDiagnostics>(*args)
}

@SpringBootApplication
class RunDiagnostics


@Configuration
@ComponentScan(basePackages = [
    "fr.gouv.monprojetsup.suggestions",
    "fr.gouv.monprojetsup.suggestions.export",
    "fr.gouv.monprojetsup.suggestions.config"
])
@EntityScan(basePackages = [
    "fr.gouv.monprojetsup.data",
    "fr.gouv.monprojetsup.suggestions.entities"
]
)
@EnableJpaRepositories(basePackages = [
    "fr.gouv.monprojetsup.suggestions"]
)
class JpaConfig

@Component
@Profile("default")
class DefaultExportRunner(
    val diagnoseSuggestionsData: AuditSuggestionsData
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        diagnoseSuggestionsData.outputDiagnostics()
    }
}

@Component
@Profile("experts_evaluate")
class Evaluate(val eval : SuggestionsEvaluator) : CommandLineRunner
{
    override fun run(vararg args: String?) {
        eval.evaluate()
    }

}

@Component
@Profile("experts_docs")
class GenerateSuggestions(val gen : SuggestionsGenerator) : CommandLineRunner
{
    override fun run(vararg args: String?) {
        gen.generate()
    }

}
