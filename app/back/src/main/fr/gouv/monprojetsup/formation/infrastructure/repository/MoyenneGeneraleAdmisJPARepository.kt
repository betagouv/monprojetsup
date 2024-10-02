package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.MoyenneGeneraleAdmisEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MoyenneGeneraleAdmisJPARepository : JpaRepository<MoyenneGeneraleAdmisEntity, String> {
    fun findAllByAnneeAndBaccalaureatIdNotIn(
        annee: String,
        idsBaccalaureatsExclus: List<String>,
    ): List<MoyenneGeneraleAdmisEntity>

    fun findAllByAnneeAndIdFormationAndBaccalaureatIdNotIn(
        annee: String,
        idFormation: String,
        idsBaccalaureatsExclus: List<String>,
    ): List<MoyenneGeneraleAdmisEntity>

    fun findAllByAnneeAndIdFormationInAndBaccalaureatIdNotIn(
        annee: String,
        idsFormations: List<String>,
        idsBaccalaureatsExclus: List<String>,
    ): List<MoyenneGeneraleAdmisEntity>
}
