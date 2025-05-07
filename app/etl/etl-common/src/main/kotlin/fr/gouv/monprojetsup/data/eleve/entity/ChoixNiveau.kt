package fr.gouv.monprojetsup.data.eleve.entity

import com.fasterxml.jackson.annotation.JsonValue

enum class ChoixNiveau(
    @JsonValue val jsonValeur: String,
    val apiSuggestionValeur: String,
) {
    SECONDE(jsonValeur = "seconde", apiSuggestionValeur = "sec"),
    PREMIERE(jsonValeur = "premiere", apiSuggestionValeur = "prem"),
    TERMINALE(jsonValeur = "terminale", apiSuggestionValeur = "term");

    companion object {
        fun deserialize(s: String?): ChoixNiveau? {
            return ChoixNiveau.entries.firstOrNull { it.jsonValeur == s }
        }
    }

}
