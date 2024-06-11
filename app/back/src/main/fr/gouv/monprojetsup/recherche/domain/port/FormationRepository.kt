package fr.gouv.monprojetsup.recherche.domain.port

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.recherche.domain.entity.Formation
import fr.gouv.monprojetsup.recherche.domain.entity.FormationDetaillee
import fr.gouv.monprojetsup.recherche.domain.entity.Metier
import kotlin.jvm.Throws

interface FormationRepository {
    @Throws(MonProjetIllegalStateErrorException::class, MonProjetSupNotFoundException::class)
    fun recupererUneFormationAvecSesMetiers(idFormation: String): FormationDetaillee

    fun recupererLesFormationsAvecLeursMetiers(idsFormations: List<String>): Map<Formation, List<Metier>>

    fun recupererLesNomsDesFormations(idsFormations: List<String>): List<Formation>
}
