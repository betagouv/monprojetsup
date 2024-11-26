package fr.gouv.monprojetsup.referentiel.application.dto

import fr.gouv.monprojetsup.referentiel.domain.entity.AdmissionsParcoursup
import fr.gouv.monprojetsup.referentiel.domain.entity.AdmissionsParcoursup.PourcentagesPourChaqueMoyenneParBaccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.AdmissionsParcoursup.PourcentagesPourChaqueMoyenneParBaccalaureat.PourcentagesMoyenne
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
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
    val categoriesDInteretsAvecLeursSousCategories: List<CategorieInteretAvecSousCategoriesDTO>,
    val categoriesDomaineAvecLeursDomaines: List<CategorieDomaineAvecDomainesDTO>,
    val admissionsParcoursup: AdmissionsParcoursupDTO,
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
                CategorieInteretAvecSousCategoriesDTO(
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
        admissionsParcoursup = AdmissionsParcoursupDTO(referentiel.admissionsParcoursup),
    )

    data class BaccalaureatDTO(
        val id: String,
        val nom: String,
        val idCarteParcoursup: String,
    ) {
        constructor(baccalaureat: Baccalaureat) : this(
            id = baccalaureat.id,
            nom = baccalaureat.nom,
            idCarteParcoursup = baccalaureat.idCarteParcoursup,
        )
    }

    data class BaccalaureatAvecSesSpecialitesDTO(
        val baccalaureat: BaccalaureatDTO,
        val specialites: List<SpecialitesDTO>,
    )

    data class CategorieDomaineAvecDomainesDTO(
        val categorieDomaine: CategorieDomaineDTO,
        val domaines: List<DomaineDTO>,
    )

    data class CategorieInteretAvecSousCategoriesDTO(
        val categorieInteret: InteretCategorieDTO,
        val sousCategoriesInterets: List<InteretSousCategorieDTO>,
    )

    data class AdmissionsParcoursupDTO(
        val annee: String,
        val parBaccalaureat: List<PourcentagesPourChaqueMoyenneParBaccalaureatDTO>,
    ) {
        constructor(admissionsParcoursup: AdmissionsParcoursup) : this(
            annee = admissionsParcoursup.annee,
            parBaccalaureat = admissionsParcoursup.parBaccalaureat.map { PourcentagesPourChaqueMoyenneParBaccalaureatDTO(it) },
        )

        data class PourcentagesPourChaqueMoyenneParBaccalaureatDTO(
            val baccalaureat: BaccalaureatDTO,
            val pourcentages: List<PourcentagesMoyenneDTO>,
        ) {
            constructor(pourcentagesPourChaqueMoyenneParBaccalaureat: PourcentagesPourChaqueMoyenneParBaccalaureat) : this(
                baccalaureat = BaccalaureatDTO(pourcentagesPourChaqueMoyenneParBaccalaureat.baccalaureat),
                pourcentages = pourcentagesPourChaqueMoyenneParBaccalaureat.pourcentages.map { PourcentagesMoyenneDTO(it) },
            )

            data class PourcentagesMoyenneDTO(
                val note: Float,
                val pourcentageAdmisAyantCetteMoyenneOuMoins: Int,
            ) {
                constructor(pourcentagesMoyenne: PourcentagesMoyenne) : this(
                    note = pourcentagesMoyenne.note,
                    pourcentageAdmisAyantCetteMoyenneOuMoins = pourcentagesMoyenne.pourcentageAdmisAyantCetteMoyenneOuMoins,
                )
            }
        }
    }
}
