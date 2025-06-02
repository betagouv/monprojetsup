package fr.gouv.monprojetsup.data.eleve.entity

enum class ChoixDureeEtudesPrevue {
    INDIFFERENT,
    COURTE,
    LONGUE,
    AUCUNE_IDEE,
    ;

    companion object {
        fun deserialise(valeur: String?): ChoixDureeEtudesPrevue? {
            return ChoixDureeEtudesPrevue.entries.firstOrNull { it.name == valeur }
        }
    }
}
