package fr.gouv.monprojetsup.referentiel.domain.port

import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie

interface InteretRepository {
    fun recupererLesSousCategoriesDInterets(idsInterets: List<String>): Map<String, InteretSousCategorie>
}
