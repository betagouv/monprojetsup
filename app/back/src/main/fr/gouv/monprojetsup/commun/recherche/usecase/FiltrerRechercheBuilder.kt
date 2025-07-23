package fr.gouv.monprojetsup.commun.recherche.usecase

import fr.gouv.monprojetsup.commun.Constantes.REGEX_NON_ALPHA_NUMERIC_AVEC_ACCENT
import org.springframework.stereotype.Component

@Component
class FiltrerRechercheBuilder {
    fun filtrerMotsRecherches(
        recherche: String,
        tailleMinimumRecherche: Int,
    ): List<String> {
        val regexNonAlphaNumericAvecAccent = Regex(REGEX_NON_ALPHA_NUMERIC_AVEC_ACCENT)
        return recherche
            .split(regexNonAlphaNumericAvecAccent)
            .distinct()
            .filter { it.length >= tailleMinimumRecherche }
    }
}
