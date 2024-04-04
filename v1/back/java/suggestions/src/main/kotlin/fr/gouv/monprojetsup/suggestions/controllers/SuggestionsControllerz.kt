package fr.gouv.monprojetsup.suggestions.controllers

import fr.gouv.monprojetsup.data.ServerData
import fr.gouv.monprojetsup.data.services.GetSimpleStatsService
import fr.gouv.monprojetsup.suggestions.BASE_PATH
import fr.gouv.monprojetsup.suggestions.services.GetExplanationsAndExamplesService
import fr.gouv.monprojetsup.suggestions.services.GetFormationsOfInterestService
import fr.gouv.monprojetsup.suggestions.services.GetSuggestionsService
import org.jetbrains.annotations.NotNull
import org.springframework.context.annotation.Bean
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("$BASE_PATH")
class SuggestionsControllerz(
    private val getExplanationsAndExamplesService: GetExplanationsAndExamplesService,
    private val getFormationsOfInterestService: GetFormationsOfInterestService,
    private val getSuggestionsService: GetSuggestionsService,
    private val getSimpleStatsService: GetSimpleStatsService
) {
    @PostMapping("/explanations")
    fun getExplanationsAndExamples(@RequestBody request : GetExplanationsAndExamplesService.Request ): GetExplanationsAndExamplesService.Response = getExplanationsAndExamplesService.handleRequestAndExceptions(request)

    @PostMapping("/foi")
    fun getFormationsOfInterest(@RequestBody request : GetFormationsOfInterestService.Request): GetFormationsOfInterestService.Response = getFormationsOfInterestService.handleRequestAndExceptions(request)

    @PostMapping("/suggestions")
    fun getSuggestions(@RequestBody request : GetSuggestionsService.Request): GetSuggestionsService.Response {
        return getSuggestionsService.handleRequestAndExceptions(request)
    }

    @GetMapping("/stats")
    fun getStats(
        @RequestParam("key")  key: String,
        @RequestParam("bac", required = false)  bac: String?
    ): GetSimpleStatsService.Response {
        return getSimpleStatsService.handleRequestAndExceptions(GetSimpleStatsService.Request(bac, key));
    }

    @GetMapping("/label")
    fun getLabel(
        @RequestParam("key")  @NotNull key: String
    ): @NotNull String {
        return ServerData.getLabel(key, key)
    }

    @GetMapping("/ping")
    fun getPong(): String {
        return getSuggestionsService.checkHealth()
    }

}


