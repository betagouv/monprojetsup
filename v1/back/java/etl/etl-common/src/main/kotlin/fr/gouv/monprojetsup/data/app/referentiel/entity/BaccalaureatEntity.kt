package fr.gouv.monprojetsup.data.app.referentiel.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy

@Entity
@Table(name = "baccalaureat")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
class BaccalaureatEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "nom", nullable = false)
    lateinit var nom: String

    @Column(name = "id_externe", nullable = false)
    lateinit var idExterne: String

}
