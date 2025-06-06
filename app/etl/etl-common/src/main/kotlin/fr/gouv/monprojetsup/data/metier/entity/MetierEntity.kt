package fr.gouv.monprojetsup.data.metier.entity

import fr.gouv.monprojetsup.data.commun.entity.LienEntity
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsLabelEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "ref_metier")
class MetierEntity {
    @Id
    @Column(name = "id")
    lateinit var id: String

    @Column(name = "label", length = SuggestionsLabelEntity.MAX_LABEL_LENGTH)
    lateinit var label: String

    @Column(name = "descriptif_general", nullable = true, columnDefinition = "text")
    var descriptifGeneral: String? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "liens", columnDefinition = "jsonb")
    var liens = arrayListOf<LienEntity>()

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "mots_cles", nullable = true)
    var motsCles: List<String>? = null


    @Column(nullable = false)
    var obsolete: Boolean = false

}
