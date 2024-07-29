package fr.gouv.monprojetsup.eleve.application.controller

import fr.gouv.monprojetsup.authentification.application.controller.AuthentifieController
import fr.gouv.monprojetsup.eleve.application.dto.ProfilDTO
import fr.gouv.monprojetsup.eleve.usecase.MiseAJourEleveService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/profil")
@RestController
class ProfilEleveController(
    private val miseAJourEleveService: MiseAJourEleveService,
) : AuthentifieController() {
    @PostMapping
    fun postProfilEleve(
        @RequestBody profilDTO: ProfilDTO,
    ): ResponseEntity<Unit> {
        val eleve = recupererEleve()
        miseAJourEleveService.mettreAJourUnProfilEleve(miseAJourDuProfil = profilDTO.toProfil(eleve.id), profilActuel = eleve)
        return ResponseEntity<Unit>(HttpStatus.NO_CONTENT)
    }

    @GetMapping
    fun getProfilEleve(): ProfilDTO {
        return ProfilDTO(recupererEleve())
    }
}
