package fr.gouv.monprojetsup.data.formation.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "ref_join_formation_voeu")
class FormationVoeuEntity {

    constructor() {
        this.idFormation = ""
        this.idVoeu = ""
        this.id = FormationVoeuEntityId("", "")
    }

    constructor(idFormation: String, idVoeu: String) {
        this.idFormation = idFormation
        this.idVoeu = idVoeu
        this.id = FormationVoeuEntityId(idFormation, idVoeu)
    }

    @EmbeddedId
    var id: FormationVoeuEntityId

    @Column(name = "id_formation", nullable = false, insertable = false, updatable = false)
    var idFormation: String

    @Column(name = "id_voeu", nullable = false, insertable = false, updatable = false)
    var idVoeu: String

}

@Embeddable
data class FormationVoeuEntityId(
    @Column(name = "id_formation", nullable = false)
    val idFormation: String,

    @Column(name = "id_voeu", nullable = false)
    val idVoeu: String
) {
    @Suppress("unused")
    constructor() : this("", "")
}
