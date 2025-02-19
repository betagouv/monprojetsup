package fr.gouv.monprojetsup.formation.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.eleve.application.dto.ProfilDTO
import io.swagger.v3.oas.annotations.media.Schema

data class RechercheFormationsDTO(
    @Schema(description = "Termes de recherche", required = true)
    @JsonProperty("recherche")
    val recherche: String,

    @Schema(description = "Profil de l'élève", required = false)
    @JsonProperty("profil")
    val profil: ProfilDTO?,

    @Schema(description = "Numéro de page", required = false, defaultValue = "1")
    @JsonProperty("numeroDePage")
    val numeroDePage: Int
)
