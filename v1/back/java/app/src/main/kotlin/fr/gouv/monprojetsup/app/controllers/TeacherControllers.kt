package fr.gouv.monprojetsup.app.controllers

import fr.gouv.monprojetsup.app.BASE_PATH
import fr.gouv.monprojetsup.app.services.accounts.CreateAccountService
import fr.gouv.monprojetsup.app.services.info.SearchService
import fr.gouv.monprojetsup.app.services.teacher.*
import fr.gouv.monprojetsup.common.server.Server
import fr.gouv.monprojetsup.common.server.Server.BasicRequest
import fr.gouv.monprojetsup.common.server.Server.BasicResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("$BASE_PATH/teacher")
class TeacherControllers(
    private val getAdminInfosService: GetAdminInfosService,
    private val getGroupDetailsService: GetGroupDetailsService,
    private val getStudentProfileService: GetStudentProfileService,
    private val getStudentSelectionService: GetStudentSelectionService,
    private val switchRoleService: SwitchRoleService,
    private val setGroupMemberService: SetGroupMemberService,
    private val resetStudentPasswordService: ResetUserPasswordService,
    private val setStudentFeedbackService: SetStudentFeedbackService,
    private val switchToNewRefProfileRoleService: SwitchToNewRefProfileRoleService
) {


    @PostMapping("/groups/list")
    fun getGroupInfos(@RequestBody request: BasicRequest): GetAdminInfosService.Response {
        return getAdminInfosService.handleRequestAndExceptions(request)
    }

    @PostMapping("/groups/details")
    fun getGroupDetails(@RequestBody request: GetGroupDetailsService.Request): GetGroupDetailsService.Response {
        return getGroupDetailsService.handleRequestAndExceptions(request)
    }

    @PostMapping("/groups/add")
    fun setMember(@RequestBody request: SetGroupMemberService.Request): GetAdminInfosService.Response {
        return setGroupMemberService.handleRequestAndExceptions(request)
    }

    @PostMapping("/student/profile")
    fun getStudentDetails(@RequestBody request: GetStudentProfileService.Request): GetStudentProfileService.Response {
        return getStudentProfileService.handleRequestAndExceptions(request)
    }

    @PostMapping("/student/feedback")
    fun setStudentFeedback(@RequestBody request: SetStudentFeedbackService.Request): BasicResponse {
        return setStudentFeedbackService.handleRequestAndExceptions(request)
    }

    @PostMapping("/student/selection")
    fun getStudentSelection(@RequestBody request: GetStudentProfileService.Request): GetStudentSelectionService.Response {
        return getStudentSelectionService.handleRequestAndExceptions(request)
    }

    @PostMapping("/student/resetPassword")
    fun resetStudentPassword(@RequestBody request: ResetUserPasswordService.Request): ResetUserPasswordService.Response {
        return resetStudentPasswordService.handleRequestAndExceptions(request)
    }

    @PostMapping("/role")
    fun switchRole(@RequestBody request: SwitchRoleService.Request): BasicResponse {
        return switchRoleService.handleRequestAndExceptions(request)
    }

    @PostMapping("/switchToNewRefProfile")
    fun switchToNewRefProfileRoleService(@RequestBody request: BasicRequest): CreateAccountService.Response {
        return switchToNewRefProfileRoleService.handleRequestAndExceptions(request)
    }

}