package fr.gouv.monprojetsup.app.controllers

import fr.gouv.monprojetsup.app.BASE_PATH
import fr.gouv.monprojetsup.app.services.info.GetSimpleStatsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$BASE_PATH")
class StatsControllers(
    private val getSimpleStatsService: GetSimpleStatsService
) {
    @GetMapping("/stats")
    fun getStats(
        @RequestParam("bac")  bac: String,
        @RequestParam("key")  key: String
    ): GetSimpleStatsService.Response {
        return getSimpleStatsService.handleRequestAndExceptions(GetSimpleStatsService.Request(bac, key));
    }

}

