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
import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatRepository
import fr.gouv.monprojetsup.referentiel.domain.port.DomaineRepository
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import fr.gouv.monprojetsup.referentiel.domain.port.SpecialitesRepository
import org.springframework.stereotype.Service

@Service
class RecupererExplicationsEtExemplesMetiersPourFormationService(
    val suggestionHttpClient: SuggestionHttpClient,
    val formationRepository: FormationRepository,
    val baccalaureatRepository: BaccalaureatRepository,
    val specialitesRepository: SpecialitesRepository,
    val interetRepository: InteretRepository,
    val domaineRepository: DomaineRepository,
    val metierRepository: MetierRepository,
) {
    fun recupererExplicationsEtExemplesDeMetiers(
        profilEleve: ProfilEleve.AvecProfilExistant,
        idsFormations: List<String>,
    ): Map<String, Pair<ExplicationsSuggestionDetaillees, List<Metier>>> {
        val explicationsParFormation = suggestionHttpClient.recupererLesExplications(profilEleve, idsFormations)
        val domainesInteretsMetiersDistincts =
            explicationsParFormation.flatMap { it.value?.interetsDomainesMetiersChoisis ?: emptyList() }.takeUnless {
                it.isEmpty()
            }?.distinct()

        val domainesChoisis = domainesInteretsMetiersDistincts?.let { domaineRepository.recupererLesDomaines(it) } ?: emptyList()
        val interetsChoisis =
            domainesInteretsMetiersDistincts?.let { interetRepository.recupererLesSousCategoriesDInterets(it) } ?: emptyMap()

        val formationsSimilaires = recupererFormationsSimilaires(explicationsParFormation)
        val baccalaureats = recupererBaccalaureats(explicationsParFormation)
        val metiers = recupererMetiers(explicationsParFormation)
        val specialites =
            explicationsParFormation.flatMap { it.value?.specialitesChoisies ?: emptyList() }.map { it.idSpecialite }
                .distinct().takeUnless {
                    it.isEmpty()
                }?.let {
                    specialitesRepository.recupererLesSpecialites(it)
                } ?: emptyList()
        return idsFormations.associateWith { idFormation ->
            val explications: ExplicationsSuggestionEtExemplesMetiers? = explicationsParFormation[idFormation]
            ExplicationsSuggestionDetaillees(
                geographique = filtrerEtTrier(explicationsGeographique = explications?.geographique ?: emptyList()),
                dureeEtudesPrevue = explications?.dureeEtudesPrevue,
                alternance = explications?.alternance,
                specialitesChoisies =
                    explications?.specialitesChoisies?.mapNotNull { affiniteSpecialite ->
                        specialites.firstOrNull { it.id == affiniteSpecialite.idSpecialite }?.label?.let { label ->
                            ExplicationsSuggestionDetaillees.AffiniteSpecialite(
                                idSpecialite = affiniteSpecialite.idSpecialite,
                                nomSpecialite = label,
                                pourcentage = affiniteSpecialite.pourcentage,
                            )
                        }
                    } ?: emptyList(),
                formationsSimilaires =
                    explications?.formationsSimilaires?.mapNotNull {
                        formationsSimilaires.firstOrNull { formation -> formation.id == it }
                    } ?: emptyList(),
                interetsChoisis =
                    explications?.interetsDomainesMetiersChoisis?.mapNotNull { interetOuDomaineOuMetierId ->
                        interetsChoisis[interetOuDomaineOuMetierId]
                    }?.distinct() ?: emptyList(),
                domainesChoisis =
                    explications?.interetsDomainesMetiersChoisis?.mapNotNull { interetOuDomaineOuMetierId ->
                        domainesChoisis.firstOrNull { domaine -> domaine.id == interetOuDomaineOuMetierId }
                    } ?: emptyList(),
                metiersChoisis =
                    explications?.interetsDomainesMetiersChoisis?.mapNotNull { interetOuDomaineOuMetierId ->
                        metiers.firstOrNull { metier -> metier.id == interetOuDomaineOuMetierId }?.let { metier ->
                            MetierCourt(metier.id, metier.nom)
                        }
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
                detailsCalculScore =
                    explications?.detailsCalculScore.takeUnless { it.isNullOrEmpty() } ?: emptyList(),
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
        profilEleve: ProfilEleve.AvecProfilExistant,
        idFormation: String,
    ): Pair<ExplicationsSuggestionDetaillees, List<Metier>> {
        val explications =
            suggestionHttpClient.recupererLesExplications(profilEleve, listOf(idFormation))[idFormation]!!
        val interetDomainesMetiersChoisis =
            explications.interetsDomainesMetiersChoisis.takeUnless {
                it.isEmpty()
            }
        val domaines = interetDomainesMetiersChoisis?.let { domaineRepository.recupererLesDomaines(it) } ?: emptyList()
        val interets =
            interetDomainesMetiersChoisis?.let {
                interetRepository.recupererLesSousCategoriesDInterets(it).map { entry -> entry.value }.distinct()
            } ?: emptyList()
        val metiersChoisis = interetDomainesMetiersChoisis?.let { metierRepository.recupererLesMetiersCourts(it) } ?: emptyList()
        val formationsSimilaires =
            if (explications.formationsSimilaires.isNotEmpty()) {
                formationRepository.recupererLesNomsDesFormations(explications.formationsSimilaires)
            } else {
                emptyList()
            }
        val specialites =
            explications.specialitesChoisies.takeUnless { it.isEmpty() }
                ?.map { it.idSpecialite }?.let { idsSpecialites ->
                    specialitesRepository.recupererLesSpecialites(idsSpecialites)
                } ?: emptyList()
        val autres =
            explications.detailsCalculScore.takeUnless { it.isEmpty() }
                ?: emptyList()
        return ExplicationsSuggestionDetaillees(
            geographique = filtrerEtTrier(explications.geographique),
            dureeEtudesPrevue = explications.dureeEtudesPrevue,
            alternance = explications.alternance,
            specialitesChoisies =
                explications.specialitesChoisies.mapNotNull { affiniteSpecialite ->
                    specialites.firstOrNull { it.id == affiniteSpecialite.idSpecialite }?.label?.let { label ->
                        ExplicationsSuggestionDetaillees.AffiniteSpecialite(
                            idSpecialite = affiniteSpecialite.idSpecialite,
                            nomSpecialite = label,
                            pourcentage = affiniteSpecialite.pourcentage,
                        )
                    }
                },
            formationsSimilaires = formationsSimilaires,
            interetsChoisis = interets,
            domainesChoisis = domaines,
            metiersChoisis = metiersChoisis,
            explicationAutoEvaluationMoyenne = recupererExplicationAutoEvaluationMoyenne(explications),
            explicationTypeBaccalaureat = recupererExplicationTypeBaccalaureat(explications.typeBaccalaureat),
            detailsCalculScore = autres,
        ) to
            explications.exemplesDeMetiers.let { metiers ->
                metierRepository.recupererLesMetiers(metiers)
            }
    }

    private fun recupererMetiers(explicationsParFormation: Map<String, ExplicationsSuggestionEtExemplesMetiers?>): List<Metier> {
        val idsDesMetiers = explicationsParFormation.flatMap { it.value?.exemplesDeMetiers ?: emptyList() }
        val idsDesMetiersDomainesInterets = explicationsParFormation.flatMap { it.value?.interetsDomainesMetiersChoisis ?: emptyList() }
        val totalIds = (idsDesMetiers + idsDesMetiersDomainesInterets).distinct()
        val metiers = metierRepository.recupererLesMetiers(totalIds)
        return metiers
    }

    private fun recupererFormationsSimilaires(explicationsParFormation: Map<String, ExplicationsSuggestionEtExemplesMetiers?>) =
        explicationsParFormation.flatMap { it.value?.formationsSimilaires ?: emptyList() }.takeUnless {
            it.isEmpty()
        }?.let {
            formationRepository.recupererLesNomsDesFormations(it.distinct())
        } ?: emptyList()

    private fun recupererBaccalaureats(
        explicationsParFormation: Map<String, ExplicationsSuggestionEtExemplesMetiers?>,
    ): List<Baccalaureat> {
        val idsExternesBaccalaureats =
            (
                explicationsParFormation.mapNotNull { it.value?.autoEvaluationMoyenne?.baccalaureatUtilise } +
                    explicationsParFormation.mapNotNull { it.value?.typeBaccalaureat?.nomBaccalaureat }
            ).distinct()
        val baccalaureats =
            idsExternesBaccalaureats.takeUnless { it.isEmpty() }?.let {
                baccalaureatRepository.recupererDesBaccalaureatsParIdsExternes(idsExternesBaccalaureats)
            } ?: emptyList()
        return baccalaureats
    }

    private fun filtrerEtTrier(explicationsGeographique: List<ExplicationGeographique>): List<ExplicationGeographique> {
        val explicationsGeographiquesFiltrees =
            explicationsGeographique.sortedBy { it.distanceKm }.distinctBy { it.ville }
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
        moyenneAutoEvalue = autoEvaluationMoyenne.echellonDeLaMoyenneAutoEvalue * TAILLE_ECHELLON_NOTES,
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
                id = it.nomBaccalaureat,
                idExterne = it.nomBaccalaureat,
                nom = it.nomBaccalaureat,
            ),
        pourcentage = it.pourcentage,
    )
}
