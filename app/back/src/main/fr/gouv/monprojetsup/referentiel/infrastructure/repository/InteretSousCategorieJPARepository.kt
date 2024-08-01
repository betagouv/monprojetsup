package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.infrastructure.entity.InteretSousCategorieEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InteretSousCategorieJPARepository : JpaRepository<InteretSousCategorieEntity, String> {
    fun countAllByIdIn(ids: List<String>): Int
}
