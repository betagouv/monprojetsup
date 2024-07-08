package fr.gouv.monprojetsup.app.controllers

import fr.gouv.monprojetsup.app.BASE_PATH
import fr.gouv.monprojetsup.app.server.Server
import fr.gouv.monprojetsup.app.services.info.SearchService
import fr.gouv.monprojetsup.app.services.profiles.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$BASE_PATH/profile")
class ProfileControilers(
    val getMyProfileService: GetMyProfileService,
    val getMySelectionService: GetMySelectionService,
    val updateProfileService: UpdateProfileService,
    val addMessageService: AddMessageService,
    val getMessagesService: GetMessagesService
    ) {

    @PostMapping("/get")
    fun getProfile(@RequestBody request: Server.BasicRequest): GetMyProfileService.Response {
        return getMyProfileService.handleRequestAndExceptions(request)
    }

    @PostMapping("/favoris")
    fun getSelection(@RequestBody request: Server.BasicRequest): SearchService.Response {
        return getMySelectionService.handleRequestAndExceptions(request)
    }

    @PostMapping("/update")
    fun updateProfile(@RequestBody request: UpdateProfileService.Request): UpdateProfileService.Response {
        return updateProfileService.handleRequestAndExceptions(request)
    }

    @PostMapping("/messages")
    fun getMessages(@RequestBody request: GetMessagesService.Request): GetMessagesService.Response {
        return getMessagesService.handleRequestAndExceptions(request)
    }
    @PostMapping("/message")
    fun addMessage(@RequestBody request: AddMessageService.Request): AddMessageService.Response {
        return addMessageService.handleRequestAndExceptions(request)
    }


}
