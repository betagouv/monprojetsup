package fr.gouv.monprojetsup.formation.application.dto

import fr.gouv.monprojetsup.commun.lien.application.dto.LienDTO
import fr.gouv.monprojetsup.eleve.application.dto.ModificationProfilDTO
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationAutoEvaluationMoyenne
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationTypeBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.MoyenneGeneraleDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.MoyenneGeneraleDesAdmis.Centile
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.RepartitionAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.RepartitionAdmis.TotalAdmisPourUnBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.Voeu
import fr.gouv.monprojetsup.metier.application.dto.MetierCourtDTO
import fr.gouv.monprojetsup.metier.application.dto.MetierDTO
import fr.gouv.monprojetsup.referentiel.application.dto.BaccalaureatDTO
import fr.gouv.monprojetsup.referentiel.application.dto.DomaineDTO
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie

data class FormationAvecExplicationsDTO(
    val formation: FicheFormationDTO,
    val explications: ExplicationsDTO?,
) {
    constructor(ficheFormation: FicheFormation) : this(
        formation = FicheFormationDTO(ficheFormation),
        explications =
            when (ficheFormation) {
                is FicheFormation.FicheFormationPourProfil -> ficheFormation.explications?.let { ExplicationsDTO(it) }
                is FicheFormation.FicheFormationSansProfil -> null
            },
    )

    data class FicheFormationDTO(
        val id: String,
        val nom: String,
        val idsFormationsAssociees: List<String>,
        val descriptifFormation: String?,
        val descriptifDiplome: String?,
        val descriptifConseils: String?,
        val descriptifAttendus: String?,
        val moyenneGeneraleDesAdmis: MoyenneGeneraleDesAdmisDTO?,
        val criteresAnalyseCandidature: List<CriteresAnalyseCandidatureDTO>,
        val repartitionAdmisAnneePrecedente: RepartitionAdmisAnneePrecedenteDTO?,
        val liens: List<LienDTO>,
        val voeux: List<VoeuAvecCommuneDTO>,
        val communesFavoritesAvecLeursVoeux: List<CommuneAvecSesVoeuxDTO>,
        val metiers: List<MetierDTO>,
        val tauxAffinite: Int?,
        val apprentissage: Boolean,
    ) {
        constructor(ficheFormation: FicheFormation) : this(
            id = ficheFormation.id,
            nom = ficheFormation.nom,
            idsFormationsAssociees = ficheFormation.formationsAssociees,
            descriptifFormation = ficheFormation.descriptifGeneral,
            descriptifDiplome = ficheFormation.descriptifDiplome,
            descriptifAttendus = ficheFormation.descriptifAttendus,
            moyenneGeneraleDesAdmis =
                when (ficheFormation) {
                    is FicheFormation.FicheFormationPourProfil ->
                        ficheFormation.statistiquesDesAdmis?.moyenneGeneraleDesAdmis?.let {
                            MoyenneGeneraleDesAdmisDTO(it)
                        }

                    is FicheFormation.FicheFormationSansProfil -> null
                },
            descriptifConseils = ficheFormation.descriptifConseils,
            liens = ficheFormation.liens.map { LienDTO(it) },
            voeux = ficheFormation.voeux.map { VoeuAvecCommuneDTO(it) },
            communesFavoritesAvecLeursVoeux =
                when (ficheFormation) {
                    is FicheFormation.FicheFormationPourProfil ->
                        ficheFormation.voeuxParCommunesFavorites.map {
                            CommuneAvecSesVoeuxDTO(it)
                        }

                    is FicheFormation.FicheFormationSansProfil -> emptyList()
                },
            metiers =
                ficheFormation.metiers.map { metier ->
                    MetierDTO(metier)
                },
            tauxAffinite =
                when (ficheFormation) {
                    is FicheFormation.FicheFormationPourProfil -> ficheFormation.tauxAffinite
                    is FicheFormation.FicheFormationSansProfil -> null
                },
            repartitionAdmisAnneePrecedente =
                ficheFormation.statistiquesDesAdmis?.repartitionAdmis?.let {
                    RepartitionAdmisAnneePrecedenteDTO(
                        it,
                    )
                },
            criteresAnalyseCandidature =
                ficheFormation.criteresAnalyseCandidature.map {
                    CriteresAnalyseCandidatureDTO(it)
                },
            apprentissage = ficheFormation.apprentissage,
        )

        data class CriteresAnalyseCandidatureDTO(
            val nom: String,
            val pourcentage: Int,
        ) {
            constructor(critereAnalyseCandidature: CritereAnalyseCandidature) :
                this(
                    critereAnalyseCandidature.nom,
                    critereAnalyseCandidature.pourcentage,
                )
        }

        data class RepartitionAdmisAnneePrecedenteDTO(
            val total: Int,
            val parBaccalaureat: List<TotalAdmisPourUnBaccalaureatDTO>,
        ) {
            constructor(repartitionAdmis: RepartitionAdmis) : this(
                repartitionAdmis.total,
                repartitionAdmis.parBaccalaureat.map {
                    TotalAdmisPourUnBaccalaureatDTO(it)
                },
            )

            data class TotalAdmisPourUnBaccalaureatDTO(
                val baccalaureat: BaccalaureatDTO,
                val nombreAdmis: Int,
            ) {
                constructor(totalAdmisPourUnBaccalaureat: TotalAdmisPourUnBaccalaureat) :
                    this(
                        baccalaureat = BaccalaureatDTO(totalAdmisPourUnBaccalaureat.baccalaureat),
                        nombreAdmis = totalAdmisPourUnBaccalaureat.nombreAdmis,
                    )
            }
        }

        data class MoyenneGeneraleDesAdmisDTO(
            val baccalaureat: BaccalaureatDTO?,
            val centiles: List<CentileDTO>,
        ) {
            constructor(moyenneGeneraleDesAdmis: MoyenneGeneraleDesAdmis) :
                this(
                    baccalaureat = moyenneGeneraleDesAdmis.baccalaureat?.let { BaccalaureatDTO(it) },
                    centiles = moyenneGeneraleDesAdmis.centiles.map { CentileDTO(it) },
                )

            data class CentileDTO(
                val centile: Int,
                val note: Float,
            ) {
                constructor(centile: Centile) :
                    this(
                        centile = centile.centile,
                        note = centile.note,
                    )
            }
        }
    }

    data class ExplicationsDTO(
        val geographique: List<ExplicationGeographiqueDTO>,
        val formationsSimilaires: List<FormationSimilaireDTO>,
        val dureeEtudesPrevue: ChoixDureeEtudesPrevue?,
        val alternance: ChoixAlternance?,
        val choixEleve: ChoixElevesDTO?,
        val specialitesChoisies: List<AffiniteSpecialiteDTO>,
        val typeBaccalaureat: TypeBaccalaureatDTO?,
        val autoEvaluationMoyenne: AutoEvaluationMoyenneDTO?,
        val detailsCalculScore: DetailsCalculScoreDTO?,
    ) {
        constructor(explications: ExplicationsSuggestionDetaillees) : this(
            geographique = explications.geographique.map { ExplicationGeographiqueDTO(it) },
            formationsSimilaires = explications.formationsSimilaires.map { FormationSimilaireDTO(it) },
            dureeEtudesPrevue = explications.dureeEtudesPrevue,
            alternance = explications.alternance,
            choixEleve = ChoixElevesDTO(explications.choixEleve),
            specialitesChoisies =
                explications.specialitesChoisies.map {
                    AffiniteSpecialiteDTO(
                        it,
                    )
                },
            typeBaccalaureat =
                explications.explicationTypeBaccalaureat?.let {
                    TypeBaccalaureatDTO(it)
                },
            autoEvaluationMoyenne =
                explications.explicationAutoEvaluationMoyenne?.let {
                    AutoEvaluationMoyenneDTO(it)
                },
            detailsCalculScore =
                DetailsCalculScoreDTO(
                    details = explications.detailsCalculScore,
                ),
        )
    }

    data class ChoixElevesDTO(
        val interets: List<InteretDTO>,
        val domaines: List<DomaineDTO>,
        val metiers: List<MetierCourtDTO>,
    ) {
        constructor(choixEleve: ExplicationsSuggestionDetaillees.ChoixEleve) : this(
            interets = choixEleve.interetsChoisis.map { InteretDTO(it) },
            domaines = choixEleve.domainesChoisis.map { DomaineDTO(it) },
            metiers = choixEleve.metiersChoisis.map { MetierCourtDTO(it) },
        )

        data class InteretDTO(
            val id: String,
            val nom: String,
        ) {
            constructor(interet: InteretSousCategorie) : this(id = interet.id, nom = interet.nom)
        }
    }

    data class FormationSimilaireDTO(
        val id: String,
        val nom: String,
    ) {
        constructor(formationCourte: FormationCourte) : this(id = formationCourte.id, nom = formationCourte.nom)
    }

    data class AffiniteSpecialiteDTO(
        val nomSpecialite: String,
        val pourcentage: Int,
    ) {
        constructor(affiniteSpecialite: ExplicationsSuggestionDetaillees.AffiniteSpecialite) : this(
            nomSpecialite = affiniteSpecialite.nomSpecialite,
            pourcentage = affiniteSpecialite.pourcentage,
        )
    }

    data class ExplicationGeographiqueDTO(
        val nomVille: String,
        val distanceKm: Int,
    ) {
        constructor(explicationGeographique: ExplicationGeographique) : this(
            nomVille = explicationGeographique.ville,
            distanceKm = explicationGeographique.distanceKm,
        )
    }

    data class AutoEvaluationMoyenneDTO(
        val moyenne: Float,
        val basIntervalleNotes: Float,
        val hautIntervalleNotes: Float,
        val baccalaureatUtilise: BaccalaureatDTO,
    ) {
        constructor(autoEvaluationMoyenne: ExplicationAutoEvaluationMoyenne) : this(
            moyenne = autoEvaluationMoyenne.moyenneAutoEvalue,
            basIntervalleNotes = autoEvaluationMoyenne.basIntervalleNotes,
            hautIntervalleNotes = autoEvaluationMoyenne.hautIntervalleNotes,
            baccalaureatUtilise = BaccalaureatDTO(autoEvaluationMoyenne.baccalaureatUtilise),
        )
    }

    data class TypeBaccalaureatDTO(
        val baccalaureat: BaccalaureatDTO,
        val pourcentage: Int,
    ) {
        constructor(typeBaccalaureat: ExplicationTypeBaccalaureat) : this(
            baccalaureat = BaccalaureatDTO(typeBaccalaureat.baccalaureat),
            pourcentage = typeBaccalaureat.pourcentage,
        )
    }

    data class CommuneAvecSesVoeuxDTO(
        val commune: ModificationProfilDTO.CommuneDTO,
        val voeuxAvecDistance: List<VoeuAvecDistanceDTO>,
    ) {
        constructor(
            communeAvecVoeuxAuxAlentours: CommuneAvecVoeuxAuxAlentours,
        ) : this(
            commune = ModificationProfilDTO.CommuneDTO(communeAvecVoeuxAuxAlentours.commune),
            voeuxAvecDistance = communeAvecVoeuxAuxAlentours.distances.map { VoeuAvecDistanceDTO(it) },
        )

        data class VoeuAvecDistanceDTO(
            val voeu: VoeuAvecCommuneDTO,
            val distanceKm: Int,
        ) {
            constructor(voeuAvecDistance: CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance) : this(
                voeu = VoeuAvecCommuneDTO(voeuAvecDistance.voeu),
                distanceKm = voeuAvecDistance.km,
            )
        }
    }

    data class VoeuAvecCommuneDTO(
        val id: String,
        val nom: String,
        val commune: CommuneCourteDTO,
    ) {
        constructor(voeu: Voeu) : this(
            id = voeu.id,
            nom = voeu.nom,
            commune =
                CommuneCourteDTO(
                    nom = voeu.commune.nom,
                    codeInsee = voeu.commune.codeInsee,
                ),
        )
    }

    data class DetailsCalculScoreDTO(
        val details: List<String>,
    )

    data class CommuneCourteDTO(
        val nom: String,
        val codeInsee: String,
    )
}
