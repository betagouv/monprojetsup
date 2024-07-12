package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.infrastructure.entity.InteretCategorieEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InteretCategorieJPARepository : JpaRepository<InteretCategorieEntity, String>
