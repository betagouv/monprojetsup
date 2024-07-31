package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.Constantes.TAILLE_ECHELLON_NOTES
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers.TypeBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationAutoEvaluationMoyenne
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationTypeBaccalaureat
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatRepository
import fr.gouv.monprojetsup.referentiel.domain.port.DomaineRepository
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import org.springframework.stereotype.Service

@Service
class RecupererExplicationsEtExemplesMetiersPourFormationService(
    val suggestionHttpClient: SuggestionHttpClient,
    val formationRepository: FormationRepository,
    val baccalaureatRepository: BaccalaureatRepository,
    val interetRepository: InteretRepository,
    val domaineRepository: DomaineRepository,
    val metierRepository: MetierRepository,
) {
    fun recupererExplicationsEtExemplesDeMetiers(
        profilEleve: ProfilEleve.Identifie,
        idsFormations: List<String>,
    ): Map<String, Pair<ExplicationsSuggestionDetaillees, List<Metier>>> {
        val explicationsParFormation: Map<String, ExplicationsSuggestionEtExemplesMetiers?> =
            suggestionHttpClient.recupererLesExplications(profilEleve, idsFormations)
        val (domaines: List<Domaine>?, interets: Map<String, InteretSousCategorie>?) =
            explicationsParFormation.flatMap { it.value?.interetsEtDomainesChoisis ?: emptyList() }.takeUnless {
                it.isEmpty()
            }?.let {
                val domainesEtInteretsDistincts = it.distinct()
                val domaines = domaineRepository.recupererLesDomaines(domainesEtInteretsDistincts)
                val interets = interetRepository.recupererLesSousCategoriesDInterets(domainesEtInteretsDistincts)
                Pair(domaines, interets)
            } ?: Pair(emptyList(), emptyMap())
        val formationsSimilaires =
            explicationsParFormation.flatMap { it.value?.formationsSimilaires ?: emptyList() }.takeUnless {
                it.isEmpty()
            }?.let {
                formationRepository.recupererLesNomsDesFormations(it.distinct())
            } ?: emptyList()
        val idsExternesBaccalaureats =
            (
                explicationsParFormation.mapNotNull { it.value?.autoEvaluationMoyenne?.baccalaureatUtilise } +
                    explicationsParFormation.mapNotNull { it.value?.typeBaccalaureat?.nomBaccalaureat }
            ).distinct()
        val baccalaureats = baccalaureatRepository.recupererDesBaccalaureatsParIdsExternes(idsExternesBaccalaureats)
        val idsDesMetiers = explicationsParFormation.flatMap { it.value?.exemplesDeMetiers ?: emptyList() }.distinct()
        val metiers = metierRepository.recupererLesMetiersDetailles(idsDesMetiers)
        return idsFormations.associateWith {
            val explications: ExplicationsSuggestionEtExemplesMetiers? = explicationsParFormation[it]
            ExplicationsSuggestionDetaillees(
                geographique = filtrerEtTrier(explicationsGeographique = explications?.geographique ?: emptyList()),
                dureeEtudesPrevue = explications?.dureeEtudesPrevue,
                alternance = explications?.alternance,
                specialitesChoisies = explications?.specialitesChoisies ?: emptyList(),
                formationsSimilaires =
                    explications?.formationsSimilaires?.mapNotNull { formationId ->
                        formationsSimilaires.firstOrNull { formation -> formation.id == formationId }
                    } ?: emptyList(),
                interets =
                    explications?.interetsEtDomainesChoisis?.mapNotNull { interetOuDomaineId ->
                        interets[interetOuDomaineId]
                    }?.distinct() ?: emptyList(),
                domaines =
                    explications?.interetsEtDomainesChoisis?.mapNotNull { interetOuDomaineId ->
                        domaines.firstOrNull { domaine -> domaine.id == interetOuDomaineId }
                    } ?: emptyList(),
                explicationAutoEvaluationMoyenne =
                    explications?.autoEvaluationMoyenne?.let { autoEvaluationMoyenne ->
                        explicationAutoEvaluationMoyenne(
                            baccalaureats.firstOrNull { baccalaureat ->
                                baccalaureat.idExterne == autoEvaluationMoyenne.baccalaureatUtilise
                            },
                            autoEvaluationMoyenne,
                        )
                    },
                explicationTypeBaccalaureat =
                    explications?.typeBaccalaureat?.let { typeBaccalaureat ->
                        explicationTypeBaccalaureat(
                            baccalaureats.firstOrNull { baccalaureat -> baccalaureat.idExterne == typeBaccalaureat.nomBaccalaureat },
                            typeBaccalaureat,
                        )
                    },
            ) to (
                explications?.exemplesDeMetiers?.let { idsMetiers ->
                    idsMetiers.mapNotNull { idMetier ->
                        metiers.firstOrNull { it.id == idMetier }
                    }
                } ?: emptyList()
            )
        }
    }

    fun recupererExplicationsEtExemplesDeMetiers(
        profilEleve: ProfilEleve.Identifie,
        idFormation: String,
    ): Pair<ExplicationsSuggestionDetaillees, List<Metier>> {
        val explications = suggestionHttpClient.recupererLesExplications(profilEleve, listOf(idFormation))[idFormation]!!
        val (domaines: List<Domaine>?, interets: List<InteretSousCategorie>?) =
            explications.interetsEtDomainesChoisis.takeUnless {
                it.isEmpty()
            }?.let {
                val domaines = domaineRepository.recupererLesDomaines(it)
                val interets = interetRepository.recupererLesSousCategoriesDInterets(it).map { entry -> entry.value }.distinct()
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
        ) to
            explications.exemplesDeMetiers.let { metiers ->
                metierRepository.recupererLesMetiersDetailles(metiers)
            }
    }

    private fun filtrerEtTrier(explicationsGeographique: List<ExplicationGeographique>): List<ExplicationGeographique> {
        val explicationsGeographiquesFiltrees = explicationsGeographique.sortedBy { it.distanceKm }.distinctBy { it.ville }
        return explicationsGeographiquesFiltrees
    }

    private fun recupererExplicationAutoEvaluationMoyenne(
        explications: ExplicationsSuggestionEtExemplesMetiers,
    ): ExplicationAutoEvaluationMoyenne? {
        return explications.autoEvaluationMoyenne?.let {
            val baccalaureat = baccalaureatRepository.recupererUnBaccalaureatParIdExterne(it.baccalaureatUtilise)
            explicationAutoEvaluationMoyenne(baccalaureat, it)
        }
    }

    private fun explicationAutoEvaluationMoyenne(
        baccalaureat: Baccalaureat?,
        autoEvaluationMoyenne: ExplicationsSuggestionEtExemplesMetiers.AutoEvaluationMoyenne,
    ) = ExplicationAutoEvaluationMoyenne(
        baccalaureatUtilise =
            baccalaureat ?: Baccalaureat(
                id = autoEvaluationMoyenne.baccalaureatUtilise,
                idExterne = autoEvaluationMoyenne.baccalaureatUtilise,
                nom = autoEvaluationMoyenne.baccalaureatUtilise,
            ),
        moyenneAutoEvalue = autoEvaluationMoyenne.moyenneAutoEvalue,
        basIntervalleNotes = autoEvaluationMoyenne.rangs.rangEch25 * TAILLE_ECHELLON_NOTES,
        hautIntervalleNotes = autoEvaluationMoyenne.rangs.rangEch75 * TAILLE_ECHELLON_NOTES,
    )

    private fun recupererExplicationTypeBaccalaureat(typeBaccalaureat: TypeBaccalaureat?): ExplicationTypeBaccalaureat? {
        return typeBaccalaureat?.let {
            val baccalaureat = baccalaureatRepository.recupererUnBaccalaureatParIdExterne(it.nomBaccalaureat)
            explicationTypeBaccalaureat(baccalaureat, it)
        }
    }

    private fun explicationTypeBaccalaureat(
        baccalaureat: Baccalaureat?,
        it: TypeBaccalaureat,
    ) = ExplicationTypeBaccalaureat(
        baccalaureat =
            baccalaureat ?: Baccalaureat(
                it.nomBaccalaureat,
                it.nomBaccalaureat,
                it.nomBaccalaureat,
            ),
        pourcentage = it.pourcentage,
    )
}
