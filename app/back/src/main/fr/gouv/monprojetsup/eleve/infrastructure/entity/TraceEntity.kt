package fr.gouv.monprojetsup.eleve.infrastructure.entity

import fr.gouv.monprojetsup.eleve.domain.entity.Trace
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity(name = "Trace")
@Table(name = "trace")
class TraceEntity() {
    @Id
    @Column(name = "id", nullable = false)
    var id: Long = 0

    @Column(name = "id_eleve", nullable = false)
    lateinit var idEleve: String

    @Column(name = "tsp", nullable = false)
    lateinit var tsp: LocalDate

    @Column(name = "action", nullable = false)
    lateinit var action: String

    @Column(name = "param1", nullable = false)
    var param1: String? = null

    @Column(name = "param2", nullable = false)
    var param2: String? = null

    constructor(
        idEleve: String,
        tsp: LocalDate,
        action: String,
        param1: String?,
        param2: String?,
    ) : this() {
        this.idEleve = idEleve
        this.tsp = tsp
        this.action = action
        this.param1 = param1
        this.param2 = param2
    }

    fun toTrace() = Trace(
        tsp = tsp,
        action = action,
        param1 = param1,
        param2 = param2,
    )

}
