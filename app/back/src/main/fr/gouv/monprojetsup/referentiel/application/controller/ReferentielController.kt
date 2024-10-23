package fr.gouv.monprojetsup.referentiel.application.controller

import fr.gouv.monprojetsup.referentiel.application.dto.ReferentielDTO
import fr.gouv.monprojetsup.referentiel.usecase.ReferentielService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/referentiel")
@RestController
@Tag(name = "Référentiel", description = "API des données référentielles de MonProjetSup")
class ReferentielController(
    val referentielService: ReferentielService,
) {
    @GetMapping
    @Operation(
        summary = "Récupérer les données nécessaires au parcours d'inscription de MonProjetSup",
        description =
            "Contient les choix des écrans, les baccalauréats et leurs spécialités associées, les statistiques des admis" +
                " Parcoursup, les interêts et domaines.",
    )
    fun getReferentielPourInscription(): ReferentielDTO {
        return ReferentielDTO(referentielService.recupererReferentiel())
    }
}
