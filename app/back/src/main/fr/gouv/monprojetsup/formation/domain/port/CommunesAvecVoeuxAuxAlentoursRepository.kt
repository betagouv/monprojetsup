package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.eleve.domain.entity.Commune
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecIdsVoeuxAuxAlentours

interface CommunesAvecVoeuxAuxAlentoursRepository {
    fun recupererVoeuxAutoursDeCommmune(communes: List<Commune>): List<CommuneAvecIdsVoeuxAuxAlentours>
}
