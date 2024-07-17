package fr.gouv.monprojetsup.suggestions.export.ml.fr.gouv.monprojetsup.suggestions.export.experts

import fr.gouv.monprojetsup.suggestions.export.experts.DocsGenerator
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class GenerateDocs(val gen : DocsGenerator) : CommandLineRunner
{
    override fun run(vararg args: String?) {
        gen.generate()
    }

}