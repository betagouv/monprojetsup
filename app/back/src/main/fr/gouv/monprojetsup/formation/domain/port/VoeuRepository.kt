package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.Voeu

interface VoeuRepository {
    fun recupererVoeux(idsVoeux: List<String>): Map<String, List<Voeu>>

    fun recupererLesVoeuxDeFormations(
        idsFormations: List<String>,
        obsoletesInclus: Boolean,
    ): Map<String, List<Voeu>>

    fun recupererLesVoeuxDUneFormation(
        idFormation: String,
        obsoletesInclus: Boolean,
    ): List<Voeu>

    fun recupererIdsVoeuxInexistants(idsVoeux: List<String>): List<String>

    fun recupererLesNomsDesVoeux(idsVoeux: List<String>): List<FormationCourte>
}
