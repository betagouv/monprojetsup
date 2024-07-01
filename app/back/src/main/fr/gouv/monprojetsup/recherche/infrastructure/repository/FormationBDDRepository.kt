package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.recherche.domain.entity.Formation
import fr.gouv.monprojetsup.recherche.domain.entity.FormationDetaillee
import fr.gouv.monprojetsup.recherche.domain.entity.Metier
import fr.gouv.monprojetsup.recherche.domain.entity.MetierDetaille
import fr.gouv.monprojetsup.recherche.domain.port.FormationRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.Throws
import kotlin.jvm.optionals.getOrElse

@Repository
class FormationBDDRepository(
    val formationJPARepository: FormationJPARepository,
    val formationDetailleeJPARepository: FormationDetailleeJPARepository,
) : FormationRepository {
    @Throws(MonProjetIllegalStateErrorException::class, MonProjetSupNotFoundException::class)
    @Transactional(readOnly = true)
    override fun recupererUneFormationAvecSesMetiers(idFormation: String): FormationDetaillee {
        val formation =
            formationDetailleeJPARepository.findById(idFormation).getOrElse {
                throw MonProjetSupNotFoundException(
                    code = "RECHERCHE_FORMATION",
                    msg = "La formation $idFormation n'existe pas",
                )
            }
        return FormationDetaillee(
            id = formation.id,
            nom = formation.label,
            descriptifGeneral = formation.descriptifGeneral,
            descriptifAttendus = formation.descriptifAttendus,
            liens = formation.liens.map { it.toLien() },
            metiers =
                formation.metiers.map { formationMetierEntity ->
                    val metier = formationMetierEntity.metier
                    MetierDetaille(
                        id = metier.id,
                        nom = metier.label,
                        descriptif = metier.descriptifGeneral,
                        liens = metier.liens.map { it.toLien() },
                    )
                },
            formationsAssociees = formation.formationsAssociees ?: emptyList(),
            descriptifConseils = formation.descriptifConseils,
            descriptifDiplome = formation.descriptifDiplome,
            valeurCriteresAnalyseCandidature = formation.criteresAnalyse,
        )
    }

    @Transactional(readOnly = true)
    override fun recupererLesFormationsAvecLeursMetiers(idsFormations: List<String>): Map<Formation, List<Metier>> {
        val formations = formationJPARepository.findAllByIdIn(idsFormations)
        return formations.associate { formationEntity ->
            formationEntity.toFormation() to formationEntity.metiers.map { it.metier.toMetier() }
        }
    }

    @Transactional(readOnly = true)
    override fun recupererLesNomsDesFormations(idsFormations: List<String>): List<Formation> {
        val formations = formationJPARepository.findAllByIdIn(idsFormations)
        return formations.map { Formation(it.id, it.label) }
    }
}
