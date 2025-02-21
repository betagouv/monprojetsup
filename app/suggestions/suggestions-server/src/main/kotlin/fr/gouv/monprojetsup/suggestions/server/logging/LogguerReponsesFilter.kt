package fr.gouv.monprojetsup.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.NegatedRequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.IOException
import java.io.UnsupportedEncodingException

@Component
class LogguerReponsesFilter(
    private val mpsLogger: MpsLogger,
) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val requestToUse: HttpServletRequest = request
        val responseWrapper = ContentCachingResponseWrapper(response)
        filterChain.doFilter(requestToUse, responseWrapper)
        logReponse(requestToUse, responseWrapper)
        responseWrapper.copyBodyToResponse()
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return AntPathRequestMatcher("/v3/api-docs/**").matches(request) ||
            NegatedRequestMatcher(AntPathRequestMatcher("/api/v1/**")).matches(
                request,
            )
    }

    @Throws(UnsupportedEncodingException::class)
    private fun logReponse(
        request: HttpServletRequest,
        responseWrapper: ContentCachingResponseWrapper,
    ) {
        val title = "Réponse - ${responseWrapper.status} ${request.method} ${request.requestURI}"
        mpsLogger.logReponse(
            message = title,
            methode = request.method,
            statusCode = responseWrapper.status,
            path = request.requestURI,
        )
    }
}
