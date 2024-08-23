package fr.gouv.monprojetsup.data.app.formation.entity

import fr.gouv.monprojetsup.data.app.commun.entity.LienEntity
import fr.gouv.monprojetsup.data.domain.model.Formation
import fr.gouv.monprojetsup.data.domain.model.StatsFormation
import fr.gouv.monprojetsup.data.infrastructure.model.descriptifs.DescriptifFormation
import fr.gouv.monprojetsup.data.infrastructure.model.stats.Statistique
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.jetbrains.annotations.NotNull
import java.io.Serializable

@Entity
@Table(name = "formation")
class FormationEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "label", nullable = false)
    lateinit var label: String

    @Column(name = "descriptif_general", nullable = true)
    var descriptifGeneral: String? = null

    @Column(name = "descriptif_conseils", nullable = true)
    var descriptifConseils: String? = null

    @Column(name = "descriptif_diplome", nullable = true)
    var descriptifDiplome: String? = null

    @Column(name = "descriptif_attendu", nullable = true)
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

    @ElementCollection
    @CollectionTable(name = "triplet_affectation", joinColumns = [JoinColumn(name = "id")])
    var voeux: List<VoeuEntity> = ArrayList()

    @JdbcTypeCode(SqlTypes.ARRAY)
    var metiers: List<@NotNull String> = ArrayList()

    /** begin ajouts suggestions **/

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "psupFlIds")
    var psupFlIds : List<@NotNull String> = ArrayList()

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "voeux")
    var voeuxIds : List<String> = ArrayList()

    @Column(name = "label_details", nullable = true)
    var labelDetails: String? = null

    @Column(name = "capacite", nullable = false)
    var capacite: Int = 0

    @Column(name = "apprentissage", nullable = false)
    var apprentissage: Boolean = false

    @Column(name = "duree", nullable = false)
    var duree: Int = -1

    @Column(nullable = true)
    var las: String? = null

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

    @JdbcTypeCode(SqlTypes.JSON)
    var descriptif : DescriptifFormation? = null


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
            psupFlIds
        )
    }

    /** end suggestions **/


}
