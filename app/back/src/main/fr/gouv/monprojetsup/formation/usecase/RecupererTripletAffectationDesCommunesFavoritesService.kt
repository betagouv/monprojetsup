package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.eleve.domain.entity.Commune
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecIdsVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.formation.domain.port.CommunesAvecVoeuxAuxAlentoursRepository
import org.springframework.stereotype.Service

@Service
class RecupererTripletAffectationDesCommunesFavoritesService(
    private val voeuxParVilleRepository: CommunesAvecVoeuxAuxAlentoursRepository,
) {
    fun recupererVoeuxAutoursDeCommmunes(
        communes: List<Commune>,
        tripletsAffectationDeLaFormation: List<TripletAffectation>,
    ): List<CommuneAvecVoeuxAuxAlentours> {
        val voeuxAuxAlentoursDeCommunes = voeuxParVilleRepository.recupererVoeuxAutoursDeCommmune(communes)
        return creerVoeuxAutoursCommunes(voeuxAuxAlentoursDeCommunes, tripletsAffectationDeLaFormation)
    }

    fun recupererVoeuxAutoursDeCommmunes(
        communes: List<Commune>,
        tripletsAffectationDeLaFormation: Map<String, List<TripletAffectation>>,
    ): Map<String, List<CommuneAvecVoeuxAuxAlentours>> {
        val voeuxAuxAlentoursDeCommunes = voeuxParVilleRepository.recupererVoeuxAutoursDeCommmune(communes)
        return tripletsAffectationDeLaFormation.map {
            it.key to creerVoeuxAutoursCommunes(voeuxAuxAlentoursDeCommunes, it.value)
        }.toMap()
    }

    private fun creerVoeuxAutoursCommunes(
        voeuxAuxAlentoursDeCommunes: List<CommuneAvecIdsVoeuxAuxAlentours>,
        tripletsAffectationDeLaFormation: List<TripletAffectation>,
    ) = voeuxAuxAlentoursDeCommunes.map { voeuxAuxAlentoursDUneCommune ->
        CommuneAvecVoeuxAuxAlentours(
            commune = voeuxAuxAlentoursDUneCommune.commune,
            distances =
                voeuxAuxAlentoursDUneCommune.distances.mapNotNull { distance ->
                    tripletsAffectationDeLaFormation.firstOrNull { it.id == distance.idVoeu }?.let { voeu ->
                        CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                            voeu,
                            distance.km,
                        )
                    }
                },
        )
    }
}
