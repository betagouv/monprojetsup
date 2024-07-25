package fr.gouv.monprojetsup.eleve.domain.port

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import kotlin.jvm.Throws

interface EleveRepository {
    @Throws(MonProjetSupNotFoundException::class)
    fun recupererUnEleve(id: String): ProfilEleve

    fun creerUnEleve(id: String): ProfilEleve
}
