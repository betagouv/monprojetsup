package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.infrastructure.entity.InteretEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InteretJPARepository : JpaRepository<InteretEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<InteretEntity>
}
