package fr.gouv.monprojetsup.recherche.domain.port

import fr.gouv.monprojetsup.recherche.domain.entity.Formation
import fr.gouv.monprojetsup.recherche.domain.entity.Metier

interface FormationRepository {
    fun recupererLesFormationsAvecLeursMetiers(idsFormations: List<String>): Map<Formation, List<Metier>>
}
