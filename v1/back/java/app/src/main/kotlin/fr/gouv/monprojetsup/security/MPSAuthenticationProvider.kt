package fr.gouv.monprojetsup.security

import fr.gouv.monprojetsup.app.auth.Authenticator
import fr.gouv.monprojetsup.app.db.dbimpl.DBMongo
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
import org.springframework.security.core.userdetails.UserDetails

class MPSAuthenticationProvider(
    private val authenticator : Authenticator,
    private val db : DBMongo)
    : AbstractUserDetailsAuthenticationProvider() {

    override fun additionalAuthenticationChecks(
        userDetails: UserDetails?,
        authentication: UsernamePasswordAuthenticationToken?
    ) {
        if(authentication == null || userDetails == null) {
            throw Exception("Authentication is null")
        }
        if(userDetails.password != authentication.credentials) {
            throw BadCredentialsException("Bad credentials")
        }
    }

    override fun retrieveUser(username: String?, authentication: UsernamePasswordAuthenticationToken?): UserDetails {
        return MPSUserDetails(db.findUser(username))
    }

}