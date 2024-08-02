package fr.gouv.monprojetsup.authentification.domain.entity

import fr.gouv.monprojetsup.eleve.domain.entity.Commune
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup

sealed class ProfilEleve(open val id: String) : ProfilUtilisateur() {
    data class Identifie(
        override val id: String,
        val situation: SituationAvanceeProjetSup?,
        val classe: ChoixNiveau?,
        val baccalaureat: String?,
        val specialites: List<String>?,
        val domainesInterets: List<String>?,
        val centresInterets: List<String>?,
        val metiersFavoris: List<String>?,
        val dureeEtudesPrevue: ChoixDureeEtudesPrevue?,
        val alternance: ChoixAlternance?,
        val communesFavorites: List<Commune>?,
        val formationsFavorites: List<String>?,
        val moyenneGenerale: Float?,
        val corbeilleFormations: List<String>,
    ) : ProfilEleve(id) {
        constructor(id: String) : this(
            id = id,
            situation = null,
            classe = null,
            baccalaureat = null,
            specialites = null,
            domainesInterets = null,
            centresInterets = null,
            metiersFavoris = null,
            dureeEtudesPrevue = null,
            alternance = null,
            communesFavorites = null,
            formationsFavorites = null,
            moyenneGenerale = null,
            corbeilleFormations = emptyList(),
        )
    }

    data class Inconnu(override val id: String) : ProfilEleve(id)
}
