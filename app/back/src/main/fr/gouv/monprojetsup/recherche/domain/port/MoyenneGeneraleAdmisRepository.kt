package fr.gouv.monprojetsup.recherche.domain.port

import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat

interface MoyenneGeneraleAdmisRepository {
    fun recupererFrequencesCumuleesDeToutLesBacs(idFormation: String): Map<Baccalaureat, List<Int>>
}
