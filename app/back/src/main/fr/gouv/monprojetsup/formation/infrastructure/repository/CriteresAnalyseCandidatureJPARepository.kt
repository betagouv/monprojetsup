package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.formation.infrastructure.entity.CritereAnalyseCandidatureEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CriteresAnalyseCandidatureJPARepository : JpaRepository<CritereAnalyseCandidatureEntity, String>
