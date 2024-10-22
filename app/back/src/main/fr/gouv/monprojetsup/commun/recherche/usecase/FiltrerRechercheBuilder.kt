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
            .filterNot { MOTS_VIDES.contains(it) }
    }

    companion object {
        private val MOTS_VIDES =
            listOf(
                "le",
                "la",
                "les",
                "aux",
                "un",
                "une",
                "des",
                "du",
                "des",
                "de",
                "en",
                "sur",
                "sous",
                "dans",
                "chez",
                "par",
                "pour",
                "avec",
                "sans",
                "contre",
                "entre",
                "parmi",
                "vers",
                "derrière",
                "devant",
                "après",
                "avant",
                "autour",
                "et",
                "ou",
                "mais",
                "donc",
                "ni",
                "car",
                "que",
                "quand",
                "comme",
                "puisque",
                "quoique",
                "mon",
                "ma",
                "mes",
                "ton",
                "ta",
                "tes",
                "son",
                "sa",
                "ses",
                "notre",
                "nos",
                "votre",
                "vos",
                "leur",
                "leurs",
                "ce",
                "cet",
                "cette",
                "ces",
                "qui",
                "que",
                "quoi",
                "dont",
                "lequel",
                "laquelle",
                "lesquels",
                "lesquelles",
            )
    }
}
