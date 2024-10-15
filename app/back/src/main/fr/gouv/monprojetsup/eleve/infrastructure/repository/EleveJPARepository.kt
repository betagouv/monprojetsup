package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.eleve.infrastructure.entity.ProfilEleveEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EleveJPARepository : JpaRepository<ProfilEleveEntity, String>
