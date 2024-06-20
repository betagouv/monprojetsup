package fr.gouv.monprojetsup.recherche.domain.entity

enum class ChoixAlternance(val jsonValeur: String, val apiSuggestionValeur: String) {
    PAS_INTERESSE(jsonValeur = "pas_interesse", apiSuggestionValeur = "D"),
    INDIFFERENT(jsonValeur = "indifferent", apiSuggestionValeur = "C"),
    INTERESSE(jsonValeur = "interesse", apiSuggestionValeur = "B"),
    TRES_INTERESSE(jsonValeur = "tres_interesse", apiSuggestionValeur = "A"),
    NON_RENSEIGNE(jsonValeur = "NC", apiSuggestionValeur = ""),
    ;

    companion object {
        fun deserialiseAPISuggestion(valeur: String?): ChoixAlternance {
            return ChoixAlternance.entries.firstOrNull { it.apiSuggestionValeur == valeur } ?: NON_RENSEIGNE
        }

        fun deserialiseApplication(valeur: String?): ChoixAlternance {
            return ChoixAlternance.entries.firstOrNull { it.jsonValeur == valeur } ?: NON_RENSEIGNE
        }
    }
}
