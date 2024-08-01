package fr.gouv.monprojetsup.metier.infrastructure.repository

import fr.gouv.monprojetsup.metier.infrastructure.dto.MetierEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MetierJPARepository : JpaRepository<MetierEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<MetierEntity>

    fun countAllByIdIn(ids: List<String>): Int
}
