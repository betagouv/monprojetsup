package fr.gouv.monprojetsup.recherche.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.commun.utilitaires.recupererUniqueValeur
import fr.gouv.monprojetsup.recherche.domain.entity.AutoEvaluationMoyenne
import fr.gouv.monprojetsup.recherche.domain.entity.Centilles
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.recherche.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion
import fr.gouv.monprojetsup.recherche.domain.entity.Tag
import fr.gouv.monprojetsup.recherche.domain.entity.TypeBaccalaureat

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
    fun toExplicationsSuggestion(): ExplicationsSuggestion {
        return explications?.let {
            ExplicationsSuggestion(
                geographique =
                    explications.flatMap { it.geographiques ?: emptyList() }.mapNotNull { explicationGeographique ->
                        if (explicationGeographique.ville != null && explicationGeographique.distance != null) {
                            ExplicationGeographique(
                                ville = explicationGeographique.ville,
                                distanceKm = explicationGeographique.distance,
                            )
                        } else {
                            null
                        }
                    }.takeUnless { it.isEmpty() },
                formationsSimilaires =
                    explications.filter { it.similaires != null }.takeUnless { it.isEmpty() }
                        ?.mapNotNull { it.similaires?.formation },
                dureeEtudesPrevue =
                    explications.recupererUniqueValeur { it.dureeEtude != null }?.dureeEtude?.option?.let {
                        ChoixDureeEtudesPrevue.deserialiseAPISuggestion(valeur = it)
                    },
                alternance =
                    explications.recupererUniqueValeur { it.apprentissage != null }?.apprentissage?.option?.let {
                        ChoixAlternance.deserialiseAPISuggestion(valeur = it)
                    },
                interets = explications.flatMap { it.interets?.tags ?: emptyList() }.takeUnless { it.isEmpty() },
                specialitesChoisies =
                    explications.recupererUniqueValeur {
                        it.specialite?.statistiques != null
                    }?.specialite?.statistiques,
                typeBaccalaureat =
                    explications.recupererUniqueValeur {
                        it.typeBaccalaureat != null
                    }?.typeBaccalaureat?.toTypeBaccalaureat(),
                autoEvaluationMoyenne =
                    explications.recupererUniqueValeur {
                        it.baccalaureat != null
                    }?.baccalaureat?.toAutoEvaluationMoyenne(),
                tags =
                    explications.recupererUniqueValeur { it.tag != null }?.tag?.paths?.map {
                        Tag(noeuds = it.noeuds, poid = it.poids)
                    },
                tagsCourts = explications.recupererUniqueValeur { it.tags != null }?.tags?.ns,
            )
        } ?: ExplicationsSuggestion()
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationDTO(
    @JsonProperty(value = "geo")
    val geographiques: List<APISuggestionExplicationGeoDTO>?,
    @JsonProperty(value = "app")
    val apprentissage: APISuggestionExplicationApprentissage?,
    @JsonProperty(value = "tag")
    val tag: APISuggestionExplicationTagDTO?,
    @JsonProperty(value = "tags")
    val tags: APISuggestionExplicationTagShortDTO?,
    @JsonProperty(value = "dur")
    val dureeEtude: APISuggestionExplicationDurationDTO?,
    @JsonProperty(value = "simi")
    val similaires: APISuggestionExplicationSimilariteDTO?,
    @JsonProperty(value = "tbac")
    val typeBaccalaureat: APISuggestionExplicationTypeBacDTO?,
    @JsonProperty(value = "bac")
    val baccalaureat: APISuggestionExplicationBacDTO?,
    @JsonProperty(value = "moygen")
    val moyenneGenerale: APISuggestionExplicationNotesDTO?,
    @JsonProperty(value = "spec")
    val specialite: APISuggestionExplicationSpecialitesDTO?,
    @JsonProperty(value = "interets")
    val interets: APISuggestionExplicationInteretTagsDTO?,
    @JsonProperty(value = "search")
    val recherchesPassees: APISuggestionExplicationSearchDTO?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationSearchDTO(
    @JsonProperty(value = "word")
    val word: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationInteretTagsDTO(
    @JsonProperty(value = "tags")
    val tags: List<String>?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationTypeBacDTO(
    @JsonProperty(value = "percentage")
    val pourcentage: Int?,
    @JsonProperty(value = "bac")
    val bac: String?,
) {
    fun toTypeBaccalaureat() =
        TypeBaccalaureat(
            nomBaccalaureat = bac,
            pourcentage = pourcentage,
        )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationBacDTO(
    @JsonProperty(value = "moy")
    val moyenne: Double?,
    @JsonProperty(value = "middle50")
    val mediane: Middle50?,
    @JsonProperty(value = "bacUtilise")
    val bacUtilise: String?,
) {
    fun toAutoEvaluationMoyenne() =
        AutoEvaluationMoyenne(
            moyenne = moyenne,
            mediane = mediane?.toCentilles(),
            bacUtilise = bacUtilise,
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
data class APISuggestionExplicationSpecialitesDTO(
    @JsonProperty(value = "stats")
    val statistiques: Map<String, Double>?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationNotesDTO(
    @JsonProperty(value = "moy")
    val moyenne: Double?,
    @JsonProperty(value = "middle50")
    val mediane: Middle50?,
    @JsonProperty(value = "bacUtilise")
    val bacUtilise: String?,
)

data class Middle50(
    @JsonProperty(value = "rangEch25")
    val rangEch25: Int?,
    @JsonProperty(value = "rangEch50")
    val rangEch50: Int?,
    @JsonProperty(value = "rangEch75")
    val rangEch75: Int?,
    @JsonProperty(value = "rangEch10")
    val rangEch10: Int?,
    @JsonProperty(value = "rangEch90")
    val rangEch90: Int?,
) {
    fun toCentilles() =
        Centilles(
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
    val option: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationTagShortDTO(
    @JsonProperty(value = "ns")
    val ns: List<String>?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationTagDTO(
    @JsonProperty(value = "paths")
    val paths: List<PathDTO>?,
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class PathDTO(
        @JsonProperty(value = "nodes")
        val noeuds: List<String>?,
        @JsonProperty(value = "weight")
        val poids: Double?,
    )
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class APISuggestionExplicationApprentissage(
    @JsonProperty(value = "option")
    val option: String?,
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
