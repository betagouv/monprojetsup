package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.SpecialiteEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SpecialiteJPARepository : JpaRepository<SpecialiteEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<SpecialiteEntity>
}
