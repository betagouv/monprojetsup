package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.CommunesAvecVoeuxAuxAlentoursEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CommunesAvecVoeuxAuxAlentoursJPARepository : JpaRepository<CommunesAvecVoeuxAuxAlentoursEntity, String> {
    fun findAllByCodeInseeIn(codesInsee: List<String>): List<CommunesAvecVoeuxAuxAlentoursEntity>
}
