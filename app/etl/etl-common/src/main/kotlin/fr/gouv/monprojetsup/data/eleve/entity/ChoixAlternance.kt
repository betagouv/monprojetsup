package fr.gouv.monprojetsup.data.eleve.entity

enum class ChoixAlternance {
    PAS_INTERESSE,
    INDIFFERENT,
    INTERESSE,
    TRES_INTERESSE,
    ;

    companion object {
        fun deserialise(valeur: String?): ChoixAlternance? {
            return ChoixAlternance.entries.firstOrNull { it.name == valeur }
        }
    }
}
