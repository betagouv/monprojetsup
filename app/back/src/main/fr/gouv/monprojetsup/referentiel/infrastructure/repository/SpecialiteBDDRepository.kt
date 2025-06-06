package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.domain.entity.Specialite
import fr.gouv.monprojetsup.referentiel.domain.port.SpecialitesRepository
import org.springframework.stereotype.Repository

@Repository
class SpecialiteBDDRepository(
    val specialiteJPARepository: SpecialiteJPARepository,
) : SpecialitesRepository {
    override fun recupererLesSpecialites(idsSpecialites: List<String>): List<Specialite> {
        return specialiteJPARepository.findAllByIdIn(idsSpecialites).map { it.toSpecialite() }
    }
}
