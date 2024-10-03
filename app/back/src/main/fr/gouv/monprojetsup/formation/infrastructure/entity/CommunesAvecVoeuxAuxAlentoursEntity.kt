package fr.gouv.monprojetsup.formation.infrastructure.entity

import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type

@Entity
@Table(name = "ref_join_ville_voeu")
class CommunesAvecVoeuxAuxAlentoursEntity {
    @Id
    @Column(name = "id_ville", nullable = false)
    lateinit var codeInsee: String

    @Type(JsonType::class)
    @Column(name = "distances_voeux_km", columnDefinition = "jsonb")
    lateinit var distancesVoeuKm: Map<String, Int>
}
