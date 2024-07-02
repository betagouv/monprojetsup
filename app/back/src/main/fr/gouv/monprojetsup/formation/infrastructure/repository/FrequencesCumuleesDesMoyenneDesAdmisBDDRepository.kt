package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.Constantes.ANNEE_DONNEES_PARCOURSUP
import fr.gouv.monprojetsup.formation.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.formation.domain.port.FrequencesCumuleesDesMoyenneDesAdmisRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class FrequencesCumuleesDesMoyenneDesAdmisBDDRepository(
    val moyenneGeneraleAdmisJPARepository: MoyenneGeneraleAdmisJPARepository,
) : FrequencesCumuleesDesMoyenneDesAdmisRepository {
    @Transactional(readOnly = true)
    override fun recupererFrequencesCumuleesDeTousLesBacs(idFormation: String): Map<Baccalaureat, List<Int>> {
        return moyenneGeneraleAdmisJPARepository.findAllByAnneeAndIdFormation(
            annee = ANNEE_DONNEES_PARCOURSUP,
            idFormation = idFormation,
        ).associate { it.baccalaureat.toBaccalaureat() to it.frequencesCumulees }
    }

    @Transactional(readOnly = true)
    override fun recupererFrequencesCumuleesDeTousLesBacs(idsFormations: List<String>): Map<String, Map<Baccalaureat, List<Int>>> {
        val groupementParIdFormation =
            moyenneGeneraleAdmisJPARepository.findAllByAnneeAndIdFormationIn(
                annee = ANNEE_DONNEES_PARCOURSUP,
                idsFormations = idsFormations,
            ).groupBy { it.idFormation }
        return idsFormations.associateWith { idFormation ->
            groupementParIdFormation[idFormation]?.associate {
                    entity ->
                entity.baccalaureat.toBaccalaureat() to entity.frequencesCumulees
            } ?: emptyMap()
        }
    }
}
