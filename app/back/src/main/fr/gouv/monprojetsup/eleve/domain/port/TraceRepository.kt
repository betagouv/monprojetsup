package fr.gouv.monprojetsup.eleve.domain.port

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.entity.ActionEleve
import fr.gouv.monprojetsup.eleve.domain.entity.Trace

interface TraceRepository {
    @Throws(MonProjetSupNotFoundException::class)
    fun ajouterTrace(
        idEleve: String,
        actionEleve: ActionEleve,
        param1: String? = null,
        param2: String? = null,
    )

    fun getTraces(idEleve: String): List<Trace>

    fun getNbFichesLues(id: String): Int
}
