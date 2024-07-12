package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.referentiel.infrastructure.entity.CategorieDomaineEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CategorieDomaineJPARepository : JpaRepository<CategorieDomaineEntity, String>
