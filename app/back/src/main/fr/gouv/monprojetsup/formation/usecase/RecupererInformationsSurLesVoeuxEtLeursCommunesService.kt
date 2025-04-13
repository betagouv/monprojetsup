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
import java.time.LocalDate
import kotlin.random.Random

@Service
class RecupererInformationsSurLesVoeuxEtLeursCommunesService(
    private val voeuRepository: VoeuRepository,
    private val communesAvecVoeuxAuxAlentoursRepository: CommunesAvecVoeuxAuxAlentoursRepository,
) {

    companion object {
        const val DISTANCE_VOEUX_SIMILAIRES_KM = 10
    }
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
        profilEleve: ProfilEleve.AvecProfilExistant?,
        obsoletesInclus: Boolean,
    ): InformationsSurLesVoeuxEtLeursCommunes {
        val voeux =
            voeuRepository.recupererLesVoeuxDeFormations(
                listOf(idFormation),
                obsoletesInclus,
            )
        return if (profilEleve != null && !profilEleve.communesFavorites.isNullOrEmpty()) {
            val voeuxAutoursDesCommunesFavorites =
                communesAvecVoeuxAuxAlentoursRepository.recupererVoeuxAutoursDeCommmune(
                    profilEleve.communesFavorites,
                )
            val idsVoeuxTriesParDistance = creerLesIdsDesVoeuxTriesParDistance(voeuxAutoursDesCommunesFavorites)
            informationsSurLesVoeuxEtLeursCommunesPourProfil(
                voeux[idFormation] ?: emptyList(),
                voeuxAutoursDesCommunesFavorites,
                idsVoeuxTriesParDistance,
            )
        } else {
            informationsSurLesVoeuxEtLeursCommunesSansProfil(voeux[idFormation] ?: emptyList())
        }
    }

    fun recupererInformationsSurLesVoeuxEtLeursCommunes(
        idsFormations: List<String>,
        obsoletesInclus: Boolean,
    ): Map<String, InformationsSurLesVoeuxEtLeursCommunes> {
        return recupererInformationsSurLesVoeuxEtLeursCommunes(idsFormations, null, obsoletesInclus)
    }

    fun recupererInformationsSurLesVoeuxEtLeursCommunes(
        idsFormations: List<String>,
        profilEleve: ProfilEleve.AvecProfilExistant?,
        obsoletesInclus: Boolean,
    ): Map<String, InformationsSurLesVoeuxEtLeursCommunes> {
        val voeux = voeuRepository.recupererLesVoeuxDeFormations(idsFormations, obsoletesInclus)
        return if (profilEleve != null && !profilEleve.communesFavorites.isNullOrEmpty()) {
            val voeuxAutoursDesCommunesFavorites =
                communesAvecVoeuxAuxAlentoursRepository.recupererVoeuxAutoursDeCommmune(
                    profilEleve.communesFavorites,
                )
            val idsVoeuxTriesParDistance = creerLesIdsDesVoeuxTriesParDistance(voeuxAutoursDesCommunesFavorites)
            voeux.map {
                it.key to
                    informationsSurLesVoeuxEtLeursCommunesPourProfil(
                        it.value,
                        voeuxAutoursDesCommunesFavorites,
                        idsVoeuxTriesParDistance,
                    )
            }.toMap()
        } else {
            voeux.map {
                it.key to informationsSurLesVoeuxEtLeursCommunesSansProfil(it.value)
            }.toMap()
        }
    }

    private fun creerLesIdsDesVoeuxTriesParDistance(voeuxAutoursDesCommunesFavorites: List<CommuneAvecIdsVoeuxAuxAlentours>) : List<String> {
        val voeuxAvecDistances = voeuxAutoursDesCommunesFavorites.flatMap { it.distances }
        val idsVoeuxTries = voeuxAvecDistances.shuffled().sortedBy { it.km / DISTANCE_VOEUX_SIMILAIRES_KM }.map { it.idVoeu }
        return idsVoeuxTries
    }

    private fun informationsSurLesVoeuxEtLeursCommunesSansProfil(voeux: List<Voeu>) =
        InformationsSurLesVoeuxEtLeursCommunes(
            voeux = voeux.shuffled(),
            communes = extraireCommunes(voeux).shuffled(),
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
            communes = extraireCommunes(voeuxTries),
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
    ): List<CommuneAvecVoeuxAuxAlentours> {

        val today = LocalDate.now()
        val seed = today.year * 10000 + today.monthValue * 100 + today.dayOfMonth
        val random = Random(seed.toLong())

        return voeuxAuxAlentoursDeCommunes.map { voeuxAuxAlentoursDUneCommune ->
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
                    }.shuffled(random).sortedBy { it.km / DISTANCE_VOEUX_SIMILAIRES_KM },
            )
        }
    }
}
