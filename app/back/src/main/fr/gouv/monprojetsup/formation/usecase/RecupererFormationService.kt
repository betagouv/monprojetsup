package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import org.springframework.stereotype.Service

@Service
class RecupererFormationService(
    val suggestionHttpClient: SuggestionHttpClient,
    val formationRepository: FormationRepository,
    val recupererCommunesDUneFormationService: RecupererCommunesDUneFormationService,
    val critereAnalyseCandidatureService: CritereAnalyseCandidatureService,
    val recupererExplicationsFormationService: RecupererExplicationsFormationService,
    val statistiquesDesAdmisPourFormationsService: StatistiquesDesAdmisPourFormationsService,
    val metiersTriesParProfilBuilder: MetiersTriesParProfilBuilder,
    val calculDuTauxDAffiniteBuilder: CalculDuTauxDAffiniteBuilder,
) {
    @Throws(MonProjetIllegalStateErrorException::class, MonProjetSupNotFoundException::class)
    fun recupererFormation(
        profilEleve: ProfilEleve?,
        idFormation: String,
    ): FicheFormation {
        val formation = formationRepository.recupererUneFormationAvecSesMetiers(idFormation)
        val criteresAnalyseCandidature = critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formation)
        val statistiquesDesAdmis =
            statistiquesDesAdmisPourFormationsService.recupererStatistiquesAdmisDUneFormation(
                idBaccalaureat = profilEleve?.bac,
                idFormation = formation.id,
                classe = profilEleve?.classe,
            )
        return if (profilEleve != null) {
            val affinitesFormationEtMetier = suggestionHttpClient.recupererLesSuggestions(profilEleve)
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
                communesTrieesParAffinites =
                    recupererCommunesDUneFormationService.recupererNomCommunesTriesParAffinites(
                        idFormation = formation.id,
                        communesFavorites = profilEleve.communesPreferees,
                    ),
                criteresAnalyseCandidature = criteresAnalyseCandidature,
                explications = recupererExplicationsFormationService.recupererExplications(profilEleve, formation.id),
                statistiquesDesAdmis = statistiquesDesAdmis,
            )
        } else {
            FicheFormation.FicheFormationSansProfil(
                id = formation.id,
                nom = formation.nom,
                descriptifGeneral = formation.descriptifGeneral,
                descriptifAttendus = formation.descriptifAttendus,
                descriptifDiplome = formation.descriptifDiplome,
                descriptifConseils = formation.descriptifConseils,
                formationsAssociees = formation.formationsAssociees,
                liens = formation.liens,
                communes = recupererCommunesDUneFormationService.recupererNomCommunes(formation.id),
                metiers = formation.metiers,
                criteresAnalyseCandidature = criteresAnalyseCandidature,
                statistiquesDesAdmis = statistiquesDesAdmis,
            )
        }
    }
}
