package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.eleve.domain.entity.Commune
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecIdsVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.port.CommunesAvecVoeuxAuxAlentoursRepository
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class CommunesAvecVoeuxAuxAlentoursBDDRepository(
    private val communesAvecVoeuxAuxAlentoursJPARepository: CommunesAvecVoeuxAuxAlentoursJPARepository,
    private val logger: MonProjetSupLogger,
) : CommunesAvecVoeuxAuxAlentoursRepository {
    @Transactional(readOnly = true)
    override fun recupererVoeuxAutoursDeCommmune(communes: List<Commune>): List<CommuneAvecIdsVoeuxAuxAlentours> {
        val entites = communesAvecVoeuxAuxAlentoursJPARepository.findAllByCodeInseeIn(communes.map { it.codeInsee })
        return communes.map { commune ->
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
                commune = commune,
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
