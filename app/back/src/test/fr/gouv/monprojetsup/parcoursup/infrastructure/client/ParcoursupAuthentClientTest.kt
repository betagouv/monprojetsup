package fr.gouv.monprojetsup.parcoursup.infrastructure.client

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.commun.helper.MockitoHelper
import fr.gouv.monprojetsup.eleve.domain.entity.ParametresPourRecupererToken
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.Jwt

class ParcoursupAuthentClientTest {
    @Mock
    private lateinit var httpClient: OkHttpClient

    @Mock
    private lateinit var logger: MonProjetSupLogger

    @Mock
    private lateinit var jwtDecoder: ParcoursupJwtDecoder

    private val objectMapper = ObjectMapper()

    private lateinit var parcoursupAuthentClient: ParcoursupAuthentClient

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        parcoursupAuthentClient =
            ParcoursupAuthentClient(
                jwtDecoder = jwtDecoder,
                clientId = "clientIdAuthent",
                clientSecret = "clientSecretAuthent",
                baseUrl = "https://monauthentification.fr/Authentification",
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
                .request(Request.Builder().url(url).build()).body(reponseBody).build()
        given(callAuthentMock.execute()).willReturn(reponse)
        return callAuthentMock
    }

    @Nested
    inner class RecupererClientAccessToken {
        @Test
        fun `quand la récupération de l'access token échoue, alors doit throw MonProjetSupInternalErrorException`() {
            // Given
            val url = "https://monauthentification.fr/Authentification/oauth2/token"
            val callAuthentMock =
                mockCall(
                    url = url,
                    stringBody = null,
                    status = HttpStatus.INTERNAL_SERVER_ERROR,
                )

            given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callAuthentMock)

            // When & Then
            assertThatThrownBy {
                parcoursupAuthentClient.recupererClientAccessToken(clientId = "clientId", clientSecret = "clientSecret")
            }.isInstanceOf(MonProjetSupInternalErrorException::class.java).hasMessage(
                "Erreur lors de la connexion à l'API à l'url $url, un code 500 a été retourné avec le body null",
            )
        }

        @Test
        fun `quand la récupération du token ne contient pas access_token, alors doit throw MonProjetSupInternalErrorException`() {
            // Given
            val url = "https://monauthentification.fr/Authentification/oauth2/token"
            val stringBody =
                """
                {
                  "token": "abc"
                }
                """.trimIndent()
            val callAuthentMock =
                mockCall(
                    url = url,
                    stringBody = stringBody,
                    status = HttpStatus.OK,
                )

            given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callAuthentMock)

