package fr.gouv.monprojetsup.suggestions.export.ml

import fr.gouv.monprojetsup.suggestions.export.ml.GenerateMlDocs
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Main (
    val generateMlDocs: GenerateMlDocs
) : CommandLineRunner {

    override fun run(vararg args: String?) {

        generateMlDocs.outputMlData()

        generateMlDocs.outputDoDiff()

    }

}