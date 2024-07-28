package fr.gouv.monprojetsup.data.infrastructure.entity

import fr.gouv.monprojetsup.data.model.stats.Middle50
import fr.gouv.monprojetsup.data.model.stats.Statistique
import fr.gouv.monprojetsup.data.domain.entity.Formation
import fr.gouv.monprojetsup.data.domain.entity.StatsFormation
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.io.Serializable

@Entity
@Table(name = "formations")
class FormationEntity {
    fun toFormation(): Formation {
        return Formation(
            id,
            label,
            capacite,
            apprentissage,
            duree,
            las,
            voeux.map { it.toVoeu() },
            metiers,
            stats.toStats()
        )
    }

    @Id
    @Column
    val id: String = ""

    @Column
    val label: String = ""

    @Column
    val capacite: Int = 0

    @Column
    val apprentissage: Boolean = false

    @Column
    val duree: Int = 0

    @Column(nullable = true)
    val las: String? = null

    @ElementCollection
    @CollectionTable(name = "voeux", joinColumns = [JoinColumn(name = "voeux_id")])
    val voeux: List<VoeuEntity> = listOf()

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    val metiers: List<String> = listOf()

    data class StatsEntity (

        //spécialité → pourcentage
        val specialites: Map<Int, Double> = mapOf(),

        //type de bac → admissions
        val admissions : Map<String, Statistique> = mapOf(),

        val nbAdmis : Map<String, Int> = mapOf(),

        //type de bac générique → formation → score
        val formationsSimilaires : Map<Int, Map<String,Int>> = mapOf(),

        ) : Serializable {
        fun toStats(): StatsFormation {
            return StatsFormation(
                specialites,
                admissions,
                nbAdmis,
                formationsSimilaires
            )
        }
    }

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    val stats : StatsEntity = StatsEntity()

}

