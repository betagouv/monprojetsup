package fr.gouv.monprojetsup.data.etl.port

import fr.gouv.monprojetsup.data.domain.Constants
import fr.gouv.monprojetsup.data.etl.MpsDataPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class FormationsMpsTests() {

    @Autowired
    lateinit var mpsDataPort: MpsDataPort

    @Test
    fun `90 pourcent des formations ont au moins un métier associé`() {
        val formationKeys = mpsDataPort.getFormationsMpsIds()
        val formationsAvecAuMoinsUnMetier =
            mpsDataPort.getFormationsVersMetiersEtMetiersAssocies().filter { it.value.isNotEmpty() }.keys
        assertThat(formationKeys).isNotEmpty()
        val pctOk = 100 * formationsAvecAuMoinsUnMetier.size / formationKeys.size
        assertThat(pctOk).isGreaterThan(90)
    }

    @Test
    fun `70 pourcent des métiers ont au moins une formation associée`() {
        val metiersIdsRef = mpsDataPort.getMetiersMpsIds()
        assertThat(metiersIdsRef).isNotEmpty()
        val metiersAvecauMoinsuneformation  = mpsDataPort.getFormationsVersMetiersEtMetiersAssocies().flatMap { (_,b) -> b }.toSet()
        val pctOk = 100 * metiersAvecauMoinsuneformation.size / metiersIdsRef.size
        assertThat(pctOk).isGreaterThan(70)
    }

    @Test
    fun `CUPGE - Droit-économie-gestion hérite de tous les métiers des écoles de commerce`() {
        //c'est le mapping CPGE - licence qui crée les assciations métiers
        val mpsIds = mpsDataPort.getFormationsMpsIds()
        val cupgeEcoGestionMpsId = Constants.gFrCodToMpsId(Constants.CUPGE_ECO_GESTION_PSUP_FR_COD)
        assertThat(mpsIds).contains(cupgeEcoGestionMpsId)

        val ecoleCommerceMpsId = Constants.gFrCodToMpsId(Constants.ECOLE_COMMERCE_PSUP_FR_COD)
        assertThat(mpsIds).contains(ecoleCommerceMpsId)

        val formationsVersMetiers = mpsDataPort.getFormationsVersMetiersEtMetiersAssocies()

        val metiersEcoleCommerce = formationsVersMetiers[ecoleCommerceMpsId]
        assertThat(metiersEcoleCommerce).isNotNull()
        assertThat(metiersEcoleCommerce).isNotEmpty()

        val metiersCupge = formationsVersMetiers[cupgeEcoGestionMpsId]
        assertThat(metiersCupge).isNotNull()

        assertThat(metiersCupge).containsAll(metiersEcoleCommerce)

    }

    @Test
    fun `CUPGE - Sciences, technologie, santé ne contient pas le mot-clé commerce international `() {
        val mpsIds = mpsDataPort.getFormationsMpsIds()
        val cupgeSciencesTechnoSanteMpsId = Constants.gFrCodToMpsId(Constants.CUPGE_ECO_SCIENCES_TECHNO_SANTE_PSUP_FR_COD)
        assertThat(mpsIds).contains(cupgeSciencesTechnoSanteMpsId)
        assertThat(mpsDataPort.getMotsClesFormations()[cupgeSciencesTechnoSanteMpsId]).doesNotContain("commerce international")
    }

}