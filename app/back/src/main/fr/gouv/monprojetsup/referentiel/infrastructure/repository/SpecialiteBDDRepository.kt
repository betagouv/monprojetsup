package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.domain.entity.Specialite
import fr.gouv.monprojetsup.referentiel.domain.port.SpecialitesRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class SpecialiteBDDRepository(
    val specialiteJPARepository: SpecialiteJPARepository,
) : SpecialitesRepository {
    @Transactional(readOnly = true)
    override fun recupererLesSpecialites(idsSpecialites: List<String>): List<Specialite> {
        return specialiteJPARepository.findAllByIdIn(idsSpecialites).map { it.toSpecialite() }
    }
}
