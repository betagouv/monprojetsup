package fr.gouv.monprojetsup.formation.domain.entity

import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationAutoEvaluationMoyenne
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationTypeBaccalaureat

data class ExplicationsSuggestion(
    val geographique: List<ExplicationGeographique> = emptyList(),
    val formationsSimilaires: List<String> = emptyList(),
    val dureeEtudesPrevue: ChoixDureeEtudesPrevue? = null,
    val alternance: ChoixAlternance? = null,
    val specialitesChoisies: List<AffiniteSpecialite> = emptyList(),
    val typeBaccalaureat: TypeBaccalaureat? = null,
    val autoEvaluationMoyenne: AutoEvaluationMoyenne? = null,
    val interetsEtDomainesChoisis: List<String> = emptyList(),
) {
    data class AutoEvaluationMoyenne(
        val moyenneAutoEvalue: Float,
        val rangs: RangsEchellons,
        val baccalaureatUtilise: String,
    )

    data class TypeBaccalaureat(
        val nomBaccalaureat: String,
        val pourcentage: Int,
    )

    data class RangsEchellons(
        val rangEch25: Int,
        val rangEch50: Int,
        val rangEch75: Int,
        val rangEch10: Int,
        val rangEch90: Int,
    )
}

data class ExplicationsSuggestionDetaillees(
    val geographique: List<ExplicationGeographique> = emptyList(),
    val dureeEtudesPrevue: ChoixDureeEtudesPrevue? = null,
    val alternance: ChoixAlternance? = null,
    val specialitesChoisies: List<AffiniteSpecialite> = emptyList(),
    val formationsSimilaires: List<Formation> = emptyList(),
    val interets: List<InteretSousCategorie> = emptyList(),
    val domaines: List<Domaine> = emptyList(),
    val explicationAutoEvaluationMoyenne: ExplicationAutoEvaluationMoyenne? = null,
    val explicationTypeBaccalaureat: ExplicationTypeBaccalaureat? = null,
)

data class AffiniteSpecialite(
    val nomSpecialite: String,
    val pourcentage: Int,
)

data class ExplicationGeographique(
    val ville: String,
    val distanceKm: Int,
)
