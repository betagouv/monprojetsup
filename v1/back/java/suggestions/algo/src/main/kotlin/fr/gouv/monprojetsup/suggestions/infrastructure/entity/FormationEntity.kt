package fr.gouv.monprojetsup.suggestions.infrastructure.entity

import fr.gouv.monprojetsup.suggestions.data.model.stats.Middle50
import fr.gouv.monprojetsup.suggestions.domain.entity.Formation
import fr.gouv.monprojetsup.suggestions.domain.entity.Stats
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SerializableType
import org.hibernate.type.SqlTypes
import java.io.Serializable

@Entity
@Table(name = "formations")
class FormationEntity {
    fun toFormation(): Formation {
        return Formation(
            id = id,
            label = label,
            capacite = capacite,
            apprentissage = apprentissage,
            duree = duree,
            las = las,
            voeux = voeux,
            metiers = metiers,
            stats = stats.toStats()
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

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    //indexes dans la table des voeux
    val voeux: List<String> = listOf()

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    val metiers: List<String> = listOf()

    data class StatsEntity (

        //spécialité --> pourcentage
        val specialites: Map<String, Int> = mapOf(),

        //type de bac --> admissions
        val admissions : Map<String, Middle50> = mapOf(),

        val nbAdmis : Map<String, Int> = mapOf(),

        //type de bac --> liste de formations similaires
        val formationsSimilaires : Map<String, List<String>> = mapOf(),

    ) : Serializable {
        fun toStats(): Stats {
            return Stats(
                specialites = specialites,
                admissions = admissions,
                nbAdmis = nbAdmis,
                formationsSimilaires = formationsSimilaires
            )
        }
    }

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    val stats : StatsEntity = StatsEntity()

}

