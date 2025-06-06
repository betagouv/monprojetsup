package fr.gouv.monprojetsup.formation.infrastructure.entity

import fr.gouv.monprojetsup.referentiel.infrastructure.entity.BaccalaureatEntity

data class MoyenneGeneraleAdmisQuery(
    val baccalaureatEntity: BaccalaureatEntity,
    val moyenneGeneraleAdmisEntity: MoyenneGeneraleAdmisEntity,
)
