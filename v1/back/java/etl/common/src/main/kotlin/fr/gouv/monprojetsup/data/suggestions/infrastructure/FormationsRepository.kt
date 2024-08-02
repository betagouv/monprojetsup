package fr.gouv.monprojetsup.data.suggestions.infrastructure

import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsFormationEntity
import fr.gouv.monprojetsup.suggestions.domain.model.Formation
import fr.gouv.monprojetsup.suggestions.domain.port.FormationsPort
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*


interface FormationsJPARepository : JpaRepository<SuggestionsFormationEntity, String> {
    override fun findAll(): MutableList<SuggestionsFormationEntity>

    override fun findById(key: String): Optional<SuggestionsFormationEntity>

}


@Repository
class FormationRepository(
    private val repo : FormationsJPARepository
)
    : FormationsPort
{
    @Transactional(readOnly = true)
    override fun retrieveFormations(): MutableMap<String, Formation> {
        return repo.findAll().associate { it.id to it.toFormation() }.toMutableMap()
    }

    @Transactional(readOnly = true)
    @Cacheable(value = ["myCache"], key = "#id")
    override fun retrieveFormation(id: String): Optional<Formation> {
        return repo.findById(id).map { it.toFormation() }
    }

    @Transactional(readOnly = false)
    override fun saveAll(formations: Collection<Formation>) {
        repo.saveAll(formations.map { SuggestionsFormationEntity(it) })
    }

}