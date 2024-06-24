package fr.gouv.monprojetsup.recherche.domain.port

import fr.gouv.monprojetsup.recherche.domain.entity.InteretSousCategorie

interface InteretRepository {
    fun recupererLesSousCategoriesDInterets(idsInterets: List<String>): List<InteretSousCategorie>
}
