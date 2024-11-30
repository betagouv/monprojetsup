package fr.gouv.monprojetsup.eleve.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.CommuneFavorite
import fr.gouv.monprojetsup.eleve.domain.entity.FormationFavorite
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFavori
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

data class ProfilDTO(
    @Schema(
        description = "Etat d'avancée du projet de l'élève",
        example = "aucune_idee",
        allowableValues = ["aucune_idee", "quelques_pistes", "projet_precis"],
    )
    @JsonProperty("situation")
    val situation: SituationAvanceeProjetSup? = null,
    @Schema(description = "Classe actuelle", example = "terminale", allowableValues = ["seconde", "premiere", "terminale"])
    @JsonProperty("classe")
    val classe: ChoixNiveau? = null,
    @Schema(
        description = "Type de Bac choisi ou envisagé",
        example = "Générale",
        allowableValues = ["NC", "Générale", "P", "PA", "S2TMD", "ST2S", "STAV", "STD2A", "STHR", "STI2D", "STL", "STMG"],
    )
    @JsonProperty("baccalaureat")
    val baccalaureat: String? = null,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Enseignements de spécialité de terminale choisis ou envisagés",
                example = "[\"mat707\",\"mat700\"]",
            ),
    )
    @JsonProperty("specialites")
    val specialites: List<String>? = null,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Domaines d'activité",
                example = "[\"dom41\",\"dom32\",\"dom26\"]",
            ),
    )
    @JsonProperty("domaines")
    val domaines: List<String>? = null,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Centres d'intérêt",
                example =
                    "[\"ci16\", \"ci27\", \"ci6\", \"ci11\"]",
            ),
    )
    @JsonProperty("centresInterets")
    val centresInterets: List<String>? = null,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Les idées de métiers de l'élève",
                example = "[\"MET_384\", \"MET_469\"]",
            ),
    )
    @JsonProperty("metiersFavoris")
    val metiersFavoris: List<String>? = null,
    @Schema(
        description = "Durée envisagée des études",
        example = "indifferent",
        allowableValues = ["indifferent", "courte", "longue", "aucune_idee"],
    )
    @JsonProperty("dureeEtudesPrevue")
    val dureeEtudesPrevue: ChoixDureeEtudesPrevue? = null,
    @Schema(
        description = "Intérêt pour les formations en apprentissage",
        example = "pas_interesse",
        allowableValues = ["pas_interesse", "indifferent", "interesse", "tres_interesse"],
    )
    @JsonProperty("alternance")
    val alternance: ChoixAlternance? = null,
    @ArraySchema(arraySchema = Schema(description = "Villes préférées pour étudier"))
    @JsonProperty("communesFavorites")
    val communesFavorites: List<CommuneDTO>? = null,
    @Schema(description = "Moyenne générale scolaire estimée en terminale", example = "14")
    @JsonProperty("moyenneGenerale")
    val moyenneGenerale: Float? = null,
    @ArraySchema(arraySchema = Schema(description = "Les idées de formations de l'élève"))
    @JsonProperty("formationsFavorites")
    val formationsFavorites: List<FormationFavoriteDTO>? = null,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Les formations mises à la corbeille par l'élève",
                example = "[\"fl1\", \"fl810505\"]",
            ),
    )
    @JsonProperty("corbeilleFormations")
    val corbeilleFormations: List<String>? = null,
    @Schema(description = "Prénom de l'élève", example = "Kévin")
    @JsonProperty("compteParcoursupAssocie")
    val compteParcoursupAssocie: Boolean,
    @ArraySchema(arraySchema = Schema(description = "Liste des voeux favoris"))
    @JsonProperty("voeuxFavoris")
    val voeuxFavoris: List<VoeuFavoriDTO>? = null,
) {
    constructor(profilEleve: ProfilEleve.AvecProfilExistant, voeuxFavoris: List<VoeuFavori>) : this(
        situation = profilEleve.situation,
        classe = profilEleve.classe,
        baccalaureat = profilEleve.baccalaureat,
        dureeEtudesPrevue = profilEleve.dureeEtudesPrevue,
        alternance = profilEleve.alternance,
        formationsFavorites = profilEleve.formationsFavorites?.map { FormationFavoriteDTO(it) },
        communesFavorites = profilEleve.communesFavorites?.map { CommuneDTO(it) },
        specialites = profilEleve.specialites,
        moyenneGenerale = profilEleve.moyenneGenerale,
        centresInterets = profilEleve.centresInterets,
        metiersFavoris = profilEleve.metiersFavoris,
        domaines = profilEleve.domainesInterets,
        corbeilleFormations = profilEleve.corbeilleFormations,
        compteParcoursupAssocie = profilEleve.compteParcoursupLie,
        voeuxFavoris = voeuxFavoris.map { VoeuFavoriDTO(it) },
    )

    data class CommuneDTO(
        @Schema(description = "Code Insee de la ville", example = "75115")
        @JsonProperty("codeInsee")
        val codeInsee: String,
        @Schema(description = "Dénomination de la ville", example = "Paris")
        @JsonProperty("nom")
        val nom: String,
        @Schema(description = "Latitude de la ville", example = "48.8512252")
        @JsonProperty("latitude")
        val latitude: Double,
        @Schema(description = "Longitude de la ville", example = "2.2885659")
        @JsonProperty("longitude")
        val longitude: Double,
    ) {
        constructor(communeFavorite: CommuneFavorite) : this(
            codeInsee = communeFavorite.codeInsee,
            nom = communeFavorite.nom,
            latitude = communeFavorite.latitude,
            longitude = communeFavorite.longitude,
        )
    }

    data class FormationFavoriteDTO(
        @Schema(description = "Id de la formation", example = "fl490030")
        @JsonProperty("idFormation")
        val idFormation: String,
        @Schema(
            description = "Niveau de l'ambition du voeux avec 1 = Plan B, 2 = Réaliste et 3 = Ambitieux",
            example = "2",
        )
        @JsonProperty("niveauAmbition")
        val niveauAmbition: Int,
        @Schema(description = "Prise de note additionnel sur la formation favorite", example = "Ma note personnalisée")
        @JsonProperty("priseDeNote")
        val priseDeNote: String?,
    ) {
        constructor(formationFavorite: FormationFavorite) : this(
            idFormation = formationFavorite.idFormation,
            niveauAmbition = formationFavorite.niveauAmbition,
            priseDeNote = formationFavorite.priseDeNote,
        )
    }

    data class VoeuFavoriDTO(
        @Schema(description = "Id du voeu", example = "ta490030")
        @JsonProperty("idVoeu")
        val idVoeu: String,
        @Schema(description = "Marquage des favoris Parcoursup", example = "true")
        @JsonProperty("estFavoriParcoursup")
        val estFavoriParcoursup: Boolean,
    ) {
        fun toVoeuFavori() =
            VoeuFavori(
                idVoeu = idVoeu,
                estFavoriParcoursup = estFavoriParcoursup,
            )

        constructor(idVoeu: VoeuFavori) : this(
            idVoeu = idVoeu.idVoeu,
            estFavoriParcoursup = idVoeu.estFavoriParcoursup,
        )
    }
}
