package fr.gouv.monprojetsup.data.app.domain

enum class ChoixNiveau(val jsonValeur: String, val apiSuggestionValeur: String) {
    SECONDE(jsonValeur = "seconde", apiSuggestionValeur = "sec"),
    PREMIERE(jsonValeur = "premiere", apiSuggestionValeur = "prem"),
    TERMINALE(jsonValeur = "terminale", apiSuggestionValeur = "term"),
    NON_RENSEIGNE(jsonValeur = "NC", apiSuggestionValeur = ""),
    ;

    companion object {
        fun deserialiseApplication(valeur: String?): ChoixNiveau {
            return ChoixNiveau.entries.firstOrNull { it.jsonValeur == valeur } ?: ChoixNiveau.NON_RENSEIGNE
        }
    }
}
