package fr.gouv.monprojetsup.data.formation.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "ref_join_ville_voeu")
class VilleVoeuxEntity {

    //code insee ou nom de la ville (double indexation)
    @Id
    @Column(name = "id_ville", nullable = false)
    lateinit var idVille: String

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "distances_voeux_km", nullable = false, columnDefinition = "jsonb")
    var distancesVoeuxKm: Map<String,Int> = HashMap()

}
