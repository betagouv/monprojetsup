package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.eleve.infrastructure.entity.CompteParcoursupEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CompteParcoursupJPARepository : JpaRepository<CompteParcoursupEntity, UUID>
