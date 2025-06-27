package fr.gouv.monprojetsup.authentification.domain.entity

import fr.gouv.monprojetsup.eleve.domain.entity.CommuneFavorite
import fr.gouv.monprojetsup.eleve.domain.entity.FormationFavorite
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFavori
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
        val formationsFavorites: List<FormationFavorite>?,
        val corbeilleFormations: List<String>,
        val compteParcoursupLie: Boolean,
        val voeuxFavoris: List<VoeuFavori>,
        var portfolioId: String? = null,
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
            corbeilleFormations = emptyList(),
            compteParcoursupLie = false,
            voeuxFavoris = emptyList(),
            portfolioId = null,
        )

        fun estProfilComplet(specialitesSelectionnablesParCandidat: List<String>?): Boolean {
            return completionProfil(specialitesSelectionnablesParCandidat) >= 100
        }

        fun completionProfil(specialitesSelectionnablesParCandidat: List<String>?): Int {
            var result = 1
            if (classe != null) result = result.inc()
            if (situation != null) result = result.inc()
            if (!baccalaureat.isNullOrEmpty()) {
                result = result.inc()
            }
            if (specialitesSelectionnablesParCandidat.isNullOrEmpty() || !this.specialites.isNullOrEmpty()) {
                result = result.inc()
            }
            if (!domainesInterets.isNullOrEmpty()) result = result.inc()
            if (!centresInterets.isNullOrEmpty()) result = result.inc()
            if (dureeEtudesPrevue != null) result = result.inc()
            if (alternance != null) result = result.inc()
            if (!communesFavorites.isNullOrEmpty()) result = result.inc()
            val resultMaximum = 10
            return 100 * result / resultMaximum
        }

        fun aAuMoinsUnFavoriMPS(): Boolean {
            return !metiersFavoris.isNullOrEmpty() || !formationsFavorites.isNullOrEmpty()
        }

        fun aAuMoinsTroisFavorisMPS(): Boolean {
            return !metiersFavoris.isNullOrEmpty() && metiersFavoris.size >= 3 ||
                !formationsFavorites.isNullOrEmpty() && formationsFavorites.size >= 3
        }

        fun aEvalueSonNiveauAmbition(): Boolean {
            return !formationsFavorites.isNullOrEmpty() && formationsFavorites.all { it.niveauAmbition > 0 }
        }

        fun aDesFavorisParcoursup(): Boolean {
            return voeuxFavoris.isNotEmpty() &&
                !formationsFavorites.isNullOrEmpty()
        }
    }

    data class SansCompte(override val id: String) : ProfilEleve(id)
}
