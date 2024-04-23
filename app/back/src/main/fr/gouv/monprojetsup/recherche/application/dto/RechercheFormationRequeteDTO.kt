package fr.gouv.monprojetsup.recherche.application.dto

import fr.gouv.monprojetsup.recherche.domain.entity.Profile
import fr.gouv.monprojetsup.recherche.domain.entity.Suggestion
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Schema

data class RechercheFormationRequeteDTO(val profile: ProfileDTO)

data class ProfileDTO(
    @Schema(
        description = "Classe actuelle",
        example = "term",
        required = false,
        allowableValues = ["", "sec", "secSTHR", "secTMD", "prem", "term"],
    )
    val niveau: String,
    @Schema(
        description = "Type de Bac choisi ou envisagé",
        example = "Générale",
        required = false,
        allowableValues = ["", "Générale", "P", "PA", "S2TMD", "ST2S", "STAV", "STD2A", "STHR", "STI2D", "STL", "STMG"],
    )
    val bac: String,
    @Schema(
        description = "durée envisagée des études",
        example = "long",
        required = false,
        allowableValues = ["", "court", "long", "indiff"],
    )
    val duree: String,
    @Schema(
        description = "intérêt pour les formations en apprentissage",
        example = "C",
        required = false,
        allowableValues = ["", "A", "B", "C", "D"],
    )
    val apprentissage: String,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "villes préférées pour étudier",
                example = "[\"Soulac-sur-Mer\",\"Nantes\"]",
                required = false,
            ),
    )
    val preferencesGeographique: List<String>,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "enseignements de spécialité de terminale choisis ou envisagés",
                example = "[\"Sciences de la vie et de la Terre\",\"Mathématiques\"]",
                required = false,
            ),
    )
    val specialites: List<String>,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "centres d'intérêt",
                example = "[\"T_ITM_1054\",\"T_ITM_1534\",\"T_ITM_1248\",\"T_ITM_1351\", \"T_ROME_2092381917\", \"T_IDEO2_4812\"]",
                required = false,
            ),
    )
    val interets: List<String>,
    @Schema(description = "moyenne générale scolaire estimée en terminale", example = "14", required = false)
    val moyenneGenerale: String,
    @ArraySchema(
        arraySchema =
            Schema(
                description = "sélection de formations, métiers et secteurs d'activité",
                required = false,
            ),
    )
    val choix: List<SuggestionDTO>,
) {
    fun toProfile() =
        Profile(
            niveau = niveau,
            bac = bac,
            duree = duree,
            apprentissage = apprentissage,
            preferencesGeographique = preferencesGeographique,
            specialites = specialites,
            moyenneGenerale = moyenneGenerale,
            interets = interets,
            choices = choix.map { it.toSuggestion() },
        )
}

data class SuggestionDTO(
    @Schema(example = "fl2014", description = "clé de la formation, du métier ou du secteur d'activité")
    val fl: String,
    @Schema(example = "1", description = "statut. \"1\": dans les favoris. \"2\": dans la corbeille.", allowableValues = ["0", "1", "2" ])
    val status: Int,
    val date: String?,
) {
    fun toSuggestion() =
        Suggestion(
            fl = fl,
            status = status,
            date = date,
        )
}
