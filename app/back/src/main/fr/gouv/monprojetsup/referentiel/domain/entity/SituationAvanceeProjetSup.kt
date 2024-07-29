package fr.gouv.monprojetsup.referentiel.domain.entity

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException

enum class SituationAvanceeProjetSup(val jsonValeur: String) {
    AUCUNE_IDEE(jsonValeur = "aucune_idee"),
    QUELQUES_PISTES(jsonValeur = "quelques_pistes"),
    PROJET_PRECIS(jsonValeur = "projet_precis"),
    ;

    companion object {
        fun deserialiseApplication(valeur: String): SituationAvanceeProjetSup {
            try {
                return SituationAvanceeProjetSup.entries.first { it.jsonValeur == valeur }
            } catch (e: Exception) {
                throw MonProjetSupBadRequestException(
                    code = "ERREUR_ DESERIALISATION_APPLICATION_CHOIX_AVANCEE_PROJET",
                    msg =
                        "La valeur $valeur n'est pas une valeur reconnue par l'application. Les possibilités sont les suivantes : " +
                            "${SituationAvanceeProjetSup.entries.map { it.jsonValeur }}",
                    origine = e,
                )
            }
        }
    }
}
