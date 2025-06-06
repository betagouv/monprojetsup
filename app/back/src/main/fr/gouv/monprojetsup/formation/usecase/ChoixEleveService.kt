package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.referentiel.domain.entity.Label
import fr.gouv.monprojetsup.referentiel.domain.port.LabelsRepository
import org.springframework.stereotype.Service

@Service
class ChoixEleveService(
    private val labelsRepository: LabelsRepository,
    private val logger: MonProjetSupLogger,
) {
    fun recupererChoixEleve(explicationsParFormation: Map<String, ExplicationsSuggestionEtExemplesMetiers?>): Map<String, List<Label>> {
        val choixDistincts = explicationsParFormation.flatMap { it.value?.choixAggreges ?: emptyList() }.distinct()
        val labelsChoix = choixDistincts.let { labelsRepository.recupererLesLabels(it) }.associateBy { it.id }

        logguerLesIdsInconnus(
            choixDistincts,
            labelsChoix.values.toList(),
        )
        return explicationsParFormation.entries
            .associate { entry ->
                entry.key to
                    (entry.value?.choixAggreges?.map { labelsChoix.get(it) }?.filterNotNull() ?: emptyList())
            }
    }

    fun recupererChoixEleve(explications: ExplicationsSuggestionEtExemplesMetiers): List<Label> {
        val choix =
            explications.choix.takeUnless {
                it.isEmpty()
            }
        val labelsChoix = choix?.let { labelsRepository.recupererLesLabels(it) } ?: emptyList()
        logguerLesIdsInconnus(choix, labelsChoix)
        return labelsChoix
    }

    private fun logguerLesIdsInconnus(
        domainesInteretsMetiersDistincts: List<String>?,
        labelsChoix: List<Label>,
    ) {
        val ids = labelsChoix.map { it.id }
        if (ids.size != domainesInteretsMetiersDistincts?.size) {
            domainesInteretsMetiersDistincts?.forEach {
                if (ids.contains(it).not()) {
                    logger.warn(
                        type = "ID_EXPLICATION_NON_RECONNU",
                        message = "L'id $it n'a pas de label",
                    )
                }
            }
        }
    }
}