            // When & Then
            assertThatThrownBy {
                parcoursupAuthentClient.recupererClientAccessToken(clientId = "clientId", clientSecret = "clientSecret")
            }.isInstanceOf(MonProjetSupInternalErrorException::class.java).hasMessage(
                "Erreur lors de la désérialisation de la réponse de l'API à l'url $url pour le body suivant : $stringBody",
            )
        }

        @Test
        fun `quand la récupération du token réussie, alors doit le retourner`() {
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

            given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callAuthentMock)

            // When
            val resultat = parcoursupAuthentClient.recupererClientAccessToken(clientId = "clientId", clientSecret = "clientSecret")

            // Then
            assertThat(resultat).isEqualTo("eyJjkezhjfgkyfbzhjzg")
        }
    }

    @Nested
    inner class RecupererIdParcoursupEleve {
        private val parametresPourRecupererToken =
            ParametresPourRecupererToken(
                codeVerifier = "codeVerifier",
                code = "code",
                redirectUri = "redirectUri",
            )

        @Test
        fun `quand la récupération de l'access token échoue, alors doit throw MonProjetSupInternalErrorException`() {
            // Given
            val url =
                "https://monauthentification.fr/Authentification/oauth2/token?grant_type=authorization_code" +
                    "&redirect_uri=redirectUri&code=code&code_verifier=codeVerifier"
            val callAuthentMock =
                mockCall(
                    url = url,
                    stringBody = null,
                    status = HttpStatus.INTERNAL_SERVER_ERROR,
                )

            given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callAuthentMock)

            // When & Then
            assertThatThrownBy {
                parcoursupAuthentClient.recupererIdParcoursupEleve(parametresPourRecupererToken)
            }.isInstanceOf(MonProjetSupInternalErrorException::class.java).hasMessage(
                "Erreur lors de la connexion à l'API à l'url $url, un code 500 a été retourné avec le body null",
            )
        }

        @Test
        fun `quand la récupération du token ne contient pas access_token, alors doit throw MonProjetSupInternalErrorException`() {
            // Given
            val url =
                "https://monauthentification.fr/Authentification/oauth2/token?grant_type=authorization_code" +
                    "&redirect_uri=redirectUri&code=code&code_verifier=codeVerifier"
            val stringBody =
                """
                {
                  "token": "abc"
                }
                """.trimIndent()
            val callAuthentMock =
                mockCall(
                    url = url,
                    stringBody = stringBody,
                    status = HttpStatus.OK,
                )

            given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callAuthentMock)

            // When & Then
            assertThatThrownBy {
                parcoursupAuthentClient.recupererIdParcoursupEleve(parametresPourRecupererToken)
            }.isInstanceOf(MonProjetSupInternalErrorException::class.java).hasMessage(
                "Erreur lors de la désérialisation de la réponse de l'API à l'url $url pour le body suivant : $stringBody",
            )
        }

        @Test
        fun `quand le decoder n'arrive pas à déchiffrer le JWT, alors doit throw MonProjetSupInternalErrorException`() {
            // Given
            val url =
                "https://monauthentification.fr/Authentification/oauth2/token?grant_type=authorization_code" +
                    "&redirect_uri=redirectUri&code=code&code_verifier=codeVerifier"
            val stringBodyAuthent =
                """
                {
                  "access_token": "eyJjkezhjfgkyfbzhjzg"
                }
                """.trimIndent()
            val callAuthentMock =
                mockCall(
                    url = url,
                    stringBody = stringBodyAuthent,
                    status = HttpStatus.OK,
                )
            val exception = BadJwtException("An error occurred while attempting to decode the Jwt: Malformed token")
            given(jwtDecoder.decode("eyJjkezhjfgkyfbzhjzg")).willThrow(exception)

            given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callAuthentMock)

            // When & Then
            assertThatThrownBy {
                parcoursupAuthentClient.recupererIdParcoursupEleve(parametresPourRecupererToken)
            }.isInstanceOf(MonProjetSupInternalErrorException::class.java).hasMessage(
                "Le JWT renvoyé n'a pas su être désérialisé",
            )
        }

        @Test
        fun `quand la récupération du token réussie, alors doit le retourner`() {
            // Given
            val url =
                "https://monauthentification.fr/Authentification/oauth2/token?grant_type=authorization_code" +
                    "&redirect_uri=redirectUri&code=code&code_verifier=codeVerifier"
            val stringBodyAuthent =
                """
                {
                  "access_token": "eyJjkezhjfgkyfbzhjzg"
                }
                """.trimIndent()
            val callAuthentMock =
                mockCall(
                    url = url,
                    stringBody = stringBodyAuthent,
                    status = HttpStatus.OK,
                )
            val jwt = mock(Jwt::class.java)
            given(jwt.getClaim<String>("sub")).willReturn("12345")
            given(jwtDecoder.decode("eyJjkezhjfgkyfbzhjzg")).willReturn(jwt)

            given(httpClient.newCall(MockitoHelper.anyObject())).willReturn(callAuthentMock)

            // When
            val resultat = parcoursupAuthentClient.recupererIdParcoursupEleve(parametresPourRecupererToken)

            // Then
            assertThat(resultat).isEqualTo(12345)
        }
    }
}
