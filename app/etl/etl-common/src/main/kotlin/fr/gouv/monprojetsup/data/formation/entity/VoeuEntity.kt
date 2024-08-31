package fr.gouv.monprojetsup.data.formation.entity

import fr.gouv.monprojetsup.data.domain.model.Voeu
import fr.gouv.monprojetsup.data.domain.model.psup.DescriptifVoeu
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsLabelEntity
import io.hypersistence.utils.hibernate.type.array.ListArrayType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.Type
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
        this.coordonneesGeographiques = listOf(voeu.lat, voeu.lng)
        this.idFormation = voeu.formation
        this.descriptif = voeu.descriptif
        this.capacite = voeu.capacite
    }

    constructor(p0: Any)

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

    //@Deprecated("Replaced by latitude and longitude fields")
    @Column(name = "coordonnees_geographiques", nullable = false)
    lateinit var coordonneesGeographiques: List<Double?>

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
