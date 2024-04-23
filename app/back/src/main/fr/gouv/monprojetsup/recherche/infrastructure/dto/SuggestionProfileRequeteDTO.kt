package fr.gouv.monprojetsup.recherche.infrastructure.dto

import fr.gouv.monprojetsup.recherche.domain.entity.Profile
import fr.gouv.monprojetsup.recherche.domain.entity.Suggestion

data class SuggestionProfileRequeteDTO(val profile: ProfileDTO) {
    constructor(profile: Profile) : this(ProfileDTO(profile))
}

data class ProfileDTO(
    val niveau: String,
    val bac: String,
    val duree: String,
    val apprentissage: String,
    val geo_pref: List<String>,
    val spe_classes: List<String>,
    val interests: List<String>,
    val moygen: String,
    val choices: List<SuggestionDTO>,
) {
    constructor(profile: Profile) : this(
        niveau = profile.niveau,
        bac = profile.bac,
        duree = profile.duree,
        apprentissage = profile.apprentissage,
        geo_pref = profile.preferencesGeographique,
        spe_classes = profile.specialites,
        interests = profile.interets,
        moygen = profile.moyenneGenerale,
        choices = profile.choices.map { SuggestionDTO(it) },
    )
}

data class SuggestionDTO(
    val fl: String,
    val status: Int,
    val date: String?,
) {
    constructor(suggestion: Suggestion) : this(
        fl = suggestion.fl,
        status = suggestion.status,
        date = suggestion.date,
    )
}
