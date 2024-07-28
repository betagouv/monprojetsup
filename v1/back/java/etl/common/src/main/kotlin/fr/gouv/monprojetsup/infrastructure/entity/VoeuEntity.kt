package fr.gouv.monprojetsup.data.infrastructure.entity

import fr.gouv.monprojetsup.data.model.descriptifs.Descriptifs
import fr.gouv.monprojetsup.data.domain.entity.Voeu
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