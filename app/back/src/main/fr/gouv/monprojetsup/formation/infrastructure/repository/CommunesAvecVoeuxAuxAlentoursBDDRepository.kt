package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.eleve.domain.entity.CommuneFavorite
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecIdsVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.port.CommunesAvecVoeuxAuxAlentoursRepository
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import org.springframework.stereotype.Repository

@Repository
class CommunesAvecVoeuxAuxAlentoursBDDRepository(
    private val communesAvecVoeuxAuxAlentoursJPARepository: CommunesAvecVoeuxAuxAlentoursJPARepository,
    private val logger: MonProjetSupLogger,
) : CommunesAvecVoeuxAuxAlentoursRepository {
    override fun recupererVoeuxAutoursDeCommmune(communeFavorites: List<CommuneFavorite>): List<CommuneAvecIdsVoeuxAuxAlentours> {
        val entites = communesAvecVoeuxAuxAlentoursJPARepository.findAllByCodeInseeIn(communeFavorites.map { it.codeInsee })
        return communeFavorites.map { commune ->
            val distances =
                entites.firstOrNull { communeAvecVoeuxAuxAlentours ->
                    commune.codeInsee == communeAvecVoeuxAuxAlentours.codeInsee
                }?.distancesVoeuKm
            if (distances == null) {
                logger.warn(
                    type = "COMMUNE_NON_PRESENTE_EN_BDD",
                    message = "La commune ${commune.nom} (${commune.codeInsee}) n'est pas présente dans la table ref_join_ville_voeu",
                    parametres = mapOf("nomCommune" to commune.nom, "codeInsee" to commune.codeInsee),
                )
            }
            CommuneAvecIdsVoeuxAuxAlentours(
                communeFavorite = commune,
                distances =
                    distances?.map { distance ->
                        CommuneAvecIdsVoeuxAuxAlentours.VoeuAvecDistance(
                            idVoeu = distance.key,
                            km = distance.value,
                        )
                    } ?: emptyList(),
            )
        }
    }
}
