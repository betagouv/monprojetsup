package fr.gouv.monprojetsup.data.app.entity

import fr.gouv.monprojetsup.data.app.domain.Formation
import io.hypersistence.utils.hibernate.type.array.ListArrayType
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.*
import org.hibernate.annotations.Type

@Entity
@Table(name = "formation")
class FormationEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "label", nullable = false)
    lateinit var label: String

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "formation")
    lateinit var metiers: List<FormationMetierEntity>

    fun toFormation() =
        Formation(
            id = id,
            nom = label,
        )
}

@Entity
@Table(name = "formation")
class FormationDetailleeEntity {

    companion object {
        const val MAX_LABEL_LENGTH: Int = 1023
        const val MAX_DESCRIPTIF_LENGTH: Int = 8191
    }

    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "label", nullable = false)
    lateinit var label: String

    @Column(name = "descriptif_general", nullable = true, length = MAX_DESCRIPTIF_LENGTH)
    var descriptifGeneral: String? = null

    @Column(name = "descriptif_conseils", nullable = true, length = MAX_DESCRIPTIF_LENGTH)
    var descriptifConseils: String? = null

    @Column(name = "descriptif_diplome", nullable = true, length = MAX_DESCRIPTIF_LENGTH)
    var descriptifDiplome: String? = null

    @Column(name = "descriptif_attendu", nullable = true, length = MAX_DESCRIPTIF_LENGTH)
    var descriptifAttendus: String? = null

    @Type(ListArrayType::class)
    @Column(name = "mots_clefs", nullable = true, columnDefinition = "varchar[]", length = MAX_LABEL_LENGTH)
    var motsClefs: List<String>? = null

    @Type(ListArrayType::class)
    @Column(name = "formations_associees", nullable = true, columnDefinition = "varchar[]")
    var formationsAssociees: List<String>? = null

    @Type(ListArrayType::class)
    @Column(name = "criteres_analyse", columnDefinition = "int[]")
    lateinit var criteresAnalyse: List<Int>

    @Type(JsonType::class)
    @Column(name = "liens", columnDefinition = "jsonb")
    var liens = arrayListOf<LienEntity>()

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "formation")
    lateinit var metiers: List<FormationMetierEntity>
}
