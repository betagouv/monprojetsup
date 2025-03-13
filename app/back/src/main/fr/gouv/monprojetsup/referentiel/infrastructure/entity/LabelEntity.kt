package fr.gouv.monprojetsup.referentiel.infrastructure.entity

import fr.gouv.monprojetsup.referentiel.domain.entity.Label
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "sugg_labels")
class LabelEntity {

    @Id
    var id: String = ""

    @Column(name = "label", nullable = false)
    var label: String = ""

    fun toLabel() =
        Label(
            id = id,
            nom = label,
        )

}
