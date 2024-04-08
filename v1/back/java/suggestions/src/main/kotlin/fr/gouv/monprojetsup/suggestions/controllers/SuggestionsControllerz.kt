package fr.gouv.monprojetsup.suggestions.controllers

import fr.gouv.monprojetsup.data.ServerData
import fr.gouv.monprojetsup.data.services.GetSimpleStatsService
import fr.gouv.monprojetsup.suggestions.BASE_PATH
import fr.gouv.monprojetsup.suggestions.services.GetDetailsService
import fr.gouv.monprojetsup.suggestions.services.GetFormationsAffinitiesService
import fr.gouv.monprojetsup.suggestions.services.GetExplanationsAndExamplesService
import fr.gouv.monprojetsup.suggestions.services.GetFormationsOfInterestService
import fr.gouv.monprojetsup.suggestions.services.GetSuggestionsService
import fr.gouv.monprojetsup.suggestions.services.SortMetiersByAffinityService
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
@Tag(name = "API Suggestions MonProjetSup")
@OpenAPIDefinition(
    info = Info(title = "MonProjetSup API", version = "1.1"),
    servers = [ Server(url = "https://monprojetsup.fr/"), Server(url = "http://localhost:8003/") ]
)
class SuggestionsControllerz(
    private val getExplanationsAndExamplesService: GetExplanationsAndExamplesService,
    private val getFormationsOfInterestService: GetFormationsOfInterestService,
    private val getSuggestionsService: GetSuggestionsService,
    private val getSimpleStatsService: GetSimpleStatsService,
    private val getAffiniteFormationsService: GetFormationsAffinitiesService,
    private val sortMetiersByAffinityService: SortMetiersByAffinityService,
    private val getDetailsService: GetDetailsService
) {

    @Operation(summary = "Récupère la liste des formations, classées par affinité avec le profil.")
    @PostMapping("/affinite/formations")
    fun getAffiniteFormations(
        @RequestBody(required = true) request : GetFormationsAffinitiesService.Request
    ): GetFormationsAffinitiesService.Response {
        return getAffiniteFormationsService.handleRequestAndExceptions(request)
    }

    @Operation(summary = "Trie une liste de métiers par affinite.")
    @PostMapping("/affinite/metiers")
    fun getAffiniteMetiers(
        @RequestBody(required = true) request : SortMetiersByAffinityService.Request
    ): SortMetiersByAffinityService.Response {
        return sortMetiersByAffinityService.handleRequestAndExceptions(request)
    }

    @Operation(summary = "Récupère des détails sur un ensemble de details.")
    @PostMapping("/details")
    fun getDetails(
        @RequestBody(required = true) request : GetDetailsService.Request
    ): GetDetailsService.Response {
        return getDetailsService.handleRequestAndExceptions(request)
    }


    @Operation(summary = "Récupère une liste de suggestion de formations et métiers associés à un profil.")
    @PostMapping("/suggestions")
    fun getSuggestions(
        @RequestBody(required = true) request : GetSuggestionsService.Request
    ): GetSuggestionsService.Response {
        return getSuggestionsService.handleRequestAndExceptions(request)
    }

    @Operation(summary = "Récupère des informations sur le profil scolaire des admis dans une formation.")
    @GetMapping("/stats")
    fun getStats(
        @RequestParam("key", required = true)  @Parameter(name = "key", description = "clé de la formation", example = "fl1") key: String,
        @RequestParam("bac", required = false)  @Parameter(name = "key", description = "type de bac", example = "STMG") bac: String?
    ): GetSimpleStatsService.Response {
        return getSimpleStatsService.handleRequestAndExceptions(GetSimpleStatsService.Request(bac, key))
    }

    @Operation(summary = "Produit des explications au sujet de l'affinité d'un profil à une formation.",
        description = "Produit des explications au sujet de l'affinité d'un profil à une formation." +
                "Les explications sont des éléments sur la cohérence entre les différents éléments de profil et les caractéristiques de la formation." +
                "Par exemple la cohérence avec les préférences géographiques ou les centres d'intérêts du candidat.")
    @PostMapping("/explanations")
    fun getExplanationsAndExamples(@RequestBody request : GetExplanationsAndExamplesService.Request ): GetExplanationsAndExamplesService.Response = getExplanationsAndExamplesService.handleRequestAndExceptions(request)

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


