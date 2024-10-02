package fr.gouv.monprojetsup.data.formation.entity

import fr.gouv.monprojetsup.data.formation.entity.VilleVoeuxEntity.Companion.REF_JOIN_VILLE_VOEU
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = REF_JOIN_VILLE_VOEU)
class VilleVoeuxEntity {

    //code insee
    @Id
    @Column(name = "id_ville", nullable = false)
    lateinit var idVille: String

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "distances_voeux_km", nullable = false, columnDefinition = "jsonb")
    var distancesVoeuxKm: Map<String,Int> = HashMap()

    companion object {
        const val REF_JOIN_VILLE_VOEU = "ref_join_ville_voeu"
    }
}
