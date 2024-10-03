package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.VoeuEntity
import org.springframework.data.jpa.repository.JpaRepository

interface VoeuJPARepository : JpaRepository<VoeuEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<VoeuEntity>

    fun findAllByIdFormationIn(idsFormations: List<String>): List<VoeuEntity>

    fun findAllByIdFormation(idFormation: String): List<VoeuEntity>
}
