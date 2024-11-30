package fr.gouv.monprojetsup.authentification.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.eleve.domain.port.EleveRepository
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatSpecialiteRepository
import fr.gouv.monprojetsup.referentiel.domain.port.DomaineRepository
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import org.springframework.stereotype.Service

@Service
class RecupererEleveService(
    private val baccalaureatSpecialiteRepository: BaccalaureatSpecialiteRepository,
    private val voeuRepository: VoeuRepository,
    private val domaineRepository: DomaineRepository,
    private val interetRepository: InteretRepository,
    private val metierRepository: MetierRepository,
    private val formationRepository: FormationRepository,
    private val eleveRepository: EleveRepository,
) {
    @Throws(MonProjetSupBadRequestException::class)
    fun recupererEleve(id: String): ProfilEleve {
        return when (val eleveBDD = eleveRepository.recupererUnEleve(id)) {
            is ProfilEleve.AvecProfilExistant -> {
                var profilFiltre: ProfilEleve.AvecProfilExistant = eleveBDD
                profilFiltre = filtrerBaccalaureatEtSesSpecialites(eleveBDD, profilFiltre)
                profilFiltre = filtrerFormationsEtVoeux(eleveBDD, profilFiltre)
                profilFiltre = filtrerCentresInteret(eleveBDD, profilFiltre)
                profilFiltre = filtrerDomaines(eleveBDD, profilFiltre)
                profilFiltre = filtrerMetiers(eleveBDD, profilFiltre)
                profilFiltre
            }

            else -> eleveBDD
        }
    }

    private fun filtrerBaccalaureatEtSesSpecialites(
        eleveBDD: ProfilEleve.AvecProfilExistant,
        profilFiltre: ProfilEleve.AvecProfilExistant,
    ): ProfilEleve.AvecProfilExistant {
        if (eleveBDD.baccalaureat == null) {
            return profilFiltre.copy(specialites = null)
        }
        val idBaccalaureat = eleveBDD.baccalaureat
        val paireBacSpecialites =
            baccalaureatSpecialiteRepository.recupererUnBaccalaureatEtLesIdsDeSesSpecialites(idBaccalaureat)
        if (paireBacSpecialites != null) {
            profilFiltre.specialites?.let { specialites ->
                val specialitesConformes = specialites.filter { paireBacSpecialites.second.contains(it) }
                if (specialitesConformes.size != specialites.size) {
                    return profilFiltre.copy(specialites = specialitesConformes)
                }
            }
        } else {
            return profilFiltre.copy(
                baccalaureat = null,
                specialites = null,
            )
        }
        return profilFiltre
    }

    private fun filtrerDomaines(
        eleveBDD: ProfilEleve.AvecProfilExistant,
        profilFiltre: ProfilEleve.AvecProfilExistant,
    ): ProfilEleve.AvecProfilExistant {
        val domainesInexistants =
            eleveBDD.domainesInterets?.let { domaineRepository.recupererIdsDomainesInexistants(it) }
                ?.takeUnless { it.isEmpty() }
        if (domainesInexistants != null) {
            return profilFiltre.copy(
                domainesInterets =
                    profilFiltre.domainesInterets?.filterNot { domain ->
                        domainesInexistants.contains(
                            domain,
                        )
                    },
            )
        }
        return profilFiltre
    }

    private fun filtrerMetiers(
        eleveBDD: ProfilEleve.AvecProfilExistant,
        profilFiltre: ProfilEleve.AvecProfilExistant,
    ): ProfilEleve.AvecProfilExistant {
        val metiersInexistants =
            eleveBDD.metiersFavoris?.let { metierRepository.recupererIdsMetiersInexistants(it) }
                ?.takeUnless { it.isEmpty() }
        if (metiersInexistants != null) {
            return profilFiltre.copy(
                metiersFavoris = profilFiltre.metiersFavoris?.filterNot { metiers -> metiersInexistants.contains(metiers) },
            )
        }
        return profilFiltre
    }

    private fun filtrerCentresInteret(
        eleveBDD: ProfilEleve.AvecProfilExistant,
        profilFiltre: ProfilEleve.AvecProfilExistant,
    ): ProfilEleve.AvecProfilExistant {
        val interetsInexistants =
            eleveBDD.centresInterets?.let { interetRepository.recupererIdsCentresInteretsInexistants(it) }
                ?.takeUnless { it.isEmpty() }
        if (interetsInexistants != null) {
            return profilFiltre.copy(
                centresInterets =
                    profilFiltre.centresInterets?.filterNot { interet ->
                        interetsInexistants.contains(
                            interet,
                        )
                    },
            )
        }
        return profilFiltre
    }

    private fun filtrerFormationsEtVoeux(
        eleveBDD: ProfilEleve.AvecProfilExistant,
        profilFiltre: ProfilEleve.AvecProfilExistant,
    ): ProfilEleve.AvecProfilExistant {
        var profilFiltre1 = profilFiltre
        val idsFormations =
            (eleveBDD.formationsFavorites?.map { it.idFormation } ?: emptyList()) + eleveBDD.corbeilleFormations
        val formationsInexistantes =
            idsFormations.distinct().takeUnless { it.isEmpty() }?.let {
                formationRepository.recupererIdsFormationsInexistantes(it)
            }?.takeUnless { it.isEmpty() }
        if (formationsInexistantes != null) {
            profilFiltre1 =
                profilFiltre1.copy(
                    formationsFavorites =
                        profilFiltre1.formationsFavorites?.filterNot { formation ->
                            formationsInexistantes.any { it == formation.idFormation }
                        },
                    corbeilleFormations =
                        profilFiltre1.corbeilleFormations.filterNot {
                            formationsInexistantes.contains(it)
                        },
                )
        }
        val voeuxInexistants =
            profilFiltre1.voeuxFavoris.map { it.idVoeu }
                .takeUnless { it.isEmpty() }
                ?.let { voeuRepository.recupererIdsVoeuxInexistants(it.distinct()) }
                ?.takeUnless { it.isEmpty() }
        return if (voeuxInexistants != null) {
            profilFiltre1.copy(
                voeuxFavoris = profilFiltre1.voeuxFavoris.filterNot { it.idVoeu in voeuxInexistants },
            )
        } else {
            profilFiltre1
        }
    }
}
