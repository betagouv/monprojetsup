package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.recherche.domain.port.BaccalaureatRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class BaccalaureatBDDRepository(
    val baccalaureatJPARepository: BaccalaureatJPARepository,
) : BaccalaureatRepository {
    @Transactional(readOnly = true)
    override fun recupererUnBaccalaureatParIdExterne(idExterneBaccalaureat: String): Baccalaureat? {
        return baccalaureatJPARepository.findByIdExterne(idExterneBaccalaureat)?.toBaccalaureat()
    }

    @Transactional(readOnly = true)
    override fun recupererUnBaccalaureat(id: String): Baccalaureat? {
        return baccalaureatJPARepository.findById(id).orElse(null)?.toBaccalaureat()
    }
}
