package fr.gouv.monprojetsup.data.app.domain

enum class ChoixAlternance(val jsonValeur: String, val apiSuggestionValeur: String) {
    PAS_INTERESSE(jsonValeur = "pas_interesse", apiSuggestionValeur = "D"),
    INDIFFERENT(jsonValeur = "indifferent", apiSuggestionValeur = "C"),
    INTERESSE(jsonValeur = "interesse", apiSuggestionValeur = "B"),
    TRES_INTERESSE(jsonValeur = "tres_interesse", apiSuggestionValeur = "A"),
    NON_RENSEIGNE(jsonValeur = "NC", apiSuggestionValeur = ""),
    ;

    companion object {
        fun deserialiseAPISuggestion(valeur: String?): fr.gouv.monprojetsup.data.app.domain.ChoixAlternance {
            return fr.gouv.monprojetsup.data.app.domain.ChoixAlternance.entries.firstOrNull { it.apiSuggestionValeur == valeur } ?: fr.gouv.monprojetsup.data.app.domain.ChoixAlternance.NON_RENSEIGNE
        }

        fun deserialiseApplication(valeur: String?): fr.gouv.monprojetsup.data.app.domain.ChoixAlternance {
            return fr.gouv.monprojetsup.data.app.domain.ChoixAlternance.entries.firstOrNull { it.jsonValeur == valeur } ?: fr.gouv.monprojetsup.data.app.domain.ChoixAlternance.NON_RENSEIGNE
        }
    }
}
