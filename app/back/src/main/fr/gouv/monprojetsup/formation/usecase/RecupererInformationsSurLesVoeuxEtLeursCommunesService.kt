package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecIdsVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.CommuneCourte
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.InformationsSurLesVoeuxEtLeursCommunes
import fr.gouv.monprojetsup.formation.domain.entity.Voeu
import fr.gouv.monprojetsup.formation.domain.port.CommunesAvecVoeuxAuxAlentoursRepository
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
import org.springframework.stereotype.Service

@Service
class RecupererInformationsSurLesVoeuxEtLeursCommunesService(
    private val voeuRepository: VoeuRepository,
    private val communesAvecVoeuxAuxAlentoursRepository: CommunesAvecVoeuxAuxAlentoursRepository,
) {
    fun recupererVoeux(
        idFormation: String,
        obsoletesInclus: Boolean,
    ): List<Voeu> {
        return voeuRepository.recupererLesVoeuxDUneFormation(idFormation, obsoletesInclus)
    }

    fun recupererVoeux(
        idsFormations: List<String>,
        obsoletesInclus: Boolean,
    ): Map<String, List<Voeu>> {
        return voeuRepository.recupererLesVoeuxDeFormations(idsFormations, obsoletesInclus)
    }

    fun recupererInformationsSurLesVoeuxEtLeursCommunes(
        idFormation: String,
        profilEleve: ProfilEleve.AvecProfilExistant,
        obsoletesInclus: Boolean,
    ): InformationsSurLesVoeuxEtLeursCommunes {
        val voeux = voeuRepository.recupererLesVoeuxDUneFormation(idFormation, obsoletesInclus)
        if (!profilEleve.communesFavorites.isNullOrEmpty()) {
            val voeuxAutoursDesCommunesFavorites =
                communesAvecVoeuxAuxAlentoursRepository.recupererVoeuxAutoursDeCommmune(
                    profilEleve.communesFavorites,
                )
            val idsVoeuxTriesParDistance = creerLesIdsDesVoeuxTriesParDistance(voeuxAutoursDesCommunesFavorites)
            return informationsSurLesVoeuxEtLeursCommunesPourProfil(voeux, voeuxAutoursDesCommunesFavorites, idsVoeuxTriesParDistance)
        } else {
            return informationsSurLesVoeuxEtLeursCommunesSansProfil(voeux)
        }
    }

    fun recupererInformationsSurLesVoeuxEtLeursCommunes(
        idsFormations: List<String>,
        profilEleve: ProfilEleve.AvecProfilExistant,
        obsoletesInclus: Boolean,
    ): Map<String, InformationsSurLesVoeuxEtLeursCommunes> {
        val voeux = voeuRepository.recupererLesVoeuxDeFormations(idsFormations, obsoletesInclus)
        if (!profilEleve.communesFavorites.isNullOrEmpty()) {
            val voeuxAutoursDesCommunesFavorites =
                communesAvecVoeuxAuxAlentoursRepository.recupererVoeuxAutoursDeCommmune(
                    profilEleve.communesFavorites,
                )
            val idsVoeuxTriesParDistance = creerLesIdsDesVoeuxTriesParDistance(voeuxAutoursDesCommunesFavorites)
            return voeux.map {
                it.key to
                    informationsSurLesVoeuxEtLeursCommunesPourProfil(
                        it.value,
                        voeuxAutoursDesCommunesFavorites,
                        idsVoeuxTriesParDistance,
                    )
            }.toMap()
        } else {
            return voeux.map {
                it.key to informationsSurLesVoeuxEtLeursCommunesSansProfil(it.value)
            }.toMap()
        }
    }

    private fun creerLesIdsDesVoeuxTriesParDistance(voeuxAutoursDesCommunesFavorites: List<CommuneAvecIdsVoeuxAuxAlentours>) =
        voeuxAutoursDesCommunesFavorites.flatMap { it.distances }.sortedBy { it.km }.map { it.idVoeu }

    private fun informationsSurLesVoeuxEtLeursCommunesSansProfil(voeux: List<Voeu>) =
        InformationsSurLesVoeuxEtLeursCommunes(
            voeux = voeux,
            communesTriees = extraireCommunes(voeux),
            voeuxParCommunesFavorites = emptyList(),
        )

    private fun informationsSurLesVoeuxEtLeursCommunesPourProfil(
        voeux: List<Voeu>,
        voeuxAutoursDesCommunesFavorites: List<CommuneAvecIdsVoeuxAuxAlentours>,
        idsVoeuxTriesParDistance: List<String>,
    ): InformationsSurLesVoeuxEtLeursCommunes {
        val voeuxTries = trierLesVoeux(voeux, idsVoeuxTriesParDistance)
        return InformationsSurLesVoeuxEtLeursCommunes(
            voeux = voeuxTries,
            communesTriees = extraireCommunes(voeuxTries),
            voeuxParCommunesFavorites =
                creerVoeuxParCommunes(
                    voeuxAuxAlentoursDeCommunes = voeuxAutoursDesCommunesFavorites,
                    voeuxDeLaFormation = voeuxTries,
                ),
        )
    }

    private fun extraireCommunes(voeux: List<Voeu>): List<CommuneCourte> {
        return voeux.map { it.commune }.distinctBy { it.codeInsee }
    }

    private fun trierLesVoeux(
        voeux: List<Voeu>,
        idsVoeuxTriesParDistance: List<String>,
    ): List<Voeu> {
        return trierUneListeDObjetsParUneListeDIds(idsVoeuxTriesParDistance, voeux) { it.id }
    }

    private fun <T> trierUneListeDObjetsParUneListeDIds(
        idsTries: List<String>,
        objetsAtrier: List<T>,
        idSelector: (T) -> String,
    ): List<T> {
        val indexMapDesIds = idsTries.withIndex().associate { it.value to it.index }
        val indexMapDesObjets = objetsAtrier.withIndex().associate { idSelector(it.value) to it.index }
        val nombreIds = idsTries.size
        val comparateur =
            compareBy<T>(
                { indexMapDesIds[idSelector(it)] ?: Int.MAX_VALUE },
                { (nombreIds + (indexMapDesObjets[idSelector(it)] ?: Int.MAX_VALUE)) },
            )
        return objetsAtrier.sortedWith(comparateur)
    }

    private fun creerVoeuxParCommunes(
        voeuxAuxAlentoursDeCommunes: List<CommuneAvecIdsVoeuxAuxAlentours>,
        voeuxDeLaFormation: List<Voeu>,
    ): List<CommuneAvecVoeuxAuxAlentours> =
        voeuxAuxAlentoursDeCommunes.map { voeuxAuxAlentoursDUneCommune ->
            CommuneAvecVoeuxAuxAlentours(
                communeFavorite = voeuxAuxAlentoursDUneCommune.communeFavorite,
                distances =
                    voeuxAuxAlentoursDUneCommune.distances.mapNotNull { distance ->
                        voeuxDeLaFormation.firstOrNull { it.id == distance.idVoeu }?.let { voeu ->
                            CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                voeu,
                                distance.km,
                            )
                        }
                    }.sortedBy { it.km },
            )
        }
}
