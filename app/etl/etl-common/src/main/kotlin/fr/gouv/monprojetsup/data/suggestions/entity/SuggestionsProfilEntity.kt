package fr.gouv.monprojetsup.data.suggestions.entity

import com.google.gson.Gson
import fr.gouv.monprojetsup.data.suggestions.entity.profil.CommuneEntity
import fr.gouv.monprojetsup.data.suggestions.entity.profil.FormationFavoriteEntity
import fr.gouv.monprojetsup.data.suggestions.entity.profil.VoeuFavoriEntity
import io.hypersistence.utils.hibernate.type.array.ListArrayType
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type

@Entity
@Table(name = "profil_reference")
class SuggestionsProfilEntity {

    @Id
    @Column(name = "id", nullable = false)
    var id: String

    @Column(name = "situation", nullable = true)
    var situation: String? = null

    @Column(name = "classe", nullable = true)
    var classe: String? = null

    @Column(name = "duree_etudes_prevue", nullable = true)
    var dureeEtudesPrevue: String? = null

    @Column(name = "alternance", nullable = true)
    var alternance: String? = null

    @Column(name = "id_baccalaureat", nullable = true)
    var idBaccalaureat: String? = null

    @Type(ListArrayType::class)
    @Column(name = "specialites", nullable = true)
    var specialites: List<String>? = null

    @Type(ListArrayType::class)
    @Column(name = "domaines", nullable = true)
    var domaines: List<String>? = null

    @Type(ListArrayType::class)
    @Column(name = "centres_interets", nullable = true)
    var centresInterets: List<String>? = null

    @Type(ListArrayType::class)
    @Column(name = "metiers_favoris", nullable = true)
    var metiersFavoris: List<String>? = null

    @Type(JsonType::class)
    @Column(name = "communes_favorites", nullable = true)
    var communesFavorites: List<CommuneEntity>? = null

    @Type(JsonType::class)
    @Column(name = "formations_favorites", nullable = true)
    var formationsFavorites: List<FormationFavoriteEntity>? = null

    @Type(JsonType::class)
    @Column(name = "voeux_favoris", nullable = true)
    var voeuxFavoris: List<VoeuFavoriEntity>? = null

    @Type(ListArrayType::class)
    @Column(name = "corbeille_formations", nullable = false)
    var corbeilleFormations: List<String> = emptyList()

    constructor(id: Int, m: Map<String, String>) {
        val gson = Gson()
        this.id = id.toString()
        situation = m["situation"]
        classe = m["classe"]
        dureeEtudesPrevue = m["duree_etudes_prevue"]
        alternance = m["alternance"]
        idBaccalaureat = m["id_baccalaureat"]
        specialites = toStringArray(m["specialites"])
        domaines = toStringArray(m["domaines"])
        centresInterets = toStringArray(m["centres_interets"])
        metiersFavoris = toStringArray(m["metiers_favoris"])
        communesFavorites = if (m["communes_favorites"].isNullOrBlank()) null else gson.fromJson(
            m["communes_favorites"],
            Array<CommuneEntity>::class.java
        ).toList()
        formationsFavorites = if (m["formations_favorites"].isNullOrBlank()) null else gson.fromJson(
            m["formations_favorites"],
            Array<FormationFavoriteEntity>::class.java
        ).toList()
        voeuxFavoris = if (m["voeux_favoris"].isNullOrBlank()) null else gson.fromJson(
            m["voeux_favoris"],
            Array<VoeuFavoriEntity>::class.java
        ).toList()
        corbeilleFormations = toStringArray(m["corbeille_formations"])
    }
}


private fun toStringArray(s: String?) =
    s?.replace("{", "")?.replace("}","")?.split(",")?.map { it.trim() }.orEmpty()

