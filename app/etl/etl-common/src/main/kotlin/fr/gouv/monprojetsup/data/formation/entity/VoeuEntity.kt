package fr.gouv.monprojetsup.data.formation.entity

import fr.gouv.monprojetsup.data.model.Voeu
import fr.gouv.monprojetsup.data.model.psup.DescriptifVoeu
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsLabelEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "ref_triplet_affectation")
class VoeuEntity {
    constructor()
    constructor(voeu: Voeu) {
        this.id = voeu.id
        this.nom = voeu.libelle
        this.commune = voeu.commune
        this.codeCommune = voeu.codeCommune
        this.lat = voeu.lat
        this.lng = voeu.lng
        this.idFormation = voeu.formation
        this.descriptif = voeu.descriptif
        this.capacite = voeu.capacite
    }

    @Id
    lateinit var id: String

    @Column(name = "nom", nullable = false, length = SuggestionsLabelEntity.MAX_LABEL_LENGTH)
    lateinit var nom: String

    @Column(name = "commune", nullable = false)
    lateinit var commune: String

    @Column(name = "code_commune", nullable = false)
    lateinit var codeCommune: String

    @Column(name = "latitude", nullable = true)
    var lat : Double? = null

    @Column(name = "longitude", nullable = true)
    var lng : Double? = null

    @Column(name = "id_formation", nullable = false)
    var idFormation: String = ""

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "descriptif", nullable = false)
    var descriptif : DescriptifVoeu? = null

    @Column(name = "capacite", nullable = false)
    var capacite : Int = 0

    fun toVoeu() : Voeu {
        return Voeu(
            id,
            idFormation,
            lat,
            lng,
            nom,
            capacite,
            descriptif,
            commune,
            codeCommune
        )
    }




}
