package fr.gouv.monprojetsup.suggestions.algo

import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

class AffinityEvaluatorTest {

    @Test
    fun `Doit donner un score maximum à une formation proche`() {
        val pf = ProfileDTO(
            "term",
            "générale",
            "court",
            "",
            setOf("44000"),
            setOf(),
            listOf(),
            null,
            listOf(),
            "0"
        )
        /*
        val affinityEvaluator = AffinityEvaluator(pf, Config(), algo)
        val score = affinityEvaluator.getBonusGeographicAffinity(TestData.PARIS_CODE_COMMUNE_INSEE, null)
        assertThat(score).isEqualTo(1.0)*/
    }


}