package fr.gouv.monprojetsup.data.etl.sources

import fr.gouv.monprojetsup.data.formation.entity.MoyenneGeneraleAdmisId
import fr.gouv.monprojetsup.data.domain.model.Matiere
import fr.gouv.monprojetsup.data.domain.model.StatsFormation
import fr.gouv.monprojetsup.data.domain.model.Voeu
import fr.gouv.monprojetsup.data.domain.model.attendus.GrilleAnalyse
import fr.gouv.monprojetsup.data.domain.model.bacs.Bac
import fr.gouv.monprojetsup.data.domain.model.descriptifs.DescriptifsFormationsMetiers
import fr.gouv.monprojetsup.data.domain.model.interets.Interets
import fr.gouv.monprojetsup.data.domain.model.specialites.Specialites
import fr.gouv.monprojetsup.data.domain.model.thematiques.CategorieThematiques

interface MpsDataPort {
    fun getLabels(): Map<String, String>
    fun getDescriptifs(): DescriptifsFormationsMetiers
    fun getSpecialites(): Specialites
    fun getAttendus(): Map<String, String>
    fun getLiens(): Map<String, List<DescriptifsFormationsMetiers.Link>>
    fun getGrilles(): Map<String, GrilleAnalyse>
    fun getMotsCles(): Map<String, List<String>>
    fun getFormationsMpsIds(): List<String>
    fun getApprentissage(): Collection<String>
    fun getLasToGenericIdMapping(): Map<String, String>
    fun getVoeux(): List<Voeu>
    fun getDebugLabels(): Map<String, String>
    fun getCapacitesAccueil(): Map<String, Int>
    fun getFormationsVersMetiers(): Map<String, Set<String>>
    fun getStatsFormation(): Map<String, StatsFormation>
    fun getMpsIdToPsupFlIds(): Map<String, Collection<String>>
    fun getDurees(): Map<String, Int?>
    fun getMoyennesGeneralesAdmis(): Map<MoyenneGeneraleAdmisId, List<Int>>
    fun getPsupIdToMpsId(): Map<String, String>
    fun getVoeuxParCandidat(): List<Set<Int>>
    fun getLasToPasIdMapping(): Map<String, String>
    fun getEdges(): List<Triple<String,String,Int>>
    fun getMatieres(): List<Matiere>
    fun getBacs(): List<Bac>
    fun getThematiques(): List<CategorieThematiques>
    fun getInterets(): Interets
    fun getConseils(): Map<String, String>
}