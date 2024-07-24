package fr.gouv.monprojetsup.authentification.filter

import fr.gouv.monprojetsup.commun.ConnecteAvecUnEleve
import fr.gouv.monprojetsup.commun.ConnecteAvecUnEnseignant
import fr.gouv.monprojetsup.commun.ConnecteSansId
import fr.gouv.monprojetsup.commun.application.controller.ControllerTest
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import okhttp3.ResponseBody
import okhttp3.internal.EMPTY_RESPONSE
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.only
import org.mockito.BDDMockito.then
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("test")
@RestController
class IdentificationMockController {
    @GetMapping
    fun getTest(): ResponseBody {
        return EMPTY_RESPONSE
    }
}

@WebMvcTest(controllers = [IdentificationMockController::class])
class IdentificationFilterTest(
    @Autowired val mvc: MockMvc,
) : ControllerTest() {
    @ConnecteAvecUnEleve("adcf627c-36dd-4df5-897b-159443a6d49c")
    @Test
    fun `si connecté avec un élève, doit retourner 204 avec body vide`() {
        // When & Then
        mvc.perform(
            get("/test").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON),
        ).andDo(MockMvcResultHandlers.print()).andExpect(status().isOk).andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(
                content().json(
                    """
                    {}
                    """.trimIndent(),
                ),
            )
    }

    @ConnecteAvecUnEleve(idEleve = "idEleve")
    @Test
    fun `si connecté avec un élève et le reconnait, doit appeler le repo pour récupérer l'élève`() {
        // When & Then
        mvc.perform(get("/test").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))

        // Then
        then(eleveRepository).should(only()).recupererUnEleve(id = "idEleve")
    }

    @ConnecteAvecUnEleve(idEleve = "idEleve")
    @Test
    fun `si connecté avec un élève mais ne le reconnait pas, doit appeler le repo pour le créer`() {
        // Given
        val exception = MonProjetSupNotFoundException(code = "ELEVE_SANS_COMPTE", msg = "L'élève n'a pas de compte")
        given(eleveRepository.recupererUnEleve("idEleve")).willThrow(exception)

        // When & Then
        mvc.perform(get("/test").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))

        // Then
        then(eleveRepository).should().creerUnEleve(id = "idEleve")
    }

    @ConnecteAvecUnEnseignant(idEnseignant = "idEnseignant")
    @Test
    fun `si connecté avec un professeur, doit retourner 403 avec body vide`() {
        // When & Then
        mvc.perform(
            get("/test").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON),
        ).andDo(MockMvcResultHandlers.print()).andExpect(status().isForbidden)
    }

    @ConnecteSansId
    @Test
    fun `si connecté sans id dans le JWT, doit retourner 403 avec body vide`() {
        // When & Then
        mvc.perform(
            get("/test").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON),
        ).andDo(MockMvcResultHandlers.print()).andExpect(status().isForbidden)
    }

    @Test
    fun `si pas connecté, doit retourner 401 avec body vide`() {
        // When & Then
        mvc.perform(
            get("/test").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON),
        ).andDo(MockMvcResultHandlers.print()).andExpect(status().isUnauthorized)
    }
}
