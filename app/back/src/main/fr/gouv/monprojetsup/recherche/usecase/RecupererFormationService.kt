package fr.gouv.monprojetsup.recherche.usecase

import fr.gouv.monprojetsup.commun.Constantes.TAILLE_ECHELLON_NOTES
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.recherche.domain.entity.AffinitesPourProfil.FormationAvecSonAffinite
import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.recherche.domain.entity.Domaine
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion.ExplicationGeographique
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion.TypeBaccalaureat
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationAutoEvaluationMoyenne
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationTypeBaccalaureat
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation.FicheFormationPourProfil.MoyenneGeneraleDesAdmis
import fr.gouv.monprojetsup.recherche.domain.entity.InteretSousCategorie
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
    val moyenneGeneraleDesAdmisService: MoyenneGeneraleDesAdmisService,
) {
    @Throws(MonProjetIllegalStateErrorException::class, MonProjetSupNotFoundException::class)
    fun recupererFormation(
        profilEleve: ProfilEleve?,
        idFormation: String,
    ): FicheFormation {
        val formation = formationRepository.recupererUneFormationAvecSesMetiers(idFormation)
        val tripletsAffectations = tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(formation.id)
        return if (profilEleve != null) {
            val moyenneGeneraleDesAdmis = recupererMoyenneGeneraleDesAdmis(idBaccalaureat = profilEleve.bac, idFormation = formation.id)
            val explications = suggestionHttpClient.recupererLesExplications(profilEleve, formation.id)
            val (domaines: List<Domaine>?, interets: List<InteretSousCategorie>?) =
                explications.interetsEtDomainesChoisis.takeUnless {
                    it.isEmpty()
                }?.let {
                    val domaines = domaineRepository.recupererLesDomaines(it)
                    val interets = interetRepository.recupererLesSousCategoriesDInterets(it)
                    Pair(domaines, interets)
                } ?: Pair(null, null)
            val formationsSimilaires =
                explications.formationsSimilaires.takeUnless { it.isEmpty() }?.let {
                    formationRepository.recupererLesNomsDesFormations(it)
                }
            val affinitesFormationEtMetier = suggestionHttpClient.recupererLesAffinitees(profilEleve)
            FicheFormation.FicheFormationPourProfil(
                formation = formation,
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
                explications = explications.copy(geographique = filtrerEtTrier(explications.geographique)),
                domaines = domaines,
                interets = interets,
                explicationAutoEvaluationMoyenne = recupererExplicationAutoEvaluationMoyenne(explications),
                formationsSimilaires = formationsSimilaires,
                explicationTypeBaccalaureat = recupererExplicationTypeBaccalaureat(explications.typeBaccalaureat),
                moyenneGeneraleDesAdmis = moyenneGeneraleDesAdmis,
            )
        } else {
            FicheFormation.FicheFormationSansProfil(
                formation = formation,
                communes = tripletsAffectations.map { it.commune }.distinct(),
            )
        }
    }

    private fun recupererMoyenneGeneraleDesAdmis(
        idBaccalaureat: String?,
        idFormation: String,
    ): MoyenneGeneraleDesAdmis? {
        val baccalaureat = idBaccalaureat?.let { baccalaureatRepository.recupererUnBaccalaureat(it) }
        return moyenneGeneraleDesAdmisService.recupererMoyenneGeneraleDesAdmisDUneFormation(baccalaureat, idFormation)
    }

    private fun filtrerEtTrier(explicationsGeographique: List<ExplicationGeographique>): List<ExplicationGeographique> {
        val explicationsGeographiquesFiltrees = explicationsGeographique.sortedBy { it.distanceKm }.distinctBy { it.ville }
        return explicationsGeographiquesFiltrees
    }

    private fun recupererExplicationAutoEvaluationMoyenne(explications: ExplicationsSuggestion): ExplicationAutoEvaluationMoyenne? {
        return explications.autoEvaluationMoyenne?.let {
            val baccalaureat = baccalaureatRepository.recupererUnBaccalaureatParIdExterne(it.bacUtilise)
            ExplicationAutoEvaluationMoyenne(
                baccalaureat = baccalaureat ?: Baccalaureat(it.bacUtilise, it.bacUtilise, it.bacUtilise),
                moyenneAutoEvalue = it.moyenneAutoEvalue,
                basIntervalleNotes = it.rangs.rangEch25 * TAILLE_ECHELLON_NOTES,
                hautIntervalleNotes = it.rangs.rangEch75 * TAILLE_ECHELLON_NOTES,
            )
        }
    }

    private fun recupererExplicationTypeBaccalaureat(typeBaccalaureat: TypeBaccalaureat?): ExplicationTypeBaccalaureat? {
        return typeBaccalaureat?.let {
            val baccalaureat = baccalaureatRepository.recupererUnBaccalaureatParIdExterne(it.nomBaccalaureat)
            ExplicationTypeBaccalaureat(
                baccalaureat =
                    baccalaureat ?: Baccalaureat(
                        it.nomBaccalaureat,
                        it.nomBaccalaureat,
                        it.nomBaccalaureat,
                    ),
                pourcentage = it.pourcentage,
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
