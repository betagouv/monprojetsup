package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte

interface FormationRepository {
    @Throws(MonProjetIllegalStateErrorException::class, MonProjetSupNotFoundException::class)
    fun recupererUneFormationAvecSesMetiers(idFormation: String): Formation

    fun recupererLesFormationsAvecLeursMetiers(idsFormations: List<String>): List<Formation>

    fun recupererLesNomsDesFormations(idsFormations: List<String>): List<FormationCourte>

    fun recupererIdsFormationsInexistantes(ids: List<String>): List<String>
}
