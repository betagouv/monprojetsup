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
        profilEleve: ProfilEleve.Identifie,
        idsFormations: List<String>,
    ): Map<String, Pair<ExplicationsSuggestionDetaillees, List<Metier>>> {
        val explicationsParFormation = suggestionHttpClient.recupererLesExplications(profilEleve, idsFormations)
        val (domaines: List<Domaine>?, interets: Map<String, InteretSousCategorie>?) =
            recupererDomainesEtInterets(
                explicationsParFormation,
            )
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
        profilEleve: ProfilEleve.Identifie,
        idFormation: String,
    ): Pair<ExplicationsSuggestionDetaillees, List<Metier>> {
        val explications =
            suggestionHttpClient.recupererLesExplications(profilEleve, listOf(idFormation))[idFormation]!!
        val (domaines: List<Domaine>?, interets: List<InteretSousCategorie>?) =
            explications.interetsEtDomainesChoisis.takeUnless {
                it.isEmpty()
            }?.let {
                val domaines = domaineRepository.recupererLesDomaines(it)
                val interets =
                    interetRepository.recupererLesSousCategoriesDInterets(it).map { entry -> entry.value }.distinct()
                Pair(domaines, interets)
            } ?: Pair(emptyList(), emptyList())
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
            interets = interets,
            domaines = domaines,
            explicationAutoEvaluationMoyenne = recupererExplicationAutoEvaluationMoyenne(explications),
            explicationTypeBaccalaureat = recupererExplicationTypeBaccalaureat(explications.typeBaccalaureat),
            detailsCalculScore = autres,
        ) to
            explications.exemplesDeMetiers.let { metiers ->
                metierRepository.recupererLesMetiers(metiers)
            }
    }

    private fun recupererDomainesEtInterets(explicationsParFormation: Map<String, ExplicationsSuggestionEtExemplesMetiers?>) =
        explicationsParFormation.flatMap { it.value?.interetsEtDomainesChoisis ?: emptyList() }.takeUnless {
            it.isEmpty()
        }?.let {
            val domainesEtInteretsDistincts = it.distinct()
            val domaines = domaineRepository.recupererLesDomaines(domainesEtInteretsDistincts)
            val interets = interetRepository.recupererLesSousCategoriesDInterets(domainesEtInteretsDistincts)
            Pair(domaines, interets)
        } ?: Pair(emptyList(), emptyMap())

    private fun recupererMetiers(explicationsParFormation: Map<String, ExplicationsSuggestionEtExemplesMetiers?>): List<Metier> {
        val idsDesMetiers = explicationsParFormation.flatMap { it.value?.exemplesDeMetiers ?: emptyList() }.distinct()
        val metiers = metierRepository.recupererLesMetiers(idsDesMetiers)
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
