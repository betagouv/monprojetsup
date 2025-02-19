package fr.gouv.monprojetsup.formation.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.eleve.application.dto.ProfilDTO
import io.swagger.v3.oas.annotations.media.Schema

data class GetFichesFormationsDTO(
    @Schema(description = "ids des formations", example = "[\"fl1\",\"fl490030\"]", required = true)
    @JsonProperty("ids")
    val ids: List<String>,

    @Schema(description = "Profil de l'élève", required = false)
    @JsonProperty("profil")
    val profil: ProfilDTO?,

    @Schema(description = "Numéro de page", required = false, defaultValue = "1")
    @JsonProperty("numeroDePage")
    val numeroDePage: Int
)
