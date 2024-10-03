package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.eleve.domain.entity.Commune
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecIdsVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.Voeu
import fr.gouv.monprojetsup.formation.domain.port.CommunesAvecVoeuxAuxAlentoursRepository
import org.springframework.stereotype.Service

@Service
class RecupererVoeuxDesCommunesFavoritesService(
    private val voeuxParVilleRepository: CommunesAvecVoeuxAuxAlentoursRepository,
) {
    fun recupererVoeuxAutoursDeCommmunes(
        communes: List<Commune>,
        voeuxDeLaFormation: List<Voeu>,
    ): List<CommuneAvecVoeuxAuxAlentours> {
        val voeuxAuxAlentoursDeCommunes = voeuxParVilleRepository.recupererVoeuxAutoursDeCommmune(communes)
        return creerVoeuxAutoursCommunes(voeuxAuxAlentoursDeCommunes, voeuxDeLaFormation)
    }

    fun recupererVoeuxAutoursDeCommmunes(
        communes: List<Commune>,
        voeuxDeLaFormation: Map<String, List<Voeu>>,
    ): Map<String, List<CommuneAvecVoeuxAuxAlentours>> {
        val voeuxAuxAlentoursDeCommunes = voeuxParVilleRepository.recupererVoeuxAutoursDeCommmune(communes)
        return voeuxDeLaFormation.map {
            it.key to creerVoeuxAutoursCommunes(voeuxAuxAlentoursDeCommunes, it.value)
        }.toMap()
    }

    private fun creerVoeuxAutoursCommunes(
        voeuxAuxAlentoursDeCommunes: List<CommuneAvecIdsVoeuxAuxAlentours>,
        voeuxDeLaFormation: List<Voeu>,
    ) = voeuxAuxAlentoursDeCommunes.map { voeuxAuxAlentoursDUneCommune ->
        CommuneAvecVoeuxAuxAlentours(
            commune = voeuxAuxAlentoursDUneCommune.commune,
            distances =
                voeuxAuxAlentoursDUneCommune.distances.mapNotNull { distance ->
                    voeuxDeLaFormation.firstOrNull { it.id == distance.idVoeu }?.let { voeu ->
                        CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                            voeu,
                            distance.km,
                        )
                    }
                },
        )
    }
}
