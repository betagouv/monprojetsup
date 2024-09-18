package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.Commune
import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.formation.domain.port.TripletAffectationRepository
import org.slf4j.Logger
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RecupererTripletAffectationDUneFormationService(
    private val tripletAffectationRepository: TripletAffectationRepository,
    private val logger: Logger,
) {
    fun recupererTripletAffectationTriesParAffinites(
        idsFormations: List<String>,
        profilEleve: ProfilEleve.Identifie,
    ): Map<String, List<TripletAffectation>> {
        val tripletsAffectations = tripletAffectationRepository.recupererLesTripletsAffectationDeFormations(idsFormations)
        val messagesDeWarning = mutableListOf<String>()
        val tripletsAffectationsTriesParAffinitesParFormation =
            tripletsAffectations.entries.associate { entry ->
                entry.key to
                    triesParAffinitesTripletsAffectations(
                        tripletsAffectation = entry.value,
                        profilEleve = profilEleve,
                        messagesDeWarning = messagesDeWarning,
                    )
            }
        messagesDeWarning.distinct().forEach {
            logger.warn(it)
        }
        return tripletsAffectationsTriesParAffinitesParFormation
    }

    fun recupererTripletAffectationTriesParAffinites(
        idFormation: String,
        profilEleve: ProfilEleve.Identifie,
    ): List<TripletAffectation> {
        val tripletsAffectations = tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation)
        val messagesDeWarning = mutableListOf<String>()
        val tripletsAffectationsTriesParAffinites =
            triesParAffinitesTripletsAffectations(tripletsAffectations, profilEleve, messagesDeWarning)
        messagesDeWarning.forEach {
            logger.warn(it)
        }
        return tripletsAffectationsTriesParAffinites
    }

    fun recupererTripletsAffectations(idFormation: String): List<TripletAffectation> {
        return tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation)
    }

    private fun triesParAffinitesTripletsAffectations(
        tripletsAffectation: List<TripletAffectation>,
        profilEleve: ProfilEleve.Identifie,
        messagesDeWarning: MutableList<String>,
    ): List<TripletAffectation> {
        return profilEleve.communesFavorites?.takeUnless { it.isEmpty() }?.let { communesFavorites ->
            val tripletsAffectationsDansUneVilleFavorite = mutableListOf<TripletAffectation>()
            val tripletsAffectationsDansUnDepartementFavoris = mutableListOf<TripletAffectation>()
            val autresTripletsAffectations = mutableListOf<TripletAffectation>()
            tripletsAffectation.forEach { tripletAffectation ->
                if (estUneCommuneFavorite(communesFavorites, tripletAffectation)) {
                    tripletsAffectationsDansUneVilleFavorite.add(tripletAffectation)
                } else if (estDansUnDepartementFavoris(profilEleve.id, communesFavorites, tripletAffectation, messagesDeWarning)) {
                    tripletsAffectationsDansUnDepartementFavoris.add(tripletAffectation)
                } else {
                    autresTripletsAffectations.add(tripletAffectation)
                }
            }
            tripletsAffectationsDansUneVilleFavorite + tripletsAffectationsDansUnDepartementFavoris + autresTripletsAffectations
        } ?: tripletsAffectation
    }

    private fun estDansUnDepartementFavoris(
        idEleve: UUID,
        communes: List<Commune>,
        tripletAffectation: TripletAffectation,
        messagesDeWarning: MutableList<String>,
    ): Boolean {
        val departementDuTripletAffectation =
            recupererDepartement(
                commune = tripletAffectation.commune,
                messageException =
                    "La commune du triplet d'affectation ${tripletAffectation.id} a un code commune " +
                        "non standard : ${tripletAffectation.commune.codeInsee}",
                messagesDeWarning,
            )
        return communes.any { commune: Commune ->
            val departementDeLaCommune =
                recupererDepartement(
                    commune = commune,
                    messageException =
                        "La commune ${commune.nom} présente dans le profil de l'élève $idEleve a un code commune " +
                            "non standard : ${commune.codeInsee}",
                    messagesDeWarning = messagesDeWarning,
                )
            departementDuTripletAffectation == departementDeLaCommune
        }
    }

    private fun recupererDepartement(
        commune: Commune,
        messageException: String,
        messagesDeWarning: MutableList<String>,
    ) = try {
        commune.codeInsee.substring(0, 2).toInt()
    } catch (e: Exception) {
        messagesDeWarning.add(messageException)
        0
    }

    private fun estUneCommuneFavorite(
        communes: List<Commune>,
        tripletAffectation: TripletAffectation,
    ) = communes.any { tripletAffectation.commune.codeInsee == it.codeInsee }
}
