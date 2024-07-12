package fr.gouv.monprojetsup.referentiel.domain.port

import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.Specialite

interface BaccalaureatSpecialiteRepository {
    fun recupererLesBaccalaureatsAvecLeursSpecialites(): Map<Baccalaureat, List<Specialite>>
}
