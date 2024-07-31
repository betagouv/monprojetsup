package fr.gouv.monprojetsup.data.app.entity

import fr.gouv.monprojetsup.data.app.domain.Domaine
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "domaine_categorie")
class DomaineCategoryEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "nom", nullable = false)
    lateinit var nom: String

    @Column(name = "emoji", nullable = false)
    lateinit var emoji: String

    fun toDomaine() =
        Domaine(
            id = id,
            nom = nom,
        )
}
