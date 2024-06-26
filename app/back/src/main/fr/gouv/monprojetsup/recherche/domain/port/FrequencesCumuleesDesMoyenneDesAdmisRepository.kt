package fr.gouv.monprojetsup.recherche.domain.port

import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat

interface FrequencesCumuleesDesMoyenneDesAdmisRepository {
    fun recupererFrequencesCumuleesDeToutLesBacs(idFormation: String): Map<Baccalaureat, List<Int>>
}
