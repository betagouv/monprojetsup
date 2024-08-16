package fr.gouv.monprojetsup.data.app.referentiel.entity

import jakarta.persistence.*
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable

@Entity
@Table(name = "join_baccalaureat_specialite")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
class BaccalaureatSpecialiteEntity {
    @EmbeddedId
    lateinit var id: BaccalaureatSpecialiteId

    @Column(name = "id_baccalaureat", insertable = false, updatable = false)
    lateinit var idBaccalaureat: String
}

@Embeddable
class BaccalaureatSpecialiteId : Serializable {
    @Column(name = "id_baccalaureat")
    lateinit var idBaccalaureat: String

    @Column(name = "id_specialite")
    lateinit var idSpecialite: String
}
