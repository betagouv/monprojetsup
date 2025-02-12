package fr.gouv.monprojetsup.eleve.domain.entity

import com.fasterxml.jackson.annotation.JsonValue

enum class ActionEleve(
    @JsonValue val jsonValeur: String,
    ) {
    FICHE_FORMATION(jsonValeur = "fiche_formation"),
    RECHERCHE_FORMATION(jsonValeur = "recherche_formation"),
    SUGGESTIONS(jsonValeur = "suggestions"),
    ONGLET_FICHE_FORMATION(jsonValeur = "onglet_fiche_formation"),
    LIEN_EXTERNE(jsonValeur = "lien_externe"),
    AJOUT_FAVORI_FORMATION(jsonValeur = "ajout_favori_formation"),
    AJOUT_FAVORI_METIER(jsonValeur = "ajout_favori_metier"),
    FICHE_METIER(jsonValeur = "fiche_metier"),
    EDITION_PROFIL(jsonValeur = "edition_profil"),
    ;

}