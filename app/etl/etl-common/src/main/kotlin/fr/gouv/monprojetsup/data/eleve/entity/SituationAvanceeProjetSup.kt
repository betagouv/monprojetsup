package fr.gouv.monprojetsup.data.eleve.entity

import com.fasterxml.jackson.annotation.JsonValue

enum class SituationAvanceeProjetSup(
    @JsonValue val jsonValeur: String,
) {
    AUCUNE_IDEE(jsonValeur = "aucune_idee"),
    QUELQUES_PISTES(jsonValeur = "quelques_pistes"),
    PROJET_PRECIS(jsonValeur = "projet_precis"), ;

    companion object {
        fun deserialize(s: String?): SituationAvanceeProjetSup? {
            return SituationAvanceeProjetSup.entries.firstOrNull { it.jsonValeur == s }
        }
    }
}
