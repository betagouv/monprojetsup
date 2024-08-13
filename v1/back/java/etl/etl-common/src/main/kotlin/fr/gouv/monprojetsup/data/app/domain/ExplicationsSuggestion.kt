package fr.gouv.monprojetsup.data.app.domain


data class ExplicationsSuggestion(
    val geographique: List<ExplicationGeographique> = emptyList(),
    val formationsSimilaires: List<String> = emptyList(),
    val dureeEtudesPrevue: ChoixDureeEtudesPrevue? = null,
    val alternance: fr.gouv.monprojetsup.data.app.domain.ChoixAlternance? = null,
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
    val alternance: fr.gouv.monprojetsup.data.app.domain.ChoixAlternance? = null,
    val specialitesChoisies: List<AffiniteSpecialite> = emptyList(),
    val formationsSimilaires: List<Formation> = emptyList(),
    val interets: List<InteretSousCategorie> = emptyList(),
    val domaines: List<Domaine> = emptyList(),
    val explicationAutoEvaluationMoyenne: FicheFormation.FicheFormationPourProfil.ExplicationAutoEvaluationMoyenne? = null,
    val explicationTypeBaccalaureat: FicheFormation.FicheFormationPourProfil.ExplicationTypeBaccalaureat? = null,
)

data class AffiniteSpecialite(
    val nomSpecialite: String,
    val pourcentage: Int,
)

data class ExplicationGeographique(
    val ville: String,
    val distanceKm: Int,
)
