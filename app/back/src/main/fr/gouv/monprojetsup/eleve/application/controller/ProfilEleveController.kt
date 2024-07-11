package fr.gouv.monprojetsup.eleve.application.controller

import fr.gouv.monprojetsup.commun.application.dto.ProfilDTO
import fr.gouv.monprojetsup.commun.application.dto.ProfilDTO.CommuneDTO
import fr.gouv.monprojetsup.formation.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.formation.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.formation.domain.entity.ChoixNiveau
import okhttp3.ResponseBody
import okhttp3.internal.EMPTY_RESPONSE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/profil")
@RestController
class ProfilEleveController() {
    @PostMapping
    fun postProfilEleve(
        @RequestBody profilDTO: ProfilDTO,
    ): ResponseBody {
        return EMPTY_RESPONSE
    }

    @GetMapping()
    fun getProfilEleve(): ProfilDTO {
        return profilMock
    }

    companion object {
        private val profilMock =
            ProfilDTO(
                id = "monEleveFictif",
                situation = "aucune_idee",
                classe = ChoixNiveau.TERMINALE.jsonValeur,
                baccalaureat = "Générale",
                specialites = listOf("706", "1077"),
                domaines = listOf("T_ITM_PERSO1", "T_ITM_1519"),
                centresInterets = listOf("rechercher", "diriger"),
                metiersFavoris = listOf("MET_372", "MET_373"),
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.INDIFFERENT.jsonValeur,
                alternance = ChoixAlternance.PAS_INTERESSE.jsonValeur,
                communesFavorites =
                    listOf(
                        CommuneDTO(
                            codeInsee = "75015",
                            nom = "Paris",
                            latitude = 2.2885659f,
                            longitude = 48.8512252f,
                        ),
                    ),
                moyenneGenerale = 15.5f,
                formationsFavorites = listOf("fr641", "fl660007"),
            )
    }
}
