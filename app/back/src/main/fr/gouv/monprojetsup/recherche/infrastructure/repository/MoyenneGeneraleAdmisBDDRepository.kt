package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.commun.Constantes.ANNEE_DONNEES_PARCOURSUP
import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.recherche.domain.port.MoyenneGeneraleAdmisRepository
import org.springframework.stereotype.Repository

@Repository
class MoyenneGeneraleAdmisBDDRepository(
    val moyenneGeneraleAdmisJPARepository: MoyenneGeneraleAdmisJPARepository,
) : MoyenneGeneraleAdmisRepository {
    override fun recupererFrequencesCumuleesDeToutLesBacs(idFormation: String): Map<Baccalaureat, List<Int>> {
        return moyenneGeneraleAdmisJPARepository.findAllByAnneeAndIdFormation(
            annee = ANNEE_DONNEES_PARCOURSUP,
            idFormation = idFormation,
        ).associate { it.baccalaureat.toBaccalaureat() to it.frequencesCumulees }
    }
}
