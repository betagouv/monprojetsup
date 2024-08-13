package fr.gouv.monprojetsup.data.app.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "interet")
class InteretEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "nom", nullable = false)
    lateinit var nom: String

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_sous_categorie")
    lateinit var sousCategorie: InteretSousCategorieEntity
}
