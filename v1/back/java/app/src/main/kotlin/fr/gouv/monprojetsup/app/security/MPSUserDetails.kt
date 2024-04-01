package fr.gouv.monprojetsup.app.security

import fr.gouv.monprojetsup.app.auth.Authenticator
import fr.gouv.monprojetsup.app.db.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomGrantedAuthority(private val role: User.Role) : GrantedAuthority {
    override fun getAuthority(): String {
        return role.toString()
    }
}

class MPSPasswordEncoder : org.springframework.security.crypto.password.PasswordEncoder {

    override fun encode(rawPassword: CharSequence?): String {
        return rawPassword.toString()
    }

    override fun matches(rawPassword: CharSequence?, encodedPassword: String?): Boolean {
        //TODO: deal with newly created accounts
        return Authenticator.matches(rawPassword, encodedPassword)
    }

}

class MPSUserDetails(
    private val user : User
) : UserDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return when(user.role) {
            null -> mutableListOf()
            User.Role.USER -> mutableListOf( CustomGrantedAuthority(User.Role.USER) )
            User.Role.TEACHER -> mutableListOf( CustomGrantedAuthority(User.Role.USER), CustomGrantedAuthority(User.Role.TEACHER))
            User.Role.ADMIN -> mutableListOf( CustomGrantedAuthority(User.Role.USER), CustomGrantedAuthority(User.Role.TEACHER), CustomGrantedAuthority(User.Role.ADMIN))
        }
    }

    override fun getPassword(): String {
        return user.hashAndSaltConcatenated;
    }

    override fun getUsername(): String {
        return user.login()
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return user.isActivated
    }
}