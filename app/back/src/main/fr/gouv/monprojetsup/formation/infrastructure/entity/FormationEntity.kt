package fr.gouv.monprojetsup.formation.infrastructure.entity

import fr.gouv.monprojetsup.commun.lien.infrastructure.entity.LienEntity
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import io.hypersistence.utils.hibernate.type.array.ListArrayType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.Type
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "ref_formation")
class FormationCourteEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "label", nullable = false)
    lateinit var label: String

    fun toFormationCourte() =
        FormationCourte(
            id = id,
            nom = label,
        )
}

@Entity
@Table(name = "ref_formation")
class FormationDetailleeEntity {
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

    @Type(ListArrayType::class)
    @Column(name = "formations_psup", nullable = true, columnDefinition = "varchar[]")
    var formationsAssociees: List<String>? = null

    @Type(ListArrayType::class)
    @Column(name = "criteres_analyse", columnDefinition = "int[]")
    lateinit var criteresAnalyse: List<Int>

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "liens", columnDefinition = "jsonb")
    var liens = arrayListOf<LienEntity>()

    @Column(name = "apprentissage", nullable = false)
    var apprentissage: Boolean = false

    @Column(name = "obsolete", nullable = false)
    var obsolete: Boolean = false

    fun toFormation() =
        Formation(
            id = id,
            nom = label,
            descriptifGeneral = descriptifGeneral,
            descriptifAttendus = descriptifAttendus,
            liens = liens.map { it.toLien() },
            formationsAssociees = formationsAssociees ?: emptyList(),
            descriptifConseils = descriptifConseils,
            descriptifDiplome = descriptifDiplome,
            valeurCriteresAnalyseCandidature = criteresAnalyse,
            apprentissage = apprentissage,
        )
}
