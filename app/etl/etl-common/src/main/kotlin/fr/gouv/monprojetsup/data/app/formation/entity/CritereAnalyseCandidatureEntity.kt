package fr.gouv.monprojetsup.data.app.formation.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "critere_analyse_candidature")
class CritereAnalyseCandidatureEntity {
    @Id
    lateinit var id: String

    @Column(name = "nom", nullable = false)
    lateinit var nom: String

    @Column(name = "index", nullable = false)
    var index: Int = -1
}
