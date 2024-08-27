package fr.gouv.monprojetsup.data.referentiel.entity

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "join_baccalaureat_specialite")
class BaccalaureatSpecialiteEntity {

    @EmbeddedId
    lateinit var id: BaccalaureatSpecialiteId

    @Column(name = "id_specialite", insertable = false, updatable = false)
    lateinit var idSpecialite: String

    @Column(name = "id_baccalaureat", insertable = false, updatable = false)
    lateinit var idBaccalaureat: String
}

@Embeddable
class BaccalaureatSpecialiteId : Serializable {

    constructor()
    constructor(idBaccalaureat: String, idSpecialite: String) {
        this.idBaccalaureat = idBaccalaureat
        this.idSpecialite = idSpecialite
    }

    @Column(name = "id_baccalaureat")
    lateinit var idBaccalaureat: String

    @Column(name = "id_specialite")
    lateinit var idSpecialite: String
}