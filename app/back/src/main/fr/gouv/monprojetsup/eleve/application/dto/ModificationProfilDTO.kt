package fr.gouv.monprojetsup.eleve.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.CommuneFavorite
import fr.gouv.monprojetsup.eleve.domain.entity.ModificationProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFormation
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
    val situation: SituationAvanceeProjetSup? = null,
    @Schema(
        description = "Classe actuelle",
        example = "terminale",
        required = false,
        allowableValues = ["seconde", "premiere", "terminale"],
    )
    @JsonProperty("classe")
    val classe: ChoixNiveau? = null,
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
                example = "[\"mat707\",\"mat700\"]",
                required = false,
            ),
    )
    @JsonProperty("specialites")
    val specialites: List<String>? = null,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Domaines d'activité",
                example = "[\"dom41\",\"dom32\",\"dom26\"]",
                required = false,
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
    val dureeEtudesPrevue: ChoixDureeEtudesPrevue? = null,
    @Schema(
        description = "Intérêt pour les formations en apprentissage",
        example = "pas_interesse",
        required = false,
        allowableValues = ["pas_interesse", "indifferent", "interesse", "tres_interesse"],
    )
    @JsonProperty("alternance")
    val alternance: ChoixAlternance? = null,
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
    @ArraySchema(arraySchema = Schema(description = "Les idées de formations de l'élève", required = false))
    @JsonProperty("formationsFavorites")
    val formationsFavorites: List<VoeuFormationDTO>? = null,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Les formations mises à la corbeille par l'élève",
                example = "[\"fl1\", \"fl810505\"]",
                required = false,
            ),
    )
    @JsonProperty("corbeilleFormations")
    val corbeilleFormations: List<String>? = null,
) {
    constructor(profilEleve: ProfilEleve.AvecProfilExistant) : this(
        situation = profilEleve.situation,
        classe = profilEleve.classe,
        baccalaureat = profilEleve.baccalaureat,
        dureeEtudesPrevue = profilEleve.dureeEtudesPrevue,
        alternance = profilEleve.alternance,
        formationsFavorites = profilEleve.formationsFavorites?.map { VoeuFormationDTO(it) },
        communesFavorites = profilEleve.communesFavorites?.map { CommuneDTO(it) },
        specialites = profilEleve.specialites,
        moyenneGenerale = profilEleve.moyenneGenerale,
        centresInterets = profilEleve.centresInterets,
        metiersFavoris = profilEleve.metiersFavoris,
        domaines = profilEleve.domainesInterets,
        corbeilleFormations = profilEleve.corbeilleFormations,
    )

    fun toModificationProfilEleve() =
        ModificationProfilEleve(
            situation = situation,
            classe = classe,
            baccalaureat = baccalaureat,
            dureeEtudesPrevue = dureeEtudesPrevue,
            alternance = alternance,
            formationsFavorites = formationsFavorites?.map { it.toVoeuFormation() },
            communesFavorites = communesFavorites?.map { it.toCommuneFavoris() },
            specialites = specialites,
            moyenneGenerale = moyenneGenerale,
            centresInterets = centresInterets,
            metiersFavoris = metiersFavoris,
            domainesInterets = domaines,
            corbeilleFormations = corbeilleFormations,
        )

    data class CommuneDTO(
        @Schema(description = "Code Insee de la ville", example = "75115", required = true)
        @JsonProperty("codeInsee")
        val codeInsee: String,
        @Schema(description = "Dénomination de la ville", example = "Paris", required = true)
        @JsonProperty("nom")
        val nom: String,
        @Schema(description = "Latitude de la ville", example = "48.8512252", required = true)
        @JsonProperty("latitude")
        val latitude: Double,
        @Schema(description = "Longitude de la ville", example = "2.2885659", required = true)
        @JsonProperty("longitude")
        val longitude: Double,
    ) {
        constructor(communeFavorite: CommuneFavorite) : this(
            codeInsee = communeFavorite.codeInsee,
            nom = communeFavorite.nom,
            latitude = communeFavorite.latitude,
            longitude = communeFavorite.longitude,
        )

        fun toCommuneFavoris() =
            CommuneFavorite(
                codeInsee = codeInsee,
                nom = nom,
                latitude = latitude,
                longitude = longitude,
            )
    }

    data class VoeuFormationDTO(
        @Schema(description = "Id de la formation", example = "fl490030", required = true)
        @JsonProperty("idFormation")
        val idFormation: String,
        @Schema(
            description = "Niveau de l'ambition du voeux avec 1 = Plan B, 2 = Réaliste et 3 = Ambitieux",
            example = "2",
            required = true,
        )
        @JsonProperty("niveauAmbition")
        val niveauAmbition: Int,
        @ArraySchema(
            arraySchema =
                Schema(
                    description = "Les voeux (triplets d'affectation) souhaités",
                    example = "[\"ta15974\", \"ta17831\"]",
                    required = true,
                ),
        )
        @JsonProperty("voeuxChoisis")
        val voeuxChoisis: List<String>,
        @Schema(description = "Prise de note additionnel sur le voeu", example = "Ma note personnalisée", required = false)
        @JsonProperty("priseDeNote")
        val priseDeNote: String?,
    ) {
        constructor(voeuDeFormation: VoeuFormation) : this(
            idFormation = voeuDeFormation.idFormation,
            niveauAmbition = voeuDeFormation.niveauAmbition,
            voeuxChoisis = voeuDeFormation.voeuxChoisis,
            priseDeNote = voeuDeFormation.priseDeNote,
        )

        fun toVoeuFormation() =
            VoeuFormation(
                idFormation = idFormation,
                niveauAmbition = niveauAmbition,
                voeuxChoisis = voeuxChoisis,
                priseDeNote = priseDeNote,
            )
    }
}
