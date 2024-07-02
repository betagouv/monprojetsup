package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.formation.domain.entity.Baccalaureat

interface FrequencesCumuleesDesMoyenneDesAdmisRepository {
    fun recupererFrequencesCumuleesDeTousLesBacs(idFormation: String): Map<Baccalaureat, List<Int>>

    fun recupererFrequencesCumuleesDeTousLesBacs(idsFormations: List<String>): Map<String, Map<Baccalaureat, List<Int>>>
}
