package fr.gouv.monprojetsup.referentiel.infrastructure.entity

import fr.gouv.monprojetsup.referentiel.domain.entity.CategorieDomaine
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "ref_domaine_categorie")
class CategorieDomaineEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "nom", nullable = false)
    lateinit var nom: String

    @Column(name = "emoji", nullable = false)
    lateinit var emoji: String

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "idCategorie")
    lateinit var domaines: List<DomaineEntity>

    fun toCategorieDomaine() =
        CategorieDomaine(
            id = id,
            nom = nom,
            emoji = emoji,
        )
}
