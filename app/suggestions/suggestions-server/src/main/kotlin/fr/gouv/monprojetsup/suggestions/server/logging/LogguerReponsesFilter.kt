package fr.gouv.monprojetsup.suggestions.server.logging

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
