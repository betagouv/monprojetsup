package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.infrastructure.entity.DomaineEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DomaineJPARepository : JpaRepository<DomaineEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<DomaineEntity>

    @Query("SELECT domaine.id FROM DomaineEntity domaine WHERE domaine.id IN :ids")
    fun findExistingIds(
        @Param("ids") ids: List<String>,
    ): List<String>
}
