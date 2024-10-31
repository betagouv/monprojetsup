package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.Commune
import fr.gouv.monprojetsup.formation.domain.entity.Voeu
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.logging.domain.WarningALogguer
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
        val messagesDeWarning = mutableListOf<WarningALogguer>()
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
            logger.warn(type = it.type, message = it.messageException, parametres = it.parametres)
        }
        return voeuxTriesParAffinitesParFormation
    }

    fun recupererVoeuxTriesParAffinites(
        idFormation: String,
        profilEleve: ProfilEleve.AvecProfilExistant,
        obsoletesInclus: Boolean,
    ): List<Voeu> {
        val voeux = voeuRepository.recupererLesVoeuxDUneFormation(idFormation)
        val messagesDeWarning = mutableListOf<WarningALogguer>()
        val voeuxTriesParAffinites =
            triesParAffinitesVoeux(voeux, profilEleve, messagesDeWarning)
        messagesDeWarning.forEach {
            logger.warn(type = it.type, message = it.messageException, parametres = it.parametres)
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
        messagesDeWarning: MutableList<WarningALogguer>,
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
        messagesDeWarning: MutableList<WarningALogguer>,
    ): Boolean {
        val departementDuVoeu =
            recupererDepartement(
                commune = voeu.commune,
                messagesDeWarning = messagesDeWarning,
                warning =
                    WarningALogguer(
                        messageException =
                            "La commune du voeu ${voeu.id} a un code commune " +
                                "non standard : ${voeu.commune.codeInsee}",
                        parametres = mapOf("idVoeu" to voeu.id, "codeInsee" to voeu.commune.codeInsee),
                        type = "CODE_INSEE_NON_RECONNU_SUR_VOEU",
                    ),
            )
        return communes.any { commune: Commune ->
            val departementDeLaCommune =
                recupererDepartement(
                    commune = commune,
                    messagesDeWarning = messagesDeWarning,
                    warning =
                        WarningALogguer(
                            messageException =
                                "La commune ${commune.nom} présente dans le profil de l'élève $idEleve a un code commune " +
                                    "non standard : ${commune.codeInsee}",
                            parametres = mapOf("nomCommune" to commune.nom, "codeInsee" to commune.codeInsee),
                            type = "CODE_INSEE_NON_RECONNU_DANS_PROFIL",
                        ),
                )
            departementDuVoeu == departementDeLaCommune
        }
    }

    private fun recupererDepartement(
        commune: Commune,
        warning: WarningALogguer,
        messagesDeWarning: MutableList<WarningALogguer>,
    ) = try {
        val codeCommune = commune.codeInsee.substring(0, 2)
        when {
            codeCommune == "2A" || codeCommune == "2B" -> 20
            codeCommune.toInt() <= 96 -> commune.codeInsee.substring(0, 2).toInt()
            codeCommune.toInt() > 96 -> commune.codeInsee.substring(0, 3).toInt()
            else -> {
                messagesDeWarning.add(warning)
                0
            }
        }
    } catch (e: Exception) {
        messagesDeWarning.add(warning)
        0
    }

    private fun estUneCommuneFavorite(
        communes: List<Commune>,
        voeu: Voeu,
    ) = communes.any { voeu.commune.codeInsee == it.codeInsee }
}
