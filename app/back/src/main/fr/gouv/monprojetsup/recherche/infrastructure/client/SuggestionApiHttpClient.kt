package fr.gouv.monprojetsup.recherche.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.recherche.domain.entity.AffinitesPourProfil
import fr.gouv.monprojetsup.recherche.domain.entity.FormationAvecSonAffinite
import fr.gouv.monprojetsup.recherche.domain.entity.Profile
import fr.gouv.monprojetsup.recherche.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.recherche.infrastructure.dto.AffinitesProfileReponseDTO
import fr.gouv.monprojetsup.recherche.infrastructure.dto.SuggestionProfileRequeteDTO
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.jvm.Throws

@Component
class SuggestionApiHttpClient(
    private val httpClient: OkHttpClient,
    @Value("\${suggestions.api.url}")
    private val baseUrl: String,
    private val objectMapper: ObjectMapper,
) : SuggestionHttpClient {
    @Throws(MonProjetSupInternalErrorException::class)
    override fun recupererLesAffinitees(profile: Profile): AffinitesPourProfil {
        val url = "$baseUrl/affinites"
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val jsonRequete = objectMapper.writeValueAsString(SuggestionProfileRequeteDTO(profile)).toRequestBody(mediaType)
        val requete = Request.Builder().post(jsonRequete).url(url).build()
        val reponse =
            try {
                httpClient.newCall(requete).execute()
            } catch (e: Exception) {
                throw MonProjetSupInternalErrorException(
                    "ERREUR_API_SUGGESTIONS_CONNEXION",
                    "Erreur lors de la connexion à l'API de suggestions",
                    e,
                )
            }
        val reponseDTO =
            try {
                objectMapper.readValue(reponse.body?.string(), AffinitesProfileReponseDTO::class.java)
            } catch (e: Exception) {
                throw MonProjetSupInternalErrorException(
                    "ERREUR_API_SUGGESTIONS_REPONSE",
                    "Erreur lors de la désérialisation de la réponse de l'API de suggestions",
                    e,
                )
            }
        return AffinitesPourProfil(
            formations =
                reponseDTO.affinites.sortedByDescending { it.affinite }.map {
                    FormationAvecSonAffinite(
                        idFormation = it.key,
                        tauxAffinite = it.affinite,
                    )
                },
            metiersTriesParAffinites = reponseDTO.metiersTriesParAffinites,
        )
    }
}
