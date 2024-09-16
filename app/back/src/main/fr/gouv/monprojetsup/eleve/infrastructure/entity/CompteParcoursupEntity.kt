package fr.gouv.monprojetsup.eleve.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.Date
import java.util.UUID

@Entity
@Table(name = "eleve_compte_parcoursup")
class CompteParcoursupEntity() {
    @Id
    @Column(name = "id_eleve", nullable = false)
    lateinit var idEleve: UUID

    @Column(name = "id_parcoursup", nullable = false)
    var idParcoursup: Int = 0

    @Column(name = "date_mise_a_jour", nullable = false)
    lateinit var dateMiseAJour: Date
}
