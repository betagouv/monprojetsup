package fr.gouv.monprojetsup.data.referentiel.entity

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "ref_join_baccalaureat_specialite")
class BaccalaureatSpecialiteEntity {

    @EmbeddedId
    lateinit var id: BaccalaureatSpecialiteId

    @Column(name = "id_specialite", insertable = false, updatable = false)
    lateinit var idSpecialite: String

    @Column(name = "id_baccalaureat", insertable = false, updatable = false)
    lateinit var idBaccalaureat: String
}

@Embeddable
data class BaccalaureatSpecialiteId(
    @Column(name = "id_baccalaureat")
    var idBaccalaureat: String = "",

    @Column(name = "id_specialite")
    var idSpecialite: String = ""

) : Serializable {

    constructor() : this("","")

}