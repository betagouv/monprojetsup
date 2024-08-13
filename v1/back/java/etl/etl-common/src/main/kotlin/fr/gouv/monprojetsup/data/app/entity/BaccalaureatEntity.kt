package fr.gouv.monprojetsup.data.app.entity

import fr.gouv.monprojetsup.data.app.domain.Baccalaureat
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

    fun toBaccalaureat() =
        Baccalaureat(
            id = id,
            idExterne = idExterne,
            nom = nom,
        )
}
