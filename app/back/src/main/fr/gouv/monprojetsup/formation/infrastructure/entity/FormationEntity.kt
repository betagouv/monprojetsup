package fr.gouv.monprojetsup.formation.infrastructure.entity

import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.entity.FormationDetaillee
import fr.gouv.monprojetsup.formation.domain.entity.MetierDetaille
import io.hypersistence.utils.hibernate.type.array.ListArrayType
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Type

@Entity
@Table(name = "formation")
class FormationEntity {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Column(name = "label", nullable = false)
    lateinit var label: String

    fun toFormation() =
        Formation(
            id = id,
            nom = label,
        )
}

@Entity
@Table(name = "formation")
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

    fun toFormationDetaille() =
        FormationDetaillee(
            id = id,
            nom = label,
            descriptifGeneral = descriptifGeneral,
            descriptifAttendus = descriptifAttendus,
            liens = liens.map { it.toLien() },
            metiers =
                metiers.map { formationMetierEntity ->
                    val metier = formationMetierEntity.metier
                    MetierDetaille(
                        id = metier.id,
                        nom = metier.label,
                        descriptif = metier.descriptifGeneral,
                        liens = metier.liens.map { it.toLien() },
                    )
                },
            formationsAssociees = formationsAssociees ?: emptyList(),
            descriptifConseils = descriptifConseils,
            descriptifDiplome = descriptifDiplome,
            valeurCriteresAnalyseCandidature = criteresAnalyse,
        )
}