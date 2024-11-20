package fr.gouv.monprojetsup.formation.domain.entity

import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat

sealed class FicheFormation(
    open val id: String,
    open val nom: String,
    open val descriptifGeneral: String?,
    open val descriptifAttendus: String?,
    open val descriptifDiplome: String?,
    open val descriptifConseils: String?,
    open val formationsAssociees: List<String>,
    open val liens: List<Lien>,
    open val metiers: List<Metier>,
    open val voeux: List<Voeu>,
    open val criteresAnalyseCandidature: List<CritereAnalyseCandidature>,
    open val statistiquesDesAdmis: StatistiquesDesAdmis?,
    open val apprentissage: Boolean,
) {
    data class FicheFormationSansProfil(
        override val id: String,
        override val nom: String,
        override val descriptifGeneral: String?,
        override val descriptifAttendus: String?,
        override val descriptifDiplome: String?,
        override val descriptifConseils: String?,
        override val formationsAssociees: List<String>,
        override val liens: List<Lien>,
        override val metiers: List<Metier>,
        override val voeux: List<Voeu>,
        override val criteresAnalyseCandidature: List<CritereAnalyseCandidature>,
        override val statistiquesDesAdmis: StatistiquesDesAdmis?,
        override val apprentissage: Boolean,
    ) : FicheFormation(
            id = id,
            nom = nom,
            descriptifGeneral = descriptifGeneral,
            descriptifAttendus = descriptifAttendus,
            descriptifDiplome = descriptifDiplome,
            descriptifConseils = descriptifConseils,
            formationsAssociees = formationsAssociees,
            liens = liens,
            metiers = metiers,
            voeux = voeux,
            criteresAnalyseCandidature = criteresAnalyseCandidature,
            statistiquesDesAdmis = statistiquesDesAdmis,
            apprentissage = apprentissage,
        )

    data class FicheFormationPourProfil(
        override val id: String,
        override val nom: String,
        override val descriptifGeneral: String?,
        override val descriptifAttendus: String?,
        override val descriptifDiplome: String?,
        override val descriptifConseils: String?,
        override val formationsAssociees: List<String>,
        override val liens: List<Lien>,
        override val criteresAnalyseCandidature: List<CritereAnalyseCandidature>,
        override val statistiquesDesAdmis: StatistiquesDesAdmis?,
        override val apprentissage: Boolean,
        val informationsSurLesVoeuxEtLeursCommunes: InformationsSurLesVoeuxEtLeursCommunes,
        val tauxAffinite: Int,
        val metiersTriesParAffinites: List<Metier>,
        val explications: ExplicationsSuggestionDetaillees?,
    ) : FicheFormation(
            id = id,
            nom = nom,
            descriptifGeneral = descriptifGeneral,
            descriptifAttendus = descriptifAttendus,
            descriptifDiplome = descriptifDiplome,
            descriptifConseils = descriptifConseils,
            formationsAssociees = formationsAssociees,
            liens = liens,
            metiers = metiersTriesParAffinites,
            voeux = informationsSurLesVoeuxEtLeursCommunes.voeux,
            criteresAnalyseCandidature = criteresAnalyseCandidature,
            statistiquesDesAdmis = statistiquesDesAdmis,
            apprentissage = apprentissage,
        ) {
        data class ExplicationAutoEvaluationMoyenne(
            val baccalaureatUtilise: Baccalaureat,
            val moyenneAutoEvalue: Float,
            val basIntervalleNotes: Float,
            val hautIntervalleNotes: Float,
        )

        data class ExplicationTypeBaccalaureat(
            val baccalaureat: Baccalaureat,
            val pourcentage: Int,
        )

        data class InformationsSurLesVoeuxEtLeursCommunes(
            val voeux: List<Voeu>,
            val communesTriees: List<CommuneCourte>,
            val voeuxParCommunesFavorites: List<CommuneAvecVoeuxAuxAlentours>,
        )
    }
}
