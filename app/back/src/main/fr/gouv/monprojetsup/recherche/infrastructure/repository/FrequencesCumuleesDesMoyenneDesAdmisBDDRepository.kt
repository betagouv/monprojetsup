package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.commun.Constantes.ANNEE_DONNEES_PARCOURSUP
import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.recherche.domain.port.FrequencesCumuleesDesMoyenneDesAdmisRepository
import org.springframework.stereotype.Repository

@Repository
class FrequencesCumuleesDesMoyenneDesAdmisBDDRepository(
    val moyenneGeneraleAdmisJPARepository: MoyenneGeneraleAdmisJPARepository,
) : FrequencesCumuleesDesMoyenneDesAdmisRepository {
    override fun recupererFrequencesCumuleesDeToutLesBacs(idFormation: String): Map<Baccalaureat, List<Int>> {
        return moyenneGeneraleAdmisJPARepository.findAllByAnneeAndIdFormation(
            annee = ANNEE_DONNEES_PARCOURSUP,
            idFormation = idFormation,
        ).associate { it.baccalaureat.toBaccalaureat() to it.frequencesCumulees }
    }
}
