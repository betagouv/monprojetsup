package fr.gouv.monprojetsup.data.app.referentiel.entity

import jakarta.persistence.*

@Entity
@Table(name = "domaine_categorie")
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

}
