package fr.gouv.monprojetsup.data.suggestions.infrastructure

import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsCandidatEntity
import fr.gouv.monprojetsup.suggestions.domain.model.Candidat
import fr.gouv.monprojetsup.suggestions.domain.port.CandidatsPort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


interface CandidatsJPARepository : JpaRepository<SuggestionsCandidatEntity, String> {

}

@Repository
class CandidatsRepository(
            val repo : CandidatsJPARepository
)
    : CandidatsPort
{

    @Transactional(readOnly = true)
    override fun findAll(): List<Candidat> {
        return repo.findAll().map { it.toCandidat() }.toMutableList()
    }

    @Transactional(readOnly = false)
    override fun save(candidat: Candidat) {
        repo.save(SuggestionsCandidatEntity(candidat));
    }

    @Transactional(readOnly = false)
    override fun saveAll(candidat: MutableCollection<Candidat>) {
        repo.saveAll(candidat.map { SuggestionsCandidatEntity(it) })
    }

}
