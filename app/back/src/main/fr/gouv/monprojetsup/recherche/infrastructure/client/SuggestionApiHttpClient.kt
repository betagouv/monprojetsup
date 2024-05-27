package fr.gouv.monprojetsup.recherche.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.recherche.domain.entity.AffinitesPourProfil
import fr.gouv.monprojetsup.recherche.domain.entity.FormationAvecSonAffinite
import fr.gouv.monprojetsup.recherche.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.recherche.domain.port.SpecialitesRepository
import fr.gouv.monprojetsup.recherche.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.recherche.infrastructure.dto.AffinitesProfilReponseDTO
import fr.gouv.monprojetsup.recherche.infrastructure.dto.ProfilDTO
import fr.gouv.monprojetsup.recherche.infrastructure.dto.SuggestionProfilRequeteDTO
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import kotlin.jvm.Throws

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
        val url = "$baseUrl/affinites"
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val specialites = profilEleve.specialites?.let { specialitesRepository.recupererLesSpecialites(it).map { it.label } }
        val jsonRequete =
            objectMapper.writeValueAsString(SuggestionProfilRequeteDTO(ProfilDTO(profilEleve, specialites)))
                .toRequestBody(mediaType)
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
                objectMapper.readValue(reponse.body?.string(), AffinitesProfilReponseDTO::class.java)
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

    override fun recupererLesExplications(
        profilEleve: ProfilEleve,
        idFormation: String,
    ): AffinitesPourProfil {
        TODO("Not yet implemented")
    }
}
