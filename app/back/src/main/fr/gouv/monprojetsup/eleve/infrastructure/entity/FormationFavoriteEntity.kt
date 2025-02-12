package fr.gouv.monprojetsup.eleve.infrastructure.entity

import fr.gouv.monprojetsup.eleve.domain.entity.FormationFavorite
import java.io.Serializable

data class FormationFavoriteEntity(
    val idFormation: String,
    val niveauAmbition: Int,
    val priseDeNote: String?,
) : Serializable {
    constructor(formation: FormationFavorite) : this(
        idFormation = formation.idFormation,
        niveauAmbition = formation.niveauAmbition,
        priseDeNote = formation.priseDeNote,
    )

    fun toFormationFavorite() =
        FormationFavorite(
            idFormation = idFormation,
            niveauAmbition = niveauAmbition,
            priseDeNote = priseDeNote,
        )
}
