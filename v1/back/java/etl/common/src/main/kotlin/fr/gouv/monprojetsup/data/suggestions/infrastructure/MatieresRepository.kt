package fr.gouv.monprojetsup.data.suggestions.infrastructure

import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsMatiereEntity
import fr.gouv.monprojetsup.suggestions.domain.model.Matiere
import fr.gouv.monprojetsup.suggestions.domain.port.MatieresPort
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*


interface MatieresJPARepository : JpaRepository<SuggestionsMatiereEntity, String> {
    override fun findAll(): MutableList<SuggestionsMatiereEntity>

    override fun findById(key: String): Optional<SuggestionsMatiereEntity>

}


@Repository
class MatieresRepository(
    private val repo : MatieresJPARepository
)
    : MatieresPort
{
    @Transactional(readOnly = true)
    @Cacheable(value = ["myCache"])
    override fun retrieveSpecialites(): List<Matiere> {
        return repo.findAll().filter { m -> m.estSpecialite }.map { it.toMatiere() }
    }

    override fun saveAll(matieres: List<Matiere>) {
        repo.saveAll(
            matieres.map { SuggestionsMatiereEntity(it) }
        )
    }

}