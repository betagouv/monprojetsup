package fr.gouv.monprojetsup.authentification.domain.entity

enum class Profil(val jsonValeur: String) {
    ELEVE(jsonValeur = "APP-SEC"),
    ENSEIGNANT(jsonValeur = "APP-EDU"),
    INCONNU(jsonValeur = ""), ;

    companion object {
        fun deserialise(valeur: String?): Profil {
            return Profil.entries.firstOrNull { it.jsonValeur == valeur } ?: INCONNU
        }
    }
}
