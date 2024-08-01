package fr.gouv.monprojetsup.referentiel.domain.port

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import kotlin.jvm.Throws

interface BaccalaureatRepository {
    fun recupererUnBaccalaureatParIdExterne(idExterneBaccalaureat: String): Baccalaureat?

    @Throws(MonProjetSupNotFoundException::class)
    fun verifierBaccalaureatExiste(id: String): Boolean

    fun recupererDesBaccalaureatsParIdsExternes(idsExternesBaccalaureats: List<String>): List<Baccalaureat>
}
