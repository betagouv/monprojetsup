package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.formation.domain.entity.AffinitesPourProfil.FormationAvecSonAffinite
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.port.CriteresAnalyseCandidatureRepository
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.formation.domain.port.TripletAffectationRepository
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class RecupererFormationService(
    val suggestionHttpClient: SuggestionHttpClient,
    val formationRepository: FormationRepository,
    val tripletAffectationRepository: TripletAffectationRepository,
    val criteresAnalyseCandidatureRepository: CriteresAnalyseCandidatureRepository,
    val recupererExplicationsFormationService: RecupererExplicationsFormationService,
    val statistiquesDesAdmisService: StatistiquesDesAdmisService,
) {
    @Throws(MonProjetIllegalStateErrorException::class, MonProjetSupNotFoundException::class)
    fun recupererFormation(
        profilEleve: ProfilEleve?,
        idFormation: String,
    ): FicheFormation {
        val formation = formationRepository.recupererUneFormationAvecSesMetiers(idFormation)
        val tripletsAffectations = tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(formation.id)
        val criteresAnalyseCandidature =
            criteresAnalyseCandidatureRepository.recupererLesCriteresDUneFormation(
                valeursCriteresAnalyseCandidature = formation.valeurCriteresAnalyseCandidature,
            ).filterNot { it.pourcentage == 0 }
        val statistiquesDesAdmis =
            statistiquesDesAdmisService.recupererStatistiquesAdmisDUneFormation(
                idBaccalaureat = profilEleve?.bac,
                idFormation = formation.id,
                classe = profilEleve?.classe,
            )
        return if (profilEleve != null) {
            val affinitesFormationEtMetier = suggestionHttpClient.recupererLesAffinitees(profilEleve)
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
                    calculDuTauxDAffinite(
                        formationAvecLeurAffinite = affinitesFormationEtMetier.formations,
                        idFormation = formation.id,
                    ),
                metiersTriesParAffinites =
                    TrieParProfilBuilder.trierMetiersParAffinites(
                        metiers = formation.metiers,
                        idsMetierTriesParAffinite = affinitesFormationEtMetier.metiersTriesParAffinites,
                    ),
                communesTrieesParAffinites =
                    TrieParProfilBuilder.getNomCommunesTriesParAffinites(
                        tripletsAffectation = tripletsAffectations,
                        communesFavorites = profilEleve.villesPreferees,
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
                communes = tripletsAffectations.map { it.commune }.distinct(),
                metiers = formation.metiers,
                criteresAnalyseCandidature = criteresAnalyseCandidature,
                statistiquesDesAdmis = statistiquesDesAdmis,
            )
        }
    }

    private fun calculDuTauxDAffinite(
        formationAvecLeurAffinite: List<FormationAvecSonAffinite>,
        idFormation: String,
    ): Int {
        return formationAvecLeurAffinite.firstOrNull { it.idFormation == idFormation }?.tauxAffinite?.let { (it * 100).roundToInt() } ?: 0
    }
}
