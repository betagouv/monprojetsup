package fr.gouv.monprojetsup.referentiel.domain.entity

enum class SituationAvanceeProjetSup(val jsonValeur: String) {
    AUCUNE_IDEE(jsonValeur = "aucune_idee"),
    QUELQUES_PISTES(jsonValeur = "quelques_pistes"),
    PROJET_PRECIS(jsonValeur = "projet_precis"),
    ;

    companion object {
        fun deserialiseApplication(valeur: String?): SituationAvanceeProjetSup {
            return SituationAvanceeProjetSup.entries.first { it.jsonValeur == valeur }
        }
    }
}
