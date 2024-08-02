package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.suggestions.domain.model.Voeu
import fr.gouv.monprojetsup.suggestions.infrastructure.psup.DescriptifVoeu
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes


@Entity
@Table(name = "sugg_voeux")
class SuggestionsVoeuEntity {

    constructor()
    constructor(voeu: Voeu) {
        this.id = voeu.id
        this.formation = voeu.formation
        this.lat = voeu.lat
        this.lng = voeu.lng
        this.libelle = voeu.libelle
        this.capacite = voeu.capacite
        this.descriptif = voeu.descriptif
    }

    fun toVoeu() : Voeu {
        return Voeu(
            id,
            formation,
            lat,
            lng,
            libelle,
            capacite,
            descriptif
        )
    }

    @Id
    @Column
    var id: String = ""

    @Column
    var formation: String = ""

    @Column
    var lat : Double? = null

    @Column
    var lng : Double? = null

    @Column
    var libelle : String = ""

    @Column
    var capacite : Int = 0

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    var descriptif : DescriptifVoeu? = null


}