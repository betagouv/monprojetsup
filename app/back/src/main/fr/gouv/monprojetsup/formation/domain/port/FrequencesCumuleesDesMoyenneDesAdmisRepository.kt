package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat

interface FrequencesCumuleesDesMoyenneDesAdmisRepository {
    fun recupererFrequencesCumuleesParBacs(annee: String): Map<Baccalaureat, List<Int>>

    fun recupererFrequencesCumuleesDeTousLesBacs(
        idFormation: String,
        annee: String,
    ): Map<Baccalaureat, List<Int>>

    fun recupererFrequencesCumuleesDeTousLesBacs(
        idsFormations: List<String>,
        annee: String,
    ): Map<String, Map<Baccalaureat, List<Int>>>
}
