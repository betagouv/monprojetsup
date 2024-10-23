package fr.gouv.monprojetsup.metier.infrastructure.repository

import fr.gouv.monprojetsup.metier.infrastructure.entity.MetierEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MetierJPARepository : JpaRepository<MetierEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<MetierEntity>

    @Query("SELECT metier.id FROM MetierEntity metier WHERE metier.id IN :ids")
    fun findExistingIds(
        @Param("ids") ids: List<String>,
    ): List<String>
}
