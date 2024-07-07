package fr.gouv.monprojetsup.formation.infrastructure.entity

import fr.gouv.monprojetsup.formation.domain.entity.Domaine
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "domaine")
class DomaineEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "nom", nullable = false)
    lateinit var nom: String

    @Column(name = "emoji", nullable = false)
    lateinit var emoji: String

    @Column(name = "id_categorie", nullable = false)
    lateinit var idCategorie: String

    fun toDomaine() =
        Domaine(
            id = id,
            nom = nom,
        )
}
