package fr.gouv.monprojetsup.referentiel.domain.port

import fr.gouv.monprojetsup.referentiel.domain.entity.Interet
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretCategorie
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie

interface InteretRepository {
    fun recupererLesSousCategories(idsSousCategoriesInterets: List<String>): List<InteretSousCategorie>

    fun recupererLesInteretsDeSousCategories(idsSousCategoriesInterets: List<String>): List<Interet>

    fun recupererToutesLesCategoriesEtLeursSousCategoriesDInterets(): Map<InteretCategorie, List<InteretSousCategorie>>

    fun recupererIdsCentresInteretsInexistants(ids: List<String>): List<String>
}
