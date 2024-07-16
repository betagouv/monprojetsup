package fr.gouv.monprojetsup.suggestions.server.controllers

import fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.GetExplanationsAndExamplesServiceDTO
import fr.gouv.monprojetsup.suggestions.server.BASE_PATH
import fr.gouv.monprojetsup.suggestions.services.*
import fr.gouv.monprojetsup.suggestions.services.GetExplanationsAndExamplesService.EXPLANATIONS_ENDPOINT
import fr.gouv.monprojetsup.suggestions.services.GetSuggestionsService.SUGGESTIONS_ENDPOINT
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping(BASE_PATH)
@Tag(name = "API Suggestions MonProjetSup",
    description = """
       API de suggestions de formations et métiers pour MonProjetSup.                    
    """)
@OpenAPIDefinition(
    info = Info(title = "MonProjetSup API", version = "1.2"),
    servers = [ Server(url = "https://monprojetsup.fr/"), Server(url = "http://localhost:8004/") ]
)


class SuggestionsControllerz(
    private val getExplanationsAndExamplesService: fr.gouv.monprojetsup.suggestions.services.GetExplanationsAndExamplesService,
    private val getFormationsOfInterestService: fr.gouv.monprojetsup.suggestions.services.GetFormationsOfInterestService,
    private val getSuggestionsService: fr.gouv.monprojetsup.suggestions.services.GetSuggestionsService,
    private val getSimpleStatsService: fr.gouv.monprojetsup.suggestions.services.GetSimpleStatsService
) {
    @Operation(summary = "Récupère une liste de suggestion de formations et métiers associés à un profil.")
    @PostMapping("/$SUGGESTIONS_ENDPOINT")
    fun getSuggestions(
        @RequestBody(required = true) request : GetAffinitiesServiceDTO.Request
    ): GetAffinitiesServiceDTO.Response {
        return getSuggestionsService.handleRequestAndExceptions(request)
    }

    @Operation(summary = "Récupère des informations sur le profil scolaire des admis dans une formation.")
    @GetMapping("/stats")
    fun getStats(
        @RequestParam("key")  @Parameter(name = "key", description = "clé de la formation", example = "fl1") key: String,
        @RequestParam("bac", required = false)  @Parameter(name = "key", description = "type de bac", example = "STMG") bac: String?
    ): fr.gouv.monprojetsup.suggestions.services.GetSimpleStatsService.Response {
        return getSimpleStatsService.handleRequestAndExceptions(fr.gouv.monprojetsup.suggestions.services.GetSimpleStatsService.Request(bac, key))
    }

    @Operation(summary = "Produit des explications au sujet de l'affinité d'un profil à une formation.",
        description = "Produit des explications au sujet de l'affinité d'un profil à une formation." +
                "Les explications sont des éléments sur la cohérence entre les différents éléments de profil et les caractéristiques de la formation." +
                "Par exemple la cohérence avec les préférences géographiques ou les centres d'intérêts du candidat.")
    @PostMapping("/$EXPLANATIONS_ENDPOINT")
    fun getExplanationsAndExamples(@RequestBody request : GetExplanationsAndExamplesServiceDTO.Request): GetExplanationsAndExamplesServiceDTO.Response = getExplanationsAndExamplesService.handleRequestAndExceptions(request)

    @Operation(summary = "Récupère une liste de formations d'affectation d'un ou plusieurs types, les plus proches d'une liste de villes données.")
    @PostMapping("/foi")
    fun getFormationsOfInterest(@RequestBody request : fr.gouv.monprojetsup.suggestions.services.GetFormationsOfInterestService.Request): fr.gouv.monprojetsup.suggestions.services.GetFormationsOfInterestService.Response = getFormationsOfInterestService.handleRequestAndExceptions(request)

    @Operation(summary = "Vérifie la santé du service.")
    @GetMapping("/ping")
    fun getPong(): String {
        return getSuggestionsService.checkHealth()
    }

}


