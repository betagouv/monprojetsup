package fr.gouv.monprojetsup.suggestions.infrastructure

import fr.gouv.monprojetsup.data.model.Matiere
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsMatiereEntity
import fr.gouv.monprojetsup.suggestions.port.MatieresPort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*


interface MatieresJPARepository : JpaRepository<SuggestionsMatiereEntity, String> {
    override fun findAll(): MutableList<SuggestionsMatiereEntity>

    override fun findById(key: String): Optional<SuggestionsMatiereEntity>

}


@Repository
open class MatieresRepository(
    private val repo : MatieresJPARepository
)
    : MatieresPort
{
    @Transactional(readOnly = true)
    override fun retrieveSpecialites(): List<Matiere> {
        return repo.findAll().filter { m -> m.estSpecialite }.map { it.toMatiere() }
    }

}