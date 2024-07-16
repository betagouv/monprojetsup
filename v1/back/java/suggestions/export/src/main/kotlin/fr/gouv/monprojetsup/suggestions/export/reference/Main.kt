package fr.gouv.monprojetsup.suggestions.export.reference

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class Main(
    val exportSuggestionsData: ExportSuggestionsData,
    val analyzeSuggestionsData: AnalyzeSuggestionsData
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        exportSuggestionsData.export()
        analyzeSuggestionsData.analyze()
    }
}