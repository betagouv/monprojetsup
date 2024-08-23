package fr.gouv.monprojetsup.data.app.formation.entity

import fr.gouv.monprojetsup.data.domain.model.Voeu
import fr.gouv.monprojetsup.data.infrastructure.psup.DescriptifVoeu
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "triplet_affectation")
class VoeuEntity {
    constructor()
    constructor(voeu: Voeu, commune: String, codeCommune: String) {
        this.id = voeu.id
        this.nom = voeu.libelle
        this.commune = commune
        this.codeCommune = codeCommune
        this.coordonneesGeographiques = "${voeu.lat},${voeu.lng}"
        this.idFormation = voeu.formation
        this.descriptif = voeu.descriptif
        this.capacite = voeu.capacite
    }

    @Id
    lateinit var id: String

    @Column(name = "nom", nullable = false)
    //@Column(length = SuggestionsLabelEntity.MAX_LABEL_LENGTH)
    lateinit var nom: String

    @Column(name = "commune", nullable = false)
    lateinit var commune: String

    @Column(name = "code_commune", nullable = false)
    lateinit var codeCommune: String

    @Column(name = "coordonnees_geographiques", nullable = false)
    lateinit var coordonneesGeographiques: String

    @Column(name = "lat", nullable = true)
    var lat : Double? = null

    @Column(name = "lng", nullable = true)
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
            descriptif
        )
    }




}
