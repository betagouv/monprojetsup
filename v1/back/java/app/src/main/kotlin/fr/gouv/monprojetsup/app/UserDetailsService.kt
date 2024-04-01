package fr.gouv.monprojetsup.app

import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        // In a real scenario, you should fetch user details from your database
        // Here's a hardcoded user for demonstration
        return User.withUsername(username)
            .password("password") // Use a password encoder in real applications
            .roles("USER") // Assign roles as necessary
            .build()
    }
}