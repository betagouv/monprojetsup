package fr.gouv.monprojetsup.app.controllers

import fr.gouv.monprojetsup.app.BASE_PATH
import fr.gouv.monprojetsup.app.services.info.GetDetailsService
import fr.gouv.monprojetsup.app.services.info.RechercheService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$BASE_PATH")
class SearchControilers(
    private val getDetailsService: GetDetailsService,
    private val searchService: RechercheService,
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
        @RequestBody(required = true) request : RechercheService.Request
    ): RechercheService.Response {
        return searchService.handleRequestAndExceptions(request)
    }

}
