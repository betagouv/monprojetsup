package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.eleve.infrastructure.entity.CompteParcoursupEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CompteParcoursupJPARepository : JpaRepository<CompteParcoursupEntity, String>
