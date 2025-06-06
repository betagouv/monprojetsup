package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.infrastructure.entity.InteretSousCategorieEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface InteretSousCategorieJPARepository : JpaRepository<InteretSousCategorieEntity, String> {
    @Query("SELECT interet.id FROM InteretSousCategorieEntity interet WHERE interet.id IN :ids")
    fun findExistingIds(
        @Param("ids") ids: List<String>,
    ): List<String>
}
