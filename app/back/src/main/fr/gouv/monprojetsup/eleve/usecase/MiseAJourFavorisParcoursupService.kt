package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFormation
import fr.gouv.monprojetsup.eleve.domain.port.CompteParcoursupRepository
import fr.gouv.monprojetsup.formation.domain.port.TripletAffectationRepository
import fr.gouv.monprojetsup.parcoursup.domain.entity.FavorisParcoursup
import fr.gouv.monprojetsup.parcoursup.infrastructure.client.ParcoursupApiHttpClient
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MiseAJourFavorisParcoursupService(
    private val compteParcoursupRepository: CompteParcoursupRepository,
    private val parcoursupApiHttpClient: ParcoursupApiHttpClient,
    private val tripletAffectationRepository: TripletAffectationRepository,
) {
    fun mettreAJourFavorisParcoursup(profil: ProfilEleve.Identifie): ProfilEleve.Identifie {
        val formationsFavorites = profil.formationsFavorites ?: emptyList()
        val idsTripletsAffectationParcoursup = recupererFavorisParcoursup(profil.id).map { it.idTripletAffectation }

        return if (lesTripletsAffectationsSurParcoursupSontDejaTousDansLeProfil(
                formationsFavorites,
                idsTripletsAffectationParcoursup,
            )
        ) {
            profil
        } else {
            val nouveauxFavoris =
                recupererFavorisParcoursupMisAJour(
                    formationsFavorites,
                    idsTripletsAffectationParcoursup,
                )
            profil.copy(formationsFavorites = nouveauxFavoris)
        }
    }

    private fun recupererFavorisParcoursupMisAJour(
        formationsFavorites: List<VoeuFormation>,
        idsTripletsAffectationParcoursup: List<String>,
    ): List<VoeuFormation> {
        val tripletsAffectationParcoursup = creerVoeuxFormationParcoursup(idsTripletsAffectationParcoursup)
        return if (formationsFavorites.isNotEmpty()) {
            fusionnerFavorisParcoursupEtFavorisMonProjetSup(formationsFavorites, tripletsAffectationParcoursup)
        } else {
            tripletsAffectationParcoursup
        }
    }

    private fun creerVoeuxFormationParcoursup(idsTripletsAffectationParcoursup: List<String>) =
        tripletAffectationRepository.recupererTripletsAffectation(idsTripletsAffectationParcoursup).map {
            VoeuFormation(
                idFormation = it.key,
                niveauAmbition = 0,
                tripletsAffectationsChoisis = it.value.map { tripletAffectation -> tripletAffectation.id },
                priseDeNote = null,
            )
        }

    private fun fusionnerFavorisParcoursupEtFavorisMonProjetSup(
        formationsFavorites: List<VoeuFormation>,
        tripletsAffectationParcoursup: List<VoeuFormation>,
    ) = (formationsFavorites + tripletsAffectationParcoursup).groupBy { it.idFormation }.map { entry ->
        VoeuFormation(
            idFormation = entry.key,
            niveauAmbition = entry.value.first().niveauAmbition,
            tripletsAffectationsChoisis = entry.value.flatMap { it.tripletsAffectationsChoisis }.distinct(),
            priseDeNote = entry.value.first().priseDeNote,
        )
    }

    private fun lesTripletsAffectationsSurParcoursupSontDejaTousDansLeProfil(
        formationsFavorites: List<VoeuFormation>,
        idsTripletsAffectationParcoursup: List<String>,
    ): Boolean {
        val idsTripletsAffectationMonProjetSup = formationsFavorites.flatMap { it.tripletsAffectationsChoisis }
        val idsTripletsAffectationParcoursupEtMonProjetSupDistincts =
            (idsTripletsAffectationParcoursup + idsTripletsAffectationMonProjetSup).distinct()
        return idsTripletsAffectationParcoursupEtMonProjetSupDistincts.size == idsTripletsAffectationMonProjetSup.size
    }

    private fun recupererFavorisParcoursup(idEleve: UUID): List<FavorisParcoursup> {
        return compteParcoursupRepository.recupererIdCompteParcoursup(idEleve)?.let { compteParcoursup ->
            parcoursupApiHttpClient.recupererLesTripletsAffectationSelectionnesSurParcoursup(compteParcoursup)
        } ?: emptyList()
    }
}
