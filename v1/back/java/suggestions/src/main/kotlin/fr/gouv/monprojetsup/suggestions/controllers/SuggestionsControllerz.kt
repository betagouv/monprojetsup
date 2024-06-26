package fr.gouv.monprojetsup.suggestions.controllers

import fr.gouv.monprojetsup.data.dto.GetExplanationsAndExamplesServiceDTO
import fr.gouv.monprojetsup.data.ServerData
import fr.gouv.monprojetsup.data.dto.GetAffinitiesServiceDTO
import fr.gouv.monprojetsup.data.dto.SortMetiersByAffinityServiceDTO
import fr.gouv.monprojetsup.data.services.GetSimpleStatsService
import fr.gouv.monprojetsup.suggestions.BASE_PATH
import fr.gouv.monprojetsup.suggestions.services.*
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import io.swagger.v3.oas.annotations.tags.Tag
import org.jetbrains.annotations.NotNull
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
    private val getExplanationsAndExamplesService: GetExplanationsAndExamplesService,
    private val getFormationsOfInterestService: GetFormationsOfInterestService,
    private val getSuggestionsService: GetSuggestionsService,
    private val getSimpleStatsService: GetSimpleStatsService
) {

    @Operation(summary = "Récupère la liste des formations et des métiers, classés par affinité.")
    @PostMapping("/affinites")
    fun getAffiniteFormations(
        @RequestBody(required = true) request : GetAffinitiesServiceDTO.Request
    ): GetAffinitiesServiceDTO.Response {
        return getSuggestionsService.handleRequestAndExceptions(request)
    }

    @Operation(summary = "Récupère une liste de suggestion de formations et métiers associés à un profil.")
    @PostMapping("/suggestions")
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
    ): GetSimpleStatsService.Response {
        return getSimpleStatsService.handleRequestAndExceptions(GetSimpleStatsService.Request(bac, key))
    }

    @Operation(summary = "Produit des explications au sujet de l'affinité d'un profil à une formation.",
        description = "Produit des explications au sujet de l'affinité d'un profil à une formation." +
                "Les explications sont des éléments sur la cohérence entre les différents éléments de profil et les caractéristiques de la formation." +
                "Par exemple la cohérence avec les préférences géographiques ou les centres d'intérêts du candidat.")
    @PostMapping("/explanations")
    fun getExplanationsAndExamples(@RequestBody request : GetExplanationsAndExamplesServiceDTO.Request): GetExplanationsAndExamplesServiceDTO.Response = getExplanationsAndExamplesService.handleRequestAndExceptions(request)

    @Operation(summary = "Récupère une liste de formations d'affectation d'un ou plusieurs types, les plus proches d'une liste de villes données.")
    @PostMapping("/foi")
    fun getFormationsOfInterest(@RequestBody request : GetFormationsOfInterestService.Request): GetFormationsOfInterestService.Response = getFormationsOfInterestService.handleRequestAndExceptions(request)


    @Operation(summary = "Fournit le libellé associé à une clé formation, métier ou secteur d'activité.")
    @GetMapping("/label")
    fun getLabel(
        @RequestParam("key") @Parameter(name = "key", description = "clé", example = "fl1")  @NotNull key: String
    ): String {
        return ServerData.getLabel(key, "")
    }


    @Operation(summary = "Vérifie la santé du service.")
    @GetMapping("/ping")
    fun getPong(): String {
        return getSuggestionsService.checkHealth()
    }

}


