package fr.gouv.monprojetsup.suggestions.infrastructure.entity

import fr.gouv.monprojetsup.suggestions.data.model.descriptifs.Descriptifs
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "voeux")
class VoeuEntity {
    @Id
    @Column
    val id: String = ""

    @Column
    val formation : String = ""

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