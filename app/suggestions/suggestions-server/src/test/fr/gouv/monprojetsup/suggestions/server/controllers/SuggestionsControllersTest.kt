package fr.gouv.monprojetsup.suggestions.server.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import fr.gouv.monprojetsup.data.Constants.isFiliere
import fr.gouv.monprojetsup.suggestions.dto.GetAffinitiesServiceDTO
import fr.gouv.monprojetsup.suggestions.dto.ProfileDTO
import org.apache.commons.lang3.tuple.ImmutablePair
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.core.io.Resource
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.lang.reflect.Type

class ImmutablePairDeserializer : JsonDeserializer<ImmutablePair<String, ProfileDTO?>> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ImmutablePair<String, ProfileDTO?> {
        val jsonObject = json?.asJsonObject ?: throw JsonParseException("Invalid JSON for ImmutablePair")

        val key = jsonObject.get("left").asString // Assuming JSON has "left" for the key
        val value = context?.deserialize<ProfileDTO>(jsonObject.get("right"), ProfileDTO::class.java) // Deserialize ProfileDTO (can be null)

        return ImmutablePair(key, value)
    }
}

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SuggestionsControllersTest(
    @Autowired val mvc: MockMvc,
    @Autowired val objectMapper: ObjectMapper,
) {

    private val logger = LoggerFactory.getLogger(SuggestionsControllersTest::class.java)

    @Value("\${profilsReferencePath}")
    lateinit var resourceFile: Resource

    @Nested
    inner class `Quand on appelle la route des suggestions` {


        @Test
        fun `les suggestions sont conformes aux profils de référence`() {
            // given
            //load fichier de proils de référence
            val type = object : TypeToken<List<ImmutablePair<String, ProfileDTO>>>() {}.type
            resourceFile.file.bufferedReader().use { reader ->
                val gsonBuilder = GsonBuilder()
                gsonBuilder.registerTypeAdapter(ImmutablePair::class.java, ImmutablePairDeserializer())
                val profils = gsonBuilder.create().fromJson<List<ImmutablePair<String, ProfileDTO>>>(reader, type)
                profils.filter { it.right != null && it.right!!.suggApproved().isNotEmpty() }.forEach { pair ->
                    logger.info("Test du profil ${pair.left}")
                    val profil = pair.right!!
                    val obligatoires =
                        profil.suggApproved().filter { isFiliere(it.id) && it.score != null && it.score!! >= 5 }.map { s -> s.id }
                            .toSet()
                    val recommandees =
                        profil.suggApproved().filter { isFiliere(it.id) && it.score != null && it.score!! >= 3 }.map { s -> s.id }
                            .toSet()
                    val deconseillees = profil.suggRejected().filter { isFiliere(it.id) }.map { s -> s.id }.toSet()

                    profil.choix.removeIf { isFiliere(it.id) }
                    val resultat = getSuggestions(profil)
                    val suggestions = resultat.affinites.filter { it.affinite > 0 }.map { it.key }.toSet()
                    val premieresSuggestions =
                        resultat.affinites.sortedBy { -it.affinite }.take(20).map { it.key }.toSet()

                    //pour chacun on veut voir les 5 dans les premiers et les autres pas trop loin et on ne veut pas voir la corbeille
                    if(obligatoires.isNotEmpty()) {
                        assertThat(suggestions).containsAll(obligatoires)
                        assertThat(premieresSuggestions).containsAll(obligatoires)
                    }
                    if(recommandees.isNotEmpty()) {
                        assertThat(suggestions).containsAll(recommandees)
                    }
                    if(deconseillees.isNotEmpty()) {
                        assertThat(suggestions).doesNotContainAnyElementsOf(deconseillees)
                    }
                }
            }
        }
    }

    private fun getSuggestions(profil: ProfileDTO): GetAffinitiesServiceDTO.Response {
        val requete = Gson().toJson(mapOf("profile" to profil))
        val resultat = mvc.perform(
            post(ENDPOINT_SUGGESTION).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requete),
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
        return objectMapper.readValue(resultat.response.contentAsString, GetAffinitiesServiceDTO.Response::class.java)

    }


    @Nested
    inner class `Quand on appelle la route des explanations` {
        // TODO
        // définir un contexte pour chaque type de suggestions dans la quelle l'explanation est attendue
    }

    @Nested
    inner class `Quand on appelle la route des formations of interest` {
        // TODO
        // définir un contexte de préférences géographiques qui génère une foi de référence
        // exemple préférence Bordeaux et foi est la licence informatique de bordeaux
    }

    companion object {
        private const val ENDPOINT_SUGGESTION = "/api/1.2/suggestions"
    }
}