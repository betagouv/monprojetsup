package fr.gouv.monprojetsup.formation.infrastructure.entity

import fr.gouv.monprojetsup.formation.domain.entity.CommuneCourte
import fr.gouv.monprojetsup.formation.domain.entity.Voeu

data class JoinFormationVoeuQuery(
    val idFormation: String,
    val idVoeu: String,
    val nomVoeu: String,
    val nomCommune: String,
    val codeCommune: String,
    val latitude: Double,
    val longitude: Double,
    val obsolete: Boolean,
) {
    constructor(jointureEntity: JoinFormationVoeuEntity, voeuEntity: VoeuEntity) : this(
        idFormation = jointureEntity.id.idFormation,
        idVoeu = jointureEntity.id.idVoeu,
        nomVoeu = voeuEntity.nom,
        nomCommune = voeuEntity.commune,
        codeCommune = voeuEntity.codeCommune,
        latitude = voeuEntity.latitude,
        longitude = voeuEntity.longitude,
        obsolete = voeuEntity.obsolete,
    )

    fun toVoeu() =
        Voeu(
            id = idVoeu,
            nom = nomVoeu,
            commune =
                CommuneCourte(
                    nom = nomCommune,
                    codeInsee = codeCommune,
                ),
            latitude = latitude,
            longitude = longitude,
        )
}
