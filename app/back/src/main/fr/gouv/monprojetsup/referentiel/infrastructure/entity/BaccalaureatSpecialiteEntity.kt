package fr.gouv.monprojetsup.referentiel.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import java.io.Serializable

@Entity
@Table(name = "join_baccalaureat_specialite")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
class BaccalaureatSpecialiteEntity {
    @EmbeddedId
    lateinit var id: BaccalaureatSpecialiteId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_baccalaureat", insertable = false, updatable = false)
    lateinit var baccalaureat: BaccalaureatEntity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_specialite", insertable = false, updatable = false)
    lateinit var specialite: SpecialiteEntity
}

@Embeddable
class BaccalaureatSpecialiteId : Serializable {
    @Column(name = "id_baccalaureat")
    lateinit var idBaccalaureat: String

    @Column(name = "id_specialite")
    lateinit var idSpecialite: String
}
