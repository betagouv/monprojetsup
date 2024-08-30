package fr.gouv.monprojetsup.referentiel.domain.entity

import com.fasterxml.jackson.annotation.JsonValue

enum class SituationAvanceeProjetSup(
    @JsonValue val jsonValeur: String,
) {
    AUCUNE_IDEE(jsonValeur = "aucune_idee"),
    QUELQUES_PISTES(jsonValeur = "quelques_pistes"),
    PROJET_PRECIS(jsonValeur = "projet_precis"),
}
