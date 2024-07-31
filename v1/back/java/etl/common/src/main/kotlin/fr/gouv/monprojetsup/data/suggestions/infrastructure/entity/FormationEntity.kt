package fr.gouv.monprojetsup.data.suggestions.infrastructure.entity

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
@Table(name = "formations")
class FormationEntity {

    constructor()
    constructor(f : Formation) {
        this.id = f.id;
        this.label = f.label;
        this.labelDebug = f.labelDebug;
        this.capacite = f.capacite;
        this.apprentissage = f.apprentissage;
        this.duree = f.duree;
        this.las = f.las;
        this.voeux = f.voeux.map { VoeuEntity(it) };
        this.metiers = f.metiers;
        this.stats = StatsEntity(
            f.stats.admissions,
            f.stats.nbAdmisParBac,
            f.stats.nbAdmisParSpecialite,
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
    var id: String = ""

    @Column
    var label: String = ""

    @Column
    var labelDebug: String? = ""

    @Column
    var capacite: Int = 0

    @Column
    var apprentissage: Boolean = false

    @Column
    var duree: Int = 0

    @Column(nullable = true)
    var las: String? = null

    @ElementCollection
    @CollectionTable(name = "voeux", joinColumns = [JoinColumn(name = "voeux_id")])
    var voeux: List<VoeuEntity> = listOf()

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    var metiers: List<String> = listOf()

    @Column
    @JdbcTypeCode(SqlTypes.JSON)
    var filieresPsup : List<@NotNull String> = listOf()

    data class StatsEntity (


        //type de bac → admissions
        val admissions : Map<String, Statistique> = mapOf(),

        val nbAdmisParBac : Map<String, Int> = mapOf(),

        //spécialité → pourcentage
        val nbAdmisParSpecialite: Map<Int, Int> = mapOf(),

        //type de bac générique → formation → score
        val formationsSimilaires : Map<Int, Map<String,Int>> = mapOf(),

        ) : Serializable {
        fun toStats(): StatsFormation {
            return StatsFormation(
                admissions,
                nbAdmisParBac,
                nbAdmisParSpecialite,
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

