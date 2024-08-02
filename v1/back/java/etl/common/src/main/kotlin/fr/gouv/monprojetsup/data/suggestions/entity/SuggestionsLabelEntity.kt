package fr.gouv.monprojetsup.data.suggestions.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "sugg_labels")
class SuggestionsLabelEntity {
    constructor(key: String, label: String, labelDebug: String?) {
        this.id = key
        this.label = label
        this.labelDebug = labelDebug
    }

    @Id
    @Column
    val id: String

    @Column
    val label: String

    @Column
    val labelDebug: String?

}
