package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.Constantes.ANNEE_DONNEES_PARCOURSUP
import fr.gouv.monprojetsup.formation.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.formation.domain.port.FrequencesCumuleesDesMoyenneDesAdmisRepository
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
