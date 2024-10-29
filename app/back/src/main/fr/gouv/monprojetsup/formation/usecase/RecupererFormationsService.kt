package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import org.springframework.stereotype.Service

@Service
class RecupererFormationsService(
    private val formationRepository: FormationRepository,
    private val metierRepository: MetierRepository,
    private val recupererVoeuxDUneFormationService: RecupererVoeuxDUneFormationService,
    private val recupererVoeuxDesCommunesFavoritesService: RecupererVoeuxDesCommunesFavoritesService,
    private val critereAnalyseCandidatureService: CritereAnalyseCandidatureService,
    private val recupererExplicationsEtExemplesMetiersPourFormationService: RecupererExplicationsEtExemplesMetiersPourFormationService,
    private val statistiquesDesAdmisPourFormationsService: StatistiquesDesAdmisPourFormationsService,
    private val metiersTriesParProfilBuilder: MetiersTriesParProfilBuilder,
    private val calculDuTauxDAffiniteBuilder: CalculDuTauxDAffiniteBuilder,
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
            recupererVoeuxDUneFormationService.recupererVoeuxTriesParAffinites(
                idsDesFormationsRetournees,
                profilEleve,
                obsoletesInclus,
            )
        val voeuxAutoursDesCommunesFavorites =
            profilEleve.communesFavorites?.let {
                recupererVoeuxDesCommunesFavoritesService.recupererVoeuxAutoursDeCommmunes(it, voeux)
            } ?: emptyMap()
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
                voeux = voeux[formation.id] ?: emptyList(),
                voeuxParCommunesFavorites = voeuxAutoursDesCommunesFavorites[formation.id] ?: emptyList(),
                criteresAnalyseCandidature = criteresAnalyseCandidature[formation.id] ?: emptyList(),
                explications = explicationsDeLaFormation,
                statistiquesDesAdmis = statistiquesDesAdmis[formation.id],
                apprentissage = formation.apprentissage,
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
        val voeux = recupererVoeuxDUneFormationService.recupererVoeux(idsDesFormationsRetournees, obsoletesInclus)
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
