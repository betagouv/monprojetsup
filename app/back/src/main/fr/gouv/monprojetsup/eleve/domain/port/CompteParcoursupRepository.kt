package fr.gouv.monprojetsup.eleve.domain.port

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException

interface CompteParcoursupRepository {
    @Throws(MonProjetSupNotFoundException::class)
    fun enregistrerIdCompteParcoursup(
        idEleve: String,
        idParcoursup: Int,
    )

    fun recupererIdCompteParcoursup(idEleve: String): Int?
}
