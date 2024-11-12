package fr.gouv.monprojetsup.data.referentiel.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "ref_baccalaureat")
class BaccalaureatEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "nom", nullable = false)
    lateinit var nom: String

    @Column(name = "id_externe", nullable = false)
    lateinit var idExterne: String

    @Column(name = "id_carte_parcoursup", nullable = false)
    lateinit var idCarteParcoursup: String

}
