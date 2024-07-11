package fr.gouv.monprojetsup.suggestions

import fr.gouv.monprojetsup.suggestions.analysis.ExportSuggestionsData
import fr.gouv.monprojetsup.suggestions.data.SuggestionsData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication


@SpringBootApplication
class ExportSuggestionsDataApp : CommandLineRunner {

	@Autowired
	lateinit var export : ExportSuggestionsData

	override fun run(vararg args: String?) {
		export.export()
	}

}


