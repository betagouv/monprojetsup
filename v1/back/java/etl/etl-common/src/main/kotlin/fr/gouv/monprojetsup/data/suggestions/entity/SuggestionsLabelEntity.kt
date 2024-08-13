package fr.gouv.monprojetsup.data.suggestions.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "suggestions_labels")
class SuggestionsLabelEntity {

    constructor()

    companion object {
        const val MAX_LABEL_LENGTH: Int = 1023
    }

    constructor(key: String, label: String, labelDebug: String?) {
        if(label.length > MAX_LABEL_LENGTH)
            throw RuntimeException("Label too long id:$id label:$label")
        if(labelDebug != null && labelDebug.length > MAX_LABEL_LENGTH)
            throw RuntimeException("labelDebug too long id:$id labelDebug:$labelDebug")
        this.id = key
        this.label = label
        if (labelDebug == null || labelDebug == label)
            this.labelDebug = null
        else
            this.labelDebug = labelDebug
    }

    @Id
    var id: String = ""

    @Column(length = MAX_LABEL_LENGTH)
    var label: String = ""

    @Column(length = MAX_LABEL_LENGTH)
    var labelDebug: String? = null

}
