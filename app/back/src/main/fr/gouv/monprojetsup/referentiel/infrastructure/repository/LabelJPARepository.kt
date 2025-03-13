package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.infrastructure.entity.LabelEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LabelJPARepository : JpaRepository<LabelEntity, String> {
    fun findAllByIdIn(ids: List<String>): List<LabelEntity>
}
