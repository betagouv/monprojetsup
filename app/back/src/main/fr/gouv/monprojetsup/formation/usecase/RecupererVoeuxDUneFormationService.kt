package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.Commune
import fr.gouv.monprojetsup.formation.domain.entity.Voeu
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import org.springframework.stereotype.Service

@Service
class RecupererVoeuxDUneFormationService(
    private val voeuRepository: VoeuRepository,
    private val logger: MonProjetSupLogger,
) {
    fun recupererVoeuxTriesParAffinites(
        idsFormations: List<String>,
        profilEleve: ProfilEleve.AvecProfilExistant,
        obsoletesInclus: Boolean,
    ): Map<String, List<Voeu>> {
        val voeux = voeuRepository.recupererLesVoeuxDeFormations(idsFormations, obsoletesInclus)
        val messagesDeWarning = mutableListOf<String>()
        val voeuxTriesParAffinitesParFormation =
            voeux.entries.associate { entry ->
                entry.key to
                    triesParAffinitesVoeux(
                        voeux = entry.value,
                        profilEleve = profilEleve,
                        messagesDeWarning = messagesDeWarning,
                    )
            }
        messagesDeWarning.distinct().forEach {
            logger.warn(type = "ERREUR_TRI_VOEUX", message = it)
        }
        return voeuxTriesParAffinitesParFormation
    }

    fun recupererVoeuxTriesParAffinites(
        idFormation: String,
        profilEleve: ProfilEleve.AvecProfilExistant,
        obsoletesInclus: Boolean,
    ): List<Voeu> {
        val voeux = voeuRepository.recupererLesVoeuxDUneFormation(idFormation)
        val messagesDeWarning = mutableListOf<String>()
        val voeuxTriesParAffinites =
            triesParAffinitesVoeux(voeux, profilEleve, messagesDeWarning)
        messagesDeWarning.forEach {
            logger.warn(type = "ERREUR_TRI_VOEUX", message = it)
        }
        return voeuxTriesParAffinites
    }

    fun recupererVoeux(
        idFormation: String,
        obsoletesInclus: Boolean,
    ): List<Voeu> {
        return voeuRepository.recupererLesVoeuxDUneFormation(idFormation)
    }

    fun recupererVoeux(
        idsFormations: List<String>,
        obsoletesInclus: Boolean,
    ): Map<String, List<Voeu>> {
        return voeuRepository.recupererLesVoeuxDeFormations(idsFormations, obsoletesInclus)
    }

    private fun triesParAffinitesVoeux(
        voeux: List<Voeu>,
        profilEleve: ProfilEleve.AvecProfilExistant,
        messagesDeWarning: MutableList<String>,
    ): List<Voeu> {
        return profilEleve.communesFavorites?.takeUnless { it.isEmpty() }?.let { communesFavorites ->
            val voeuxDansUneVilleFavorite = mutableListOf<Voeu>()
            val voeuxDansUnDepartementFavoris = mutableListOf<Voeu>()
            val autresVoeux = mutableListOf<Voeu>()
            voeux.forEach { voeu ->
                if (estUneCommuneFavorite(communesFavorites, voeu)) {
                    voeuxDansUneVilleFavorite.add(voeu)
                } else if (estDansUnDepartementFavoris(profilEleve.id, communesFavorites, voeu, messagesDeWarning)) {
                    voeuxDansUnDepartementFavoris.add(voeu)
                } else {
                    autresVoeux.add(voeu)
                }
            }
            voeuxDansUneVilleFavorite + voeuxDansUnDepartementFavoris + autresVoeux
        } ?: voeux
    }

    private fun estDansUnDepartementFavoris(
        idEleve: String,
        communes: List<Commune>,
        voeu: Voeu,
        messagesDeWarning: MutableList<String>,
    ): Boolean {
        val departementDuVoeu =
            recupererDepartement(
                commune = voeu.commune,
                messageException =
                    "La commune du voeu ${voeu.id} a un code commune " +
                        "non standard : ${voeu.commune.codeInsee}",
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
            departementDuVoeu == departementDeLaCommune
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
        voeu: Voeu,
    ) = communes.any { voeu.commune.codeInsee == it.codeInsee }
}
