package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.VoeuCourtEntity
import org.springframework.data.jpa.repository.JpaRepository

interface VoeuCourtJPARepository : JpaRepository<VoeuCourtEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<VoeuCourtEntity>
}
