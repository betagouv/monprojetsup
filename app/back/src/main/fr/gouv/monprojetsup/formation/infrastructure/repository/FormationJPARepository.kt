package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.FormationCourteEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FormationJPARepository : JpaRepository<FormationCourteEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<FormationCourteEntity>
}
