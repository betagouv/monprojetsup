package fr.gouv.monprojetsup.eleve.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity(name = "CompteParcoursup")
@Table(name = "eleve_compte_parcoursup")
class CompteParcoursupEntity() {
    @Id
    @Column(name = "id_eleve", nullable = false)
    lateinit var idEleve: String

    @Column(name = "id_parcoursup", nullable = false)
    var idParcoursup: Int = 0

    @Column(name = "date_mise_a_jour", nullable = false)
    lateinit var dateMiseAJour: LocalDate

    constructor(
        idEleve: String,
        idParcoursup: Int,
        dateMiseAJour: LocalDate,
    ) : this() {
        this.idEleve = idEleve
        this.idParcoursup = idParcoursup
        this.dateMiseAJour = dateMiseAJour
    }
}
