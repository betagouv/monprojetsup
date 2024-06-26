package fr.gouv.monprojetsup.recherche.infrastructure.entity

import fr.gouv.monprojetsup.recherche.domain.entity.Formation
import io.hypersistence.utils.hibernate.type.array.ListArrayType
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "formation")
    lateinit var metiers: List<FormationMetierEntity>
}
