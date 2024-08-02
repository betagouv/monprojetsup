package fr.gouv.monprojetsup.data.suggestions.entity

import fr.gouv.monprojetsup.suggestions.domain.model.Formation
import fr.gouv.monprojetsup.suggestions.domain.model.StatsFormation
import fr.gouv.monprojetsup.suggestions.infrastructure.model.descriptifs.DescriptifsFormations
import fr.gouv.monprojetsup.suggestions.infrastructure.model.stats.Statistique
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity
@Table(name = "suggestions_formations")
class SuggestionsFormationEntity {

    constructor(f : Formation) {
        this.id = f.id;
        this.label = f.label;
        this.labelDebug = f.labelDebug;
        this.capacite = f.capacite;
        this.apprentissage = f.apprentissage;
        this.duree = f.duree;
        this.las = f.las;
        this.voeux = f.voeux.map { SuggestionsVoeuEntity(it) };
        this.metiers = f.metiers;
        this.stats = StatsEntity(
            f.stats.admissions,
            f.stats.nbAdmisParBac,
            f.stats.pctAdmisParBac,
            f.stats.nbAdmisParSpecialite,
            f.stats.pctAdmisParSpecialite,
            f.stats.formationsSimilaires
        );
        this.filieresPsup = f.filieresPsup;
    }
    fun toFormation(): Formation {
        return Formation(
            id,
            label,
            labelDebug,
            capacite,
            apprentissage,
            duree,
            las,
            voeux.map { it.toVoeu() },
            metiers,
            stats.toStats(),
            filieresPsup
        )
    }

    @Id
    @Column
    val id: String

    @Column
    val label: String

    @Column(nullable = true)
    val labelDebug: String?

    @Column
    val capacite: Int

    @Column
    val apprentissage: Boolean

    @Column
    val duree: Int

    @Column(nullable = true)
    val las: String?

    @ElementCollection
    @CollectionTable(name = "sugg_voeux", joinColumns = [JoinColumn(name = "id")])
    val voeux: List<SuggestionsVoeuEntity>

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    val metiers: List<String>

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    val filieresPsup : List<@NotNull String>

    data class StatsEntity (


        //type de bac → admissions
        val admissions : Map<String, Statistique> = mapOf(),

        val nbAdmisParBac : Map<String, Int> = mapOf(),
        val pctAdmisParBac : Map<String, Int> = mapOf(),

        //spécialité → pourcentage
        val nbAdmisParSpecialite: Map<Int, Int> = mapOf(),
        val pctAdmisParSpecialite: Map<Int, Int> = mapOf(),

        //type de bac générique → formation → score
        val formationsSimilaires : Map<Int, Map<String,Int>> = mapOf(),

        ) : Serializable {
        fun toStats(): StatsFormation {
            return StatsFormation(
                admissions,
                nbAdmisParBac,
                pctAdmisParBac,
                nbAdmisParSpecialite,
                pctAdmisParSpecialite,
                formationsSimilaires
            )
        }
    }

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    var stats : StatsEntity = StatsEntity()

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    var descriptif : DescriptifsFormations.DescriptifFormation? = null

}

