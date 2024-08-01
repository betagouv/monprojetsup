package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.eleve.infrastructure.entity.ProfilEleveEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface EleveJPARepository : JpaRepository<ProfilEleveEntity, UUID>
