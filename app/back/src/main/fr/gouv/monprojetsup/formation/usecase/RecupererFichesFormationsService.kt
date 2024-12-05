package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.InformationsSurLesVoeuxEtLeursCommunes
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import org.springframework.stereotype.Service

@Service
class RecupererFichesFormationsService(
    private val formationRepository: FormationRepository,
    private val metierRepository: MetierRepository,
    private val recupererInformationsSurLesVoeuxEtLeursCommunesService: RecupererInformationsSurLesVoeuxEtLeursCommunesService,
    private val critereAnalyseCandidatureService: CritereAnalyseCandidatureService,
    private val recupererExplicationsEtExemplesMetiersPourFormationService: RecupererExplicationsEtExemplesMetiersPourFormationService,
    private val statistiquesDesAdmisPourFormationsService: StatistiquesDesAdmisPourFormationsService,
    private val metiersTriesParProfilBuilder: MetiersTriesParProfilBuilder,
    private val calculDuTauxDAffiniteBuilder: CalculDuTauxDAffiniteBuilder,
    private val logger: MonProjetSupLogger,
) {
    fun recupererFichesFormationPourProfil(
        profilEleve: ProfilEleve.AvecProfilExistant,
        suggestionsPourUnProfil: SuggestionsPourUnProfil,
        idsFormations: List<String>,
        obsoletesInclus: Boolean,
    ): List<FicheFormation.FicheFormationPourProfil> {
        val formations = formationRepository.recupererLesFormations(idsFormations, obsoletesInclus)
        val idsDesFormationsRetournees = formations.map { it.id }
        val criteresAnalyseCandidature = critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formations)
        val statistiquesDesAdmis =
            statistiquesDesAdmisPourFormationsService.recupererStatistiquesAdmisDeFormations(
                idBaccalaureat = profilEleve.baccalaureat,
                idsFormations = idsDesFormationsRetournees,
                classe = profilEleve.classe,
            )
        val explications =
            recupererExplicationsEtExemplesMetiersPourFormationService.recupererExplicationsEtExemplesDeMetiers(
                profilEleve,
                idsDesFormationsRetournees,
            )
        val voeux =
            recupererInformationsSurLesVoeuxEtLeursCommunesService.recupererInformationsSurLesVoeuxEtLeursCommunes(
                idsDesFormationsRetournees,
                profilEleve,
                obsoletesInclus,
            )

        return formations.map { formation ->
            val (explicationsDeLaFormation, exemplesDeMetiersDeLaFormation) = explications[formation.id] ?: Pair(null, emptyList())
            FicheFormation.FicheFormationPourProfil(
                id = formation.id,
                nom = formation.nom,
                descriptifGeneral = formation.descriptifGeneral,
                descriptifAttendus = formation.descriptifAttendus,
                descriptifDiplome = formation.descriptifDiplome,
                descriptifConseils = formation.descriptifConseils,
                formationsAssociees = formation.formationsAssociees,
                liens = formation.liens,
                tauxAffinite =
                    calculDuTauxDAffiniteBuilder.calculDuTauxDAffinite(
                        formationAvecLeurAffinite = suggestionsPourUnProfil.formations,
                        idFormation = formation.id,
                    ),
                metiersTriesParAffinites =
                    metiersTriesParProfilBuilder.trierMetiersParAffinites(
                        metiers = exemplesDeMetiersDeLaFormation,
                        idsMetierTriesParAffinite = suggestionsPourUnProfil.metiersTriesParAffinites,
                    ),
                informationsSurLesVoeuxEtLeursCommunes = recupererInformationsSurLesVoeuxEtLeursCommunes(voeux, formation),
                criteresAnalyseCandidature = criteresAnalyseCandidature[formation.id] ?: emptyList(),
                explications = explicationsDeLaFormation,
                statistiquesDesAdmis = statistiquesDesAdmis[formation.id],
                apprentissage = formation.apprentissage,
            )
        }
    }

    private fun recupererInformationsSurLesVoeuxEtLeursCommunes(
        voeux: Map<String, InformationsSurLesVoeuxEtLeursCommunes>,
        formation: Formation,
    ): InformationsSurLesVoeuxEtLeursCommunes {
        val voeuxDeLaFormation = voeux[formation.id]
        return if (voeuxDeLaFormation != null) {
            return voeuxDeLaFormation
        } else {
            logger.error(
                type = "FORMATION_SANS_VOEUX",
                message = "La formation ${formation.id} n'est pas présente dans la map des formations",
                exception = null,
                parametres = mapOf("idFormation" to formation.id),
            )
            InformationsSurLesVoeuxEtLeursCommunes(
                voeux = emptyList(),
                communesTriees = emptyList(),
                voeuxParCommunesFavorites = emptyList(),
            )
        }
    }

    fun recupererFichesFormation(
        idsFormations: List<String>,
        obsoletesInclus: Boolean,
    ): List<FicheFormation.FicheFormationSansProfil> {
        val formations = formationRepository.recupererLesFormations(idsFormations, obsoletesInclus)
        val metiers = metierRepository.recupererMetiersDeFormations(idsFormations, obsoletesInclus)
        val idsDesFormationsRetournees = formations.map { it.id }
        val criteresAnalyseCandidature = critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formations)
        val statistiquesDesAdmis =
            statistiquesDesAdmisPourFormationsService.recupererStatistiquesAdmisDeFormations(
                idBaccalaureat = null,
                idsFormations = idsDesFormationsRetournees,
                classe = null,
            )
        val voeux =
            recupererInformationsSurLesVoeuxEtLeursCommunesService.recupererVoeux(idsDesFormationsRetournees, obsoletesInclus)
        return formations.map { formation ->
            FicheFormation.FicheFormationSansProfil(
                id = formation.id,
                nom = formation.nom,
                descriptifGeneral = formation.descriptifGeneral,
                descriptifAttendus = formation.descriptifAttendus,
                descriptifDiplome = formation.descriptifDiplome,
                descriptifConseils = formation.descriptifConseils,
                formationsAssociees = formation.formationsAssociees,
                liens = formation.liens,
                voeux = voeux[formation.id] ?: emptyList(),
                metiers = metiers[formation.id] ?: emptyList(),
                criteresAnalyseCandidature = criteresAnalyseCandidature[formation.id] ?: emptyList(),
                statistiquesDesAdmis = statistiquesDesAdmis[formation.id],
                apprentissage = formation.apprentissage,
            )
        }
    }
}
