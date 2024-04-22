package fr.gouv.monprojetsup.app.controllers

import fr.gouv.monprojetsup.app.BASE_PATH
import fr.gouv.monprojetsup.app.services.info.GetDetailsService
import fr.gouv.monprojetsup.app.services.info.SearchService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$BASE_PATH")
class SearchControilers(
    private val getDetailsService: GetDetailsService,
    private val searchService: SearchService,
    ) {

    @Operation(summary = "Récupère des détails sur un ensemble de formations.")
    @PostMapping("/details")
    fun details(
        @RequestBody(required = true) request : GetDetailsService.Request
    ): GetDetailsService.Response {
        return getDetailsService.handleRequestAndExceptions(request)
    }

    @Operation(summary = "Effectue une recherche sur la base de mots-clés et de profils.")
    @PostMapping("/recherche")
    fun search(
        @RequestBody(required = true) request : SearchService.Request
    ): SearchService.Response {
        return searchService.handleRequestAndExceptions(request)
    }

}
