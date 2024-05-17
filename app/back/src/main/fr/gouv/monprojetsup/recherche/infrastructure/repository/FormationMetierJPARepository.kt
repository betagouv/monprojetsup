package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.infrastructure.entity.FormationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FormationMetierJPARepository : JpaRepository<FormationEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<FormationEntity>
}
