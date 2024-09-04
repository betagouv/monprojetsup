package fr.gouv.monprojetsup.data.etl

import fr.gouv.monprojetsup.data.domain.model.*
import fr.gouv.monprojetsup.data.domain.model.attendus.GrilleAnalyse
import fr.gouv.monprojetsup.data.domain.model.bacs.Bac
import fr.gouv.monprojetsup.data.domain.model.descriptifs.DescriptifsFormationsMetiers
import fr.gouv.monprojetsup.data.domain.model.interets.Interets
import fr.gouv.monprojetsup.data.domain.model.specialites.Specialites
import fr.gouv.monprojetsup.data.domain.model.thematiques.CategorieThematiques
import fr.gouv.monprojetsup.data.formation.entity.MoyenneGeneraleAdmisId


interface MpsDataPort {

    fun getFormationsMpsIds(): List<String>
    fun getMetiersMpsIds(): List<String>
    fun getLabels(): Map<String, String>
    fun getDescriptifs(): DescriptifsFormationsMetiers
    fun getSpecialites(): Specialites
    fun getAttendus(): Map<String, String>
    fun getLiens(): Map<String, List<DescriptifsFormationsMetiers.Link>>
    fun getGrilles(): Map<String, GrilleAnalyse>
    fun getMotsClesFormations(): Map<String, List<String>>
    fun getApprentissage(): Collection<String>
    fun getLasToGenericIdMapping(): Map<String, String>
    fun getVoeux(): List<Voeu>
    fun getDebugLabels(): Map<String, String>
    fun getCapacitesAccueil(): Map<String, Int>
    fun getFormationsVersMetiersEtMetiersAssocies(): Map<String, Set<String>>
    fun getStatsFormation(): Map<String, StatsFormation>
    fun getMpsIdToPsupFlIds(): Map<String, Collection<String>>
    fun getDurees(): Map<String, Int?>
    fun getMoyennesGeneralesAdmis(): Map<MoyenneGeneraleAdmisId, List<Int>>
    fun getPsupIdToMpsId(): Map<String, String>
    fun getVoeuxParCandidat(): List<Candidat>
    fun getLasToPasIdMapping(): Map<String, String>
    fun getEdges(): List<Triple<String,String,Int>>
    fun getMatieres(): List<Matiere>
    fun getBacs(): List<Bac>
    fun getDomaines(): List<CategorieThematiques>
    fun getInterets(): Interets
    fun getConseils(): Map<String, String>
    fun getCities(): List<Ville>
    fun getFormationsLabels(): Map<String, String>
    fun getMetiersLabels(): Map<String, String>

    fun getMetiersAssociesLabels(): Map<String, List<String>>
    fun getMpsIdToIdeoIds(): Map<String, List<String>>

    fun exportDiagnostic()
}