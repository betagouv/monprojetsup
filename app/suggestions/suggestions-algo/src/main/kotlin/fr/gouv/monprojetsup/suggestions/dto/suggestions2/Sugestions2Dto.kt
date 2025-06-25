package fr.gouv.monprojetsup.suggestions.dto.suggestions2

import fr.gouv.monprojetsup.suggestions.dto.ResponseHeader
import fr.gouv.monprojetsup.suggestions.dto.explanations.NaivesBayesExplanation

/*
output de /suggestions2
{
  "scores": [
    {
      "key": "fl2014",
      "scores": {
        "eleve": 0.3,
        "expert": 0.5
      }
    }
  ]
}
*/
data class Suggestions2Answer(
    val header: ResponseHeader = ResponseHeader(),
    val scores: List<Suggestions2SuggestionsDto> = emptyList()
)

data class Suggestions2SuggestionsDto(
    val key: String = "",
    //indexé par "eleve" ou "expert"
    val scores: Map<String,Double> = emptyMap()
) {
}

/*
output de /explanations
{
  "explanations": [
    {
      "key": "fl210",
      "explanations": {
        "expert": {
          "popularity": 3.6699821356981728,
          "scores": [
            {
              "key": "Générale",
              "log_influence": 0.9176905251227548,
              "categorie": "id_baccalaureat"
            },
            {
              "key": "mat5",
              "log_influence": -3.273289227406298,
              "categorie": "specialites"
            },

 */
data class Suggestions2ExplanationsDto(
    //fl210
    val key: String = "",
    //key is 'expert' or 'eleve'
    val explanations: Map<String, NaivesBayesExplanation> = emptyMap()
) {
    constructor(key: String) : this(key, emptyMap())
}

data class NaiveBayesExplanations(
    val affinities : List<Suggestions2SuggestionsDto>,
    val explanations: List<Suggestions2ExplanationsDto>
) {
    constructor() : this(emptyList(), emptyList())
}