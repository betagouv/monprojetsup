package fr.gouv.monprojetsup.metier.application.dto

import fr.gouv.monprojetsup.commun.hateoas.application.dto.PaginationDTO

data class MetiersAvecSesFormationsDTO(
    val metiers: List<MetierAvecSesFormationsDTO>,
) : PaginationDTO()
