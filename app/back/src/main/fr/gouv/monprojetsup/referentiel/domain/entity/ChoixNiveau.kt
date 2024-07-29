package fr.gouv.monprojetsup.referentiel.domain.entity

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException

enum class ChoixNiveau(val jsonValeur: String, val apiSuggestionValeur: String) {
    SECONDE(jsonValeur = "seconde", apiSuggestionValeur = "sec"),
    PREMIERE(jsonValeur = "premiere", apiSuggestionValeur = "prem"),
    TERMINALE(jsonValeur = "terminale", apiSuggestionValeur = "term"),
    NON_RENSEIGNE(jsonValeur = "NC", apiSuggestionValeur = ""),
    ;

    companion object {
        fun deserialiseApplication(valeur: String): ChoixNiveau {
            try {
                return ChoixNiveau.entries.first { it.jsonValeur == valeur }
            } catch (e: Exception) {
                throw MonProjetSupBadRequestException(
                    code = "ERREUR_ DESERIALISATION_APPLICATION_CHOIX_NIVEAU",
                    msg =
                        "La valeur $valeur n'est pas une valeur reconnue par l'application. Les possibilités sont les suivantes : " +
                            "${ChoixNiveau.entries.map { it.jsonValeur }}",
                    origine = e,
                )
            }
        }
    }
}
