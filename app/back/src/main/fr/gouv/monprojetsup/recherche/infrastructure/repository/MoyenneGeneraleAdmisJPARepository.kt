package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.infrastructure.entity.MoyenneGeneraleAdmisEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MoyenneGeneraleAdmisJPARepository : JpaRepository<MoyenneGeneraleAdmisEntity, String> {
    fun findAllByAnneeAndIdFormation(
        annee: String,
        idFormation: String,
    ): List<MoyenneGeneraleAdmisEntity>
}
