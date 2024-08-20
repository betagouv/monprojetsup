package fr.gouv.monprojetsup.formation.infrastructure.entity

import fr.gouv.monprojetsup.eleve.domain.entity.Commune
import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation
import io.hypersistence.utils.hibernate.type.array.ListArrayType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type

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

    @Type(ListArrayType::class)
    @Column(name = "coordonnees_geographiques", nullable = false)
    lateinit var coordonneesGeographiques: List<Double>

    @Column(name = "id_formation", nullable = false)
    lateinit var idFormation: String

    fun toTripletAffectation() =
        TripletAffectation(
            id = id,
            nom = nom,
            commune =
                Commune(
                    codeInsee = codeCommune,
                    nom = commune,
                    latitude = coordonneesGeographiques[0],
                    longitude = coordonneesGeographiques[1],
                ),
        )
}
