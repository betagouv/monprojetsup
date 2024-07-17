package fr.gouv.monprojetsup.suggestions.export.ml.fr.gouv.monprojetsup.suggestions.export.experts

import fr.gouv.monprojetsup.suggestions.export.experts.SuggestionsEvaluator
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Evaluate(val eval : SuggestionsEvaluator) : CommandLineRunner
{
    override fun run(vararg args: String?) {
        eval.evaluate()
    }

}