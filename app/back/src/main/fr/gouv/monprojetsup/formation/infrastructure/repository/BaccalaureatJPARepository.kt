package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.BaccalaureatEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BaccalaureatJPARepository : JpaRepository<BaccalaureatEntity, String> {
    fun findByIdExterne(idExterne: String): BaccalaureatEntity?
}
