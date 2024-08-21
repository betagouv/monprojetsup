package fr.gouv.monprojetsup.eleve.infrastructure.entity

import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFormation
import java.io.Serializable

data class VoeuEntity(
    val idFormation: String,
    val niveauAmbition: Int,
    val tripletsAffectationsChoisis: List<String>,
    val priseDeNote: String?,
) : Serializable {
    constructor(voeu: VoeuFormation) : this(
        idFormation = voeu.idFormation,
        niveauAmbition = voeu.niveauAmbition,
        tripletsAffectationsChoisis = voeu.tripletsAffectationsChoisis,
        priseDeNote = voeu.priseDeNote,
    )

    fun toVoeuFormation() =
        VoeuFormation(
            idFormation = idFormation,
            niveauAmbition = niveauAmbition,
            tripletsAffectationsChoisis = tripletsAffectationsChoisis,
            priseDeNote = priseDeNote,
        )
}
