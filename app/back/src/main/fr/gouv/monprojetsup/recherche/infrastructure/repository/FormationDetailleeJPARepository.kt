package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.infrastructure.entity.FormationDetailleeEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FormationDetailleeJPARepository : JpaRepository<FormationDetailleeEntity, String> {
    @Query(
        value = "SELECT * FROM formation WHERE id = :idFormation OR :idFormation = ANY(formations_associees)",
        nativeQuery = true,
    )
    fun findByIdOrFormationsAssociees(
        @Param("idFormation") idFormation: String,
    ): List<FormationDetailleeEntity>
}
