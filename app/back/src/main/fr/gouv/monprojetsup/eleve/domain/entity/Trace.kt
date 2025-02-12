package fr.gouv.monprojetsup.eleve.domain.entity

import java.time.LocalDateTime

data class Trace(
    val actionEleve: ActionEleve,
    val param1: String?,
    val param2: String?,
    val tsp: LocalDateTime?,
) {
    constructor(actionEleve: ActionEleve, param1: String?, param2: String?) : this(actionEleve, param1, param2, null)
}
