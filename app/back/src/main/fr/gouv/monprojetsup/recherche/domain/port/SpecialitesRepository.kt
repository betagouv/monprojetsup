package fr.gouv.monprojetsup.recherche.domain.port

import fr.gouv.monprojetsup.recherche.domain.entity.Specialite

interface SpecialitesRepository {
    fun recupererLesSpecialites(idsSpecialites: List<String>): List<Specialite>
}
