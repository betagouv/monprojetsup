package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.commun.Constantes.ANNEE_DONNEES_PARCOURSUP
import fr.gouv.monprojetsup.recherche.domain.port.MoyenneGeneraleAdmisRepository
import org.springframework.stereotype.Repository

@Repository
class MoyenneGeneraleAdmisBDDRepository(
    val moyenneGeneraleAdmisJPARepository: MoyenneGeneraleAdmisJPARepository,
) : MoyenneGeneraleAdmisRepository {
    override fun recupererFrequencesCumuleesDeToutLesBacs(idFormation: String): Map<String, List<Int>> {
        return moyenneGeneraleAdmisJPARepository.findAllByAnneeAndIdFormation(
            annee = ANNEE_DONNEES_PARCOURSUP,
            idFormation = idFormation,
        ).associate { it.id.idBaccalaureat to it.frequencesCumulees }
    }
}
