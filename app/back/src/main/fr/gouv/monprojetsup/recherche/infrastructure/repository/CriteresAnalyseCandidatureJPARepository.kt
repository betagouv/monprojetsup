package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.recherche.infrastructure.entity.CritereAnalyseCandidatureEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CriteresAnalyseCandidatureJPARepository : JpaRepository<CritereAnalyseCandidatureEntity, String>
