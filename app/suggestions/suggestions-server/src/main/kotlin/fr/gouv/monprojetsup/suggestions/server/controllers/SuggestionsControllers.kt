package fr.gouv.monprojetsup.suggestions.server.controllers

import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.GetSuggestionsConfigServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.SetSuggestionsConfigServiceDTO
import fr.gouv.monprojetsup.suggestions.server.BASE_PATH
import fr.gouv.monprojetsup.suggestions.services.GetExplanationsAndExamplesService
import fr.gouv.monprojetsup.suggestions.services.GetExplanationsAndExamplesService.EXPLANATIONS_ENDPOINT
import fr.gouv.monprojetsup.suggestions.services.GetFormationsOfInterestService
import fr.gouv.monprojetsup.suggestions.services.GetSuggestionsConfigService
import fr.gouv.monprojetsup.suggestions.services.GetSuggestionsService
import fr.gouv.monprojetsup.suggestions.services.GetSuggestionsService.SUGGESTIONS_ENDPOINT
import fr.gouv.monprojetsup.suggestions.services.SetSuggestionsConfigService
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping(BASE_PATH)
@Tag(name = "API Suggestions MonProjetSup",
    description = """
       API de suggestions de formations et métiers pour MonProjetSup.                    
    """)
@OpenAPIDefinition(
    info = Info(title = "MonProjetSup API", version = "1.2"),
    //servers = [ Server(url = "https://monprojetsup.fr/"), Server(url = "http://localhost:8004/") ]
)


class SuggestionsControllers(
    private val getExplanationsAndExamplesService: GetExplanationsAndExamplesService,
    private val getFormationsOfInterestService: GetFormationsOfInterestService,
    private val getSuggestionsService: GetSuggestionsService,
    private val setSuggestionsConfigService: SetSuggestionsConfigService,
    private val getSuggestionsConfigService: GetSuggestionsConfigService
) {
    @Operation(summary = "Récupère une liste de suggestion de formations et métiers associés à un profil.")
    @PostMapping("/$SUGGESTIONS_ENDPOINT")
    fun getSuggestions(
        @RequestBody(required = true) request: fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO.Request
    ): fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO.Response {
        return getSuggestionsService.handleRequestAndExceptions(request)
    }

    @Operation(
        summary = "Génère des explications au sujet de l'affinité d'un profil à une formation.",
        description = "Génère des explications au sujet de l'affinité d'un profil à une formation." +
                "Les explications sont des éléments sur la cohérence entre les différents éléments de profil et les caractéristiques de la formation." +
                "Par exemple la cohérence avec les préférences géographiques ou les centres d'intérêts du candidat."
    )
    @PostMapping("/$EXPLANATIONS_ENDPOINT")
    fun getExplanationsAndExamples(@RequestBody request: GetExplanationsAndExamplesServiceDTO.Request): GetExplanationsAndExamplesServiceDTO.Response =
        getExplanationsAndExamplesService.handleRequestAndExceptions(request)

    @Operation(summary = "Récupère une liste de formations d'affectation d'un ou plusieurs types, les plus proches d'une liste de villes données.")
    @PostMapping("/foi")
    fun getFormationsOfInterest(@RequestBody request: GetFormationsOfInterestService.Request): GetFormationsOfInterestService.Response =
        getFormationsOfInterestService.handleRequestAndExceptions(request)

    @Operation(summary = "Vérifie la santé du service.")
    @GetMapping("/ping")
    fun getPong(): String = getSuggestionsService.checkHealth()

    @Operation(summary = "Récupère la configuration courante de l'algorithme de suggestions")
    @GetMapping("/getConfig")
    fun getSuggestionsConfig(): GetSuggestionsConfigServiceDTO.Response =
        getSuggestionsConfigService.handleRequestAndExceptions(GetSuggestionsConfigServiceDTO.Request())

    @ConditionalOnProperty(name = ["mps.suggestions.dynamic_parameter_service.enabled"], havingValue = "true")
    @Operation(summary = "Change la configuration de l'algorithme de suggestions")
    @PostMapping("/setConfig")
    fun setSuggestionsConfig(@RequestBody request: SetSuggestionsConfigServiceDTO.Request): SetSuggestionsConfigServiceDTO.Response =
        setSuggestionsConfigService.handleRequestAndExceptions(request)
    
}


