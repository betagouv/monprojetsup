package fr.gouv.monprojetsup.suggestions.export

import fr.gouv.monprojetsup.suggestions.export.experts.SuggestionsEvaluator
import fr.gouv.monprojetsup.suggestions.export.experts.SuggestionsGenerator
import fr.gouv.monprojetsup.suggestions.export.reference.AnalyzeSuggestionsData
import fr.gouv.monprojetsup.suggestions.export.reference.ExportSuggestionsData
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Profile


fun main(args: Array<String>) {
    runApplication<Main>(*args)
}

@SpringBootApplication
class Main

@Profile("export")
class ExportRunner(
    val exportSuggestionsData: ExportSuggestionsData,
    val analyzeSuggestionsData: AnalyzeSuggestionsData
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        exportSuggestionsData.export()
        analyzeSuggestionsData.analyze()
    }
}

@Profile("experts_evaluate")
class Evaluate(val eval : SuggestionsEvaluator) : CommandLineRunner
{
    override fun run(vararg args: String?) {
        eval.evaluate()
    }

}

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
