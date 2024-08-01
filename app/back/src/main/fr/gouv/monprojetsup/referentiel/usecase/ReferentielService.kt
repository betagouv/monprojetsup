package fr.gouv.monprojetsup.referentiel.usecase

import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.Referentiel
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatSpecialiteRepository
import fr.gouv.monprojetsup.referentiel.domain.port.DomaineRepository
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import org.springframework.stereotype.Service

@Service
class ReferentielService(
    val baccalaureatSpecialiteRepository: BaccalaureatSpecialiteRepository,
    val interetRepository: InteretRepository,
    val domaineRepository: DomaineRepository,
) {
    fun recupererReferentiel(): Referentiel {
        val baccalaureatsAvecLeursSpecialites = baccalaureatSpecialiteRepository.recupererLesBaccalaureatsAvecLeursSpecialites()
        val categorieDInteretsAvecLeursSousCategories = interetRepository.recupererToutesLesCategoriesEtLeursSousCategoriesDInterets()
        val categorieDomaineAvecLeursDomaines = domaineRepository.recupererTousLesDomainesEtLeursCategories()
        return Referentiel(
            situations =
                listOf(
                    SituationAvanceeProjetSup.AUCUNE_IDEE,
                    SituationAvanceeProjetSup.QUELQUES_PISTES,
                    SituationAvanceeProjetSup.PROJET_PRECIS,
                ),
            baccalaureatsAvecLeursSpecialites = baccalaureatsAvecLeursSpecialites,
            choixNiveau =
                listOf(
                    ChoixNiveau.SECONDE,
                    ChoixNiveau.PREMIERE,
                    ChoixNiveau.TERMINALE,
                    ChoixNiveau.NON_RENSEIGNE,
                ),
            choixAlternance =
                listOf(
                    ChoixAlternance.PAS_INTERESSE,
                    ChoixAlternance.INDIFFERENT,
                    ChoixAlternance.INTERESSE,
                    ChoixAlternance.TRES_INTERESSE,
                    ChoixAlternance.NON_RENSEIGNE,
                ),
            choixDureeEtudesPrevue =
                listOf(
                    ChoixDureeEtudesPrevue.INDIFFERENT,
                    ChoixDureeEtudesPrevue.COURTE,
                    ChoixDureeEtudesPrevue.LONGUE,
                    ChoixDureeEtudesPrevue.AUCUNE_IDEE,
                    ChoixDureeEtudesPrevue.NON_RENSEIGNE,
                ),
            categoriesDInteretsAvecLeursSousCategories = categorieDInteretsAvecLeursSousCategories,
            categoriesDomaineAvecLeursDomaines = categorieDomaineAvecLeursDomaines,
        )
    }
}
