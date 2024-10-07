package fr.gouv.monprojetsup.eleve.application.controller

import fr.gouv.monprojetsup.authentification.application.controller.AuthentifieController
import fr.gouv.monprojetsup.eleve.application.dto.AjoutCompteParcoursupDTO
import fr.gouv.monprojetsup.eleve.application.dto.ModificationProfilDTO
import fr.gouv.monprojetsup.eleve.usecase.MiseAJourEleveService
import fr.gouv.monprojetsup.eleve.usecase.MiseAJourFavorisParcoursupService
import fr.gouv.monprojetsup.eleve.usecase.MiseAJourIdParcoursupService
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
    private val miseAJourFavorisParcoursupService: MiseAJourFavorisParcoursupService,
    private val miseAJourIdParcoursupService: MiseAJourIdParcoursupService,
) : AuthentifieController() {
    @PostMapping
    fun postProfilEleve(
        @RequestBody modificationProfilDTO: ModificationProfilDTO,
    ): ResponseEntity<Unit> {
        val eleve = recupererEleve()
        miseAJourEleveService.mettreAJourUnProfilEleve(
            miseAJourDuProfil = modificationProfilDTO.toModificationProfilEleve(),
            profilActuel = eleve,
        )
        return ResponseEntity<Unit>(HttpStatus.NO_CONTENT)
    }

    @GetMapping
    fun getProfilEleve(): ModificationProfilDTO {
        val profil = recupererEleveIdentifie()
        val profilMisAJour = miseAJourFavorisParcoursupService.mettreAJourFavorisParcoursup(profil)
        return ModificationProfilDTO(profilMisAJour)
    }

    @PostMapping("/parcoursup")
    fun postCompteParcoursup(
        @RequestBody ajoutCompteParcoursup: AjoutCompteParcoursupDTO,
    ): ResponseEntity<Unit> {
        val profil = recupererEleveIdentifie()
        miseAJourIdParcoursupService.mettreAJourIdParcoursup(profil, ajoutCompteParcoursup.jwtParcoursup)
        return ResponseEntity<Unit>(HttpStatus.NO_CONTENT)
    }
}
