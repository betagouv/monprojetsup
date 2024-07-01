package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.formation.domain.entity.Specialite

interface SpecialitesRepository {
    fun recupererLesSpecialites(idsSpecialites: List<String>): List<Specialite>
}
