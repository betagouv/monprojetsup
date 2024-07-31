package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.infrastructure.entity.InteretEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InteretJPARepository : JpaRepository<InteretEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<InteretEntity>

    fun findAllByIdSousCategorieIn(ids: List<String>): List<InteretEntity>
}
