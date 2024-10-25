package fr.gouv.monprojetsup.parcoursup.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.commun.helper.MockitoHelper
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.parcoursup.domain.entity.FavorisParcoursup
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus

class ParcoursupApiHttpClientTest {
    @Mock
    private lateinit var httpClient: OkHttpClient

    @Mock
    private lateinit var logger: MonProjetSupLogger

    private val objectMapper = ObjectMapper()

    private lateinit var parcoursupApiHttpClient: ParcoursupApiHttpClient

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        parcoursupApiHttpClient =
            ParcoursupApiHttpClient(
                clientId = "clientId",
                clientSecret = "clientSecret",
                urlToken = "https://monauthentification.fr/Authentification/oauth2/token",
                baseUrl = "https://parcoursup.fr",
                objectMapper = objectMapper,
                httpClient = httpClient,
                logger = logger,
            )
    }

    private fun mockCall(
        url: String,
        stringBody: String?,
        status: HttpStatus,
    ): Call? {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val reponseBody = stringBody?.toResponseBody(mediaType)
        val callAuthentMock = mock(Call::class.java)
        val reponse =
            Response.Builder().code(status.value()).message(status.reasonPhrase).protocol(Protocol.HTTP_1_1)
                .request(Request.Builder().url(url).build())
                .body(reponseBody).build()
        given(callAuthentMock.execute()).willReturn(reponse)
        return callAuthentMock
    }

    @Test
    fun `quand la récupération de l'access token échoue, alors doit throw MonProjetSupInternalErrorException`() {
        // Given
        val callAuthentMock =
            mockCall(
                url = "https://monauthentification.fr/Authentification/oauth2/token",
                stringBody = null,
                status = HttpStatus.INTERNAL_SERVER_ERROR,
            )

        given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callAuthentMock)

        // When & Then
        assertThatThrownBy {
            parcoursupApiHttpClient.recupererLesVoeuxSelectionnesSurParcoursup(idParcoursup = 753)
        }.isInstanceOf(MonProjetSupInternalErrorException::class.java)
            .hasMessage(
                "Erreur lors de la connexion à l'API à l'url https://monauthentification.fr/Authentification/oauth2/token, " +
                    "un code 500 a été retourné avec le body null",
            )
    }

    @Test
    fun `quand la récupération du token ne contient pas access_token, alors doit throw MonProjetSupInternalErrorException`() {
        // Given
        val stringBody =
            """
            {
              "token": "abc"
            }
            """.trimIndent()
        val callAuthentMock =
            mockCall(
                url = "https://monauthentification.fr/Authentification/oauth2/token",
                stringBody = stringBody,
                status = HttpStatus.OK,
            )

        given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callAuthentMock)

        // When & Then
        assertThatThrownBy {
            parcoursupApiHttpClient.recupererLesVoeuxSelectionnesSurParcoursup(idParcoursup = 753)
        }.isInstanceOf(MonProjetSupInternalErrorException::class.java)
            .hasMessage(
                "Erreur lors de la désérialisation de la réponse de l'API à l'url " +
                    "https://monauthentification.fr/Authentification/oauth2/token pour le body suivant : $stringBody",
            )
    }

    @Test
    fun `quand l'appel à l'API échoue, alors doit throw MonProjetSupInternalErrorException`() {
        // Given
        val stringBodyAuthent =
            """
            {
              "access_token": "eyJjkezhjfgkyfbzhjzg"
            }
            """.trimIndent()
        val callAuthentMock =
            mockCall(
                url = "https://monauthentification.fr/Authentification/oauth2/token",
                stringBody = stringBodyAuthent,
                status = HttpStatus.OK,
            )
        val callApiMock =
            mockCall(
                url = "https://monauthentification.fr/ApiFavoris/favoris/753",
                stringBody = null,
                status = HttpStatus.INTERNAL_SERVER_ERROR,
            )

        given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callAuthentMock, callApiMock)

        // When & Then
        assertThatThrownBy {
            parcoursupApiHttpClient.recupererLesVoeuxSelectionnesSurParcoursup(idParcoursup = 753)
        }.isInstanceOf(MonProjetSupInternalErrorException::class.java)
            .hasMessage(
                "Erreur lors de la connexion à l'API à l'url " +
                    "https://parcoursup.fr/ApiFavoris/favoris/753, un code 500 a été retourné avec le body null",
            )
    }

    @Test
    fun `quand l'appel à l'API n'est pas correctement déserialisé', alors doit throw MonProjetSupInternalErrorException`() {
        // Given
        val stringBodyAuthent =
            """
            {
              "access_token": "eyJjkezhjfgkyfbzhjzg"
            }
            """.trimIndent()
        val callAuthentMock =
            mockCall(
                url = "https://monauthentification.fr/Authentification/oauth2/token",
                stringBody = stringBodyAuthent,
                status = HttpStatus.OK,
            )

        val stringBodyApi =
            """
            [
              {
                "objet": "objet",
                "valeur": 1
              },
              {
                "objet": "objet",
                "valeur": 1
              }
            ]
            """.trimIndent()
        val callApiMock =
            mockCall(
                url = "https://monauthentification.fr/ApiFavoris/favoris/753",
                stringBody = stringBodyApi,
                status = HttpStatus.OK,
            )

        given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callAuthentMock, callApiMock)

        // When & Then
        assertThatThrownBy {
            parcoursupApiHttpClient.recupererLesVoeuxSelectionnesSurParcoursup(idParcoursup = 753)
        }.isInstanceOf(MonProjetSupInternalErrorException::class.java)
            .hasMessage(
                "Erreur lors de la désérialisation de la réponse de l'API à l'url " +
                    "https://parcoursup.fr/ApiFavoris/favoris/753 pour le body suivant : $stringBodyApi",
            )
    }

    @Test
    fun `quand parcoursup renvoie une liste de favoris vide, alors doit renvoyer vide`() {
        // Given
        val stringBodyAuthent =
            """
            {
              "access_token": "eyJjkezhjfgkyfbzhjzg"
            }
            """.trimIndent()
        val callAuthentMock =
            mockCall(
                url = "https://monauthentification.fr/Authentification/oauth2/token",
                stringBody = stringBodyAuthent,
                status = HttpStatus.OK,
            )

        val stringBodyApi =
            """
            [
            
            ]
            """.trimIndent()
        val callApiMock =
            mockCall(
                url = "https://monauthentification.fr/ApiFavoris/favoris/753",
                stringBody = stringBodyApi,
                status = HttpStatus.OK,
            )

        given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callAuthentMock, callApiMock)

        // When
        val resultat = parcoursupApiHttpClient.recupererLesVoeuxSelectionnesSurParcoursup(idParcoursup = 753)

        // Then
        assertThat(resultat).isEqualTo(emptyList<FavorisParcoursup>())
    }

    @Test
    fun `quand parcoursup renvoie une liste de favoris, alors doit renvoyer la liste de voeux associée`() {
        // Given
        val stringBodyAuthent =
            """
            {
              "access_token": "eyJjkezhjfgkyfbzhjzg"
            }
            """.trimIndent()
        val callAuthentMock =
            mockCall(
                url = "https://monauthentification.fr/Authentification/oauth2/token",
                stringBody = stringBodyAuthent,
                status = HttpStatus.OK,
            )

        val stringBodyApi =
            """
            [
              {
                "id_compte": 753,
                "id_formation": 123,
                "commentaire": "",
                "notation": 4
              },
              {
                "id_compte": 753,
                "id_formation": 9583,
                "commentaire": null,
                "notation": 4
              },
              {
                "id_compte": 753,
                "id_formation": 5729,
                "commentaire": "Mon commentaire",
                "notation": 4
              }
            ]
            """.trimIndent()
        val callApiMock =
            mockCall(
                url = "https://monauthentification.fr/ApiFavoris/favoris/753",
                stringBody = stringBodyApi,
                status = HttpStatus.OK,
            )

        given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callAuthentMock, callApiMock)

        // When
        val resultat = parcoursupApiHttpClient.recupererLesVoeuxSelectionnesSurParcoursup(idParcoursup = 753)

        // Then
        val attendu =
            listOf(
                FavorisParcoursup(
                    idVoeu = "ta123",
                    commentaire = null,
                    notation = 0,
                ),
                FavorisParcoursup(
                    idVoeu = "ta9583",
                    commentaire = null,
                    notation = 0,
                ),
                FavorisParcoursup(
                    idVoeu = "ta5729",
                    commentaire = null,
                    notation = 0,
                ),
            )
        assertThat(resultat).isEqualTo(attendu)
    }
}
