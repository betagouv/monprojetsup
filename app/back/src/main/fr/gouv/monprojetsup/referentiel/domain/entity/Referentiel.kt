package fr.gouv.monprojetsup.referentiel.domain.entity

data class Referentiel(
    val situations: List<SituationAvanceeProjetSup>,
    val choixNiveau: List<ChoixNiveau>,
    val choixAlternance: List<ChoixAlternance>,
    val choixDureeEtudesPrevue: List<ChoixDureeEtudesPrevue>,
    val baccalaureatsAvecLeursSpecialites: Map<Baccalaureat, List<Specialite>>,
    val categoriesDInteretsAvecLeursSousCategories: Map<InteretCategorie, List<InteretSousCategorie>>,
    val categoriesDomaineAvecLeursDomaines: Map<CategorieDomaine, List<Domaine>>,
)
