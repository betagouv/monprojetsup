package fr.gouv.monprojetsup.data.infrastructure.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "metiers")
class MetierEntity {
    @Id
    @Column
    val id: String = ""

    @Column
    val label: String = ""

}