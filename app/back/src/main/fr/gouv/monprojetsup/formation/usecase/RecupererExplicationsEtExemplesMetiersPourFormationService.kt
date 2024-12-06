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
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatRepository
import fr.gouv.monprojetsup.referentiel.domain.port.SpecialitesRepository
import org.springframework.stereotype.Service

@Service
class RecupererExplicationsEtExemplesMetiersPourFormationService(
    private val suggestionHttpClient: SuggestionHttpClient,
    private val formationRepository: FormationRepository,
    private val voeuxRepository: VoeuRepository,
    private val baccalaureatRepository: BaccalaureatRepository,
    private val specialitesRepository: SpecialitesRepository,
    private val metierRepository: MetierRepository,
    private val choixEleveService: ChoixEleveService,
) {
    fun recupererExplicationsEtExemplesDeMetiers(
        profilEleve: ProfilEleve.AvecProfilExistant,
        idsFormations: List<String>,
    ): Map<String, Pair<ExplicationsSuggestionDetaillees, List<Metier>>> {
        val explicationsParFormation = suggestionHttpClient.recupererLesExplications(profilEleve, idsFormations)
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
        val choixEleve = choixEleveService.recupererChoixEleve(explicationsParFormation)
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
                    }?.distinct() ?: emptyList(),
                formationsSimilaires =
                    explications?.formationsSimilaires?.mapNotNull {
                        formationsSimilaires.firstOrNull { formation -> formation.id == it }
                    }?.distinct() ?: emptyList(),
                choixEleve = choixEleve[idFormation]!!,
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
                }?.distinct() ?: emptyList()
            )
        }
    }

    fun recupererExplicationsEtExemplesDeMetiers(
        profilEleve: ProfilEleve.AvecProfilExistant,
        idFormation: String,
    ): Pair<ExplicationsSuggestionDetaillees, List<Metier>> {
        val explications = suggestionHttpClient.recupererLesExplications(profilEleve, listOf(idFormation))[idFormation]!!
        val formationsSimilaires =
            if (explications.formationsSimilaires.isNotEmpty()) {
                formationRepository.recupererLesNomsDesFormations(explications.formationsSimilaires) +
                    voeuxRepository.recupererLesNomsDesVoeux(explications.formationsSimilaires)
            } else {
                emptyList()
            }
        val specialites =
            explications.specialitesChoisies.takeUnless { it.isEmpty() }
                ?.map { it.idSpecialite }?.let { idsSpecialites ->
                    specialitesRepository.recupererLesSpecialites(idsSpecialites)
                } ?: emptyList()
        val autres = explications.detailsCalculScore.takeUnless { it.isEmpty() } ?: emptyList()
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
            choixEleve = choixEleveService.recupererChoixEleve(explications),
            explicationAutoEvaluationMoyenne = recupererExplicationAutoEvaluationMoyenne(explications),
            explicationTypeBaccalaureat = recupererExplicationTypeBaccalaureat(explications.typeBaccalaureat),
            detailsCalculScore = autres,
        ) to
            explications.exemplesDeMetiers.let { metiers ->
                metierRepository.recupererLesMetiers(metiers)
            }
    }

    private fun recupererMetiers(explicationsParFormation: Map<String, ExplicationsSuggestionEtExemplesMetiers?>): List<Metier> {
        val idsDesMetiers = explicationsParFormation.flatMap { it.value?.exemplesDeMetiers ?: emptyList() }.distinct()
        val metiers = metierRepository.recupererLesMetiers(idsDesMetiers)
        return metiers
    }

    private fun recupererFormationsSimilaires(explicationsParFormation: Map<String, ExplicationsSuggestionEtExemplesMetiers?>) =
        explicationsParFormation.flatMap { it.value?.formationsSimilaires ?: emptyList() }.takeUnless {
            it.isEmpty()
        }?.let {
            formationRepository.recupererLesNomsDesFormations(it.distinct()) +
                voeuxRepository.recupererLesNomsDesVoeux(it.distinct())
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
        baccalaureatUtilise = baccalaureat ?: creerBaccalaureatParDefaut(autoEvaluationMoyenne.baccalaureatUtilise),
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
        typeBaccalaureat: TypeBaccalaureat,
    ) = ExplicationTypeBaccalaureat(
        baccalaureat = baccalaureat ?: creerBaccalaureatParDefaut(typeBaccalaureat.nomBaccalaureat),
        pourcentage = typeBaccalaureat.pourcentage,
    )

    private fun creerBaccalaureatParDefaut(nomBaccalaureat: String) =
        Baccalaureat(
            id = nomBaccalaureat,
            idExterne = nomBaccalaureat,
            nom = nomBaccalaureat,
            idCarteParcoursup = "0",
        )
}
