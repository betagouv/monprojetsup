package fr.gouv.monprojetsup.data.suggestions.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "suggestions_metiers")
class SuggestionsMetierEntity {

    constructor()

    @Id
    var id: String = ""

    var label: String = ""

}