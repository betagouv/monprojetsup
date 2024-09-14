package fr.gouv.monprojetsup.data.etl.suggestions

import fr.gouv.monprojetsup.data.TestData
import fr.gouv.monprojetsup.data.etl.db.BDDRepositoryTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired

@Nested
class UpdateSuggestionsTest : BDDRepositoryTest() {

    @Autowired
    lateinit var updateSuggestionsDbs: UpdateSuggestionsDbs

    @Autowired
    lateinit var villesDb: SuggestionsVillesDb

    @Autowired
    lateinit var candidatsDb: SuggestionsCandidatsDb

    @Autowired
    lateinit var edgesDb: SuggestionsEdgesDb

    @Test
    fun `Doit réussir à mettre à jour les tables sugg_`() {
        assertDoesNotThrow { updateSuggestionsDbs.updateSuggestionDbs() }
    }

    @Test
    fun `La table des villes doit inclure Soulac-sur-Mer, à la fois sous son nom et sous son code INSEE`() {
        updateSuggestionsDbs.updateVillesDb()
        val ville1 = villesDb.findById("33514").map { it.toVille() }
        val ville2 = villesDb.findById("Soulac-sur-Mer").map { it.toVille() }
        assertThat(ville1).isPresent
        assertThat(ville2).isPresent
        assertThat(ville1.get()).isEqualTo(ville2.get())
    }

    @Test
    fun `La table des candidats doit être non vide`() {
        updateSuggestionsDbs.updateCandidatsDb()
        val items = candidatsDb.findAll()
        assertThat(items).isNotEmpty
    }

    @Test
    fun `La table des edges doit contenir suffisamment d arètes`() {
        updateSuggestionsDbs.updateEdgesDb()
        assertThat(edgesDb.count()).isGreaterThanOrEqualTo(TestData.MIN_NB_ARETES_SUGGESTIONS_GRAPH)
    }

}