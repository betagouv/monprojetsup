package fr.gouv.monprojetsup.recherche.infrastructure.entity

import fr.gouv.monprojetsup.recherche.domain.entity.Formation
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

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

    @Column(name = "descriptif_specialites", nullable = true)
    var descriptifSpecialites: String? = null

    @Column(name = "descriptif_attendu", nullable = true)
    var descriptifAttendu: String? = null

    @ElementCollection
    @Column(name = "mots_clefs", nullable = true)
    var motsClefs: List<String>? = null

    @ElementCollection
    @Column(name = "urls", nullable = false)
    var urls: List<String>? = null

    @Column(name = "formation_parente", nullable = false)
    lateinit var formationParente: String

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "formation")
    lateinit var metiers: List<FormationMetierEntity>

    fun toFormation() =
        Formation(
            id = id,
            nom = label,
        )
}
