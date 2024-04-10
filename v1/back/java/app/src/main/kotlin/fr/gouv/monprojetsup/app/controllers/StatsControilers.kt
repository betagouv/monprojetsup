package fr.gouv.monprojetsup.app.controllers

import fr.gouv.monprojetsup.app.BASE_PATH
import fr.gouv.monprojetsup.app.services.info.GetDetailsService
import fr.gouv.monprojetsup.app.services.profiles.AddMessageService
import fr.gouv.monprojetsup.app.services.profiles.GetMessagesService
import fr.gouv.monprojetsup.app.services.profiles.GetMyProfileService
import fr.gouv.monprojetsup.app.services.profiles.UpdateProfileService
import fr.gouv.monprojetsup.common.server.Server
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$BASE_PATH")
class StatsControilers(
    private val getDetailsService: GetDetailsService
    ) {

    @Operation(summary = "Récupère des détails sur un ensemble de formations.")
    @PostMapping("/details")
    fun getDetails(
        @RequestBody(required = true) request : GetDetailsService.Request
    ): GetDetailsService.Response {
        return getDetailsService.handleRequestAndExceptions(request)
    }

}
