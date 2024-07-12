package fr.gouv.monprojetsup.referentiel.domain.port

import fr.gouv.monprojetsup.referentiel.domain.entity.Specialite

interface SpecialitesRepository {
    fun recupererLesSpecialites(idsSpecialites: List<String>): List<Specialite>
}
