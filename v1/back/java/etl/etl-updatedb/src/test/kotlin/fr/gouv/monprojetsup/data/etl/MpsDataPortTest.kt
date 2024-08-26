package fr.gouv.monprojetsup.data.etl

import fr.gouv.monprojetsup.data.MpsDataPort
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

}