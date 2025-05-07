package fr.gouv.monprojetsup.data.eleve.entity

import com.fasterxml.jackson.annotation.JsonValue

enum class ChoixAlternance(
    @JsonValue val jsonValeur: String,
    val apiSuggestionValeur: String,
) {
    PAS_INTERESSE(jsonValeur = "pas_interesse", apiSuggestionValeur = "D"),
    INDIFFERENT(jsonValeur = "indifferent", apiSuggestionValeur = "C"),
    INTERESSE(jsonValeur = "interesse", apiSuggestionValeur = "B"),
    TRES_INTERESSE(jsonValeur = "tres_interesse", apiSuggestionValeur = "A"),
    ;

    companion object {
        fun deserialise(valeur: String?): ChoixAlternance? {
            return ChoixAlternance.entries.firstOrNull { it.jsonValeur == valeur }
        }
    }
}
