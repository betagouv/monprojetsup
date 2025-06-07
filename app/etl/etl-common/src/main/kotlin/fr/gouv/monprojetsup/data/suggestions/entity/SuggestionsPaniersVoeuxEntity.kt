package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.data.model.PanierVoeux
import fr.gouv.monprojetsup.data.model.psup.LettreMotivation
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "sugg_paniers_voeux")
class SuggestionsPaniersVoeuxEntity {

    constructor()

    @Id
    @GeneratedValue
    var id: Long = 0

    constructor(c: PanierVoeux) {
        this.bac = c.bac
        this.voeux = ArrayList(c.voeux)
        this.lettres = c.lettres
    }

    lateinit var bac: String

    @JdbcTypeCode(SqlTypes.ARRAY)
    lateinit var voeux: List<String>

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "lettres", columnDefinition = "jsonb")
    var lettres: List<LettreMotivation> = listOf()

}
