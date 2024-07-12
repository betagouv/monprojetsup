package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.infrastructure.entity.BaccalaureatSpecialiteEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BaccalaureatSpecialiteJPARepository : JpaRepository<BaccalaureatSpecialiteEntity, String>
