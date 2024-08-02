package fr.gouv.monprojetsup.authentification.domain.entity

import fr.gouv.monprojetsup.eleve.domain.entity.Commune
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup

data class ModificationProfilEleve(
    val situation: SituationAvanceeProjetSup? = null,
    val classe: ChoixNiveau? = null,
    val baccalaureat: String? = null,
    val specialites: List<String>? = null,
    val domainesInterets: List<String>? = null,
    val centresInterets: List<String>? = null,
    val metiersFavoris: List<String>? = null,
    val dureeEtudesPrevue: ChoixDureeEtudesPrevue? = null,
    val alternance: ChoixAlternance? = null,
    val communesFavorites: List<Commune>? = null,
    val formationsFavorites: List<String>? = null,
    val moyenneGenerale: Float? = null,
    val corbeilleFormations: List<String>? = null,
)
