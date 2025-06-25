package fr.gouv.monprojetsup.suggestions.entities

import fr.gouv.monprojetsup.suggestions.dto.ResponseHeader
import fr.gouv.monprojetsup.suggestions.dto.explanations.NaivesBayesExplanation

data class NaiveBayesSuggestions(
    val key: String = "",
    //indexé par "eleve" ou "expert"
    val scores: Map<String,Double> = emptyMap()
)

data class Suggestions2ExplanationsDto(
    //fl210
    val key: String = "",
    //key is 'expert' or 'eleve'
    val explanations: Map<String, NaivesBayesExplanation> = emptyMap()
) {
    constructor(key: String) : this(key, emptyMap())
}

data class NaiveBayesExplanations(
    val affinities : List<NaiveBayesSuggestions>,
    val explanations: List<Suggestions2ExplanationsDto>
) {
    constructor() : this(emptyList(), emptyList())
}