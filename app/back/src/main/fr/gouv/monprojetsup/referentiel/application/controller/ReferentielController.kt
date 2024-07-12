package fr.gouv.monprojetsup.referentiel.application.controller

import fr.gouv.monprojetsup.referentiel.application.dto.ReferentielDTO
import fr.gouv.monprojetsup.referentiel.usecase.ReferentielService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/referentiel")
@RestController
class ReferentielController(
    val referentielService: ReferentielService,
) {
    @GetMapping
    fun getReferentielPourInscription(): ReferentielDTO {
        return ReferentielDTO(referentielService.recupererReferentiel())
    }
}
