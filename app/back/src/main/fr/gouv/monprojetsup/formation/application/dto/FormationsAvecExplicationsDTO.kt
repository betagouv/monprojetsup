package fr.gouv.monprojetsup.formation.application.dto

import fr.gouv.monprojetsup.commun.hateoas.application.dto.PaginationDTO

data class FormationsAvecExplicationsDTO(
    val formations: List<FormationAvecExplicationsDTO>,
) : PaginationDTO()
