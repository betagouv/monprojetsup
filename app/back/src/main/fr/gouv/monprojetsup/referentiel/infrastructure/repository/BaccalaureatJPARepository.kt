package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.infrastructure.entity.BaccalaureatEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BaccalaureatJPARepository : JpaRepository<BaccalaureatEntity, String> {
    fun findByIdExterne(idExterne: String): BaccalaureatEntity?

    fun findAllByIdExterneIn(idsExterne: List<String>): List<BaccalaureatEntity>
}
