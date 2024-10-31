package fr.gouv.monprojetsup.parametre.usecase

import com.fasterxml.jackson.databind.ObjectMapper
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupServiceUnavailableException
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.parametre.domain.entity.Parametre
import fr.gouv.monprojetsup.parametre.domain.port.ParametreRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class EtlEnCoursFilter(
    private val parametreRepository: ParametreRepository,
    private val logguer: MonProjetSupLogger,
    private val objectMapper: ObjectMapper,
) : OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        if (parametreRepository.estActif(Parametre.ETL_EN_COURS)) {
            val status = HttpStatus.SERVICE_UNAVAILABLE
            val domainError =
                MonProjetSupServiceUnavailableException(
                    code = "ETL_EN_COURS",
                    msg = "Le service est indisponible pour cause de mise a jour de la BDD",
                )
            logguer.logException(domainError, status.value())
            val reponse = ProblemDetail.forStatus(status)
            reponse.title = domainError.code
            reponse.detail = domainError.msg
            response.contentType = MediaType.APPLICATION_PROBLEM_JSON.toString()
            response.status = status.value()
            response.writer.write(objectMapper.writeValueAsString(reponse))
            return
        }
        filterChain.doFilter(request, response)
    }
}
