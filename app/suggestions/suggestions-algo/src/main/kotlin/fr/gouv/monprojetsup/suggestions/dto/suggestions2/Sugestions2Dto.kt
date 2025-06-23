package fr.gouv.monprojetsup.suggestions.dto.suggestions2

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
    val scores: List<Suggestions2AffinityDto>
)
data class Suggestions2AffinityDto(
    val key: String,
    //indexé par "eleve" ou "expert"
    val scores: Map<String,Double>
)
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
data class Explanations2Answer(
    val explanations: List<Suggestions2MultiExplanationsDto>
)
data class Suggestions2MultiExplanationsDto(
    val key: String,
    val explanations: Map<String,SingleExplanation>
)
data class SingleExplanation(
    val popularity: Double,
    val scores: List<Score>
)
data class Score(
    val key: String,
    val log_influence: Double,
    val categorie: String
)
