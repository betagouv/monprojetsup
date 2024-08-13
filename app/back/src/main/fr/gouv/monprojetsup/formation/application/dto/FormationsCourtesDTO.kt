package fr.gouv.monprojetsup.formation.application.dto

import fr.gouv.monprojetsup.commun.hateoas.application.dto.PaginationDTO

data class FormationsCourtesDTO(
    val formations: List<FormationCourteDTO>,
) : PaginationDTO()
