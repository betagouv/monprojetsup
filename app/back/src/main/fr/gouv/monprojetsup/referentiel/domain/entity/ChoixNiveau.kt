package fr.gouv.monprojetsup.referentiel.domain.entity

import com.fasterxml.jackson.annotation.JsonValue

enum class ChoixNiveau(
    @JsonValue val jsonValeur: String,
    val apiSuggestionValeur: String,
) {
    SECONDE(jsonValeur = "seconde", apiSuggestionValeur = "sec"),
    PREMIERE(jsonValeur = "premiere", apiSuggestionValeur = "prem"),
    TERMINALE(jsonValeur = "terminale", apiSuggestionValeur = "term"),
}
