package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.commun.Constantes.ANNEE_DONNEES_PARCOURSUP
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis
import fr.gouv.monprojetsup.formation.domain.port.FrequencesCumuleesDesMoyenneDesAdmisRepository
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import org.springframework.stereotype.Service

@Service
class StatistiquesDesAdmisPourFormationsService(
    val frequencesCumuleesDesMoyenneDesAdmisRepository: FrequencesCumuleesDesMoyenneDesAdmisRepository,
    val statistiquesDesAdmisBuilder: StatistiquesDesAdmisBuilder,
    val logger: MonProjetSupLogger,
) {
    fun recupererStatistiquesAdmisDeFormations(
        idBaccalaureat: String?,
        idsFormations: List<String>,
        classe: ChoixNiveau?,
    ): Map<String, StatistiquesDesAdmis?> {
        val frequencesCumulees =
            frequencesCumuleesDesMoyenneDesAdmisRepository.recupererFrequencesCumuleesDeTousLesBacs(
                idsFormations,
                ANNEE_DONNEES_PARCOURSUP,
            )
        val statistiques =
            idsFormations.associateWith {
                frequencesCumulees[it]?.let { frequencesCumulees ->
                    statistiquesDesAdmisBuilder.creerStatistiquesDesAdmis(frequencesCumulees, idBaccalaureat, classe)
                }
            }
        logguerLesFormationsSansStatistiques(statistiques)
        return statistiques
    }

    fun recupererStatistiquesAdmisDUneFormation(
        idBaccalaureat: String?,
        idFormation: String,
        classe: ChoixNiveau?,
    ): StatistiquesDesAdmis {
        val frequencesCumulees: Map<Baccalaureat, List<Int>> =
            frequencesCumuleesDesMoyenneDesAdmisRepository.recupererFrequencesCumuleesDeTousLesBacs(
                idFormation = idFormation,
                annee = ANNEE_DONNEES_PARCOURSUP,
            )
        return statistiquesDesAdmisBuilder.creerStatistiquesDesAdmis(frequencesCumulees, idBaccalaureat, classe)
    }

    private fun logguerLesFormationsSansStatistiques(statistiques: Map<String, StatistiquesDesAdmis?>) {
        val formationsSansStatistiques = statistiques.filter { it.value == null }.map { it.key }
        if (formationsSansStatistiques.isNotEmpty()) {
            logger.warn(
                type = "FORMATION_SANS_STATISTIQUE",
                message = "Les formations suivantes n'ont pas de statistiques : $formationsSansStatistiques",
                parametres = mapOf("formationsSansStatistiques" to formationsSansStatistiques),
            )
        }
    }
}
