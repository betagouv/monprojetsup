package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import org.springframework.stereotype.Service

@Service
class RecupererFormationsService(
    val formationRepository: FormationRepository,
    val recupererCommunesDUneFormationService: RecupererCommunesDUneFormationService,
    val critereAnalyseCandidatureService: CritereAnalyseCandidatureService,
    val recupererExplicationsFormationService: RecupererExplicationsFormationService,
    val statistiquesDesAdmisPourFormationsService: StatistiquesDesAdmisPourFormationsService,
    val metiersTriesParProfilBuilder: MetiersTriesParProfilBuilder,
    val calculDuTauxDAffiniteBuilder: CalculDuTauxDAffiniteBuilder,
) {
    fun recupererFichesFormationPourProfil(
        profilEleve: ProfilEleve,
        affinitesFormationEtMetier: SuggestionsPourUnProfil,
        idsFormations: List<String>,
    ): List<FicheFormation.FicheFormationPourProfil> {
        val formations = formationRepository.recupererLesFormationsAvecLeursMetiers(idsFormations)
        val idsDesFormationsRetournees = formations.map { it.id }
        val criteresAnalyseCandidature = critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formations)
        val statistiquesDesAdmis =
            statistiquesDesAdmisPourFormationsService.recupererStatistiquesAdmisDeFormations(
                idBaccalaureat = profilEleve.bac,
                idsFormations = idsDesFormationsRetournees,
                classe = profilEleve.classe,
            )
        val explications = recupererExplicationsFormationService.recupererExplications(profilEleve, idsDesFormationsRetournees)
        val communes =
            recupererCommunesDUneFormationService.recupererNomCommunesTriesParAffinites(
                idsDesFormationsRetournees,
                profilEleve.communesPreferees,
            )
        return formations.map { formation ->
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
                        formationAvecLeurAffinite = affinitesFormationEtMetier.formations,
                        idFormation = formation.id,
                    ),
                metiersTriesParAffinites =
                    metiersTriesParProfilBuilder.trierMetiersParAffinites(
                        metiers = formation.metiers,
                        idsMetierTriesParAffinite = affinitesFormationEtMetier.metiersTriesParAffinites,
                    ),
                communesTrieesParAffinites = communes[formation.id] ?: emptyList(),
                criteresAnalyseCandidature = criteresAnalyseCandidature[formation.id] ?: emptyList(),
                explications = explications[formation.id],
                statistiquesDesAdmis = statistiquesDesAdmis[formation.id],
            )
        }
    }
}
