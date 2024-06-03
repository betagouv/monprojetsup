package fr.gouv.monprojetsup.recherche.usecase

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.recherche.domain.entity.CriteresAdmission
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation
import fr.gouv.monprojetsup.recherche.domain.entity.MoyenneGenerale
import fr.gouv.monprojetsup.recherche.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.recherche.domain.port.FormationRepository
import fr.gouv.monprojetsup.recherche.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.recherche.domain.port.TripletAffectationRepository
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class RecupererFormationService(
    val suggestionHttpClient: SuggestionHttpClient,
    val formationRepository: FormationRepository,
    val tripletAffectationRepository: TripletAffectationRepository,
) {
    @Throws(MonProjetIllegalStateErrorException::class, MonProjetSupNotFoundException::class)
    fun recupererFormation(
        profilEleve: ProfilEleve?,
        idFormation: String,
    ): FicheFormation {
        val formation = formationRepository.recupererUneFormationAvecSesMetiers(idFormation)
        val tripletsAffectations = tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation)
        val criteresAdmission =
            CriteresAdmission(
                principauxPoints = formation.pointsAttendus,
                moyenneGenerale =
                    MoyenneGenerale(
                        centille5eme = 0f,
                        centille25eme = 12f,
                        centille75eme = 15f,
                        centille95eme = 19f,
                    ),
                // TODO recuperer la moyenne Générale + les totaux par Bac
            )
        return if (profilEleve != null) {
            val affinitesFormationEtMetier = suggestionHttpClient.recupererLesAffinitees(profilEleve)
            val nomMetiersTriesParAffinites =
                TrieParProfilBuilder.trierMetiersParAffinites(
                    metiers = formation.metiers,
                    idsMetierTriesParAffinite = affinitesFormationEtMetier.metiersTriesParAffinites,
                )
            val nomCommunesTriesParAffinites =
                TrieParProfilBuilder.getNomCommunesTriesParAffinites(
                    tripletsAffectation = tripletsAffectations,
                    communesFavorites = profilEleve.villesPreferees,
                )
            FicheFormation.FicheFormationPourProfil(
                id = formation.id,
                nom = formation.nom,
                formationsAssociees = formation.formationsAssociees,
                descriptifFormation = formation.descriptifGeneral,
                descriptifDiplome = formation.descriptifDiplome,
                descriptifConseils = formation.descriptifConseils,
                descriptifAttendus = formation.descriptifAttendus,
                criteresAdmission = criteresAdmission,
                liens = formation.liens,
                tauxAffinite = affinitesFormationEtMetier.formations.first { it.idFormation == formation.id }.tauxAffinite,
                metiersTriesParAffinites = nomMetiersTriesParAffinites,
                communesTrieesParAffinites = nomCommunesTriesParAffinites,
                explications = null, // TODO appeler le endpoint explications
            )
        } else {
            FicheFormation.FicheFormationSansProfil(
                id = formation.id,
                nom = formation.nom,
                formationsAssociees = formation.formationsAssociees,
                descriptifFormation = formation.descriptifGeneral,
                descriptifDiplome = formation.descriptifDiplome,
                descriptifConseils = formation.descriptifConseils,
                descriptifAttendus = formation.descriptifAttendus,
                criteresAdmission = criteresAdmission,
                liens = formation.liens,
                metiers = formation.metiers,
                communes = tripletsAffectations.map { it.commune }.distinct(),
            )
        }
    }
}
