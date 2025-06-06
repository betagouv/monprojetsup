package fr.gouv.monprojetsup.referentiel.infrastructure.entity

import fr.gouv.monprojetsup.referentiel.domain.entity.Interet
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "ref_interet")
class InteretEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "nom", nullable = false)
    lateinit var nom: String

    @Column(name = "id_sous_categorie", nullable = false)
    lateinit var idSousCategorie: String

    fun toInteret() = Interet(id = id, nom = nom)
}
