package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFavori
import fr.gouv.monprojetsup.eleve.domain.port.CompteParcoursupRepository
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.parcoursup.domain.port.ParcoursupApiFavorisClient
import org.springframework.stereotype.Service

@Service
class RecupererAssociationFormationsVoeuxService(
    private val compteParcoursupRepository: CompteParcoursupRepository,
    private val parcoursupApiFavorisClient: ParcoursupApiFavorisClient,
    private val logger: MonProjetSupLogger,
) {
    fun recupererVoeuxFavoris(profil: ProfilEleve.AvecProfilExistant): List<VoeuFavori> {
        val result = HashSet(profil.voeuxFavoris)
        try {
            val compteParcoursup = compteParcoursupRepository.recupererIdCompteParcoursup(profil.id)
            if (compteParcoursup != null) {
                val favorisParcoursup =
                    parcoursupApiFavorisClient.recupererLesVoeuxSelectionnesSurParcoursup(compteParcoursup).map { it.idVoeu }.toSet()
                result.removeIf { it.estFavoriParcoursup || favorisParcoursup.contains(it.idVoeu) }
                result.addAll(favorisParcoursup.map { VoeuFavori(it, true) })
            }
        } catch (exception: MonProjetSupInternalErrorException) {
            logger.error(exception.code, exception.message, exception.origine)
        }
        return result.distinct().sortedBy { it.idVoeu }
    }
}
