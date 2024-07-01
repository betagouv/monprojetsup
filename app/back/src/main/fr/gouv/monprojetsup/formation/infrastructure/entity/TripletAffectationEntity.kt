package fr.gouv.monprojetsup.formation.infrastructure.entity

import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "triplet_affectation")
class TripletAffectationEntity {
    @Id
    lateinit var id: String

    @Column(name = "nom", nullable = false)
    lateinit var nom: String

    @Column(name = "commune", nullable = false)
    lateinit var commune: String

    @Column(name = "code_commune", nullable = false)
    lateinit var codeCommune: String

    @Column(name = "coordonnees_geographiques", nullable = false)
    lateinit var coordonneesGeographiques: String

    @Column(name = "id_formation", nullable = false)
    lateinit var idFormation: String

    fun toTripletAffectation() =
        TripletAffectation(
            id = id,
            commune = commune,
        )
}
