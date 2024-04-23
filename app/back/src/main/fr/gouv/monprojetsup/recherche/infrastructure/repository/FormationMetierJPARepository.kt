package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.infrastructure.entity.FormationMetierEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FormationMetierJPARepository : JpaRepository<FormationMetierEntity, String> {
    fun findAllByIdFormationIdIn(ids: List<String>): List<FormationMetierEntity>
}
