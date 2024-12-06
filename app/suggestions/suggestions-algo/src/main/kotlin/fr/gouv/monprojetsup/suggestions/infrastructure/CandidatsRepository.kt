package fr.gouv.monprojetsup.suggestions.infrastructure

import fr.gouv.monprojetsup.data.model.PanierVoeux
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsPaniersVoeuxEntity
import fr.gouv.monprojetsup.suggestions.port.CandidatsPort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


interface CandidatsJPARepository : JpaRepository<SuggestionsPaniersVoeuxEntity, String>

@Repository
open class CandidatsRepository(
            val repo : CandidatsJPARepository
)
    : CandidatsPort
{

    @Transactional(readOnly = true)
    override fun findAll(): List<PanierVoeux> {
        return repo.findAll().map { it.toCandidat() }
    }

}
