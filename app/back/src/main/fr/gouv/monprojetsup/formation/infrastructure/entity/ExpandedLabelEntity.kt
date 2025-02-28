package fr.gouv.monprojetsup.formation.infrastructure.entity

import jakarta.persistence.Column

data class ExpandedLabelEntity(
    @Column(name = "id", nullable = false)
    val id: String,
    @Column(name = "label", nullable = true)
    val label: String?,
    @Column(name = "label_sans_accents", nullable = true)
    val label_sans_accents: String?,
    @Column(name = "label_decoupe_brut", nullable = true)
    val label_decoupe_brut: String?,
)
