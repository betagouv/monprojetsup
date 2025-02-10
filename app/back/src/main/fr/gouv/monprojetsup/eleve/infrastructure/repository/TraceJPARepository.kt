package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.eleve.infrastructure.entity.TraceEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TraceJPARepository : JpaRepository<TraceEntity, Int> {
    fun findByIdEleve(idEleve: String): List<TraceEntity>
}
