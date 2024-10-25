package fr.gouv.monprojetsup.formation.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.client.ApiHttpClient
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.formation.infrastructure.dto.APISuggestionProfilDTO
import fr.gouv.monprojetsup.formation.infrastructure.dto.AffiniteProfilRequeteDTO
import fr.gouv.monprojetsup.formation.infrastructure.dto.AffinitesProfilReponseDTO
import fr.gouv.monprojetsup.formation.infrastructure.dto.ExplicationFormationPourUnProfilReponseDTO
import fr.gouv.monprojetsup.formation.infrastructure.dto.ExplicationFormationPourUnProfilRequeteDTO
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SuggestionApiHttpClient(
    @Value("\${suggestions.api.url}")
    override val baseUrl: String,
    override val objectMapper: ObjectMapper,
    override val httpClient: OkHttpClient,
    override val logger: MonProjetSupLogger,
) : ApiHttpClient(baseUrl, objectMapper, httpClient, logger), SuggestionHttpClient {
    @Throws(MonProjetSupInternalErrorException::class)
    override fun recupererLesSuggestions(profilEleve: ProfilEleve.AvecProfilExistant): SuggestionsPourUnProfil {
        val reponseDTO =
            post<AffinitesProfilReponseDTO>(
                url = "$baseUrl/suggestions",
                requeteDTO = AffiniteProfilRequeteDTO(profil = APISuggestionProfilDTO(profilEleve = profilEleve)),
            )
        return reponseDTO.toAffinitesPourProfil()
    }

    @Throws(MonProjetSupInternalErrorException::class, MonProjetIllegalStateErrorException::class)
    override fun recupererLesExplications(
        profilEleve: ProfilEleve.AvecProfilExistant,
        idsFormations: List<String>,
    ): Map<String, ExplicationsSuggestionEtExemplesMetiers?> {
        val reponseDTO =
            post<ExplicationFormationPourUnProfilReponseDTO>(
                url = "$baseUrl/explanations",
                requeteDTO =
                    ExplicationFormationPourUnProfilRequeteDTO(
                        profil = APISuggestionProfilDTO(profilEleve = profilEleve),
                        formations = idsFormations,
                    ),
            ).resultats
        val explications =
            idsFormations.associateWith { idFormation ->
                reponseDTO.firstOrNull { it.cle == idFormation }?.toExplicationsSuggestion()
            }
        val formationsSansExplications = explications.filter { it.value == null }
        if (formationsSansExplications.isNotEmpty()) {
            val idsFormationsSansExplications = formationsSansExplications.map { it.key }
            logger.error(
                type = "FORMATIONS_SANS_EXPLICATIONS",
                message =
                    "Les formations $idsFormationsSansExplications n'ont pas d'explications renvoyées par " +
                        "l'API suggestion pour le profil élève avec l'id ${profilEleve.id}",
                parametres = mapOf("formationsSansExplications" to idsFormationsSansExplications, "idEleve" to profilEleve.id),
            )
        }
        return explications
    }
}
