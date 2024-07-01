package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.FormationDetailleeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FormationDetailleeJPARepository : JpaRepository<FormationDetailleeEntity, String>
