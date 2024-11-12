package fr.gouv.monprojetsup.referentiel.infrastructure.entity

import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "ref_domaine")
class DomaineEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "nom", nullable = false)
    lateinit var nom: String

    @Column(name = "description", nullable = true)
    var description: String? = null

    @Column(name = "emoji", nullable = false)
    lateinit var emoji: String

    @Column(name = "id_categorie", nullable = false)
    lateinit var idCategorie: String

    fun toDomaine() =
        Domaine(
            id = id,
            nom = nom,
            description = description,
            emoji = emoji,
        )
}
