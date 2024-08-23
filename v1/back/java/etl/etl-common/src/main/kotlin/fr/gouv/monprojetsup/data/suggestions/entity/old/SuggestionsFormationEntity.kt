package fr.gouv.monprojetsup.data.suggestions.entity.old

import fr.gouv.monprojetsup.data.app.formation.entity.VoeuEntity
import fr.gouv.monprojetsup.data.domain.model.Formation
import fr.gouv.monprojetsup.data.domain.model.StatsFormation
import fr.gouv.monprojetsup.data.infrastructure.model.descriptifs.DescriptifFormation
import fr.gouv.monprojetsup.data.infrastructure.model.stats.Statistique
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsLabelEntity
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity
@Table(name = "suggestions_formations")
class SuggestionsFormationEntity {

    fun toFormation(): Formation {
        return Formation(
            id,
            label,
            labelDetails,
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
    var id: String = ""

    var label: String = ""

    @Column(nullable = true, length = SuggestionsLabelEntity.MAX_LABEL_LENGTH)
    var labelDetails: String? = null

    var capacite: Int = 0

    var apprentissage: Boolean = false

    var duree: Int = -1

    @Column(nullable = true)
    var las: String? = null

    @ElementCollection
    @CollectionTable(name = "triplet_affectation", joinColumns = [JoinColumn(name = "id")])
    var voeux: List<VoeuEntity> = ArrayList()

    @JdbcTypeCode(SqlTypes.ARRAY)
    var metiers: List<@NotNull String> = ArrayList()

    @JdbcTypeCode(SqlTypes.ARRAY)
    var filieresPsup : List<@NotNull String> = ArrayList()

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

    @JdbcTypeCode(SqlTypes.JSON)
    var stats : StatsEntity = StatsEntity()

    @JdbcTypeCode(SqlTypes.JSON)
    var descriptif : DescriptifFormation? = null

}

