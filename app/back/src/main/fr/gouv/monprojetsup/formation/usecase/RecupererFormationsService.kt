package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import org.springframework.stereotype.Service

@Service
class RecupererFormationsService(
    private val formationRepository: FormationRepository,
    private val recupererTripletAffectationDUneFormationService: RecupererTripletAffectationDUneFormationService,
    private val critereAnalyseCandidatureService: CritereAnalyseCandidatureService,
    private val recupererExplicationsEtExemplesMetiersPourFormationService: RecupererExplicationsEtExemplesMetiersPourFormationService,
    private val statistiquesDesAdmisPourFormationsService: StatistiquesDesAdmisPourFormationsService,
    private val metiersTriesParProfilBuilder: MetiersTriesParProfilBuilder,
    private val calculDuTauxDAffiniteBuilder: CalculDuTauxDAffiniteBuilder,
) {
    fun recupererFichesFormationPourProfil(
        profilEleve: ProfilEleve.Identifie,
        suggestionsPourUnProfil: SuggestionsPourUnProfil,
        idsFormations: List<String>,
    ): List<FicheFormation.FicheFormationPourProfil> {
        val formations = formationRepository.recupererLesFormationsAvecLeursMetiers(idsFormations)
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
        val tripletsAffectations =
            recupererTripletAffectationDUneFormationService.recupererTripletAffectationTriesParAffinites(
                idsDesFormationsRetournees,
                profilEleve,
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
                communesTrieesParAffinites = tripletsAffectations[formation.id]?.map { it.commune.nom }?.distinct() ?: emptyList(),
                tripletsAffectation = tripletsAffectations[formation.id] ?: emptyList(),
                criteresAnalyseCandidature = criteresAnalyseCandidature[formation.id] ?: emptyList(),
                explications = explicationsDeLaFormation,
                statistiquesDesAdmis = statistiquesDesAdmis[formation.id],
            )
        }
    }
}
