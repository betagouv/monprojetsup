package fr.gouv.monprojetsup.recherche.domain.entity

enum class ChoixNiveau(val jsonValeur: String, val apiSuggestionValeur: String) {
    SECONDE(jsonValeur = "seconde", apiSuggestionValeur = "sec"),
    PREMIERE(jsonValeur = "premiere", apiSuggestionValeur = "prem"),
    TERMINALE(jsonValeur = "terminale", apiSuggestionValeur = "term"),
    NON_RENSEIGNE(jsonValeur = "NC", apiSuggestionValeur = ""),
    ;

    companion object {
        fun deserialiseApplication(valeur: String?): ChoixNiveau {
            return ChoixNiveau.entries.firstOrNull { it.jsonValeur == valeur } ?: NON_RENSEIGNE
        }
    }
}
