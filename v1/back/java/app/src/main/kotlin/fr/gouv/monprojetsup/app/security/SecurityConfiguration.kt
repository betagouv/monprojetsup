package fr.gouv.monprojetsup.app.security
import fr.gouv.monprojetsup.app.AppCustomUserDetailsService
import fr.gouv.monprojetsup.app.auth.Authenticator
import fr.gouv.monprojetsup.app.db.dbimpl.DBMongo
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler


@Configuration
@EnableWebSecurity
class AppSecurityConfig(
    private val customUserDetailsService: AppCustomUserDetailsService,
    private val authenticator: Authenticator,
    private val db : DBMongo
    ) {


    @Bean
    fun authenticationProvider(): MPSAuthenticationProvider {
        return MPSAuthenticationProvider(authenticator, db)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        // You can customize the strength of BCrypt here if needed
        return MPSPasswordEncoder()
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authorize ->
                authorize.anyRequest().permitAll()
            }
            .oauth2Login { oauth2 ->
                oauth2.loginPage("/index.html")
                oauth2.successHandler(OAuth2AuthenticationSuccessHandler())
                oauth2.failureHandler(OAuth2AuthenticationFailureHandler())
            }
            .csrf { csrf -> csrf.disable() }
        // Add more configurations if needed
        return http.build()
    }


}



class OAuth2AuthenticationSuccessHandler : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        if (authentication is OAuth2AuthenticationToken) {
            // This is an OAuth2 authentication
            println("OAuth2 authentication successful")
        }
    }
}

class OAuth2AuthenticationFailureHandler : AuthenticationFailureHandler {

    override fun onAuthenticationFailure(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        exception: AuthenticationException?
    ) {
        // This is an OAuth2 authentication failure
        println("OAuth2 authentication failed")
    }
}


@Configuration
@EnableWebSecurity
class OAuth2LoginConfig {
    @Bean
    fun clientRegistrationRepository(): ClientRegistrationRepository {
        return InMemoryClientRegistrationRepository(this.googleClientRegistration())
    }
    /* TODO gérer plusieurs clients */


    @Value("\${googleOauth2ClientId}")
    private val googleOauth2ClientId: String? = null

    @Value("\${googleOauth2ClientSecret}")
    private val googleOauth2ClientSecret: String? = null

    @Value("\${psupOauth2ClientId}")
    private val psupOauth2ClientId: String? = null

    @Value("\${psupOauth2ClientSecret}")
    private val psupOauth2ClientSecret: String? = null

    @Value("\${frontUri}")
    private val frontUri: String? = null

    private fun googleClientRegistration(): ClientRegistration {
        return ClientRegistration.withRegistrationId("google")
            .clientId(googleOauth2ClientId)
            .clientSecret(googleOauth2ClientSecret)
            .tokenUri("https://oauth2.googleapis.com/token")
            .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
            .redirectUri(frontUri + "/login/oauth2/code/google")
            .scope("openid", "email")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientAuthenticationMethod(org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .build()
    }

    private fun psupClientRegistration(): ClientRegistration {
        return ClientRegistration.withRegistrationId("psup")
            .clientId(psupOauth2ClientId)
            .clientSecret(psupOauth2ClientSecret)
            .tokenUri("http://test2.parcoursup.fr/Authentification/oauth2/token")
            .authorizationUri("http://test2.parcoursup.fr/Authentification/oauth2/authorize")
            .redirectUri(frontUri + "/login/oauth2/code/psup")
            .scope("openid", "email")
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .clientAuthenticationMethod(org.springframework.security.oauth2.core.ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .build()
    }
    /*
    {"issuer":"http://authentification.parcoursup.fr/Authentification",
    "authorization_endpoint":"http://authentification.parcoursup.fr/Authentification/oauth2/authorize",
    "device_authorization_endpoint":"http://authentification.parcoursup.fr/Authentification/oauth2/device_authorization",
    "token_endpoint":"http://authentification.parcoursup.fr/Authentification/oauth2/token",
    "token_endpoint_auth_methods_supported":["client_secret_basic","client_secret_post","client_secret_jwt","private_key_jwt"],
    "jwks_uri":"http://authentification.parcoursup.fr/Authentification/oauth2/jwks",
    "userinfo_endpoint":"http://authentification.parcoursup.fr/Authentification/userinfo",
    "end_session_endpoint":"http://authentification.parcoursup.fr/Authentification/connect/logout",
    "response_types_supported":["code"],
    "grant_types_supported":["authorization_code","client_credentials","refresh_token","urn:ietf:params:oauth:grant-type:device_code"],"revocation_endpoint":"http://authentification.parcoursup.fr/Authentification/oauth2/revoke","revocation_endpoint_auth_methods_supported":["client_secret_basic","client_secret_post","client_secret_jwt","private_key_jwt"],"introspection_endpoint":"http://authentification.parcoursup.fr/Authentification/oauth2/introspect","introspection_endpoint_auth_methods_supported":["client_secret_basic","client_secret_post","client_secret_jwt","private_key_jwt"],"code_challenge_methods_supported":["S256"],"subject_types_supported":["public"],"id_token_signing_alg_values_supported":["RS256"],"scopes_supported":["openid"]}
     */
}
/*
@Service
class CustomOAuth2UserService : DefaultOAuth2UserService() {
    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val user = super.loadUser(userRequest)
        return CustomOAuth2User(user)
    }
}

class CustomOAuth2User(private val oauth2User: OAuth2User) : OAuth2User {
    override fun getAttributes(): Map<String, Any> {
        return oauth2User.attributes
    }

    override fun getAuthorities(): Collection<GrantedAuthority?> {
        return oauth2User.authorities
    }

    override fun getName(): String {
        return oauth2User.getAttribute("name")!!
    }

    val email: String?
        get() = oauth2User.getAttribute("email")
}

 */

/*
class JwtAuthenticationFilter(
    private val customUserDetailsService: CustomUserDetailsService,
    private val secretKey: String
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader("Authorization")

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val jwt = authorizationHeader.substring(7)
            val claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt).body

            // The JWT is valid, you can now get the user's details from the claims
            val username = claims.subject

            // Load the UserDetails from the database
            val userDetails: UserDetails = customUserDetailsService.loadUserByUsername(username)

            // Create an authentication token and set it in the SecurityContext
            val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }
}*/


