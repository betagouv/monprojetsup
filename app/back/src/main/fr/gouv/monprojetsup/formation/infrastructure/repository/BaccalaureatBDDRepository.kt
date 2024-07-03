package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.formation.domain.port.BaccalaureatRepository
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

    @Transactional(readOnly = true)
    override fun recupererTousLesBaccalaureats(): List<Baccalaureat> {
        return baccalaureatJPARepository.findAll().map { it.toBaccalaureat() }
    }
}
