package fr.gouv.monprojetsup.referentiel.application.dto

import fr.gouv.monprojetsup.referentiel.domain.entity.Referentiel

data class ReferentielDTO(
    val situations: List<String>,
    val choixNiveau: List<String>,
    val choixAlternance: List<String>,
    val choixDureeEtudesPrevue: List<String>,
    val baccalaureatsAvecLeurSpecialites: List<BaccalaureatAvecSesSpecialitesDTO>,
    val categoriesDInteretsAvecLeursSousCategories: List<CategorieInteretAevcSousCategoriesDTO>,
    val categoriesDomaineAvecLeursDomaines: List<CategorieDomaineAvecDomainesDTO>,
) {
    constructor(referentiel: Referentiel) : this(
        situations = referentiel.situations.map { it.jsonValeur },
        choixNiveau = referentiel.choixNiveau.map { it.jsonValeur },
        choixAlternance = referentiel.choixAlternance.map { it.jsonValeur },
        choixDureeEtudesPrevue = referentiel.choixDureeEtudesPrevue.map { it.jsonValeur },
        baccalaureatsAvecLeurSpecialites =
            referentiel.baccalaureatsAvecLeursSpecialites.map {
                BaccalaureatAvecSesSpecialitesDTO(
                    baccalaureat = BaccalaureatDTO(it.key),
                    specialites = it.value.map { SpecialitesDTO(it) },
                )
            },
        categoriesDInteretsAvecLeursSousCategories =
            referentiel.categoriesDInteretsAvecLeursSousCategories.map {
                CategorieInteretAevcSousCategoriesDTO(
                    categorieInteret = InteretCategorieDTO(it.key),
                    sousCategoriesInterets = it.value.map { InteretSousCategorieDTO(it) },
                )
            },
        categoriesDomaineAvecLeursDomaines =
            referentiel.categoriesDomaineAvecLeursDomaines.map {
                CategorieDomaineAvecDomainesDTO(
                    categorieDomaine = CategorieDomaineDTO(it.key),
                    domaines = it.value.map { DomaineDTO(it) },
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
