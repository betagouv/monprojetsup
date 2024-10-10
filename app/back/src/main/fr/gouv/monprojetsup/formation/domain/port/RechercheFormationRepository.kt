package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.formation.domain.entity.ResultatRechercheFormationCourte

interface RechercheFormationRepository {
    fun rechercherUneFormation(motRecherche: String): List<ResultatRechercheFormationCourte>
}
