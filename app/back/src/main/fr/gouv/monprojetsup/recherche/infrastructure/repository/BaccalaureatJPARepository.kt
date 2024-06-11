package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.infrastructure.entity.BaccalaureatEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BaccalaureatJPARepository : JpaRepository<BaccalaureatEntity, String> {
    fun findByIdExterne(idExterne: String): BaccalaureatEntity?
}
