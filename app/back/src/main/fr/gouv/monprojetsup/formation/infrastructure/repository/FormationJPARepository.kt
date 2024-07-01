package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.FormationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FormationJPARepository : JpaRepository<FormationEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<FormationEntity>
}
