package fr.gouv.monprojetsup.suggestions.infrastructure.repository

import fr.gouv.monprojetsup.suggestions.domain.entity.Matiere
import fr.gouv.monprojetsup.suggestions.domain.port.MatieresPort
import fr.gouv.monprojetsup.suggestions.infrastructure.entity.MatiereEntity
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*


interface MatieresJPARepository : JpaRepository<MatiereEntity, String> {
    override fun findAll(): MutableList<MatiereEntity>

    override fun findById(key: String): Optional<MatiereEntity>

}


@Repository
class MatieresRepository(
    private val repo : MatieresJPARepository )
    : MatieresPort()
{
    @Transactional(readOnly = true)
    @Cacheable(value = ["myCache"])
    override fun retrieveSpecialites(): List<Matiere> {
        return repo.findAll().filter { m -> m.estSpecialite }.map { it.toMatiere() }
    }

}