package fr.gouv.monprojetsup.eleve.domain.port

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.entity.ProfilEleve
import kotlin.jvm.Throws

interface EleveRepository {
    @Throws(MonProjetSupNotFoundException::class)
    fun recupererUnEleve(id: String): ProfilEleve

    fun creerUnEleve(id: String): ProfilEleve
}
