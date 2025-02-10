package fr.gouv.monprojetsup.eleve.domain.entity

import java.time.LocalDate

data class Trace(
    val tsp: LocalDate?,
    val action: String,
    val param1: String?,
    val param2: String?,
) {
    constructor(action: String, param1: String?, param2: String?) : this(null, action, param1, param2)
}
