package fr.gouv.monprojetsup.data.referentiel.infrastructure

import fr.gouv.monprojetsup.data.formation.entity.CritereAnalyseCandidatureEntity
import fr.gouv.monprojetsup.data.formation.entity.MoyenneGeneraleAdmisEntity
import fr.gouv.monprojetsup.data.formation.entity.VoeuEntity
import fr.gouv.monprojetsup.data.metier.entity.MetierEntity
import fr.gouv.monprojetsup.data.referentiel.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface BaccalaureatDb :
    JpaRepository<BaccalaureatEntity, String>

@Repository
interface SpecialitesDb :
    JpaRepository<SpecialiteEntity, String>

@Repository
interface BaccalaureatSpecialiteDb :
    JpaRepository<BaccalaureatSpecialiteEntity, String>

@Repository
interface CategorieDomaineDb :
    JpaRepository<CategorieDomaineEntity, String>

@Repository
interface DomainesDb :
    JpaRepository<DomaineEntity, String>

@Repository
interface DomainesCategoryDb :
    JpaRepository<CategorieDomaineEntity, String>

@Repository
interface InteretsDb :
    JpaRepository<InteretEntity, String>

@Repository
interface InteretsCategoryDb :
    JpaRepository<InteretCategorieEntity, String>

@Repository
interface InteretsSousCategorieDb :
    JpaRepository<InteretSousCategorieEntity, String>
