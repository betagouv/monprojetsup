package fr.gouv.monprojetsup.data.parametre.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "parametre")
class ParametreEntity {

    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "statut", nullable = false)
    var statut: Boolean = true

}