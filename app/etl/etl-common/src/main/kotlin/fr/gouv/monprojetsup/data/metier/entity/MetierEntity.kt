package fr.gouv.monprojetsup.data.metier.entity

import fr.gouv.monprojetsup.data.commun.entity.LienEntity
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsLabelEntity
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type

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

    @Type(JsonType::class)
    @Column(name = "liens", columnDefinition = "jsonb")
    var liens = arrayListOf<LienEntity>()
}
