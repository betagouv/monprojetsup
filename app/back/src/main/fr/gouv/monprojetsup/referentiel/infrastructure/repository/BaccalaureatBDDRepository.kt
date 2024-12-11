package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatRepository
import org.springframework.stereotype.Repository

@Repository
class BaccalaureatBDDRepository(
    val baccalaureatJPARepository: BaccalaureatJPARepository,
) : BaccalaureatRepository {
    override fun recupererUnBaccalaureatParIdExterne(idExterneBaccalaureat: String): Baccalaureat? {
        return baccalaureatJPARepository.findByIdExterne(idExterneBaccalaureat)?.toBaccalaureat()
    }

    override fun verifierBaccalaureatExiste(id: String): Boolean {
        return baccalaureatJPARepository.existsById(id)
    }

    override fun recupererDesBaccalaureatsParIdsExternes(idsExternesBaccalaureats: List<String>): List<Baccalaureat> {
        return baccalaureatJPARepository.findAllByIdExterneIn(idsExternesBaccalaureats).map { it.toBaccalaureat() }
    }
}
