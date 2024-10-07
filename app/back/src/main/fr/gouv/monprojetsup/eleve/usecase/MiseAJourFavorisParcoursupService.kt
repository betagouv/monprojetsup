package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFormation
import fr.gouv.monprojetsup.eleve.domain.port.CompteParcoursupRepository
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
import fr.gouv.monprojetsup.parcoursup.domain.entity.FavorisParcoursup
import fr.gouv.monprojetsup.parcoursup.domain.port.ParcoursupApiFavorisClient
import org.springframework.stereotype.Service

@Service
class MiseAJourFavorisParcoursupService(
    private val compteParcoursupRepository: CompteParcoursupRepository,
    private val parcoursupApiFavorisClient: ParcoursupApiFavorisClient,
    private val voeuRepository: VoeuRepository,
) {
    fun mettreAJourFavorisParcoursup(profil: ProfilEleve.AvecProfilExistant): ProfilEleve.AvecProfilExistant {
        val formationsFavorites = profil.formationsFavorites ?: emptyList()
        val idsVoeuxParcoursup = recupererFavorisParcoursup(profil.id).map { it.idVoeu }

        return if (lesVoeuxSurParcoursupSontDejaTousDansLeProfil(formationsFavorites, idsVoeuxParcoursup)
        ) {
            profil
        } else {
            val nouveauxFavoris =
                recupererFavorisParcoursupMisAJour(
                    formationsFavorites,
                    idsVoeuxParcoursup,
                )
            profil.copy(formationsFavorites = nouveauxFavoris)
        }
    }

    private fun recupererFavorisParcoursupMisAJour(
        formationsFavorites: List<VoeuFormation>,
        idsVoeuxParcoursup: List<String>,
    ): List<VoeuFormation> {
        val voeuxParcoursup = creerVoeuxFormationParcoursup(idsVoeuxParcoursup)
        return if (formationsFavorites.isNotEmpty()) {
            fusionnerFavorisParcoursupEtFavorisMonProjetSup(formationsFavorites, voeuxParcoursup)
        } else {
            voeuxParcoursup
        }
    }

    private fun creerVoeuxFormationParcoursup(idsVoeuxParcoursup: List<String>) =
        voeuRepository.recupererVoeux(idsVoeuxParcoursup).map {
            VoeuFormation(
                idFormation = it.key,
                niveauAmbition = 0,
                voeuxChoisis = it.value.map { voeu -> voeu.id },
                priseDeNote = null,
            )
        }

    private fun fusionnerFavorisParcoursupEtFavorisMonProjetSup(
        formationsFavorites: List<VoeuFormation>,
        voeuxParcoursup: List<VoeuFormation>,
    ) = (formationsFavorites + voeuxParcoursup).groupBy { it.idFormation }.map { entry ->
        VoeuFormation(
            idFormation = entry.key,
            niveauAmbition = entry.value.first().niveauAmbition,
            voeuxChoisis = entry.value.flatMap { it.voeuxChoisis }.distinct(),
            priseDeNote = entry.value.first().priseDeNote,
        )
    }

    private fun lesVoeuxSurParcoursupSontDejaTousDansLeProfil(
        formationsFavorites: List<VoeuFormation>,
        idsVoeuxParcoursup: List<String>,
    ): Boolean {
        val idsVoeuxMonProjetSup = formationsFavorites.flatMap { it.voeuxChoisis }
        val idsVoeuxParcoursupEtMonProjetSupDistincts =
            (idsVoeuxParcoursup + idsVoeuxMonProjetSup).distinct()
        return idsVoeuxParcoursupEtMonProjetSupDistincts.size == idsVoeuxMonProjetSup.size
    }

    private fun recupererFavorisParcoursup(idEleve: String): List<FavorisParcoursup> {
        return compteParcoursupRepository.recupererIdCompteParcoursup(idEleve)?.let { compteParcoursup ->
            parcoursupApiFavorisClient.recupererLesVoeuxSelectionnesSurParcoursup(compteParcoursup)
        } ?: emptyList()
    }
}
