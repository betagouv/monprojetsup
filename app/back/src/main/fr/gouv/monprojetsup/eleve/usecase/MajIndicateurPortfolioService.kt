package fr.gouv.monprojetsup.eleve.usecase

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.auth.infrastructure.BearerTokenProviderService
import fr.gouv.monprojetsup.commun.client.ApiHttpClient
import fr.gouv.monprojetsup.eleve.domain.entity.ActionEleve
import fr.gouv.monprojetsup.eleve.domain.port.TraceRepository
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import okhttp3.OkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MajIndicateurPortfolioService(
    @Value("\${pfa.api.baseUrl}")
    override val baseUrl: String,
    override val objectMapper: ObjectMapper,
    override val httpClient: OkHttpClient,
    override val logger: MonProjetSupLogger,
    private val traceRepository: TraceRepository,
) : ApiHttpClient(baseUrl, objectMapper, httpClient, logger) {

    @Value("\${pfa.api.auth.uri}")
    lateinit var authUri: String

    @Value("\${pfa.api.auth.client.id}")
    lateinit var authClientId: String

    @Value("\${pfa.api.auth.client.secret}")
    lateinit var authClientSecret: String

    @Value("\${pfa.api.publication.endpoint}")
    lateinit var pfaIndicateurPortFolioEndpoint: String

    @Value("\${pfa.api.publication.modalite.id}")
    lateinit var outilModaliteId: String

    @Value("\${pfa.api.publication.activite.id}")
    lateinit var activiteId: String

    @Value("\${pfa.api.publication.eleve.id.debug}")
    lateinit var eleveIdDebug: String

    // create BearerTokenProviderService instance
    private val bearerTokenProviderService by lazy {
        BearerTokenProviderService(
            clientId = authClientId,
            clientSecret = authClientSecret,
            authUri = authUri,
            httpClient = httpClient,
            objectMapper = objectMapper,
            logger = logger,
        )
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class MajIndicateurPortfolioReponseDTO(
        val id: String,
    ) {
        constructor() : this(id = "")
    }

    /**
     * Ajoute une publication PFA pour un élève.
     *
     * @param idElevePortfolio L'identifiant de l'élève.
     * @param libelle La clé de la publication.
     * @param valeur La valeur de la publication.
     * @param idIndicateur Le code de la publication.
     */
    @Transactional(readOnly = false)
    fun ajouterPublication(
        idElevePortfolio: String,
        libelle: String,
        valeur: String,
        idIndicateur: String,
        valeurNumerique: Int = 0,
    ) {
        traceRepository.ajouterTrace(idElevePortfolio, ActionEleve.PUBLICATION_ACTIVITE, libelle, valeur)
        post<MajIndicateurPortfolioReponseDTO>(
            url = "$baseUrl/$pfaIndicateurPortFolioEndpoint",
            requeteDTO =
                mapOf(
                    "outil_modalite_id" to outilModaliteId,
                    "activite_id" to activiteId,
                    "eleve_id" to idElevePortfolio,
                    "valeur_numerique" to valeurNumerique,
                    "valeur" to valeur,
                    "cle" to libelle,
                    "code" to idIndicateur,
                ),
            token = bearerTokenProviderService.getToken(),
        )
    }
}
