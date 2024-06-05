package fr.gouv.monprojetsup.recherche.application.dto

import com.fasterxml.jackson.annotation.JsonProperty

class RecupererFormationRequeteDTO(
    @JsonProperty("profil")
    val profil: ProfilDTO?,
)
