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
            situations = SituationAvanceeProjetSup.entries,
            baccalaureatsAvecLeursSpecialites = baccalaureatsAvecLeursSpecialites,
            choixNiveau = ChoixNiveau.entries,
            choixAlternance = ChoixAlternance.entries,
            choixDureeEtudesPrevue = ChoixDureeEtudesPrevue.entries,
            categoriesDInteretsAvecLeursSousCategories = categorieDInteretsAvecLeursSousCategories,
            categoriesDomaineAvecLeursDomaines = categorieDomaineAvecLeursDomaines,
        )
    }
}
