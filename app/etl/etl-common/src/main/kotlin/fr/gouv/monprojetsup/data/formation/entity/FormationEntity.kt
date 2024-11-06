package fr.gouv.monprojetsup.data.formation.entity

import fr.gouv.monprojetsup.data.commun.entity.LienEntity
import fr.gouv.monprojetsup.data.model.Formation
import fr.gouv.monprojetsup.data.model.StatsFormation
import fr.gouv.monprojetsup.data.model.stats.Middle50
import fr.gouv.monprojetsup.data.suggestions.entity.SuggestionsLabelEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.jetbrains.annotations.Nullable
import java.io.Serializable

@Entity
@Table(name = "ref_formation")
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

    @Column(name = "type_formation", nullable = true)
    var typeFormation: String? = null

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "formations_psup", nullable = true, columnDefinition = "varchar[]")
    var formationsAssociees: List<String>? = null

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "formations_ideo", nullable = true, columnDefinition = "varchar[]")
    var formationsIdeo: List<String>? = null

    //même taille que critere_analyse_candidature
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "criteres_analyse", columnDefinition = "int[]", nullable = true)
    var criteresAnalyse: List<Int>? = null

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "liens", columnDefinition = "jsonb")
    lateinit var liens: List<LienEntity>

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "mots_clefs", nullable = true)
    var motsClefs: List<String>? = null

    /** begin ajouts suggestions **/

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = "id_formation")
    lateinit var voeux: List<VoeuEntity>

    @Column(name = "label_details", nullable = true, length = SuggestionsLabelEntity.MAX_LABEL_LENGTH)
    var labelDetails: String? = null

    @Column(name = "capacite", nullable = true)
    var capacite: Int? = null

    @Column(name = "apprentissage", nullable = true)
    var apprentissage: Boolean? = false

    @Column(name = "apprentissage_pct", nullable = true)
    var apprentissagePct: Int? = 0

    @Nullable
    @Column(name = "duree", nullable = true)
    var duree: Int? = null

    @Column(nullable = true)
    var las: String? = null

    @Column(nullable = false)
    var obsolete: Boolean = false

    @JdbcTypeCode(SqlTypes.JSON)
    var stats : StatsEntity = StatsEntity()

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
        val admissions : Map<String, Middle50> = mapOf(),

        val nbAdmisParBac : Map<String, Int> = mapOf(),

        //spécialité → pourcentage
        val pctAdmisParSpecialite: Map<String, Int> = mapOf(),

        //type de bac générique → formation → score
        val formationsSimilaires : Map<Int, Map<String,Int>> = mapOf(),

        ) : Serializable {

        constructor() : this(mapOf(), mapOf(), mapOf(), mapOf())
        constructor(admissions: StatsFormation) : this(
            admissions.admissions,
            admissions.nbAdmisParBac,
            admissions.pctAdmisParSpecialite,
            admissions.formationsSimilaires)

        fun toStats(): StatsFormation {
            return StatsFormation(
                admissions,
                nbAdmisParBac,
                pctAdmisParSpecialite,
                formationsSimilaires
            )
        }
    }


    fun toFormation(): Formation {
        return Formation(
            id,
            typeFormation.orEmpty(),
            label,
            labelDetails,
            capacite ?: -1,
            apprentissage ?: false,
            duree ?: -1,
            las,
            voeux.map { it.toVoeu() },
            stats.toStats(),
            formationsAssociees ?: emptyList(),
        )
    }

    /** end suggestions **/


}
