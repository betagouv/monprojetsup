package fr.gouv.monprojetsup.data.referentiel.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "ref_domaine_ideo")
class DomaineIdeoEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "nom", nullable = false)
    lateinit var nom: String

    @Column(name = "id_domaine_mps", nullable = false)
    lateinit var idDomaineMps: String

}
