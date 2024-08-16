package fr.gouv.monprojetsup.data.app.infrastructure

import fr.gouv.monprojetsup.data.app.formation.entity.CritereAnalyseCandidatureEntity
import fr.gouv.monprojetsup.data.app.formation.entity.FormationDetailleeEntity
import fr.gouv.monprojetsup.data.app.formation.entity.MoyenneGeneraleAdmisEntity
import fr.gouv.monprojetsup.data.app.formation.entity.TripletAffectationEntity
import fr.gouv.monprojetsup.data.app.metier.entity.MetierEntity
import fr.gouv.monprojetsup.data.app.referentiel.entity.BaccalaureatEntity
import fr.gouv.monprojetsup.data.app.referentiel.entity.BaccalaureatSpecialiteEntity
import fr.gouv.monprojetsup.data.app.referentiel.entity.CategorieDomaineEntity
import fr.gouv.monprojetsup.data.app.referentiel.entity.DomaineEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/*** Formation ****/
@Repository
interface CriteresDb :
    JpaRepository<CritereAnalyseCandidatureEntity, String>

@Repository
interface FormationsDb :
    JpaRepository<FormationDetailleeEntity, String>


@Repository
interface MoyennesGeneralesAdmisDb :
    JpaRepository<MoyenneGeneraleAdmisEntity, String>

@Repository
interface TripletsAffectationDb :
    JpaRepository<TripletAffectationEntity, String>

/** metiers **/

@Repository
interface MetiersDb :
    JpaRepository<MetierEntity, String>


/** referentiel **/

@Repository
interface BaccalaureatDb :
    JpaRepository<BaccalaureatEntity, String>


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

