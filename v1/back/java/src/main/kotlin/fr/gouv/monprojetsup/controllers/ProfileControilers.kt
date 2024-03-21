package fr.gouv.monprojetsup.controllers

import fr.gouv.monprojetsup.BASE_PATH
import fr.gouv.monprojetsup.tools.server.MyService.BasicRequest
import fr.gouv.monprojetsup.web.services.profiles.AddMessageService
import fr.gouv.monprojetsup.web.services.profiles.GetMessagesService
import fr.gouv.monprojetsup.web.services.profiles.GetMyProfileService
import fr.gouv.monprojetsup.web.services.profiles.UpdateProfileService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$BASE_PATH/profile")
class ProfileControilers(
    val getMyProfileService: GetMyProfileService,
    val updateProfileService: UpdateProfileService,
    val addMessageService: AddMessageService,
    val getMessagesService: GetMessagesService
    ) {

    @PostMapping("/get")
    fun getProfile(@RequestBody request: BasicRequest): GetMyProfileService.Response {
        return getMyProfileService.handleRequestAndExceptions(request)
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
