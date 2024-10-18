package fr.gouv.monprojetsup.parcoursup.domain.port

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupInternalErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.entity.ParametresPourRecupererToken
import fr.gouv.monprojetsup.parcoursup.domain.entity.FavorisParcoursup

interface ParcoursupApiHttpClient {
    @Throws(MonProjetSupInternalErrorException::class)
    fun recupererLesVoeuxSelectionnesSurParcoursup(idParcoursup: Int): List<FavorisParcoursup>
}
