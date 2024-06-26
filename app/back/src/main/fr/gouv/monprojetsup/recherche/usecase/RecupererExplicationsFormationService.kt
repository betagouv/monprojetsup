package fr.gouv.monprojetsup.recherche.usecase

import fr.gouv.monprojetsup.commun.Constantes.TAILLE_ECHELLON_NOTES
import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.recherche.domain.entity.Domaine
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion.TypeBaccalaureat
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationAutoEvaluationMoyenne
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationTypeBaccalaureat
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation.FicheFormationPourProfil.MoyenneGeneraleDesAdmis
import fr.gouv.monprojetsup.recherche.domain.entity.InteretSousCategorie
import fr.gouv.monprojetsup.recherche.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.recherche.domain.port.BaccalaureatRepository
import fr.gouv.monprojetsup.recherche.domain.port.DomaineRepository
import fr.gouv.monprojetsup.recherche.domain.port.FormationRepository
import fr.gouv.monprojetsup.recherche.domain.port.InteretRepository
import fr.gouv.monprojetsup.recherche.domain.port.SuggestionHttpClient
import org.springframework.stereotype.Service

@Service
class RecupererExplicationsFormationService(
    val suggestionHttpClient: SuggestionHttpClient,
    val formationRepository: FormationRepository,
    val baccalaureatRepository: BaccalaureatRepository,
    val interetRepository: InteretRepository,
    val domaineRepository: DomaineRepository,
    val moyenneGeneraleDesAdmisService: MoyenneGeneraleDesAdmisService,
) {
    fun recupererExplications(
        profilEleve: ProfilEleve,
        idFormation: String,
    ): ExplicationsSuggestionDetaillees {
        val moyenneGeneraleDesAdmis =
            recupererMoyenneGeneraleDesAdmis(
                idBaccalaureat = profilEleve.bac,
                idFormation = idFormation,
                classe = profilEleve.classe,
            )
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
            moyenneGeneraleDesAdmis = moyenneGeneraleDesAdmis,
            formationsSimilaires = formationsSimilaires,
            interets = interets,
            domaines = domaines,
            explicationAutoEvaluationMoyenne = recupererExplicationAutoEvaluationMoyenne(explications),
            explicationTypeBaccalaureat = recupererExplicationTypeBaccalaureat(explications.typeBaccalaureat),
        )
    }

    private fun recupererMoyenneGeneraleDesAdmis(
        idBaccalaureat: String?,
        idFormation: String,
        classe: ChoixNiveau,
    ): MoyenneGeneraleDesAdmis? {
        val baccalaureat = idBaccalaureat?.let { baccalaureatRepository.recupererUnBaccalaureat(it) }
        return moyenneGeneraleDesAdmisService.recupererMoyenneGeneraleDesAdmisDUneFormation(baccalaureat, idFormation, classe)
    }

    private fun filtrerEtTrier(explicationsGeographique: List<ExplicationGeographique>): List<ExplicationGeographique> {
        val explicationsGeographiquesFiltrees = explicationsGeographique.sortedBy { it.distanceKm }.distinctBy { it.ville }
        return explicationsGeographiquesFiltrees
    }

    private fun recupererExplicationAutoEvaluationMoyenne(explications: ExplicationsSuggestion): ExplicationAutoEvaluationMoyenne? {
        return explications.autoEvaluationMoyenne?.let {
            val baccalaureat = baccalaureatRepository.recupererUnBaccalaureatParIdExterne(it.bacUtilise)
            ExplicationAutoEvaluationMoyenne(
                baccalaureat = baccalaureat ?: Baccalaureat(it.bacUtilise, it.bacUtilise, it.bacUtilise),
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
