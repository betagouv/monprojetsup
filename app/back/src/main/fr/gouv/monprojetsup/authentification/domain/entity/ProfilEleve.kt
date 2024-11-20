package fr.gouv.monprojetsup.authentification.domain.entity

import fr.gouv.monprojetsup.eleve.domain.entity.CommuneFavorite
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFormation
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup

sealed class ProfilEleve(open val id: String) : ProfilUtilisateur() {
    data class AvecProfilExistant(
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
        val communesFavorites: List<CommuneFavorite>?,
        val formationsFavorites: List<VoeuFormation>?,
        val moyenneGenerale: Float?,
        val corbeilleFormations: List<String>,
        val compteParcoursupLie: Boolean,
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
            compteParcoursupLie = false,
        )
    }

    data class SansCompte(override val id: String) : ProfilEleve(id)
}
