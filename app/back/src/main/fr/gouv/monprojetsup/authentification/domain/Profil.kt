package fr.gouv.monprojetsup.authentification.domain

enum class Profil(val jsonValeur: String) {
    ELEVE("APP-SEC"),
    ENSEIGNANT("APP-EDU"),
    INCONNU(""), ;

    companion object {
        fun deserialise(valeur: String?): Profil {
            return Profil.entries.firstOrNull { it.jsonValeur == valeur } ?: INCONNU
        }
    }
}
