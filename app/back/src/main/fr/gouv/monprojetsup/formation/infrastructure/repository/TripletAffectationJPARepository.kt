package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.TripletAffectationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TripletAffectationJPARepository : JpaRepository<TripletAffectationEntity, String> {
    fun findAllByIdFormationIn(idsFormations: List<String>): List<TripletAffectationEntity>

    fun findAllByIdFormation(idFormation: String): List<TripletAffectationEntity>
}
