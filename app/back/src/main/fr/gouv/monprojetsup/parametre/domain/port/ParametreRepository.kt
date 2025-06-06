package fr.gouv.monprojetsup.parametre.domain.port

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.parametre.domain.entity.Parametre

interface ParametreRepository {
    @Throws(MonProjetSupNotFoundException::class)
    fun estActif(parametre: Parametre): Boolean
}
