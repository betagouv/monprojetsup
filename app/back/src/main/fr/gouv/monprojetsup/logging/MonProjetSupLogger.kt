package fr.gouv.monprojetsup.logging

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupExceptions
import fr.gouv.monprojetsup.logging.domain.ExceptionALogguer
import fr.gouv.monprojetsup.logging.domain.ReponseALogguer
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.Logger
import org.springframework.stereotype.Component

@Component
class MonProjetSupLogger(
    private val logger: Logger,
) {
    fun logReponse(
        message: String,
        methode: String,
        statusCode: Int,
        path: String,
    ) {
        val requete =
            ReponseALogguer(
                statusCode = statusCode,
                methode = methode,
                path = path,
            )
        logger.info(message, kv(REPONSE, requete))
    }

    fun logException(
        exception: MonProjetSupExceptions,
        statusCode: Int,
    ) {
        val exceptionALogguer =
            ExceptionALogguer(
                statusCode = statusCode,
                code = exception.code,
                message = exception.message,
                exception = exception.cause,
            )
        logger.info("L'appel a échoué", kv(MPS_EXCEPTION, exceptionALogguer))
    }

    fun info(
        type: String,
        message: String,
    ) {
        logger.info(message, kv(TYPE, type))
    }

    fun warn(
        type: String,
        message: String,
        parametres: Map<String, Any> = emptyMap(),
    ) {
        logger.warn(message, kv(TYPE, type), kv(PARAMETRES, parametres))
    }

    fun error(
        type: String,
        message: String,
        exception: Throwable? = null,
        parametres: Map<String, Any> = emptyMap(),
    ) {
        logger.error(message, kv(TYPE, type), kv(EXCEPTION, exception), kv(PARAMETRES, exception))
    }

    companion object {
        private const val EXCEPTION = "exception"
        private const val TYPE = "type"
        private const val PARAMETRES = "parametres"
        private const val MPS_EXCEPTION = "mpsException"
        private const val REPONSE = "reponse"
    }
}
