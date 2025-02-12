package fr.gouv.monprojetsup.eleve.application.controller

import fr.gouv.monprojetsup.authentification.application.controller.AuthentifieController
import fr.gouv.monprojetsup.eleve.application.dto.TraceDTO
import fr.gouv.monprojetsup.eleve.usecase.AjoutTraceService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/trace")
@RestController
@Tag(name = "Trace Élève", description = "API de trace de navigation des utilisateurs MonProjetSup")
class TraceEleveController(
    private val ajoutTraceService: AjoutTraceService,
) : AuthentifieController() {
    @PostMapping
    @Operation(
        summary = "Ajoute une trace de navigation",
    )
    fun ajoutTrace(
        @RequestBody trace: TraceDTO,
    ): ResponseEntity<Unit> {
        val eleve = recupererEleve()
        ajoutTraceService.ajouterTrace(
            idEleve = eleve.id,
            trace = trace.toTrace(),
        )
        return ResponseEntity<Unit>(HttpStatus.NO_CONTENT)
    }
}
