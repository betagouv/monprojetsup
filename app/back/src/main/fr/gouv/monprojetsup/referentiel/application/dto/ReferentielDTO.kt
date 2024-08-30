package fr.gouv.monprojetsup.referentiel.application.dto

import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.Referentiel
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup

data class ReferentielDTO(
    val situations: List<SituationAvanceeProjetSup>,
    val choixNiveau: List<ChoixNiveau>,
    val choixAlternance: List<ChoixAlternance>,
    val choixDureeEtudesPrevue: List<ChoixDureeEtudesPrevue>,
    val baccalaureatsAvecLeurSpecialites: List<BaccalaureatAvecSesSpecialitesDTO>,
    val categoriesDInteretsAvecLeursSousCategories: List<CategorieInteretAevcSousCategoriesDTO>,
    val categoriesDomaineAvecLeursDomaines: List<CategorieDomaineAvecDomainesDTO>,
) {
    constructor(referentiel: Referentiel) : this(
        situations = referentiel.situations,
        choixNiveau = referentiel.choixNiveau,
        choixAlternance = referentiel.choixAlternance,
        choixDureeEtudesPrevue = referentiel.choixDureeEtudesPrevue,
        baccalaureatsAvecLeurSpecialites =
            referentiel.baccalaureatsAvecLeursSpecialites.map {
                BaccalaureatAvecSesSpecialitesDTO(
                    baccalaureat = BaccalaureatDTO(it.key),
                    specialites = it.value.map { specialite -> SpecialitesDTO(specialite) },
                )
            },
        categoriesDInteretsAvecLeursSousCategories =
            referentiel.categoriesDInteretsAvecLeursSousCategories.map {
                CategorieInteretAevcSousCategoriesDTO(
                    categorieInteret = InteretCategorieDTO(it.key),
                    sousCategoriesInterets = it.value.map { interet -> InteretSousCategorieDTO(interet) },
                )
            },
        categoriesDomaineAvecLeursDomaines =
            referentiel.categoriesDomaineAvecLeursDomaines.map {
                CategorieDomaineAvecDomainesDTO(
                    categorieDomaine = CategorieDomaineDTO(it.key),
                    domaines = it.value.map { domaine -> DomaineDTO(domaine) },
                )
            },
    )

    data class BaccalaureatAvecSesSpecialitesDTO(
        val baccalaureat: BaccalaureatDTO,
        val specialites: List<SpecialitesDTO>,
    )

    data class CategorieDomaineAvecDomainesDTO(
        val categorieDomaine: CategorieDomaineDTO,
        val domaines: List<DomaineDTO>,
    )

    data class CategorieInteretAevcSousCategoriesDTO(
        val categorieInteret: InteretCategorieDTO,
        val sousCategoriesInterets: List<InteretSousCategorieDTO>,
    )
}
