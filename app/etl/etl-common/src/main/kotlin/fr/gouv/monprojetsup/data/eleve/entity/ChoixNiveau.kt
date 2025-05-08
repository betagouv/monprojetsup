package fr.gouv.monprojetsup.data.eleve.entity

enum class ChoixNiveau {
    SECONDE,
    PREMIERE,
    TERMINALE;

    companion object {
        fun deserialize(s: String?): ChoixNiveau? {
            return ChoixNiveau.entries.firstOrNull { it.name == s }
        }
    }

}
