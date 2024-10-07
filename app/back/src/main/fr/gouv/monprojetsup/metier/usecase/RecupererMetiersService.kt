package fr.gouv.monprojetsup.metier.usecase

import fr.gouv.monprojetsup.metier.domain.entity.MetierAvecSesFormations
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import org.springframework.stereotype.Service

@Service
class RecupererMetiersService(
    private val metierRepository: MetierRepository,
) {
    fun recupererMetiers(ids: List<String>): List<MetierAvecSesFormations> {
        return metierRepository.recupererLesMetiersAvecSesFormations(ids)
    }
}
