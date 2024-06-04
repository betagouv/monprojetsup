package fr.gouv.monprojetsup.recherche.domain.entity

enum class ChoixNiveau(val jsonValeur: String, val apiSuggestionValeur: String) {
    SECONDE(jsonValeur = "seconde", apiSuggestionValeur = "sec"),
    SECONDE_STHR(jsonValeur = "seconde_sthr", apiSuggestionValeur = "secSTHR"),
    SECONDE_TMD(jsonValeur = "seconde_tmd", apiSuggestionValeur = "secTMD"),
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
