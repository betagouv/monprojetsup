package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.MetierEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MetierJPARepository : JpaRepository<MetierEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<MetierEntity>
}
