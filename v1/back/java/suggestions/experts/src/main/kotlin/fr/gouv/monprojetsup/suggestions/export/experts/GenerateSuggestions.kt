package fr.gouv.monprojetsup.suggestions.export.ml.fr.gouv.monprojetsup.suggestions.export.experts

import fr.gouv.monprojetsup.suggestions.export.experts.DocsGenerator
import fr.gouv.monprojetsup.suggestions.export.experts.SuggestionsGenerator
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class GenerateSuggestions(val gen : SuggestionsGenerator) : CommandLineRunner
{
    override fun run(vararg args: String?) {
        gen.generate()
    }

}