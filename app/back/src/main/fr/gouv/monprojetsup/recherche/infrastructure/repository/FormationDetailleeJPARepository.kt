package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.infrastructure.entity.FormationDetailleeEntity
import org.springframework.data.jpa.repository.JpaRepository

interface FormationDetailleeJPARepository : JpaRepository<FormationDetailleeEntity, String>
