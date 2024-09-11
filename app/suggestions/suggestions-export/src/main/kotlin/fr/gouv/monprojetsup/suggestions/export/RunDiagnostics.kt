package fr.gouv.monprojetsup.suggestions.export

import fr.gouv.monprojetsup.suggestions.export.experts.SuggestionsEvaluator
import fr.gouv.monprojetsup.suggestions.export.experts.SuggestionsGenerator
import fr.gouv.monprojetsup.suggestions.export.reference.AuditSuggestionsData
import fr.gouv.monprojetsup.suggestions.export.reference.ExportSuggestionsData
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
    "fr.gouv.monprojetsup.data"]
)
@EnableJpaRepositories(basePackages = [
    "fr.gouv.monprojetsup.suggestions"]
)
class JpaConfig

@Component
class DefaultExportRunner(
    val diagnoseSuggestionsData: AuditSuggestionsData
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        diagnoseSuggestionsData.outputDiagnostics()
    }
}

@Component
@Profile("export")
class Export(val exportSuggestionsData: ExportSuggestionsData) : CommandLineRunner
{
    override fun run(vararg args: String?) {
        exportSuggestionsData.export()
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
        var filename = SuggestionsGenerator.PROFILS_EXPERTS_MPS_PATH;
        if(args.isNotEmpty() && args[0] != null) {
            filename = args[0]!!
        }
        gen.generate(filename)
    }

}
