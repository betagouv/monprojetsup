package fr.gouv.monprojetsup.referentiel.domain.port

import fr.gouv.monprojetsup.referentiel.domain.entity.Interet
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretCategorie
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie

interface InteretRepository {
    fun recupererLesSousCategoriesDInterets(idsInterets: List<String>): Map<String, InteretSousCategorie>

    fun recupererLesInteretsDeSousCategories(idsSousCategoriesInterets: List<String>): List<Interet>

    fun recupererToutesLesCategoriesEtLeursSousCategoriesDInterets(): Map<InteretCategorie, List<InteretSousCategorie>>

    fun verifierCentresInteretsExistent(ids: List<String>): Boolean
}
