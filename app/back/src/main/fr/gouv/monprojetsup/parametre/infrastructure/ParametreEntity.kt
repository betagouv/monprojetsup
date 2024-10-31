package fr.gouv.monprojetsup.parametre.infrastructure

import fr.gouv.monprojetsup.parametre.domain.entity.Parametre
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "parametre")
class ParametreEntity() {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "id", nullable = false)
    lateinit var id: Parametre

    @Column(name = "statut", nullable = false)
    var statut: Boolean = false
}
