package fr.gouv.monprojetsup.formation.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.formation.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.formation.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.formation.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.formation.domain.entity.ProfilEleve
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

class ProfilDTO(
    @Schema(
        description = "Id de l'élève",
        example = "adcf627c-36dd-4df5-897b-159443a6d49c",
        required = true,
    )
    @JsonProperty("id")
    val id: String,
    @Schema(
        description = "Etat d'avancée du projet de l'élève",
        example = "aucune_idee",
        required = false,
        allowableValues = ["aucune_idee", "quelques_pistes", "projet_precis"],
    )
    val situation: String?,
    @Schema(
        description = "Classe actuelle",
        example = "terminale",
        required = false,
        allowableValues = ["seconde", "seconde_sthr", "seconde_tmd", "premiere", "terminale"],
    )
    @JsonProperty("classe")
    val classe: String?,
    @Schema(
        description = "Type de Bac choisi ou envisagé",
        example = "Générale",
        required = false,
        allowableValues = ["NC", "Générale", "P", "PA", "S2TMD", "ST2S", "STAV", "STD2A", "STHR", "STI2D", "STL", "STMG"],
    )
    @JsonProperty("bac")
    val bac: String?,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Enseignements de spécialité de terminale choisis ou envisagés",
                example = "[\"1056\",\"1054\"]",
                required = false,
            ),
    )
    @JsonProperty("specialites")
    val specialites: List<String>?,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Domaines d'activité",
                example = "[\"T_ITM_1054\",\"T_ITM_1534\",\"T_ITM_1248\",\"T_ITM_1351\"]",
                required = false,
            ),
    )
    @JsonProperty("domaines")
    val domaines: List<String>?,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Centres d'intérêt",
                example = "[\"T_ROME_2092381917\", \"T_IDEO2_4812\"]",
                required = false,
            ),
    )
    @JsonProperty("centresInterets")
    val centresInterets: List<String>?,
    @Schema(
        description = "L'état de situation des idées de métiers de l'élève",
        example = "quelques_pistes",
        required = false,
        allowableValues = ["aucune_idee", "quelques_pistes"],
    )
    @JsonProperty("situationMetiers")
    val situationMetiers: String?,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Les idées de métiers de l'élève",
                example = "[\"MET_123\", \"MET_456\"]",
                required = false,
            ),
    )
    @JsonProperty("metiers")
    val metiers: List<String>?,
    @Schema(
        description = "Durée envisagée des études",
        example = "options_ouvertes",
        required = false,
        allowableValues = ["options_ouvertes", "courte", "longue", "aucune_idee"],
    )
    @JsonProperty("dureeEtudesPrevue")
    val dureeEtudesPrevue: String?,
    @Schema(
        description = "Intérêt pour les formations en apprentissage",
        example = "pas_interesse",
        required = false,
        allowableValues = ["pas_interesse", "indifferent", "interesse", "tres_interesse"],
    )
    @JsonProperty("alternance")
    val alternance: String?,
    @Schema(
        description = "L'état de situation des idées de villes de l'élève",
        example = "quelques_pistes",
        required = false,
        allowableValues = ["aucune_idee", "quelques_pistes"],
    )
    @JsonProperty("situationVilles")
    val situationVilles: String?,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Villes préférées pour étudier",
                required = false,
            ),
    )
    @JsonProperty("villes")
    val villes: List<VilleDTO>?,
    @Schema(description = "Moyenne générale scolaire estimée en terminale", example = "14", required = false)
    @JsonProperty("moyenneGenerale")
    val moyenneGenerale: Float?,
    @Schema(
        description = "L'état de situation des idées de formations de l'élève",
        example = "quelques_pistes",
        required = false,
        allowableValues = ["aucune_idee", "quelques_pistes"],
    )
    @JsonProperty("situationFormations")
    val situationFormations: String?,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "Les idées de formations de l'élève",
                example = "[\"fl1234\", \"fl5678\"]",
                required = false,
            ),
    )
    @JsonProperty("formations")
    val formations: List<String>?,
) {
    fun toProfil() =
        ProfilEleve(
            id = id,
            classe = ChoixNiveau.deserialiseApplication(classe),
            bac = bac,
            dureeEtudesPrevue = ChoixDureeEtudesPrevue.deserialiseApplication(dureeEtudesPrevue),
            alternance = ChoixAlternance.deserialiseApplication(alternance),
            formationsChoisies = formations,
            villesPreferees = villes?.map { it.nom },
            specialites = specialites,
            moyenneGenerale = moyenneGenerale,
            centresInterets = centresInterets,
            metiersChoisis = metiers,
            domainesInterets = domaines,
        )

    class VilleDTO(
        @Schema(description = "Code Insee de la ville", example = "75015", required = true)
        @JsonProperty("codeInsee")
        val codeInsee: String,
        @Schema(description = "Dénomination de la ville", example = "Paris", required = true)
        @JsonProperty("nom")
        val nom: String,
        @Schema(description = "Latitude de la ville", example = "2.2885659", required = true)
        @JsonProperty("latitude")
        val latitude: Float,
        @Schema(description = "Longitude de la ville", example = "48.8512252", required = true)
        @JsonProperty("longitude")
        val longitude: Float,
    )
}
