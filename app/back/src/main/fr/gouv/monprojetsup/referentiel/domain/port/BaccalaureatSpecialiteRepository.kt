package fr.gouv.monprojetsup.referentiel.domain.port

import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.Specialite

interface BaccalaureatSpecialiteRepository {
    fun recupererLesIdsDesSpecialitesDUnBaccalaureat(idBaccalaureat: String): List<String>

    fun recupererLesBaccalaureatsAvecLeursSpecialites(): Map<Baccalaureat, List<Specialite>>
}
