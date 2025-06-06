package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.FormationCourteEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FormationJPARepository : JpaRepository<FormationCourteEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<FormationCourteEntity>

    @Query("SELECT formation.id FROM FormationCourteEntity formation WHERE formation.id IN :ids")
    fun findExistingIds(
        @Param("ids") ids: List<String>,
    ): List<String>
}
