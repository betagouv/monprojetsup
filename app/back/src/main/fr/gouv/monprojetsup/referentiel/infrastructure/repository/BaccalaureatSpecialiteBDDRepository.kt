package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.Specialite
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatSpecialiteRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class BaccalaureatSpecialiteBDDRepository(
    val baccalaureatJPARepository: BaccalaureatJPARepository,
    val baccalaureatSpecialiteJPARepository: BaccalaureatSpecialiteJPARepository,
) : BaccalaureatSpecialiteRepository {
    @Transactional(readOnly = true)
    override fun recupererLesBaccalaureatsAvecLeursSpecialites(): Map<Baccalaureat, List<Specialite>> {
        val baccalaureats = baccalaureatJPARepository.findAll()
        val baccalaureatSpecialites = baccalaureatSpecialiteJPARepository.findAll()
        return baccalaureats.associate { baccalaureatEntity ->
            baccalaureatEntity.toBaccalaureat() to
                baccalaureatSpecialites.filter { it.baccalaureat == baccalaureatEntity }
                    .map { it.specialite.toSpecialite() }
        }
    }
}
