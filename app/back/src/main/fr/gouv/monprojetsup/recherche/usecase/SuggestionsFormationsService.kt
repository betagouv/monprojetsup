package fr.gouv.monprojetsup.recherche.usecase

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.recherche.domain.entity.FormationPourProfil
import fr.gouv.monprojetsup.recherche.domain.entity.Metier
import fr.gouv.monprojetsup.recherche.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.recherche.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.recherche.domain.port.FormationRepository
import fr.gouv.monprojetsup.recherche.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.recherche.domain.port.TripletAffectationRepository
import org.springframework.stereotype.Service
import kotlin.jvm.Throws
import kotlin.math.min

@Service
class SuggestionsFormationsService(
    val suggestionHttpClient: SuggestionHttpClient,
    val formationRepository: FormationRepository,
    val tripletAffectationRepository: TripletAffectationRepository,
) {
    @Throws(MonProjetSupInternalErrorException::class)
    fun suggererFormations(
        profilEleve: ProfilEleve,
        deLIndex: Int,
        aLIndex: Int,
    ): List<FormationPourProfil> {
        val affinitesFormationEtMetier = suggestionHttpClient.recupererLesAffinitees(profilEleve)
        val idsDesPremieresFormationsTriesParAffinites =
            affinitesFormationEtMetier.formations.sortedByDescending {
                it.tauxAffinite
            }.subList(deLIndex, min(aLIndex, affinitesFormationEtMetier.formations.size)).map { it.idFormation }
        val formationsEtLeursMetiers =
            formationRepository.recupererLesFormationsAvecLeursMetiers(
                idsDesPremieresFormationsTriesParAffinites,
            )
        val tripletsAffectation =
            tripletAffectationRepository.recupererLesTripletsAffectationDeFormations(
                idsDesPremieresFormationsTriesParAffinites,
            )
        return idsDesPremieresFormationsTriesParAffinites.mapNotNull { idFormation ->
            formationsEtLeursMetiers.keys.firstOrNull { it.id == idFormation }?.let { formation ->
                val metiers = formationsEtLeursMetiers[formation]!!
                val triplesAffectation = tripletsAffectation[idFormation] ?: emptyList()
                val nomMetiersTriesParAffinites =
                    getNomMetiersTriesParAffinites(
                        metiers = metiers,
                        idsMetierTriesParAffinite = affinitesFormationEtMetier.metiersTriesParAffinites,
                    )
                val nomCommunesTriesParAffinites =
                    getNomCommunesTriesParAffinites(
                        tripletsAffectation = triplesAffectation,
                        communesFavorites = profilEleve.villesPreferees,
                    )
                FormationPourProfil(
                    id = formation.id,
                    nom = formation.nom,
                    tauxAffinite = affinitesFormationEtMetier.formations.first { it.idFormation == idFormation }.tauxAffinite,
                    metiersTriesParAffinites = nomMetiersTriesParAffinites,
                    communesTrieesParAffinites = nomCommunesTriesParAffinites,
                )
            }
        }
    }

    private fun getNomCommunesTriesParAffinites(
        tripletsAffectation: List<TripletAffectation>,
        communesFavorites: List<String>?,
    ): List<String> {
        val communesDesAffectations = tripletsAffectation.map { it.commune }.distinct()
        val communesTrieesParAffinites: MutableList<String> = mutableListOf()
        communesFavorites?.forEach { commune ->
            if (communesDesAffectations.contains(commune)) {
                communesTrieesParAffinites.add(commune)
            }
        }
        communesDesAffectations.forEach { commune ->
            if (!communesTrieesParAffinites.contains(commune)) {
                communesTrieesParAffinites.add(commune)
            }
        }
        return communesTrieesParAffinites
    }

    private fun getNomMetiersTriesParAffinites(
        metiers: List<Metier>,
        idsMetierTriesParAffinite: List<String>,
    ): List<String> {
        val orderById = idsMetierTriesParAffinite.mapIndexed { index, idMetier -> Pair(idMetier, index) }.toMap()
        val metiersTries = metiers.sortedBy { orderById[it.id] }
        return metiersTries.map { it.nom }
    }
}
