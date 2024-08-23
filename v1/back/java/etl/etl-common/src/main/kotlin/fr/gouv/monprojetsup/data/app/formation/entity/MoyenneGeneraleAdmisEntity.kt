package fr.gouv.monprojetsup.data.app.formation.entity

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.io.Serializable

@Entity
@Table(name = "moyenne_generale_admis")
class MoyenneGeneraleAdmisEntity {
    @EmbeddedId
    lateinit var id: MoyenneGeneraleAdmisId

    //taille 40 à ne pas changer
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "frequences_cumulees")
    lateinit var frequencesCumulees: List<Int>

    @Column(name = "annee")
    lateinit var annee: String

    @Column(name = "id_formation")
    lateinit var idFormation: String

    @Column(name = "id_bac")
    lateinit var idBaccalaureat: String
}

@Embeddable
class MoyenneGeneraleAdmisId : Serializable {
    constructor()
    constructor(annee: String, idFormation: String, idBaccalaureat: String) {
        this.annee = annee
        this.idFormation = idFormation
        this.idBaccalaureat = idBaccalaureat
    }

    @Column(name = "annee")
    lateinit var annee: String

    @Column(name = "id_formation")
    lateinit var idFormation: String

    @Column(name = "id_bac")
    lateinit var idBaccalaureat: String
}
