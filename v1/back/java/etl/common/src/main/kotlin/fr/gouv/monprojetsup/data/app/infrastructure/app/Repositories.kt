package fr.gouv.monprojetsup.data.app.infrastructure.app

import fr.gouv.monprojetsup.data.app.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BaccalaureatDb :
    JpaRepository<BaccalaureatEntity, String>

@Repository
interface CriteresDb :
    JpaRepository<CritereAnalyseCandidatureEntity, String>

@Repository
interface DomainesDb :
    JpaRepository<DomaineEntity, String>

@Repository
interface DomainesCategoryDb :
    JpaRepository<DomaineCategoryEntity, String>

@Repository
interface FormationsDb :
    JpaRepository<FormationDetailleeEntity, String>
