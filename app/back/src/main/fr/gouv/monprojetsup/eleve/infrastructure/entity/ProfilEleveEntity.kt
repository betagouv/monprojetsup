package fr.gouv.monprojetsup.eleve.infrastructure.entity

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import io.hypersistence.utils.hibernate.type.array.ListArrayType
import io.hypersistence.utils.hibernate.type.json.JsonType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.Type

@Entity(name = "ProfilEleve")
@Table(name = "profil_eleve")
class ProfilEleveEntity() {
    @Id
    @Column(name = "id", nullable = false)
    lateinit var id: String

    @Enumerated(EnumType.STRING)
    @Column(name = "situation", nullable = true)
    var situation: SituationAvanceeProjetSup? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "classe", nullable = true)
    var classe: ChoixNiveau? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "duree_etudes_prevue", nullable = true)
    var dureeEtudesPrevue: ChoixDureeEtudesPrevue? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "alternance", nullable = true)
    var alternance: ChoixAlternance? = null

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

    @Column(name = "moyenne_generale", nullable = true)
    var moyenneGenerale: Float? = null

    @Type(ListArrayType::class)
    @Column(name = "corbeille_formations", nullable = false)
    var corbeilleFormations: List<String> = emptyList()

    constructor(profilEleve: ProfilEleve.AvecProfilExistant) : this() {
        id = profilEleve.id
        situation = profilEleve.situation
        classe = profilEleve.classe
        dureeEtudesPrevue = profilEleve.dureeEtudesPrevue
        alternance = profilEleve.alternance
        idBaccalaureat = profilEleve.baccalaureat
        specialites = profilEleve.specialites
        domaines = profilEleve.domainesInterets
        centresInterets = profilEleve.centresInterets
        metiersFavoris = profilEleve.metiersFavoris
        communesFavorites = profilEleve.communesFavorites?.map { CommuneEntity(it) }
        formationsFavorites = profilEleve.formationsFavorites?.map { FormationFavoriteEntity(it) }
        voeuxFavoris = profilEleve.voeuxFavoris.map { VoeuFavoriEntity(it) }
        moyenneGenerale = profilEleve.moyenneGenerale
        corbeilleFormations = profilEleve.corbeilleFormations
    }

    fun toProfilEleve(compteParcoursupLie: Boolean = false) =
        ProfilEleve.AvecProfilExistant(
            id = id,
            situation = situation,
            classe = classe,
            baccalaureat = idBaccalaureat,
            specialites = specialites,
            domainesInterets = domaines,
            centresInterets = centresInterets,
            metiersFavoris = metiersFavoris,
            dureeEtudesPrevue = dureeEtudesPrevue,
            alternance = alternance,
            communesFavorites = communesFavorites?.map { it.toCommune() },
            formationsFavorites = formationsFavorites?.map { it.toFormationFavorite() },
            moyenneGenerale = moyenneGenerale,
            corbeilleFormations = corbeilleFormations,
            compteParcoursupLie = compteParcoursupLie,
            voeuxFavoris = voeuxFavoris?.map { it.toVoeuFavori() }.orEmpty(),
        )
}
