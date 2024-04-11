package fr.gouv.monprojetsup.app.controllers

import fr.gouv.monprojetsup.app.BASE_PATH
import fr.gouv.monprojetsup.app.services.log.LogErrorService
import fr.gouv.monprojetsup.app.services.log.TraceEventService
import fr.gouv.monprojetsup.common.server.Server
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$BASE_PATH/log")
class LogControllers(
    val logErrorService: LogErrorService,
    val traceEventService: TraceEventService
) {

    @PostMapping("/error")
    fun logError(@RequestBody request: LogErrorService.Request): Server.BasicResponse {
        return logErrorService.handleRequestAndExceptions(request)
    }

    @PostMapping("/trace")
    fun logTrace(@RequestBody request: TraceEventService.Request): Server.BasicResponse {
        return traceEventService.handleRequestAndExceptions(request)
    }

}