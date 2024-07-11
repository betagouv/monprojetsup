package fr.gouv.monprojetsup.formation.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers
import fr.gouv.monprojetsup.formation.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.port.SpecialitesRepository
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.formation.infrastructure.dto.APISuggestionProfilDTO
import fr.gouv.monprojetsup.formation.infrastructure.dto.APISuggestionReponseDTO
import fr.gouv.monprojetsup.formation.infrastructure.dto.APISuggestionRequeteDTO
import fr.gouv.monprojetsup.formation.infrastructure.dto.AffiniteProfilRequeteDTO
import fr.gouv.monprojetsup.formation.infrastructure.dto.AffinitesProfilReponseDTO
import fr.gouv.monprojetsup.formation.infrastructure.dto.ExplicationFormationPourUnProfilReponseDTO
import fr.gouv.monprojetsup.formation.infrastructure.dto.ExplicationFormationPourUnProfilRequeteDTO
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class SuggestionApiHttpClient(
    @Value("\${suggestions.api.url}")
    private val baseUrl: String,
    private val objectMapper: ObjectMapper,
    private val httpClient: OkHttpClient,
    private val specialitesRepository: SpecialitesRepository,
    private val logger: Logger,
) : SuggestionHttpClient {
    @Throws(MonProjetSupInternalErrorException::class)
    override fun recupererLesSuggestions(profilEleve: ProfilEleve): SuggestionsPourUnProfil {
        val reponseDTO =
            appelerAPISuggestion(
                url = "$baseUrl/suggestions",
                requeteDTO = AffiniteProfilRequeteDTO(profil = creerAPISuggestionProfilDTO(profilEleve)),
                classDeserialise = AffinitesProfilReponseDTO::class,
            )
        return reponseDTO.toAffinitesPourProfil()
    }

    @Throws(MonProjetSupInternalErrorException::class, MonProjetIllegalStateErrorException::class)
    override fun recupererLesExplications(
        profilEleve: ProfilEleve,
        idsFormations: List<String>,
    ): Map<String, ExplicationsSuggestionEtExemplesMetiers?> {
        val reponseDTO =
            appelerAPISuggestion(
                url = "$baseUrl/explanations",
                requeteDTO =
                    ExplicationFormationPourUnProfilRequeteDTO(
                        profil = creerAPISuggestionProfilDTO(profilEleve),
                        formations = idsFormations,
                    ),
                classDeserialise = ExplicationFormationPourUnProfilReponseDTO::class,
            ).resultats
        val explications =
            idsFormations.associateWith {
                    idFormation ->
                reponseDTO.firstOrNull { it.cle == idFormation }?.toExplicationsSuggestion()
            }
        val formationsSansExplications = explications.filter { it.value == null }
        if (formationsSansExplications.isNotEmpty()) {
            logger.error(
                "Les formations ${formationsSansExplications.map { it.key }} n'ont pas d'explications renvoyées " +
                    "pour le profil élève suivant $profilEleve",
            )
        }
        return explications
    }

    private fun <T : APISuggestionReponseDTO> appelerAPISuggestion(
        url: String,
        requeteDTO: APISuggestionRequeteDTO,
        classDeserialise: KClass<T>,
    ): T {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val jsonRequete = objectMapper.writeValueAsString(requeteDTO).toRequestBody(mediaType)
        val requete = Request.Builder().post(jsonRequete).url(url).build()
        val reponse =
            try {
                httpClient.newCall(requete).execute()
            } catch (e: Exception) {
                throw MonProjetSupInternalErrorException(
                    "ERREUR_API_SUGGESTIONS_CONNEXION",
                    "Erreur lors de la connexion à l'API de suggestions à l'url $url",
                    e,
                )
            }
        val reponseDTO =
            try {
                objectMapper.readValue(reponse.body?.string(), classDeserialise.java)
            } catch (e: Exception) {
                throw MonProjetSupInternalErrorException(
                    "ERREUR_API_SUGGESTIONS_REPONSE",
                    "Erreur lors de la désérialisation de la réponse de l'API de suggestions pour la " +
                        "classe ${classDeserialise.qualifiedName}",
                    e,
                )
            }
        return reponseDTO
    }

    private fun creerAPISuggestionProfilDTO(profilEleve: ProfilEleve): APISuggestionProfilDTO {
        val specialites =
            profilEleve.specialites?.let {
                specialitesRepository.recupererLesSpecialites(it).map { specialite -> specialite.label }
            }
        val apiSuggestionProfilDTO = APISuggestionProfilDTO(profilEleve, specialites)
        return apiSuggestionProfilDTO
    }
}
