package fr.gouv.monprojetsup.referentiel.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable

@Entity
@Table(name = "ref_join_baccalaureat_specialite")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
class BaccalaureatSpecialiteEntity {
    @EmbeddedId
    lateinit var id: BaccalaureatSpecialiteId

    @Column(name = "id_baccalaureat", insertable = false, updatable = false)
    lateinit var idBaccalaureat: String
}

@Embeddable
data class BaccalaureatSpecialiteId(
    @Column(name = "id_baccalaureat")
    val idBaccalaureat: String = "",
    @Column(name = "id_specialite")
    val idSpecialite: String = "",
) : Serializable
