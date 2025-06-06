package fr.gouv.monprojetsup.suggestions.infrastructure

import fr.gouv.monprojetsup.data.Constants.MAJ_SUGGESTIONS_REF_DATA_NECESSAIRE
import fr.gouv.monprojetsup.data.parametre.entity.ParametreEntity
import fr.gouv.monprojetsup.suggestions.port.ParametresPort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

interface ParametresJPARepository :
    JpaRepository<ParametreEntity, String>

@Repository
open class ParametresRepository(
    private val repo : ParametresJPARepository
) : ParametresPort {
    override fun isRefDataUpdateNeeded(): Boolean {
        return repo.findAll().firstOrNull { m -> m.id == MAJ_SUGGESTIONS_REF_DATA_NECESSAIRE }?.statut ?: false
    }

    override fun setRefDataUpdateNotNeeded() {
        val refDataUpdateNeeded = repo.findAll().firstOrNull { m -> m.id == MAJ_SUGGESTIONS_REF_DATA_NECESSAIRE }
        if (refDataUpdateNeeded != null) {
            refDataUpdateNeeded.statut = false
            repo.save(refDataUpdateNeeded)
        } else {
            repo.save(ParametreEntity().apply {
                this.id = MAJ_SUGGESTIONS_REF_DATA_NECESSAIRE
                this.statut = false
            })
        }
    }

}
