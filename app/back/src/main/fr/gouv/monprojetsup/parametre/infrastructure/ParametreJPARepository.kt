package fr.gouv.monprojetsup.parametre.infrastructure

import fr.gouv.monprojetsup.parametre.domain.entity.Parametre
import org.springframework.data.jpa.repository.JpaRepository

interface ParametreJPARepository : JpaRepository<ParametreEntity, Parametre>
