package fr.gouv.monprojetsup.data.suggestions.infrastructure

import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsVoeuEntity
import fr.gouv.monprojetsup.suggestions.domain.model.Voeu
import fr.gouv.monprojetsup.suggestions.domain.port.VoeuxPort
import fr.gouv.monprojetsup.suggestions.infrastructure.psup.DescriptifVoeu
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


interface VoeuxJPARepository : JpaRepository<SuggestionsVoeuEntity, String> {

}

@Repository
class VoeuxRepository(
    private val repo : VoeuxJPARepository
)
    : VoeuxPort
{
    override fun retrieveDescriptifs(): Map<String, DescriptifVoeu?> {
        return repo.findAll().associate { it.id to it.descriptif}
    }

    override fun saveAll(voeux: MutableList<Voeu>) {
        repo.saveAll(voeux.map { SuggestionsVoeuEntity(it) })
    }

}
