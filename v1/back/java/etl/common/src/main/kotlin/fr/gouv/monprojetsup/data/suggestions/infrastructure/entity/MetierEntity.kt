package fr.gouv.monprojetsup.data.suggestions.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "metiers")
class MetierEntity {
    @Id
    @Column
    val id: String = ""

    @Column
    val label: String = ""

}