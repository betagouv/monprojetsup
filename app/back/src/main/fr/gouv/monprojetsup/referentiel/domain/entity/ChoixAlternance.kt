package fr.gouv.monprojetsup.referentiel.domain.entity

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException

enum class ChoixAlternance(val jsonValeur: String, val apiSuggestionValeur: String) {
    PAS_INTERESSE(jsonValeur = "pas_interesse", apiSuggestionValeur = "D"),
    INDIFFERENT(jsonValeur = "indifferent", apiSuggestionValeur = "C"),
    INTERESSE(jsonValeur = "interesse", apiSuggestionValeur = "B"),
    TRES_INTERESSE(jsonValeur = "tres_interesse", apiSuggestionValeur = "A"),
    NON_RENSEIGNE(jsonValeur = "NC", apiSuggestionValeur = ""),
    ;

    companion object {
        fun deserialiseAPISuggestion(valeur: String?): ChoixAlternance {
            try {
                return ChoixAlternance.entries.first { it.apiSuggestionValeur == valeur }
            } catch (e: Exception) {
                throw MonProjetSupInternalErrorException(
                    code = "ERREUR_ DESERIALISATION_API_CHOIX_ALTERNANCE",
                    msg = "Une erreur s'est produite lors de la désérialisation du choix d'alternance $valeur côté API suggestion",
                    origine = e,
                )
            }
        }

        fun deserialiseApplication(valeur: String): ChoixAlternance {
            try {
                return ChoixAlternance.entries.first { it.jsonValeur == valeur }
            } catch (e: Exception) {
                throw MonProjetSupBadRequestException(
                    code = "ERREUR_ DESERIALISATION_APPLICATION_CHOIX_ALTERNANCE",
                    msg =
                        "La valeur $valeur n'est pas une valeur reconnue par l'application. Les possibilités sont les suivantes : " +
                            "${ChoixAlternance.entries.map { it.jsonValeur }}",
                    origine = e,
                )
            }
        }
    }
}
