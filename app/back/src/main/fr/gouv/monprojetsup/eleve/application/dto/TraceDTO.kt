package fr.gouv.monprojetsup.eleve.application.dto

import com.fasterxml.jackson.annotation.JsonProperty
import fr.gouv.monprojetsup.eleve.domain.entity.Trace

data class TraceDTO(
    @JsonProperty("action")
    val action: String,
    @JsonProperty("param1")
    val param1: String?,
    @JsonProperty("param2")
    val param2: String?,
) {
    fun toTrace(): Trace {
        return Trace(
            action = action,
            param1 = param1,
            param2 = param2,
        )
    }
}
