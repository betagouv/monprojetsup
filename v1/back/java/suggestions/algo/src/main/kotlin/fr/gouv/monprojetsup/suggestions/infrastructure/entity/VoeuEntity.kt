package fr.gouv.monprojetsup.suggestions.infrastructure.entity

import fr.gouv.monprojetsup.suggestions.data.model.descriptifs.Descriptifs
import fr.gouv.monprojetsup.suggestions.domain.entity.Voeu
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.context.annotation.Lazy


@Entity
@Table(name = "voeux")
class VoeuEntity {
    fun toVoeu() : Voeu {
        return Voeu(
            id,
            formation,
            lat,
            lng,
            libelle,
            capacite,
            descriptif!!
        )
    }

    @Id
    @Column
    val id: String = ""

    @Column
    var formation: String = ""

    @Column
    val lat : Double? = null

    @Column
    val lng : Double? = null

    @Column
    val libelle : String = ""

    @Column
    val capacite : Int = 0

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    val descriptif : Descriptifs.Descriptif? = null


}