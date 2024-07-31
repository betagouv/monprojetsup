package fr.gouv.monprojetsup.eleve.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.authentification.domain.entity.ModificationProfilEleve
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.Commune
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

data class ModificationProfilDTO(
    @Schema(
        description = "Etat d'avancée du projet de l'élève",
        example = "aucune_idee",
        required = false,
        allowableValues = ["aucune_idee", "quelques_pistes", "projet_precis"],
    )
    @JsonProperty("situation")
    val situation: String? = null,
    @Schema(
        description = "Classe actuelle",
        example = "terminale",
        required = false,
        allowableValues = ["seconde", "premiere", "terminale"],
    )
    @JsonProperty("classe")
    val classe: String? = null,
    @Schema(
        description = "Type de Bac choisi ou envisagé",
        example = "Générale",
        required = false,
        allowableValues = ["NC", "Générale", "P", "PA", "S2TMD", "ST2S", "STAV", "STD2A", "STHR", "STI2D", "STL", "STMG"],
    )
    @JsonProperty("baccalaureat")
    val baccalaureat: String? = null,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Enseignements de spécialité de terminale choisis ou envisagés",
                example = "[\"707\",\"700\"]",
                required = false,
            ),
    )
    @JsonProperty("specialites")
    val specialites: List<String>? = null,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Domaines d'activité",
                example = "[\"T_ITM_1054\",\"T_ITM_1534\",\"T_ITM_1248\",\"T_ITM_1351\"]",
                required = false,
            ),
    )
    @JsonProperty("domaines")
    val domaines: List<String>? = null,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Centres d'intérêt",
                example = "[\"transmettre_enfants\", \"travail_manuel_bricoler\", \"aider_soigner\", \"diriger_equipe\"]",
                required = false,
            ),
    )
    @JsonProperty("centresInterets")
    val centresInterets: List<String>? = null,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Les idées de métiers de l'élève",
                example = "[\"MET_384\", \"MET_469\"]",
                required = false,
            ),
    )
    @JsonProperty("metiersFavoris")
    val metiersFavoris: List<String>? = null,
    @Schema(
        description = "Durée envisagée des études",
        example = "indifferent",
        required = false,
        allowableValues = ["indifferent", "courte", "longue", "aucune_idee"],
    )
    @JsonProperty("dureeEtudesPrevue")
    val dureeEtudesPrevue: String? = null,
    @Schema(
        description = "Intérêt pour les formations en apprentissage",
        example = "pas_interesse",
        required = false,
        allowableValues = ["pas_interesse", "indifferent", "interesse", "tres_interesse"],
    )
    @JsonProperty("alternance")
    val alternance: String? = null,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Villes préférées pour étudier",
                required = false,
            ),
    ) @JsonProperty("communesFavorites")
    val communesFavorites: List<CommuneDTO>? = null,
    @Schema(description = "Moyenne générale scolaire estimée en terminale", example = "14", required = false)
    @JsonProperty("moyenneGenerale")
    val moyenneGenerale: Float? = null,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Les idées de formations de l'élève",
                example = "[\"fl720007\", \"fl490030\"]",
                required = false,
            ),
    )
    @JsonProperty("formationsFavorites")
    val formationsFavorites: List<String>? = null,
) {
    constructor(profilEleve: ProfilEleve.Identifie) : this(
        situation = profilEleve.situation?.jsonValeur,
        classe = profilEleve.classe?.jsonValeur,
        baccalaureat = profilEleve.baccalaureat,
        dureeEtudesPrevue = profilEleve.dureeEtudesPrevue?.jsonValeur,
        alternance = profilEleve.alternance?.jsonValeur,
        formationsFavorites = profilEleve.formationsFavorites,
        communesFavorites = profilEleve.communesFavorites?.map { CommuneDTO(it) },
        specialites = profilEleve.specialites,
        moyenneGenerale = profilEleve.moyenneGenerale,
        centresInterets = profilEleve.centresInterets,
        metiersFavoris = profilEleve.metiersFavoris,
        domaines = profilEleve.domainesInterets,
    )

    fun toModificationProfilEleve() =
        ModificationProfilEleve(
            situation = situation?.let { SituationAvanceeProjetSup.deserialiseApplication(it) },
            classe = classe?.let { ChoixNiveau.deserialiseApplication(it) },
            baccalaureat = baccalaureat,
            dureeEtudesPrevue = dureeEtudesPrevue?.let { ChoixDureeEtudesPrevue.deserialiseApplication(it) },
            alternance = alternance?.let { ChoixAlternance.deserialiseApplication(it) },
            formationsFavorites = formationsFavorites,
            communesFavorites = communesFavorites?.map { it.toCommune() },
            specialites = specialites,
            moyenneGenerale = moyenneGenerale,
            centresInterets = centresInterets,
            metiersFavoris = metiersFavoris,
            domainesInterets = domaines,
        )

    class CommuneDTO(
        @Schema(description = "Code Insee de la ville", example = "75015", required = true)
        @JsonProperty("codeInsee")
        val codeInsee: String,
        @Schema(description = "Dénomination de la ville", example = "Paris", required = true)
        @JsonProperty("nom")
        val nom: String,
        @Schema(description = "Latitude de la ville", example = "48.8512252", required = true)
        @JsonProperty("latitude")
        val latitude: Float,
        @Schema(description = "Longitude de la ville", example = "2.2885659", required = true)
        @JsonProperty("longitude")
        val longitude: Float,
    ) {
        constructor(commune: Commune) : this(
            codeInsee = commune.codeInsee,
            nom = commune.nom,
            latitude = commune.latitude,
            longitude = commune.longitude,
        )

        fun toCommune() =
            Commune(
                codeInsee = codeInsee,
                nom = nom,
                latitude = latitude,
                longitude = longitude,
            )
    }
}
