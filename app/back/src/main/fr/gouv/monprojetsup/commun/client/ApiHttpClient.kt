package fr.gouv.monprojetsup.commun.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

abstract class ApiHttpClient(
    open val baseUrl: String,
    protected open val objectMapper: ObjectMapper,
    protected open val httpClient: OkHttpClient,
    protected open val logger: MonProjetSupLogger,
) {
    @Throws(MonProjetSupInternalErrorException::class)
    protected inline fun <reified T> get(
        url: String,
        token: String? = null,
    ): T {
        val requete = creerLaRequeteGet(url, token)
        val reponse = appelAPI(requete, url)
        verifierCodeErreur(reponse, url)
        val reponseDTO = deserialisation<T>(reponse.body?.string(), url)
        return reponseDTO
    }

    @Throws(MonProjetSupInternalErrorException::class)
    protected inline fun <reified T> post(
        url: String,
        requeteDTO: Any,
        token: String? = null,
    ): T {
        val requete = creerLaRequetePost(requeteDTO, url, token)
        val reponse = appelAPI(requete, url)
        verifierCodeErreur(reponse, url)
        val reponseDTO = deserialisation<T>(reponse.body?.string(), url)
        return reponseDTO
    }

    protected fun creerLaRequetePost(
        requeteDTO: Any,
        url: String,
        token: String?,
    ): Request.Builder {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val jsonRequete = objectMapper.writeValueAsString(requeteDTO).toRequestBody(mediaType)
        val requete = Request.Builder().post(jsonRequete).url(url)
        token?.let { requete.header("Authorization", "Bearer $token") }
        return requete
    }

    protected fun creerLaRequeteGet(
        url: String,
        token: String?,
    ): Request.Builder {
        val requete = Request.Builder().get().url(url)
        token?.let { requete.header("Authorization", "Bearer $token") }
        return requete
    }

    @Throws(MonProjetSupInternalErrorException::class)
    protected fun verifierCodeErreur(
        reponse: Response,
        url: String,
    ) {
        if (!reponse.isSuccessful) {
            throw MonProjetSupInternalErrorException(
                "ERREUR_APPEL_API",
                "Erreur lors de la connexion à l'API à l'url $url, " +
                    "un code ${reponse.code} a été retourné avec le body ${reponse.body?.string()}",
            )
        }
    }

    @Throws(MonProjetSupInternalErrorException::class)
    protected fun appelAPI(
        requete: Request.Builder,
        url: String,
    ): Response {
        val reponse =
            try {
                httpClient.newCall(requete.build()).execute()
            } catch (e: Exception) {
                throw MonProjetSupInternalErrorException(
                    "ERREUR_APPEL_API",
                    "Erreur lors de la connexion à l'API à l'url $url",
                    e,
                )
            }
        return reponse
    }

    @Throws(MonProjetSupInternalErrorException::class)
    protected inline fun <reified T> deserialisation(
        body: String?,
        url: String,
    ): T {
        val reponseDTO =
            try {
                body?.let {
                    objectMapper.readValue<T>(body)
                } ?: throw IllegalStateException()
            } catch (e: Exception) {
                throw MonProjetSupInternalErrorException(
                    "ERREUR_API_SUGGESTIONS_REPONSE",
                    "Erreur lors de la désérialisation de la réponse de l'API à l'url $url pour le body suivant : $body",
                    e,
                )
            }
        return reponseDTO
    }
}
