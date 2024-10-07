package fr.gouv.monprojetsup.metier.domain.entity

import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte

data class Metier(
    val id: String,
    val nom: String,
    val descriptif: String?,
    val liens: List<Lien>,
)

data class MetierAvecSesFormations(
    val id: String,
    val nom: String,
    val descriptif: String?,
    val liens: List<Lien>,
    val formations: List<FormationCourte>,
)
