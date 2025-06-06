package fr.gouv.monprojetsup.parcoursup.domain.port

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.entity.ParametresPourRecupererToken

interface ParcoursupAuthentClient {
    @Throws(MonProjetSupInternalErrorException::class)
    fun recupererClientAccessToken(
        clientId: String,
        clientSecret: String,
    ): String?

    @Throws(MonProjetSupNotFoundException::class)
    fun recupererIdParcoursupEleve(parametresPourRecupererToken: ParametresPourRecupererToken): Int
}
