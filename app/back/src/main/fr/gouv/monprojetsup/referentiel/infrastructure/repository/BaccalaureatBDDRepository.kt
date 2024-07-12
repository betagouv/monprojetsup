package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatRepository
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
    override fun recupererDesBaccalaureatsParIdsExternes(idsExternesBaccalaureats: List<String>): List<Baccalaureat> {
        return baccalaureatJPARepository.findAllByIdExterneIn(idsExternesBaccalaureats).map { it.toBaccalaureat() }
    }
}
