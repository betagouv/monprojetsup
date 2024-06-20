package fr.gouv.monprojetsup.recherche.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.recherche.domain.entity.AffinitesPourProfil
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion
import fr.gouv.monprojetsup.recherche.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.recherche.domain.port.SpecialitesRepository
import fr.gouv.monprojetsup.recherche.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.recherche.infrastructure.dto.APISuggestionProfilDTO
import fr.gouv.monprojetsup.recherche.infrastructure.dto.APISuggestionReponseDTO
import fr.gouv.monprojetsup.recherche.infrastructure.dto.APISuggestionRequeteDTO
import fr.gouv.monprojetsup.recherche.infrastructure.dto.AffiniteProfilRequeteDTO
import fr.gouv.monprojetsup.recherche.infrastructure.dto.AffinitesProfilReponseDTO
import fr.gouv.monprojetsup.recherche.infrastructure.dto.ExplicationFormationPourUnProfilReponseDTO
import fr.gouv.monprojetsup.recherche.infrastructure.dto.ExplicationFormationPourUnProfilRequeteDTO
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
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
) : SuggestionHttpClient {
    @Throws(MonProjetSupInternalErrorException::class)
    override fun recupererLesAffinitees(profilEleve: ProfilEleve): AffinitesPourProfil {
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
        idFormation: String,
    ): ExplicationsSuggestion {
        val reponseDTO =
            appelerAPISuggestion(
                url = "$baseUrl/explanations",
                requeteDTO =
                    ExplicationFormationPourUnProfilRequeteDTO(
                        profil = creerAPISuggestionProfilDTO(profilEleve),
                        formations = listOf(idFormation),
                    ),
                classDeserialise = ExplicationFormationPourUnProfilReponseDTO::class,
            )
        val explicationsDeLaFormation =
            try {
                reponseDTO.resultats.first { it.cle == idFormation }
            } catch (e: Exception) {
                throw MonProjetIllegalStateErrorException(
                    "ERREUR_API_SUGGESTIONS_EXPLANATION",
                    "La clé de la formation $idFormation n'est pas présente dans la liste retournée par l'API suggestion",
                )
            }
        return explicationsDeLaFormation.toExplicationsSuggestion()
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
