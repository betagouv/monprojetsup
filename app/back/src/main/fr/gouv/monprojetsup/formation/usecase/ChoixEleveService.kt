package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie
import fr.gouv.monprojetsup.referentiel.domain.port.DomaineRepository
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import org.springframework.stereotype.Service

@Service
class ChoixEleveService(
    private val interetRepository: InteretRepository,
    private val domaineRepository: DomaineRepository,
    private val metierRepository: MetierRepository,
    private val logger: MonProjetSupLogger,
) {
    fun recupererChoixEleve(
        explicationsParFormation: Map<String, ExplicationsSuggestionEtExemplesMetiers?>,
    ): Map<String, ExplicationsSuggestionDetaillees.ChoixEleve> {
        val domainesInteretsMetiersDistincts = recupererIdsDesdomainesInteretsEtMetierDistincts(explicationsParFormation)
        val domainesChoisis = domainesInteretsMetiersDistincts?.let { domaineRepository.recupererLesDomaines(it) } ?: emptyList()
        val interetsChoisis =
            domainesInteretsMetiersDistincts?.let { interetRepository.recupererLesSousCategories(it) } ?: emptyList()
        val metiersChoisis = domainesInteretsMetiersDistincts?.let { recupererMetiersCourts(it) } ?: emptyList()
        logguerLesIdsInconnus(
            domainesInteretsMetiersDistincts,
            domainesChoisis,
            interetsChoisis,
            metiersChoisis,
        )
        return explicationsParFormation.mapValues {
            val interetsDomainesMetiersChoisis = it.value?.interetsDomainesMetiersChoisis
            ExplicationsSuggestionDetaillees.ChoixEleve(
                interetsChoisis =
                    interetsDomainesMetiersChoisis?.mapNotNull { interetOuDomaineOuMetierId ->
                        interetsChoisis.firstOrNull { interet -> interet.id == interetOuDomaineOuMetierId }
                    }?.distinct() ?: emptyList(),
                domainesChoisis =
                    interetsDomainesMetiersChoisis?.mapNotNull { interetOuDomaineOuMetierId ->
                        domainesChoisis.firstOrNull { domaine -> domaine.id == interetOuDomaineOuMetierId }
                    }?.distinct() ?: emptyList(),
                metiersChoisis =
                    interetsDomainesMetiersChoisis?.mapNotNull { interetOuDomaineOuMetierId ->
                        metiersChoisis.firstOrNull { metier -> metier.id == interetOuDomaineOuMetierId }
                    }?.distinct() ?: emptyList(),
            )
        }
    }

    fun recupererChoixEleve(explications: ExplicationsSuggestionEtExemplesMetiers): ExplicationsSuggestionDetaillees.ChoixEleve {
        val interetDomainesMetiersChoisis =
            explications.interetsDomainesMetiersChoisis.takeUnless {
                it.isEmpty()
            }
        val domaines = interetDomainesMetiersChoisis?.let { domaineRepository.recupererLesDomaines(it) } ?: emptyList()
        val interets =
            interetDomainesMetiersChoisis?.let {
                interetRepository.recupererLesSousCategories(it).distinct()
            } ?: emptyList()
        val metiersChoisis = interetDomainesMetiersChoisis?.let { metierRepository.recupererLesMetiersCourts(it) } ?: emptyList()
        logguerLesIdsInconnus(interetDomainesMetiersChoisis, domaines, interets, metiersChoisis)
        return ExplicationsSuggestionDetaillees.ChoixEleve(
            interetsChoisis = interets,
            domainesChoisis = domaines,
            metiersChoisis = metiersChoisis,
        )
    }

    private fun logguerLesIdsInconnus(
        domainesInteretsMetiersDistincts: List<String>?,
        domainesChoisis: List<Domaine>,
        interetsChoisis: List<InteretSousCategorie>,
        metiersChoisis: List<MetierCourt>,
    ) {
        val ids = domainesChoisis.map { it.id } + interetsChoisis.map { it.id } + metiersChoisis.map { it.id }
        if (ids.size != domainesInteretsMetiersDistincts?.size) {
            domainesInteretsMetiersDistincts?.forEach {
                if (ids.contains(it).not()) {
                    logger.warn(
                        type = "ID_EXPLICATION_NON_RECONNU",
                        message = "L'id $it n'est ni un métier, ni un domaine, ni un centre d'intérêt",
                    )
                }
            }
        }
    }

    private fun recupererIdsDesdomainesInteretsEtMetierDistincts(
        explicationsParFormation: Map<String, ExplicationsSuggestionEtExemplesMetiers?>,
    ) = explicationsParFormation.flatMap { it.value?.interetsDomainesMetiersChoisis ?: emptyList() }.takeUnless {
        it.isEmpty()
    }?.distinct()

    private fun recupererMetiersCourts(idsDesMetiersDomainesInterets: List<String>): List<MetierCourt> {
        val metiers = metierRepository.recupererLesMetiersCourts(idsDesMetiersDomainesInterets)
        return metiers
    }
}
