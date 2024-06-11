package fr.gouv.monprojetsup.recherche.usecase

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.recherche.domain.entity.Domaine
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationAutoEvaluationMoyenne
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationTypeBaccalaureat
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation
import fr.gouv.monprojetsup.recherche.domain.entity.Interet
import fr.gouv.monprojetsup.recherche.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.recherche.domain.port.BaccalaureatRepository
import fr.gouv.monprojetsup.recherche.domain.port.DomaineRepository
import fr.gouv.monprojetsup.recherche.domain.port.FormationRepository
import fr.gouv.monprojetsup.recherche.domain.port.InteretRepository
import fr.gouv.monprojetsup.recherche.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.recherche.domain.port.TripletAffectationRepository
import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class RecupererFormationService(
    val suggestionHttpClient: SuggestionHttpClient,
    val formationRepository: FormationRepository,
    val tripletAffectationRepository: TripletAffectationRepository,
    val baccalaureatRepository: BaccalaureatRepository,
    val interetRepository: InteretRepository,
    val domaineRepository: DomaineRepository,
) {
    companion object {
        private const val TAILLE_ECHELLON_NOTES = 0.5f
    }

    @Throws(MonProjetIllegalStateErrorException::class, MonProjetSupNotFoundException::class)
    fun recupererFormation(
        profilEleve: ProfilEleve?,
        idFormation: String,
    ): FicheFormation {
        val formation = formationRepository.recupererUneFormationAvecSesMetiers(idFormation)
        val tripletsAffectations =
            tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(formation.id)
        return if (profilEleve != null) {
            val explications = suggestionHttpClient.recupererLesExplications(profilEleve, formation.id)
            val explicationsGeographiquesFiltrees =
                explications.geographique.sortedBy { it.distanceKm }.distinctBy { it.ville }
            val explicationAutoEvaluationMoyenne =
                explications.autoEvaluationMoyenne?.let {
                    val baccalaureat = baccalaureatRepository.recupererUnBaccalaureatParIdExterne(it.bacUtilise)
                    ExplicationAutoEvaluationMoyenne(
                        baccalaureat = baccalaureat ?: Baccalaureat(it.bacUtilise, it.bacUtilise, it.bacUtilise),
                        moyenneAutoEvalue = it.moyenneAutoEvalue,
                        basIntervalleNotes = it.rangs.rangEch25 * TAILLE_ECHELLON_NOTES,
                        hautIntervalleNotes = it.rangs.rangEch75 * TAILLE_ECHELLON_NOTES,
                    )
                }
            val explicationTypeBaccalaureat =
                explications.typeBaccalaureat?.let {
                    val baccalaureat = baccalaureatRepository.recupererUnBaccalaureatParIdExterne(it.nomBaccalaureat)
                    ExplicationTypeBaccalaureat(
                        baccalaureat = baccalaureat ?: Baccalaureat(it.nomBaccalaureat, it.nomBaccalaureat, it.nomBaccalaureat),
                        pourcentage = it.pourcentage,
                    )
                }
            val (domaines: List<Domaine>?, interets: List<Interet>?) =
                explications.interetsEtDomainesChoisis.takeUnless { it.isEmpty() }
                    ?.let {
                        val domaines = domaineRepository.recupererLesDomaines(it)
                        val interets = interetRepository.recupererLesInterets(it)
                        Pair(domaines, interets)
                    } ?: Pair(null, null)
            val formationsSimilaires =
                explications.formationsSimilaires.takeUnless { it.isEmpty() }?.let {
                    formationRepository.recupererLesNomsDesFormations(it)
                }

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
            val tauxAffinite =
                affinitesFormationEtMetier.formations.firstOrNull {
                    it.idFormation == formation.id
                }?.tauxAffinite?.let { (it * 100).roundToInt() } ?: 0
            FicheFormation.FicheFormationPourProfil(
                formation = formation,
                tauxAffinite = tauxAffinite,
                metiersTriesParAffinites = nomMetiersTriesParAffinites,
                communesTrieesParAffinites = nomCommunesTriesParAffinites,
                explications = explications.copy(geographique = explicationsGeographiquesFiltrees),
                domaines = domaines,
                interets = interets,
                explicationAutoEvaluationMoyenne = explicationAutoEvaluationMoyenne,
                formationsSimilaires = formationsSimilaires,
                explicationTypeBaccalaureat = explicationTypeBaccalaureat,
            )
        } else {
            FicheFormation.FicheFormationSansProfil(
                formation = formation,
                communes = tripletsAffectations.map { it.commune }.distinct(),
            )
        }
    }
}
