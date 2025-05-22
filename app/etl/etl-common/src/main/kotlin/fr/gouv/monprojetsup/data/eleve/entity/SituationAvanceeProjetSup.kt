package fr.gouv.monprojetsup.data.eleve.entity

enum class SituationAvanceeProjetSup(
) {
    AUCUNE_IDEE,
    QUELQUES_PISTES,
    PROJET_PRECIS, ;

    companion object {
        fun deserialize(s: String?): SituationAvanceeProjetSup? {
            return SituationAvanceeProjetSup.entries.firstOrNull { it.name == s }
        }
    }
}
