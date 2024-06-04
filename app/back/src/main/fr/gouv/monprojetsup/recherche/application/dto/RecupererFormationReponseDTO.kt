package fr.gouv.monprojetsup.recherche.application.dto

import fr.gouv.monprojetsup.recherche.domain.entity.AutoEvaluationMoyenne
import fr.gouv.monprojetsup.recherche.domain.entity.Centilles
import fr.gouv.monprojetsup.recherche.domain.entity.CriteresAdmission
import fr.gouv.monprojetsup.recherche.domain.entity.ExplicationsSuggestion
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation
import fr.gouv.monprojetsup.recherche.domain.entity.MetierDetaille
import fr.gouv.monprojetsup.recherche.domain.entity.MoyenneGenerale
import fr.gouv.monprojetsup.recherche.domain.entity.TypeBaccalaureat

data class RecupererFormationReponseDTO(
    val formation: FormationDetailleDTO,
    val explications: ExplicationsDTO?,
)

data class FormationDetailleDTO(
    val id: String,
    val nom: String,
    val formationsAssociees: List<String>?,
    val descriptifFormation: String?,
    val descriptifDiplome: String?,
    val descriptifConseils: String?,
    val descriptifAttendus: String?,
    val criteresAdmission: CriteresAdmissionDTO?,
    val liens: List<String>?,
    val villes: List<String>?,
    val metiers: List<MetierDetailleDTO>?,
    val tauxAffinite: Float?,
) {
    companion object {
        fun fromFicheFormation(formation: FicheFormation): FormationDetailleDTO {
            return FormationDetailleDTO(
                id = formation.id,
                nom = formation.nom,
                formationsAssociees = formation.formationsAssociees?.takeUnless { it.isEmpty() },
                descriptifFormation = formation.descriptifFormation,
                descriptifDiplome = formation.descriptifDiplome,
                descriptifAttendus = formation.descriptifAttendus,
                criteresAdmission = formation.criteresAdmission?.let { CriteresAdmissionDTO.fromCriteresAdmission(it) },
                descriptifConseils = formation.descriptifConseils,
                liens = formation.liens?.takeUnless { it.isEmpty() },
                villes = formation.communes.takeUnless { it.isEmpty() },
                metiers =
                    formation.metiers.map { metier ->
                        MetierDetailleDTO.fromMetierDetaille(metier)
                    },
                tauxAffinite = formation.tauxAffinite,
            )
        }
    }
}

data class MetierDetailleDTO(
    val id: String,
    val nom: String,
    val descriptif: String?,
    val liens: List<String>?,
) {
    companion object {
        fun fromMetierDetaille(metier: MetierDetaille) =
            MetierDetailleDTO(
                id = metier.id,
                nom = metier.nom,
                descriptif = metier.descriptif,
                liens = metier.liens?.takeUnless { it.isEmpty() },
            )
    }
}

data class ExplicationsDTO(
    val geographique: List<ExplicationGeographiqueDTO>?,
    val formationsSimilaires: List<String>?,
    val dureeEtudesPrevue: String?,
    val alternance: String?,
    val interets: List<String>?,
    val specialitesChoisies: Map<String, Double>?,
    val typeBaccalaureat: TypeBaccalaureatDTO?,
    val autoEvaluationMoyenne: AutoEvaluationMoyenneDTO?,
    val tags: List<TagDTO>?,
    val tagsCourts: List<String>?,
) {
    companion object {
        fun fromExplications(explications: ExplicationsSuggestion): ExplicationsDTO {
            return ExplicationsDTO(
                geographique =
                    explications.geographique?.map {
                        ExplicationGeographiqueDTO(nom = it.ville, distanceKm = it.distanceKm)
                    },
                formationsSimilaires = explications.formationsSimilaires,
                dureeEtudesPrevue = explications.dureeEtudesPrevue?.jsonValeur,
                alternance = explications.alternance?.jsonValeur,
                interets = explications.interets,
                specialitesChoisies = explications.specialitesChoisies,
                typeBaccalaureat = explications.typeBaccalaureat?.let { TypeBaccalaureatDTO.fromTypeBaccalaureat(it) },
                autoEvaluationMoyenne =
                    explications.autoEvaluationMoyenne?.let {
                        AutoEvaluationMoyenneDTO.fromAutoEvaluationMoyenne(
                            it,
                        )
                    },
                tagsCourts = explications.tagsCourts,
                tags = explications.tags?.map { TagDTO(it.noeuds, it.poid) },
            )
        }
    }
}

data class ExplicationGeographiqueDTO(
    val nom: String,
    val distanceKm: Int,
)

data class CriteresAdmissionDTO(
    val principauxPoints: List<String>?,
    val moyenneGenerale: MoyenneGeneraleDTO?,
) {
    companion object {
        fun fromCriteresAdmission(criteresAdmission: CriteresAdmission): CriteresAdmissionDTO {
            return CriteresAdmissionDTO(
                principauxPoints = criteresAdmission.principauxPoints,
                moyenneGenerale = criteresAdmission.moyenneGenerale?.let { MoyenneGeneraleDTO.fromMoyenneGenerale(it) },
            )
        }
    }
}

data class MoyenneGeneraleDTO(
    val centille5eme: Float,
    val centille25eme: Float,
    val centille75eme: Float,
    val centille95eme: Float,
) {
    companion object {
        fun fromMoyenneGenerale(moyenneGenerale: MoyenneGenerale): MoyenneGeneraleDTO {
            return MoyenneGeneraleDTO(
                moyenneGenerale.centille5eme,
                moyenneGenerale.centille25eme,
                moyenneGenerale.centille75eme,
                moyenneGenerale.centille95eme,
            )
        }
    }
}

data class TagDTO(
    val noeuds: List<String>?,
    val poid: Double?,
)

data class AutoEvaluationMoyenneDTO(
    val moyenne: Double?,
    val mediane: CentillesDTO?,
    val bacUtilise: String?,
) {
    companion object {
        fun fromAutoEvaluationMoyenne(autoEvaluationMoyenne: AutoEvaluationMoyenne): AutoEvaluationMoyenneDTO {
            return AutoEvaluationMoyenneDTO(
                moyenne = autoEvaluationMoyenne.moyenne,
                mediane = autoEvaluationMoyenne.mediane?.let { CentillesDTO.fromMediane(it) },
                bacUtilise = autoEvaluationMoyenne.bacUtilise,
            )
        }
    }
}

data class TypeBaccalaureatDTO(
    val nomBaccalaureat: String?,
    val pourcentage: Int?,
) {
    companion object {
        fun fromTypeBaccalaureat(typeBaccalaureat: TypeBaccalaureat): TypeBaccalaureatDTO {
            return TypeBaccalaureatDTO(typeBaccalaureat.nomBaccalaureat, typeBaccalaureat.pourcentage)
        }
    }
}

data class CentillesDTO(
    val rangEch25: Int?,
    val rangEch50: Int?,
    val rangEch75: Int?,
    val rangEch10: Int?,
    val rangEch90: Int?,
) {
    companion object {
        fun fromMediane(mediane: Centilles): CentillesDTO {
            return CentillesDTO(
                rangEch25 = mediane.rangEch25,
                rangEch50 = mediane.rangEch50,
                rangEch75 = mediane.rangEch75,
                rangEch10 = mediane.rangEch10,
                rangEch90 = mediane.rangEch90,
            )
        }
    }
}
