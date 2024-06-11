package fr.gouv.monprojetsup.recherche.domain.entity

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
    data class AffiniteSpecialite(
        val nomSpecialite: String,
        val pourcentage: Int,
    )

    data class AutoEvaluationMoyenne(
        val moyenneAutoEvalue: Float,
        val rangs: RangsEchellons,
        val bacUtilise: String,
    )

    data class TypeBaccalaureat(
        val nomBaccalaureat: String,
        val pourcentage: Int,
    )

    data class ExplicationGeographique(
        val ville: String,
        val distanceKm: Int,
    )

    data class RangsEchellons(
        val rangEch25: Int,
        val rangEch50: Int,
        val rangEch75: Int,
        val rangEch10: Int,
        val rangEch90: Int,
    )
}
