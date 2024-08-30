package fr.gouv.monprojetsup.referentiel.domain.entity

import com.fasterxml.jackson.annotation.JsonValue
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException

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
        fun deserialiseAPISuggestion(valeur: String?): ChoixDureeEtudesPrevue {
            try {
                return ChoixDureeEtudesPrevue.entries.first { it.apiSuggestionValeur == valeur }
            } catch (e: Exception) {
                throw MonProjetSupInternalErrorException(
                    code = "ERREUR_ DESERIALISATION_API_CHOIX_DUREE_ETUDE",
                    msg = "Une erreur s'est produite lors de la désérialisation du choix de la durée d'étude $valeur côté API suggestion",
                    origine = e,
                )
            }
        }
    }
}
