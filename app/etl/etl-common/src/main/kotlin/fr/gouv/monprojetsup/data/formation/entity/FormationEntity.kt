package fr.gouv.monprojetsup.data.formation.entity

import fr.gouv.monprojetsup.data.commun.entity.LienEntity
import fr.gouv.monprojetsup.data.domain.model.Formation
import fr.gouv.monprojetsup.data.domain.model.StatsFormation
import fr.gouv.monprojetsup.data.domain.model.stats.Statistique
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsLabelEntity
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.jetbrains.annotations.Nullable
import java.io.Serializable

@Entity
@Table(name = "formation")
class FormationEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "label", nullable = false, length = SuggestionsLabelEntity.MAX_LABEL_LENGTH)
    lateinit var label: String

    @Column(name = "descriptif_general", nullable = true, columnDefinition = "text")
    var descriptifGeneral: String? = null

    @Column(name = "descriptif_conseils", nullable = true, columnDefinition = "text")
    var descriptifConseils: String? = null

    @Column(name = "descriptif_diplome", nullable = true, columnDefinition = "text")
    var descriptifDiplome: String? = null

    @Column(name = "descriptif_attendu", nullable = true, columnDefinition = "text")
    var descriptifAttendus: String? = null

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "formations_associees", nullable = true, columnDefinition = "varchar[]")
    var formationsAssociees: List<String>? = null

    //même taille que critere_analyse_candidature
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "criteres_analyse", columnDefinition = "int[]")
    lateinit var criteresAnalyse: List<Int>

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "liens", columnDefinition = "jsonb")
    var liens = arrayListOf<LienEntity>()

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "mots_clefs", nullable = true, columnDefinition = "varchar[]")
    var motsClefs: List<String>? = null

    @JdbcTypeCode(SqlTypes.ARRAY)
    var metiers: List<String> = ArrayList()

    /** begin ajouts suggestions **/

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)  // Cascading enabled here
    @JoinColumn(name = "formation_id")
    var voeux: List<VoeuEntity> = ArrayList()

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "voeux")
    var voeuxIds : List<String> = ArrayList()

    @Column(name = "label_details", nullable = true, length = SuggestionsLabelEntity.MAX_LABEL_LENGTH)
    var labelDetails: String? = null

    @Column(name = "capacite", nullable = true)
    var capacite: Int? = null

    @Column(name = "apprentissage", nullable = false)
    var apprentissage: Boolean = false

    @Nullable
    @Column(name = "duree", nullable = true)
    var duree: Int? = null

    @Column(nullable = true)
    var las: String? = null

    fun integrityCheck(): Boolean {
        if(label.isEmpty()
            || descriptifGeneral.isNullOrEmpty()
            || formationsAssociees.isNullOrEmpty()
            || motsClefs.isNullOrEmpty()
            || voeux.isEmpty()
            || liens.isEmpty()
            || duree == null
            ) {
            return false
        }
        return true
    }

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

        constructor() : this(mapOf(), mapOf(), mapOf(), mapOf(), mapOf(), mapOf())
        constructor(admissions: StatsFormation) : this(
            admissions.admissions,
            admissions.nbAdmisParBac,
            admissions.pctAdmisParBac,
            admissions.nbAdmisParSpecialite,
            admissions.pctAdmisParSpecialite,
            admissions.formationsSimilaires)

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

    fun toFormation(): Formation {
        return Formation(
            id,
            label,
            labelDetails,
            capacite ?: -1,
            apprentissage,
            duree ?: -1,
            las,
            voeux.map { it.toVoeu() },
            metiers,
            stats.toStats(),
            formationsAssociees ?: emptyList(),
        )
    }

    /** end suggestions **/


}
