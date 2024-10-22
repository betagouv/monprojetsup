package fr.gouv.monprojetsup.metier.domain.port

import fr.gouv.monprojetsup.metier.domain.entity.ResultatRechercheMetierCourt

interface RechercheMetierRepository {
    fun rechercherMetiersCourts(motRecherche: String): List<ResultatRechercheMetierCourt>
}
