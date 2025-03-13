package fr.gouv.monprojetsup.formation.domain.entity

import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationTypeBaccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.Label

data class ExplicationsSuggestionEtExemplesMetiers(
    val geographique: List<ExplicationGeographique> = emptyList(),
    val formationsSimilaires: List<String> = emptyList(),
    val dureeEtudesPrevue: ChoixDureeEtudesPrevue? = null,
    val alternance: ChoixAlternance? = null,
    val specialitesChoisies: List<AffiniteSpecialite> = emptyList(),
    val typeBaccalaureat: TypeBaccalaureat? = null,
    val autoEvaluationMoyenne: AutoEvaluationMoyenne? = null,
    val choix: List<String> = emptyList(),
    val exemplesDeMetiers: List<String> = emptyList(),
    val donneesDeReference: ListeChoixReference? = null,
    val detailsCalculScore: List<String> = emptyList(),
) {
    data class AutoEvaluationMoyenne(
        val echellonDeLaMoyenneAutoEvalue: Int,
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

    data class AffiniteSpecialite(
        val idSpecialite: String,
        val pourcentage: Int,
    )

    data class ListeChoixReference(
        val details: List<ChoixReference>
    )

    data class ChoixReference(
        val id: String,
        val score: Float,
        val side: Side,
    )

    enum class Side {
        POSITIVE,
        NEGATIVE
    }

}

data class ExplicationsSuggestionDetaillees(
    val geographique: List<ExplicationGeographique> = emptyList(),
    val dureeEtudesPrevue: ChoixDureeEtudesPrevue? = null,
    val alternance: ChoixAlternance? = null,
    val specialitesChoisies: List<AffiniteSpecialite> = emptyList(),
    val choixEleve: List<Label> = emptyList(),
    val explicationTypeBaccalaureat: ExplicationTypeBaccalaureat? = null,
    val detailsCalculScore: List<String> = emptyList(),
) {
    data class AffiniteSpecialite(
        val idSpecialite: String,
        val nomSpecialite: String,
        val pourcentage: Int,
    )

}

data class ExplicationGeographique(
    val ville: String,
    val distanceKm: Int,
)
