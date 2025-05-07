package fr.gouv.monprojetsup.data.eleve.entity

import com.fasterxml.jackson.annotation.JsonValue

enum class ChoixDureeEtudesPrevue(
    @JsonValue val jsonValeur: String,
    val apiSuggestionValeur: String,
) {
    INDIFFERENT(jsonValeur = "indifferent", apiSuggestionValeur = "indiff"),
    COURTE(jsonValeur = "courte", apiSuggestionValeur = "court"),
    LONGUE(jsonValeur = "longue", apiSuggestionValeur = "long"),
    AUCUNE_IDEE(jsonValeur = "aucune_idee", apiSuggestionValeur = ""),
    ;

    companion object {
        fun deserialise(valeur: String?): ChoixDureeEtudesPrevue? {
            return ChoixDureeEtudesPrevue.entries.firstOrNull { it.apiSuggestionValeur == valeur }
        }
    }
}
