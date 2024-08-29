package fr.gouv.monprojetsup.data.etl

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class MpsDataPortTest(
) {

    @Autowired
    lateinit var mpsDataPort: MpsDataPort

    @Test
    fun `Doit réussir à accéder aux différents ports de données`() {
        mpsDataPort.getMatieres()
        mpsDataPort.getLabels()
        mpsDataPort.getMpsIdToPsupFlIds()
        mpsDataPort.getMoyennesGeneralesAdmis()
        mpsDataPort.getFormationsMpsIds()
        mpsDataPort.getApprentissage()
        mpsDataPort.getLasToGenericIdMapping()
        mpsDataPort.getVoeux()
        mpsDataPort.getDebugLabels()
        mpsDataPort.getCapacitesAccueil()
        mpsDataPort.getFormationsVersMetiers()
        mpsDataPort.getBacs()
        mpsDataPort.getSpecialites()
    }

    @Test
    fun `Doit récupérer des grilles d'analyse non vides`() {
        val grilles = mpsDataPort.getGrilles()
        assert(grilles.isNotEmpty())
    }

    @Test
    fun `Il y a au moins 200 formations MPS`() {
        val formationsIds = mpsDataPort.getFormationsMpsIds()
        assert(formationsIds.size >= 200)
    }


    @Test
    fun `Au moins un lien par formation`() {
        val formationsIds = mpsDataPort.getFormationsMpsIds()
        assert(formationsIds.isNotEmpty())
        val liens = mpsDataPort.getLiens()
        assert(formationsIds.all { liens.containsKey(it) && liens[it]!!.isNotEmpty() })

    }


    @Test
    fun `Au moins un mot clé par formation`() {
        val formationsIds = mpsDataPort.getFormationsMpsIds()
        assert(formationsIds.isNotEmpty())
        val motsCles = mpsDataPort.getMotsCles()
        assert(formationsIds.all { motsCles.containsKey(it) && motsCles[it]!!.isNotEmpty() })

    }

    @Test
    fun `au plus 10 formations n'ont pas de durée`() {
        val formationsIds = mpsDataPort.getFormationsMpsIds()
        assert(formationsIds.isNotEmpty())
        val durees = mpsDataPort.getDurees()
        val nbSansDuree = formationsIds.count { durees[it] == null }
        assert(nbSansDuree < 10)

    }

    @Test
    fun `au plus la moitié des formations n'a pas d'attendus`() {
        val formationsIds = mpsDataPort.getFormationsMpsIds()
        assert(formationsIds.isNotEmpty())
        val attendus = mpsDataPort.getAttendus()
        val nbSansAttendus = formationsIds.count { attendus[it] == null }
        assert(nbSansAttendus < formationsIds.size / 2)
    }

    @Test
    fun `au plus la moitié des formations n'a pas de conseils`() {
        val formationsIds = mpsDataPort.getFormationsMpsIds()
        assert(formationsIds.isNotEmpty())
        val conseils = mpsDataPort.getConseils()
        val nbSansAttendus = formationsIds.count { conseils[it] == null }
        assert(nbSansAttendus < formationsIds.size / 2)
    }

    @Test
    fun `moins de 200 voeux n'ont pas de coordonnées gps`() {
        val voeux = mpsDataPort.getVoeux()
        assert(voeux.isNotEmpty())
        val nbSansCoordonnees = voeux.count { it.lat == null || it.lng == null }
        assert(nbSansCoordonnees < 200)
    }

    @Test
    fun `tous les bacs de toutes les stats sont connus`() {
        val keys = mpsDataPort.getBacs().map { b -> b.key}
        val statsKeys = mpsDataPort.getStatsFormation().flatMap{ it.value.nbAdmisParBac.keys }
        assert(keys.containsAll(statsKeys))
    }

}