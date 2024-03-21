package fr.gouv.monprojetsup.controllers

import fr.gouv.monprojetsup.BASE_PATH
import fr.gouv.monprojetsup.tools.server.MyService.BasicRequest
import fr.gouv.monprojetsup.web.services.accounts.*
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("$BASE_PATH/login")
class LoginController(
    private val passwordLoginService: PasswordLoginService,
    private val oidcLoginService: OidcLoginService

    ) {

    @PostMapping("/password")
    fun passwordLogin(@RequestBody request: PasswordLoginService.Request): PasswordLoginService.Response {
        return passwordLoginService.handleRequestAndExceptions(request)
    }

    @PostMapping("/oidc")
    fun login(@RequestBody jwt: OidcLoginService.JwtEncoded, authentication: Authentication?): PasswordLoginService.Response {
        return oidcLoginService.handleRequestAndExceptions(jwt)
    }

}



@RestController
@RequestMapping("$BASE_PATH/public/account")
class PublicAccountController(
    private val createAccountService: CreateAccountService,
    private val confirmEmailService: ConfirmEmailService,

    private val sendResetPasswordEmailService: SendResetPasswordEmailService
) {


    @PostMapping("/create")
    fun registerUser(@RequestBody request: CreateAccountService.Request): CreateAccountService.Response {
        return createAccountService.handleRequestAndExceptions(request)
    }

    @PostMapping("/confirmEmail")
    fun confirmEmail(@RequestBody request: ConfirmEmailService.Request): ConfirmEmailService.Response {
        return confirmEmailService.handleRequestAndExceptions(request)
    }


    @PostMapping("/resetPassword")
    fun sendResetPasswordEmail(@RequestBody request: SendResetPasswordEmailService.Request): SendResetPasswordEmailService.Response {
        return sendResetPasswordEmailService.handleRequestAndExceptions(request)
    }

}

@RestController
@RequestMapping("$BASE_PATH/account")
class AuthenticatedAccountController(
    private val disconnectService: DisconnectService,
    private val setNewPasswordService: SetNewPasswordService
) {


    @PostMapping("/disconnect")
    fun disconnect(@RequestBody request: BasicRequest): DisconnectService.Response {
        return disconnectService.handleRequestAndExceptions(request)
    }

    @PostMapping("/setNewPassword")
    fun setNewPassword(@RequestBody request: SetNewPasswordService.Request): SetNewPasswordService.Response {
        return setNewPasswordService.handleRequestAndExceptions(request)
    }

}

