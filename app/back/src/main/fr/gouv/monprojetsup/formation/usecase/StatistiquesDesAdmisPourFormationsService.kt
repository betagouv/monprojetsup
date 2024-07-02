package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis
import fr.gouv.monprojetsup.formation.domain.port.FrequencesCumuleesDesMoyenneDesAdmisRepository
import org.slf4j.Logger
import org.springframework.stereotype.Service

@Service
class StatistiquesDesAdmisPourFormationsService(
    val frequencesCumuleesDesMoyenneDesAdmisRepository: FrequencesCumuleesDesMoyenneDesAdmisRepository,
    val statistiquesDesAdmisBuilder: StatistiquesDesAdmisBuilder,
    val logger: Logger,
) {
    fun recupererStatistiquesAdmisDeFormations(
        idBaccalaureat: String?,
        idsFormations: List<String>,
        classe: ChoixNiveau?,
    ): Map<String, StatistiquesDesAdmis?> {
        val frequencesCumulees = frequencesCumuleesDesMoyenneDesAdmisRepository.recupererFrequencesCumuleesDeTousLesBacs(idsFormations)
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
                idFormation,
            )
        return statistiquesDesAdmisBuilder.creerStatistiquesDesAdmis(frequencesCumulees, idBaccalaureat, classe)
    }

    private fun logguerLesFormationsSansStatistiques(statistiques: Map<String, StatistiquesDesAdmis?>) {
        val formationsSansStatistiques = statistiques.filter { it.value == null }.map { it.key }
        if (formationsSansStatistiques.isNotEmpty()) {
            logger.warn("Les formations suivantes n'ont pas de statistiques : $formationsSansStatistiques")
        }
    }
}
