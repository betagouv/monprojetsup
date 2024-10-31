package fr.gouv.monprojetsup.formation.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.commun.utilitaires.recupererUniqueValeur
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExplicationFormationPourUnProfilReponseDTO(
    @JsonProperty(value = "liste")
    val resultats: List<ExplicationEtExemplesDTO>,
) : APISuggestionReponseDTO()

@JsonIgnoreProperties(ignoreUnknown = true)
data class ExplicationEtExemplesDTO(
    @JsonProperty(value = "key")
    val cle: String,
    @JsonProperty(value = "explanations")
    val explications: List<APISuggestionExplicationDTO>?,
    @JsonProperty(value = "examples")
    val exemplesDeMetiersTriesParAffinitesDecroissantes: List<String>?,
) {
    fun toExplicationsSuggestion(): ExplicationsSuggestionEtExemplesMetiers {
        return explications?.let {
            ExplicationsSuggestionEtExemplesMetiers(
                geographique =
                    explications.flatMap { it.geographiques ?: emptyList() }
                        .mapNotNull { explicationGeographique ->
                            if (explicationGeographique.ville != null && explicationGeographique.distance != null) {
                                ExplicationGeographique(
                                    ville = explicationGeographique.ville,
                                    distanceKm = explicationGeographique.distance,
                                )
                            } else {
                                null
                            }
                        },
                formationsSimilaires =
                    explications.filter { it.similaires != null }
                        .mapNotNull { it.similaires?.formation },
                detailsCalculScore =
                    explications.filter { it.autres != null }
                        .mapNotNull { it.autres?.expl },
                dureeEtudesPrevue =
                    explications.recupererUniqueValeur { it.dureeEtude != null }?.dureeEtude?.choix?.let {
                        ChoixDureeEtudesPrevue.deserialiseAPISuggestion(valeur = it)
                    },
                alternance =
                    explications.recupererUniqueValeur { it.apprentissage != null }?.apprentissage?.choix?.let {
                        ChoixAlternance.deserialiseAPISuggestion(valeur = it)
                    },
                specialitesChoisies =
                    explications.flatMap { it.specialite?.statistiques ?: emptyList() }.map {
                        ExplicationsSuggestionEtExemplesMetiers.AffiniteSpecialite(
                            idSpecialite = it.idSpecialite,
                            pourcentage = it.pourcentage,
                        )
                    },
                typeBaccalaureat =
                    explications.recupererUniqueValeur {
                        it.typeBaccalaureat != null
                    }?.typeBaccalaureat?.toTypeBaccalaureat(),
                autoEvaluationMoyenne =
                    explications.recupererUniqueValeur {
                        it.moyenneGenerale != null
                    }?.moyenneGenerale?.toAutoEvaluationMoyenne(),
                interetsDomainesMetiersChoisis = explications.flatMap { it.tags?.codesInteretsDomainesMetiers ?: emptyList() },
                exemplesDeMetiers = exemplesDeMetiersTriesParAffinitesDecroissantes ?: emptyList(),
            )
        } ?: ExplicationsSuggestionEtExemplesMetiers()
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationDTO(
    @JsonProperty(value = "geo")
    val geographiques: List<APISuggestionExplicationGeoDTO>?,
    @JsonProperty(value = "app")
    val apprentissage: APISuggestionExplicationApprentissageDTO?,
    @JsonProperty(value = "tags")
    val tags: APISuggestionExplicationTagShortDTO?,
    @JsonProperty(value = "dur")
    val dureeEtude: APISuggestionExplicationDurationDTO?,
    @JsonProperty(value = "simi")
    val similaires: APISuggestionExplicationSimilariteDTO?,
    @JsonProperty(value = "debug")
    val autres: APISuggestionExplicationsAutresDTO?,
    @JsonProperty(value = "tbac")
    val typeBaccalaureat: APISuggestionExplicationTypeBacDTO?,
    @JsonProperty(value = "moygen")
    val moyenneGenerale: APISuggestionExplicationNotesDTO?,
    @JsonProperty(value = "spec")
    val specialite: APISuggestionExplicationSpecialitesDTO?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationTypeBacDTO(
    @JsonProperty(value = "percentage")
    val pourcentage: Int,
    @JsonProperty(value = "bac")
    val nomBaccalaureat: String,
) {
    fun toTypeBaccalaureat() =
        ExplicationsSuggestionEtExemplesMetiers.TypeBaccalaureat(
            nomBaccalaureat = nomBaccalaureat,
            pourcentage = pourcentage,
        )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationSimilariteDTO(
    @JsonProperty(value = "fl")
    val formation: String?,
    @JsonProperty(value = "p")
    val pourcentageSimilitude: Double?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationsAutresDTO(
    @JsonProperty(value = "expl")
    val expl: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationSpecialitesDTO(
    @JsonProperty(value = "stats")
    val statistiques: List<AffiniteSpecialiteDTO>,
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class AffiniteSpecialiteDTO(
        @JsonProperty(value = "spe")
        val idSpecialite: String,
        @JsonProperty(value = "pct")
        val pourcentage: Int,
    )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationNotesDTO(
    @JsonProperty(value = "moy")
    val moyenneAutoEvalue: Double,
    @JsonProperty(value = "middle50")
    val mediane: Mediane,
    @JsonProperty(value = "bacUtilise")
    val bacUtilise: String,
) {
    fun toAutoEvaluationMoyenne() =
        ExplicationsSuggestionEtExemplesMetiers.AutoEvaluationMoyenne(
            echellonDeLaMoyenneAutoEvalue = moyenneAutoEvalue.toInt(),
            rangs = mediane.toRangsEchellons(),
            baccalaureatUtilise = bacUtilise,
        )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class Mediane(
    @JsonProperty(value = "rangEch25")
    val rangEch25: Int,
    @JsonProperty(value = "rangEch50")
    val rangEch50: Int,
    @JsonProperty(value = "rangEch75")
    val rangEch75: Int,
    @JsonProperty(value = "rangEch10")
    val rangEch10: Int,
    @JsonProperty(value = "rangEch90")
    val rangEch90: Int,
) {
    fun toRangsEchellons() =
        ExplicationsSuggestionEtExemplesMetiers.RangsEchellons(
            rangEch25 = rangEch25,
            rangEch50 = rangEch50,
            rangEch75 = rangEch75,
            rangEch10 = rangEch10,
            rangEch90 = rangEch90,
        )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationDurationDTO(
    @JsonProperty(value = "option")
    val choix: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationTagShortDTO(
    @JsonProperty(value = "ns")
    val codesInteretsDomainesMetiers: List<String>?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationApprentissageDTO(
    @JsonProperty(value = "option")
    val choix: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationGeoDTO(
    @JsonProperty(value = "distance")
    val distance: Int?,
    @JsonProperty(value = "city")
    val ville: String?,
    @JsonProperty(value = "form")
    val formation: String?,
)
