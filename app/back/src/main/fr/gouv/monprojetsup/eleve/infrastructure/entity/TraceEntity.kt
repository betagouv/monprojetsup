package fr.gouv.monprojetsup.eleve.infrastructure.entity

import fr.gouv.monprojetsup.eleve.domain.entity.ActionEleve
import fr.gouv.monprojetsup.eleve.domain.entity.Trace
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity(name = "Trace")
@Table(name = "trace")
class TraceEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = 0

    @Column(name = "id_eleve", nullable = false)
    lateinit var idEleve: String

    @Column(name = "tsp", nullable = false)
    lateinit var tsp: LocalDateTime

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    lateinit var actionEleve: ActionEleve

    @Column(name = "param1", nullable = true)
    var param1: String? = null

    @Column(name = "param2", nullable = true)
    var param2: String? = null

    constructor(
        idEleve: String,
        tsp: LocalDateTime,
        actionEleve: ActionEleve,
        param1: String?,
        param2: String?,
    ) : this() {
        this.idEleve = idEleve
        this.tsp = tsp
        this.actionEleve = actionEleve
        this.param1 = param1
        this.param2 = param2
    }

    fun toTrace() =
        Trace(
            actionEleve = actionEleve,
            param1 = param1,
            param2 = param2,
            tsp = tsp,
        )
}
