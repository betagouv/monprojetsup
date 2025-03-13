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
    fun recupererChoixEleve(
        explicationsParFormation: Map<String, ExplicationsSuggestionEtExemplesMetiers?>,
    ): Map<String, List<Label>> {

        val choixDistincts = recupererIdsDesdomainesInteretsEtMetierDistincts(explicationsParFormation)
        val labelsChoix = choixDistincts?.let { labelsRepository.recupererLesLabels(it) } ?: emptyList()

        logguerLesIdsInconnus(
            choixDistincts,
            labelsChoix,
        )
        return explicationsParFormation.entries
            .associate {
                it.key to (it.value?.choix?.let { itt ->
                    labelsRepository.recupererLesLabels(itt)
                } ?: emptyList()
                )
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
        labelsChoix: List<Label>
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

    private fun recupererIdsDesdomainesInteretsEtMetierDistincts(
        explicationsParFormation: Map<String, ExplicationsSuggestionEtExemplesMetiers?>,
    ) : List<String>? {
        val choix = explicationsParFormation.flatMap { it.value?.choix ?: emptyList() } +
                explicationsParFormation.flatMap { it.value?.formationsSimilaires ?: emptyList() } +
                explicationsParFormation.map {
                    it.value?.donneesDeReference?.details?.
                    filter { itt -> itt.side == ExplicationsSuggestionEtExemplesMetiers.Side.POSITIVE }?.
                    sortedByDescending { itt -> itt.score }?.
                    map { itt -> itt.id }?.
                    firstOrNull()
                }.filterNotNull()
        return choix.takeUnless {
            it.isEmpty()
        }?.distinct()
    }


}
