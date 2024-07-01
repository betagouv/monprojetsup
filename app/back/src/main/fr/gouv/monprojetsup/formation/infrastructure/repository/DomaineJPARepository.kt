package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.DomaineEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DomaineJPARepository : JpaRepository<DomaineEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<DomaineEntity>
}
