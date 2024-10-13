package fr.gouv.monprojetsup.suggestions.infrastructure

import fr.gouv.monprojetsup.suggestions.algo.Config
import fr.gouv.monprojetsup.suggestions.entities.SuggestionsConfigEntity
import fr.gouv.monprojetsup.suggestions.port.ConfigPort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

interface SuggestionConfigJPARepository : JpaRepository<SuggestionsConfigEntity, String>


@Repository
open class SuggestionConfigRepository(
    private val repo : SuggestionConfigJPARepository
) : ConfigPort
{
    @Transactional(readOnly = true)
    override fun retrieveActiveConfig(): Config? {
        return repo.findAll().filter { m -> m.active }.map { c -> c.config }.firstOrNull()
    }

    override fun setActiveConfig(config: Config) {
        val activeConfig = repo.findAll().firstOrNull { m -> m.active }

        repo.save(SuggestionsConfigEntity().apply {
            this.id = java.util.UUID.randomUUID()
            this.active = true
            this.config = config
        })

        if(activeConfig != null) {
            activeConfig.active = false
            repo.save(activeConfig)
        }

    }

}