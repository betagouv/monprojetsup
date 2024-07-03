package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.commun.Constantes.TAILLE_ECHELLON_NOTES
import fr.gouv.monprojetsup.formation.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.Domaine
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestion
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestion.TypeBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationAutoEvaluationMoyenne
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationTypeBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.InteretSousCategorie
import fr.gouv.monprojetsup.formation.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.port.BaccalaureatRepository
import fr.gouv.monprojetsup.formation.domain.port.DomaineRepository
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.InteretRepository
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import org.springframework.stereotype.Service

@Service
class RecupererExplicationsFormationService(
    val suggestionHttpClient: SuggestionHttpClient,
    val formationRepository: FormationRepository,
    val baccalaureatRepository: BaccalaureatRepository,
    val interetRepository: InteretRepository,
    val domaineRepository: DomaineRepository,
) {
    fun recupererExplications(
        profilEleve: ProfilEleve,
        idFormation: String,
    ): ExplicationsSuggestionDetaillees {
        val explications = suggestionHttpClient.recupererLesExplications(profilEleve, idFormation)
        val (domaines: List<Domaine>?, interets: List<InteretSousCategorie>?) =
            explications.interetsEtDomainesChoisis.takeUnless {
                it.isEmpty()
            }?.let {
                val domaines = domaineRepository.recupererLesDomaines(it)
                val interets = interetRepository.recupererLesSousCategoriesDInterets(it)
                Pair(domaines, interets)
            } ?: Pair(emptyList(), emptyList())
        val formationsSimilaires =
            if (explications.formationsSimilaires.isNotEmpty()) {
                formationRepository.recupererLesNomsDesFormations(explications.formationsSimilaires)
            } else {
                emptyList()
            }
        return ExplicationsSuggestionDetaillees(
            geographique = filtrerEtTrier(explications.geographique),
            dureeEtudesPrevue = explications.dureeEtudesPrevue,
            alternance = explications.alternance,
            specialitesChoisies = explications.specialitesChoisies,
            formationsSimilaires = formationsSimilaires,
            interets = interets,
            domaines = domaines,
            explicationAutoEvaluationMoyenne = recupererExplicationAutoEvaluationMoyenne(explications),
            explicationTypeBaccalaureat = recupererExplicationTypeBaccalaureat(explications.typeBaccalaureat),
        )
    }

    private fun filtrerEtTrier(explicationsGeographique: List<ExplicationGeographique>): List<ExplicationGeographique> {
        val explicationsGeographiquesFiltrees = explicationsGeographique.sortedBy { it.distanceKm }.distinctBy { it.ville }
        return explicationsGeographiquesFiltrees
    }

    private fun recupererExplicationAutoEvaluationMoyenne(explications: ExplicationsSuggestion): ExplicationAutoEvaluationMoyenne? {
        return explications.autoEvaluationMoyenne?.let {
            val baccalaureat = baccalaureatRepository.recupererUnBaccalaureatParIdExterne(it.baccalaureatUtilise)
            ExplicationAutoEvaluationMoyenne(
                baccalaureatUtilise = baccalaureat ?: Baccalaureat(it.baccalaureatUtilise, it.baccalaureatUtilise, it.baccalaureatUtilise),
                moyenneAutoEvalue = it.moyenneAutoEvalue,
                basIntervalleNotes = it.rangs.rangEch25 * TAILLE_ECHELLON_NOTES,
                hautIntervalleNotes = it.rangs.rangEch75 * TAILLE_ECHELLON_NOTES,
            )
        }
    }

    private fun recupererExplicationTypeBaccalaureat(typeBaccalaureat: TypeBaccalaureat?): ExplicationTypeBaccalaureat? {
        return typeBaccalaureat?.let {
            val baccalaureat = baccalaureatRepository.recupererUnBaccalaureatParIdExterne(it.nomBaccalaureat)
            ExplicationTypeBaccalaureat(
                baccalaureat =
                    baccalaureat ?: Baccalaureat(
                        it.nomBaccalaureat,
                        it.nomBaccalaureat,
                        it.nomBaccalaureat,
                    ),
                pourcentage = it.pourcentage,
            )
        }
    }
}
