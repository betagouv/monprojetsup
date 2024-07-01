package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.formation.domain.entity.Baccalaureat

interface FrequencesCumuleesDesMoyenneDesAdmisRepository {
    fun recupererFrequencesCumuleesDeToutLesBacs(idFormation: String): Map<Baccalaureat, List<Int>>
}
