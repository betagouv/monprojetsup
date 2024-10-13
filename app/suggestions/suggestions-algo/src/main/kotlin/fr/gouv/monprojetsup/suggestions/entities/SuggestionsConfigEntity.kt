package fr.gouv.monprojetsup.suggestions.entities

import fr.gouv.monprojetsup.suggestions.algo.Config
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
@Table(name = "sugg_config")
class SuggestionsConfigEntity {

    constructor()

    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: UUID

    @Column(name = "active", nullable = false)
    var active: Boolean = false

    @Column(name = "description", nullable = false)
    var description: String = ""

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config", nullable = false, columnDefinition = "jsonb")
    var config: Config = Config()

}