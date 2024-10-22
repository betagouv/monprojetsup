package fr.gouv.monprojetsup.eleve.application.controller

import fr.gouv.monprojetsup.authentification.application.controller.AuthentifieController
import fr.gouv.monprojetsup.eleve.application.dto.ModificationProfilDTO
import fr.gouv.monprojetsup.eleve.usecase.MiseAJourEleveService
import fr.gouv.monprojetsup.eleve.usecase.MiseAJourFavorisParcoursupService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/profil")
@RestController
@Tag(name = "Profil Élève", description = "API des profils des utilisateurs MonProjetSup")
class ProfilEleveController(
    private val miseAJourEleveService: MiseAJourEleveService,
    private val miseAJourFavorisParcoursupService: MiseAJourFavorisParcoursupService,
) : AuthentifieController() {
    @PostMapping
    @Operation(
        summary = "Modifier le profil de l'utilisateur connecté",
        description = " Mise à jour d'un profil en totalité ou partiellement (ex: mettre à jour la classe)",
    )
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
    @Operation(
        summary = "Récupérer le profil de l'utilisateur connecté",
        description = "Récupère le profil de l'utilisateur connecté tout en récupérant ses favoris Parcoursup",
    )
    fun getProfilEleve(): ModificationProfilDTO {
        val profil = recupererEleveIdentifie()
        val profilMisAJour = miseAJourFavorisParcoursupService.mettreAJourFavorisParcoursup(profil)
        return ModificationProfilDTO(profilMisAJour)
    }
}
