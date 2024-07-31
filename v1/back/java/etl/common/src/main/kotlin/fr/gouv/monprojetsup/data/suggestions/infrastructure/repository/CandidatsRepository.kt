package fr.gouv.monprojetsup.data.suggestions.infrastructure.repository

import fr.gouv.monprojetsup.data.suggestions.infrastructure.entity.CandidatEntity
import fr.gouv.monprojetsup.suggestions.domain.model.Candidat
import fr.gouv.monprojetsup.suggestions.domain.port.CandidatsPort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


interface CandidatsJPARepository : JpaRepository<CandidatEntity, String> {

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
        repo.save(CandidatEntity(candidat));
    }

    @Transactional(readOnly = false)
    override fun saveAll(candidat: MutableCollection<Candidat>) {
        repo.saveAll(candidat.map { CandidatEntity(it) })
    }

}
