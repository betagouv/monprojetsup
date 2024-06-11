package fr.gouv.monprojetsup.app.controllers

import fr.gouv.monprojetsup.app.BASE_PATH
import fr.gouv.monprojetsup.app.services.admin.*
import fr.gouv.monprojetsup.app.services.teacher.GetAdminInfosService
import fr.gouv.monprojetsup.app.services.teacher.ResetUserPasswordService
import fr.gouv.monprojetsup.app.services.teacher.SetGroupMemberService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@RestController
@RequestMapping("$BASE_PATH/admin")
class AdminControllers(
    val createGroupHandler: CreateGroupHandler,
    val setGroupMemberService: SetGroupMemberService,
    val deleteUserHandler: DeleteUserHandler,
    val resetUserPasswordService: ResetUserPasswordService,
    val moderateAccountCreationHandler: ModerateAccountCreationHandler,
    val setModerationHandler: SetModerationHandler,
    val setAdminGroupHandler: SetGroupAdminHandler
) {
    @PostMapping("/groups/create")
    fun createGroup(@RequestBody request: CreateGroupHandler.Request): GetAdminInfosService.Response {
        return createGroupHandler.handleRequestAndExceptions(request)
    }


    @PostMapping("/groups/setMember")
    fun setMember(@RequestBody request: SetGroupMemberService.Request): GetAdminInfosService.Response {
        return setGroupMemberService.handleRequestAndExceptions(request)
    }

    @PostMapping("/deleteUser")
    fun deleteUser(@RequestBody request: DeleteUserHandler.Request): GetAdminInfosService.Response {
        return deleteUserHandler.handleRequestAndExceptions(request)
    }

    @PostMapping("/user/resetPassword")
    fun resetUserPassword(@RequestBody request: ResetUserPasswordService.Request): ResetUserPasswordService.Response {
        return resetUserPasswordService.handleRequestAndExceptions(request)
    }

    @PostMapping("/moderateAccountCreation")
    fun moderateAccountCreation(@RequestBody request: ModerateAccountCreationHandler.Request): GetAdminInfosService.Response {
        return moderateAccountCreationHandler.handleRequestAndExceptions(request)
    }

    @PostMapping("/setModeration")
    fun setModeration(@RequestBody request: SetModerationHandler.Request): GetAdminInfosService.Response {
        return setModerationHandler.handleRequestAndExceptions(request)
    }

    @PostMapping("/groups/setAdmin")
    fun setGrouAdmin(@RequestBody request: SetGroupAdminHandler.Request): GetAdminInfosService.Response {
        return setAdminGroupHandler.handleRequestAndExceptions(request)
    }


}
