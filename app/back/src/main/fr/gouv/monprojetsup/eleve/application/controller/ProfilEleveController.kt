package fr.gouv.monprojetsup.eleve.application.controller

import fr.gouv.monprojetsup.authentification.application.controller.AuthentifieController
import fr.gouv.monprojetsup.eleve.application.dto.AjoutCompteParcoursupDTO
import fr.gouv.monprojetsup.eleve.application.dto.IndicateursDTO
import fr.gouv.monprojetsup.eleve.application.dto.ModificationProfilDTO
import fr.gouv.monprojetsup.eleve.application.dto.ProfilDTO
import fr.gouv.monprojetsup.eleve.usecase.MiseAJourEleveService
import fr.gouv.monprojetsup.eleve.usecase.MiseAJourIdParcoursupService
import fr.gouv.monprojetsup.eleve.usecase.RecupererAssociationFormationsVoeuxService
import fr.gouv.monprojetsup.eleve.usecase.RecupererIndicateursService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/auth/profil")
@RestController
@Tag(name = "Profil Élève", description = "API des profils des utilisateurs MonProjetSup")
class ProfilEleveController(
    private val miseAJourEleveService: MiseAJourEleveService,
    private val recupererAssociationFormationsVoeuxService: RecupererAssociationFormationsVoeuxService,
    private val miseAJourIdParcoursupService: MiseAJourIdParcoursupService,
    private val recupererIndicateursService: RecupererIndicateursService,
) : AuthentifieController() {
    @PostMapping
    @Operation(
        summary = "Modifier le profil de l'utilisateur connecté",
        description = " Mise à jour d'un profil en totalité ou partiellement (ex: mettre à jour la classe)",
    )
    fun postProfilEleve(
        @RequestBody modificationProfilDTO: ModificationProfilDTO,
    ): ProfilDTO {
        val eleve = recupererEleve()
        val profilEleve =
            miseAJourEleveService.mettreAJourUnProfilEleve(
                miseAJourDuProfil = modificationProfilDTO.toModificationProfilEleve(),
                profilActuel = eleve,
            )
        return ProfilDTO(profilEleve)
    }

    @GetMapping
    @Operation(
        summary = "Récupérer le profil de l'utilisateur connecté",
        description = "Récupère le profil de l'utilisateur connecté tout en récupérant ses favoris Parcoursup",
    )
    fun getProfilEleve(): ProfilDTO {
        val profil = recupererEleveAvecProfilExistant()
        val voeuxFavoris = recupererAssociationFormationsVoeuxService.recupererVoeuxFavoris(profil)
        return ProfilDTO(profil, voeuxFavoris)
    }

    @PostMapping("/parcoursup")
    @Operation(
        summary = "Lier à un compte Parcoursup",
        description = "Lie le compte MPS à un compte Parcoursup pour récupération automatique des favoris Parcoursup",
    )
    fun postCompteParcoursup(
        @RequestBody ajoutCompteParcoursup: AjoutCompteParcoursupDTO,
    ): ResponseEntity<Unit> {
        miseAJourIdParcoursupService.mettreAJourIdParcoursup(
            profil = recupererEleveAvecProfilExistant(),
            parametresPourRecupererToken = ajoutCompteParcoursup.toParametresPourRecupererToken(),
        )
        return ResponseEntity<Unit>(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/progression")
    @Operation(
        summary = "Déprécié",
    )
    fun getProgression(): Int {
        return 6
    }

    @GetMapping("/indicateurs")
    @Operation(
        summary = "Récupérer les indicateurs MPS",
        description = "Récupère les trois indicateurs MPS",
    )
    fun getIndicateursMPS(): IndicateursDTO {
        val profil = recupererEleveAvecProfilExistant()
        val indicateurs =
            recupererIndicateursService.recupererIndicateurs(
                profil,
            )
        return IndicateursDTO(
            formationsRealistes = indicateurs.formationsRealistes,
            formationsAmbitieuses = indicateurs.formationsAmbitieuses,
            formationsPlanB = indicateurs.formationsPlanB,
        )
    }
}
