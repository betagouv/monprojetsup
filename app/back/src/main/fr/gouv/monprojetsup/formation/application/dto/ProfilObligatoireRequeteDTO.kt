package fr.gouv.monprojetsup.formation.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

class ProfilObligatoireRequeteDTO(
    @JsonProperty("profil")
    val profil: ProfilDTO,
)
