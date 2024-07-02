package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.MoyenneGeneraleAdmisEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MoyenneGeneraleAdmisJPARepository : JpaRepository<MoyenneGeneraleAdmisEntity, String> {
    fun findAllByAnneeAndIdFormation(
        annee: String,
        idFormation: String,
    ): List<MoyenneGeneraleAdmisEntity>

    fun findAllByAnneeAndIdFormationIn(
        annee: String,
        idsFormations: List<String>,
    ): List<MoyenneGeneraleAdmisEntity>
}
