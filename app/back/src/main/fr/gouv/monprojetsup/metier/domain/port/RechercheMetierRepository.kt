package fr.gouv.monprojetsup.metier.domain.port

import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt

interface RechercheMetierRepository {
    fun rechercherMetiersCourts(motRecherche: String): List<MetierCourt>
}
