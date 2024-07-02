package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.formation.domain.entity.InteretSousCategorie

interface InteretRepository {
    fun recupererLesSousCategoriesDInterets(idsInterets: List<String>): Map<String, InteretSousCategorie>
}
