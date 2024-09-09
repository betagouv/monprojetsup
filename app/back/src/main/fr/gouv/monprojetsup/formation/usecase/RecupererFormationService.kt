package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import org.springframework.stereotype.Service

@Service
class RecupererFormationService(
    val suggestionHttpClient: SuggestionHttpClient,
    val formationRepository: FormationRepository,
    val recupererTripletAffectationDUneFormationService: RecupererTripletAffectationDUneFormationService,
    val critereAnalyseCandidatureService: CritereAnalyseCandidatureService,
    val recupererExplicationsEtExemplesMetiersPourFormationService: RecupererExplicationsEtExemplesMetiersPourFormationService,
    val statistiquesDesAdmisPourFormationsService: StatistiquesDesAdmisPourFormationsService,
    val metiersTriesParProfilBuilder: MetiersTriesParProfilBuilder,
    val calculDuTauxDAffiniteBuilder: CalculDuTauxDAffiniteBuilder,
) {
    @Throws(MonProjetIllegalStateErrorException::class, MonProjetSupNotFoundException::class)
    fun recupererFormation(
        profilEleve: ProfilEleve.Identifie?,
        idFormation: String,
    ): FicheFormation {
        val formation = formationRepository.recupererUneFormationAvecSesMetiers(idFormation)
        val criteresAnalyseCandidature = critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formation)
        val statistiquesDesAdmis =
            statistiquesDesAdmisPourFormationsService.recupererStatistiquesAdmisDUneFormation(
                idBaccalaureat = profilEleve?.baccalaureat,
                idFormation = formation.id,
                classe = profilEleve?.classe,
            )
        return if (profilEleve != null) {
            val affinitesFormationEtMetier = suggestionHttpClient.recupererLesSuggestions(profilEleve)
            val (explications, exemplesDeMetiers) =
                recupererExplicationsEtExemplesMetiersPourFormationService.recupererExplicationsEtExemplesDeMetiers(
                    profilEleve,
                    formation.id,
                )
            val tripletsAffectation =
                recupererTripletAffectationDUneFormationService.recupererTripletAffectationTriesParAffinites(
                    idFormation = formation.id,
                    profilEleve = profilEleve,
                )
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
                        metiers = exemplesDeMetiers,
                        idsMetierTriesParAffinite = affinitesFormationEtMetier.metiersTriesParAffinites,
                    ),
                tripletsAffectation = tripletsAffectation,
                criteresAnalyseCandidature = criteresAnalyseCandidature,
                explications = explications,
                statistiquesDesAdmis = statistiquesDesAdmis,
            )
        } else {
            val tripletsAffectation = recupererTripletAffectationDUneFormationService.recupererTripletsAffectations(formation.id)
            FicheFormation.FicheFormationSansProfil(
                id = formation.id,
                nom = formation.nom,
                descriptifGeneral = formation.descriptifGeneral,
                descriptifAttendus = formation.descriptifAttendus,
                descriptifDiplome = formation.descriptifDiplome,
                descriptifConseils = formation.descriptifConseils,
                formationsAssociees = formation.formationsAssociees,
                liens = formation.liens,
                tripletsAffectation = tripletsAffectation,
                metiers = emptyList(), // Voir avec Hugo
                criteresAnalyseCandidature = criteresAnalyseCandidature,
                statistiquesDesAdmis = statistiquesDesAdmis,
            )
        }
    }
}
