package fr.gouv.monprojetsup.recherche.infrastructure.entity

import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "baccalaureat")
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
