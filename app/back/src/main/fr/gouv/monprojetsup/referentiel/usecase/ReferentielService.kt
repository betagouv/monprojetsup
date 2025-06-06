package fr.gouv.monprojetsup.referentiel.usecase

import fr.gouv.monprojetsup.commun.Constantes.ANNEE_DONNEES_PARCOURSUP
import fr.gouv.monprojetsup.commun.Constantes.TAILLE_ECHELLON_NOTES
import fr.gouv.monprojetsup.formation.domain.port.FrequencesCumuleesDesMoyenneDesAdmisRepository
import fr.gouv.monprojetsup.referentiel.domain.entity.AdmissionsParcoursup
import fr.gouv.monprojetsup.referentiel.domain.entity.AdmissionsParcoursup.PourcentagesPourChaqueMoyenneParBaccalaureat.PourcentagesMoyenne
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.Referentiel
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatSpecialiteRepository
import fr.gouv.monprojetsup.referentiel.domain.port.DomaineRepository
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReferentielService(
    val baccalaureatSpecialiteRepository: BaccalaureatSpecialiteRepository,
    val interetRepository: InteretRepository,
    val domaineRepository: DomaineRepository,
    val frequencesCumuleesDesMoyenneDesAdmisRepository: FrequencesCumuleesDesMoyenneDesAdmisRepository,
) {
    @Transactional(readOnly = true)
    fun recupererReferentiel(): Referentiel {
        val baccalaureatsAvecLeursSpecialites = baccalaureatSpecialiteRepository.recupererLesBaccalaureatsAvecLeursSpecialites()
        val categorieDInteretsAvecLeursSousCategories = interetRepository.recupererToutesLesCategoriesEtLeursSousCategoriesDInterets()
        val categorieDomaineAvecLeursDomaines = domaineRepository.recupererTousLesDomainesEtLeursCategories()
        return Referentiel(
            situations = SituationAvanceeProjetSup.entries,
            baccalaureatsAvecLeursSpecialites = baccalaureatsAvecLeursSpecialites,
            choixNiveau = ChoixNiveau.entries,
            choixAlternance = ChoixAlternance.entries,
            choixDureeEtudesPrevue = ChoixDureeEtudesPrevue.entries,
            categoriesDInteretsAvecLeursSousCategories = categorieDInteretsAvecLeursSousCategories,
            categoriesDomaineAvecLeursDomaines = categorieDomaineAvecLeursDomaines,
            admissionsParcoursup = creerAdmissionsParcoursup(),
        )
    }

    private fun creerAdmissionsParcoursup(): AdmissionsParcoursup {
        val frequencesCumuleesParBaccalaureat =
            frequencesCumuleesDesMoyenneDesAdmisRepository.recupererFrequencesCumuleesParBacs(
                ANNEE_DONNEES_PARCOURSUP,
            )
        return AdmissionsParcoursup(
            annee = ANNEE_DONNEES_PARCOURSUP,
            parBaccalaureat =
                frequencesCumuleesParBaccalaureat.map { entry ->
                    AdmissionsParcoursup.PourcentagesPourChaqueMoyenneParBaccalaureat(
                        baccalaureat = entry.key,
                        pourcentages = calculerpourcentageAdmisAyantCetteMoyenneOuMoins(entry.value),
                    )
                },
        )
    }

    private fun calculerpourcentageAdmisAyantCetteMoyenneOuMoins(frequencesCumulees: List<Int>): List<PourcentagesMoyenne> {
        val totalAdmisBaccalaureat = frequencesCumulees.last()
        val pourcentages =
            frequencesCumulees.mapIndexed { index, frequenceCumulee ->
                val pourcentageAdmisAyantCetteMoyenneOuMoins = ((frequenceCumulee.toFloat() / totalAdmisBaccalaureat) * 100).toInt()
                PourcentagesMoyenne(
                    note = index * TAILLE_ECHELLON_NOTES,
                    pourcentageAdmisAyantCetteMoyenneOuMoins = pourcentageAdmisAyantCetteMoyenneOuMoins,
                )
            }
        return pourcentages
    }
}
