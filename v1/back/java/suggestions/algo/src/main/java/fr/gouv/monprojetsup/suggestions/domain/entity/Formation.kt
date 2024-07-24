package fr.gouv.monprojetsup.suggestions.domain.entity

import fr.gouv.monprojetsup.suggestions.data.model.stats.Middle50
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SerializableType
import org.hibernate.type.SqlTypes
import java.io.Serializable

data class Stats (

    //spécialité --> pourcentage
    val specialites: Map<String, Int> = mapOf(),

    //type de bac --> admissions
    val admissions : Map<String, Middle50> = mapOf(),

    val nbAdmis : Map<String, Int> = mapOf(),

    //type de bac --> liste de formations similaires
    val formationsSimilaires : Map<String, List<String>> = mapOf(),

    )

data class Formation(

    val id: String = "",
    val label: String = "",
    val capacite: Int = 0,
    val apprentissage: Boolean = false,
    val duree: Int = 0,
    val las: String? = null,
    val voeux: List<String> = listOf(),
    val metiers: List<String> = listOf(),
    val stats : Stats = Stats()

) {
    fun isLas() = las != null
}

