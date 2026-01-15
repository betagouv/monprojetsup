package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFavori
import fr.gouv.monprojetsup.eleve.domain.port.CompteParcoursupRepository
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.parcoursup.domain.port.ParcoursupApiFavorisClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.collections.joinToString

@Service
class RecupererAssociationFormationsVoeuxService(
    private val compteParcoursupRepository: CompteParcoursupRepository,
    private val parcoursupApiFavorisClient: ParcoursupApiFavorisClient,
    private val voeuRepository: VoeuRepository,
    private val logger: MonProjetSupLogger,
) {
    @Transactional(readOnly = true)
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
            val voeuxInconnus =
                result
                    .takeUnless { it.isEmpty() }
                    ?.map { it.idVoeu }
                    ?.let { voeuRepository.recupererIdsVoeuxInexistants(it.distinct()) }
                    ?.takeUnless { it.isEmpty() }
            if (voeuxInconnus != null) {
                logger.warn(
                    "VOEUX_FAVORIS_API_PSUP_INCONNUS",
                    "Des voeux favoris inconnus ont été envoyés par l'API parcoursup pour le compte $compteParcoursup : ${
                        voeuxInconnus.joinToString(
                            ", ",
                        )
                    }",
                )
                result.removeIf { voeuxInconnus.contains(it.idVoeu) }
            }
        } catch (exception: MonProjetSupInternalErrorException) {
            logger.error(exception.code, exception.message, exception.origine)
        }
        return result.distinct().sortedBy { it.idVoeu }
    }
}
