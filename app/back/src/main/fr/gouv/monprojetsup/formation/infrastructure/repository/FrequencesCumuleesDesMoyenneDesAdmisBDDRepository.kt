package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.domain.port.FrequencesCumuleesDesMoyenneDesAdmisRepository
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class FrequencesCumuleesDesMoyenneDesAdmisBDDRepository(
    val moyenneGeneraleAdmisJPARepository: MoyenneGeneraleAdmisJPARepository,
) : FrequencesCumuleesDesMoyenneDesAdmisRepository {
    override fun recupererFrequencesCumuleesParBacs(annee: String): Map<Baccalaureat, List<Int>> {
        return moyenneGeneraleAdmisJPARepository.findAllByAnneeAndBaccalaureatIdNotIn(
            annee = annee,
            idsBaccalaureatsExclus = idsBaccalaureatsExclus,
        )
            .groupBy { it.baccalaureat }.map { entry ->
                val listeDesFrequencesCumulesPourUnBac = entry.value.map { it.frequencesCumulees }
                val sommeDesFrequencesCumulees =
                    (0 until listeDesFrequencesCumulesPourUnBac[0].size).map { index ->
                        listeDesFrequencesCumulesPourUnBac.sumOf { it[index] }
                    }
                entry.key.toBaccalaureat() to sommeDesFrequencesCumulees
            }.toMap()
    }

    @Transactional(readOnly = true)
    override fun recupererFrequencesCumuleesDeTousLesBacs(
        idFormation: String,
        annee: String,
    ): Map<Baccalaureat, List<Int>> {
        return moyenneGeneraleAdmisJPARepository.findAllByAnneeAndIdFormationAndBaccalaureatIdNotIn(
            annee = annee,
            idFormation = idFormation,
            idsBaccalaureatsExclus = idsBaccalaureatsExclus,
        ).associate { it.baccalaureat.toBaccalaureat() to it.frequencesCumulees }
    }

    @Transactional(readOnly = true)
    override fun recupererFrequencesCumuleesDeTousLesBacs(
        idsFormations: List<String>,
        annee: String,
    ): Map<String, Map<Baccalaureat, List<Int>>> {
        val groupementParIdFormation =
            moyenneGeneraleAdmisJPARepository.findAllByAnneeAndIdFormationInAndBaccalaureatIdNotIn(
                annee = annee,
                idsFormations = idsFormations,
                idsBaccalaureatsExclus = idsBaccalaureatsExclus,
            ).groupBy { it.idFormation }
        return idsFormations.associateWith { idFormation ->
            groupementParIdFormation[idFormation]?.associate { entity ->
                entity.baccalaureat.toBaccalaureat() to entity.frequencesCumulees
            } ?: emptyMap()
        }
    }

    companion object {
        private val idsBaccalaureatsExclus = listOf("NC")
    }
}
