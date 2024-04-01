package fr.gouv.monprojetsup.suggestions.controllers

import fr.gouv.monprojetsup.suggestions.BASE_PATH
import fr.gouv.monprojetsup.suggestions.services.GetExplanationsAndExamplesService
import fr.gouv.monprojetsup.suggestions.services.GetFormationsOfInterestService
import fr.gouv.monprojetsup.suggestions.services.GetSuggestionsService
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("$BASE_PATH/public")
class SuggestionsControllerz(
    private val getExplanationsAndExamplesService: GetExplanationsAndExamplesService,
    private val getFormationsOfInterestService: GetFormationsOfInterestService,
    private val getSuggestionsService: GetSuggestionsService
) {
    @PostMapping("/explanations")
    fun getExplanationsAndExamples(@RequestBody request : GetExplanationsAndExamplesService.Request ): GetExplanationsAndExamplesService.Response = getExplanationsAndExamplesService.handleRequestAndExceptions(request)

    @PostMapping("/foi")
    fun getFormationsOfInterest(@RequestBody request : GetFormationsOfInterestService.Request): GetFormationsOfInterestService.Response = getFormationsOfInterestService.handleRequestAndExceptions(request)

    @PostMapping("/suggestions")
    fun getSuggestions(@RequestBody request : GetSuggestionsService.Request): GetSuggestionsService.Response {
        return getSuggestionsService.handleRequestAndExceptions(request)
    }

    @GetMapping("/ping")
    fun getPong(): String {
        return getSuggestionsService.checkHealth()
    }

}