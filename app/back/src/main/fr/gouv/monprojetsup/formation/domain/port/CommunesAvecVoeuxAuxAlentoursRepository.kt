package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.eleve.domain.entity.CommuneFavorite
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecIdsVoeuxAuxAlentours

interface CommunesAvecVoeuxAuxAlentoursRepository {
    fun recupererVoeuxAutoursDeCommmune(communeFavorites: List<CommuneFavorite>): List<CommuneAvecIdsVoeuxAuxAlentours>
}
