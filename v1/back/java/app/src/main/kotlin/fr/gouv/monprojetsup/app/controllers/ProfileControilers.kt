package fr.gouv.monprojetsup.app.controllers

import fr.gouv.monprojetsup.app.BASE_PATH
import fr.gouv.monprojetsup.app.server.MyService
import fr.gouv.monprojetsup.app.services.profiles.AddMessageService
import fr.gouv.monprojetsup.app.services.profiles.GetMessagesService
import fr.gouv.monprojetsup.app.services.profiles.GetMyProfileService
import fr.gouv.monprojetsup.app.services.profiles.UpdateProfileService
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
    fun getProfile(@RequestBody request: MyService.BasicRequest): GetMyProfileService.Response {
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
