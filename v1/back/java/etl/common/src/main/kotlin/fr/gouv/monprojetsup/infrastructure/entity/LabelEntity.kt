package fr.gouv.monprojetsup.data.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "labels")
class LabelEntity {
    @Id
    @Column
    val id: String = ""

    @Column
    val label: String = ""

}
