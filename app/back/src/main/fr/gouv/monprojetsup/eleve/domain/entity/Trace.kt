package fr.gouv.monprojetsup.eleve.domain.entity

import java.time.LocalDate

data class Trace(
    val tsp: LocalDate,
    val action: String,
    val param1: String?,
    val param2: String?,
)
