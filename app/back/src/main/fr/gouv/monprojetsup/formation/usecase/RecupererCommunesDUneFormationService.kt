package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.eleve.domain.entity.Commune
import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.formation.domain.port.TripletAffectationRepository
import org.springframework.stereotype.Service

@Service
class RecupererCommunesDUneFormationService(
    val tripletAffectationRepository: TripletAffectationRepository,
) {
    fun recupererNomCommunesTriesParAffinites(
        idsDesPremieresFormationsTriesParAffinites: List<String>,
        communesFavorites: List<Commune>?,
    ): Map<String, List<String>> {
        val tripletsAffectations: Map<String, List<TripletAffectation>> =
            tripletAffectationRepository.recupererLesTripletsAffectationDeFormations(
                idsDesPremieresFormationsTriesParAffinites,
            )
        return tripletsAffectations.entries.associate {
            it.key to
                recupererNomCommunesTriesParAffinites(
                    tripletsAffectation = it.value,
                    communesFavorites = communesFavorites,
                )
        }
    }

    fun recupererNomCommunesTriesParAffinites(
        idFormation: String,
        communesFavorites: List<Commune>?,
    ): List<String> {
        val tripletsAffectations = tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation)
        return recupererNomCommunesTriesParAffinites(tripletsAffectations, communesFavorites)
    }

    fun recupererNomCommunes(idFormation: String): List<String> {
        val tripletsAffectations = tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation)
        return tripletsAffectations.map { it.commune }.distinct()
    }

    private fun recupererNomCommunesTriesParAffinites(
        tripletsAffectation: List<TripletAffectation>,
        communesFavorites: List<Commune>?,
    ): List<String> {
        val communesDesAffectations = tripletsAffectation.map { it.commune }.distinct()
        val communesTrieesParAffinites: MutableList<String> = mutableListOf()
        communesFavorites?.forEach { commune ->
            if (communesDesAffectations.contains(commune.nom)) {
                communesTrieesParAffinites.add(commune.nom)
            }
        }
        communesDesAffectations.forEach { commune ->
            if (!communesTrieesParAffinites.contains(commune)) {
                communesTrieesParAffinites.add(commune)
            }
        }
        return communesTrieesParAffinites
    }
}
