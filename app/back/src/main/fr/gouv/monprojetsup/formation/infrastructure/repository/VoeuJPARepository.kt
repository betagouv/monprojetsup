package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.VoeuEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface VoeuJPARepository : JpaRepository<VoeuEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<VoeuEntity>

    fun findAllByIdFormationIn(idsFormations: List<String>): List<VoeuEntity>

    fun findAllByIdFormationInAndObsolete(
        idsFormations: List<String>,
        obsoletes: Boolean,
    ): List<VoeuEntity>

    fun findAllByIdFormation(idFormation: String): List<VoeuEntity>

    @Query("SELECT voeu.id FROM VoeuEntity voeu WHERE voeu.id IN :ids")
    fun findExistingIds(
        @Param("ids") ids: List<String>,
    ): List<String>
}
