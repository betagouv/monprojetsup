package fr.gouv.monprojetsup.formation.domain.entity

enum class ChoixDureeEtudesPrevue(val jsonValeur: String, val apiSuggestionValeur: String) {
    INDIFFERENT(jsonValeur = "indifferent", apiSuggestionValeur = "indiff"),
    COURTE(jsonValeur = "courte", apiSuggestionValeur = "court"),
    LONGUE(jsonValeur = "longue", apiSuggestionValeur = "long"),
    AUCUNE_IDEE(jsonValeur = "aucune_idee", apiSuggestionValeur = ""),
    NON_RENSEIGNE(jsonValeur = "NC", apiSuggestionValeur = ""),
    ;

    companion object {
        fun deserialiseAPISuggestion(valeur: String?): ChoixDureeEtudesPrevue {
            return ChoixDureeEtudesPrevue.entries.firstOrNull { it.apiSuggestionValeur == valeur } ?: NON_RENSEIGNE
        }

        fun deserialiseApplication(valeur: String?): ChoixDureeEtudesPrevue {
            return ChoixDureeEtudesPrevue.entries.firstOrNull { it.jsonValeur == valeur } ?: NON_RENSEIGNE
        }
    }
}
