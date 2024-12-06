package fr.gouv.monprojetsup.formation.infrastructure.entity

import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "ref_voeu")
class VoeuCourtEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "nom", nullable = false)
    lateinit var label: String

    fun toFormationCourte() =
        FormationCourte(
            id = id,
            nom = label,
        )
}

@Entity
@Table(name = "ref_voeu")
class VoeuEntity {
    @Id
    lateinit var id: String

    @Column(name = "nom", nullable = false)
    lateinit var nom: String

    @Column(name = "commune", nullable = false)
    lateinit var commune: String

    @Column(name = "code_commune", nullable = false)
    lateinit var codeCommune: String

    @Column(name = "latitude", nullable = false)
    var latitude: Double = 0.0

    @Column(name = "longitude", nullable = false)
    var longitude: Double = 0.0

    @Column(name = "obsolete", nullable = false)
    var obsolete: Boolean = false

    fun toFormationCourte() = FormationCourte(this.id, this.nom)
}
