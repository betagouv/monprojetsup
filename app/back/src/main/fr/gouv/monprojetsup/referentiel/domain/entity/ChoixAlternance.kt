package fr.gouv.monprojetsup.referentiel.domain.entity

import com.fasterxml.jackson.annotation.JsonValue
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException

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
    }
}
