package fr.gouv.monprojetsup.formation.domain.port

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.entity.FormationDetaillee

interface FormationRepository {
    @Throws(MonProjetIllegalStateErrorException::class, MonProjetSupNotFoundException::class)
    fun recupererUneFormationAvecSesMetiers(idFormation: String): FormationDetaillee

    fun recupererLesFormationsAvecLeursMetiers(idsFormations: List<String>): List<FormationDetaillee>

    fun recupererLesNomsDesFormations(idsFormations: List<String>): List<Formation>
}
