package fr.gouv.monprojetsup.formation.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.eleve.application.dto.ProfilDTO

class ProfilOptionnelRequeteDTO(
    @JsonProperty("profil")
    val profil: ProfilDTO?,
)
