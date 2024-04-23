package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.infrastructure.entity.TripletAffectationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TripletAffectationJPARepository : JpaRepository<TripletAffectationEntity, String> {
    fun findAllByIdFormationIn(idsFormations: List<String>): List<TripletAffectationEntity>
}
