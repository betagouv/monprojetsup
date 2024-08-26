package fr.gouv.monprojetsup.data.etl.formation

import fr.gouv.monprojetsup.data.app.infrastructure.*
import fr.gouv.monprojetsup.data.etl.BDDRepositoryTest
import fr.gouv.monprojetsup.data.etl.referentiel.UpdateReferentielDbs
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class UpdateReferentielsDbsTest : BDDRepositoryTest() {
    @Autowired
    lateinit var  baccalaureatBd: BaccalaureatDb
    @Autowired
    lateinit var specialitesDb: SpecialitesDb
    @Autowired
    lateinit var bacSpecDb: BaccalaureatSpecialiteDb
    @Autowired
    lateinit var domainesDb: DomainesDb
    @Autowired
    lateinit var domainesCategoriesDb: DomainesCategoryDb
    @Autowired
    lateinit var interetsDb: InteretsDb
    @Autowired
    lateinit var interetsCategorieDb: InteretsCategoryDb
    @Autowired
    lateinit var interetsSousCategorieDb: InteretsSousCategorieDb
    @Autowired
    lateinit var updateReferentielDbs: UpdateReferentielDbs

    @Nested
    inner class UpdateFormationsDbTest {

        @Test
        @Sql
        fun `Doit réussir à mettre à jour le referentiels`() {
            // When
            // Then
            assertDoesNotThrow { updateReferentielDbs.updateReferentielDbs() }
        }
    }


}
