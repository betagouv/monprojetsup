package fr.gouv.monprojetsup.metier.infrastructure.repository

import fr.gouv.monprojetsup.metier.infrastructure.entity.MetierCourtEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MetierCourtJPARepository : JpaRepository<MetierCourtEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<MetierCourtEntity>
}
