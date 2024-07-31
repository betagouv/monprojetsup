package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte

interface RechercheFormationRepository {
    fun rechercherUneFormation(motRecherche: String): List<FormationCourte>
}
