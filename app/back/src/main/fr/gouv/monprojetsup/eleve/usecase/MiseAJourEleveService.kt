package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.utilitaires.aUneValeurCommune
import fr.gouv.monprojetsup.eleve.domain.entity.FormationFavorite
import fr.gouv.monprojetsup.eleve.domain.entity.ModificationProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFavori
import fr.gouv.monprojetsup.eleve.domain.port.EleveRepository
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatRepository
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatSpecialiteRepository
import fr.gouv.monprojetsup.referentiel.domain.port.DomaineRepository
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MiseAJourEleveService(
    private val baccalaureatRepository: BaccalaureatRepository,
    private val baccalaureatSpecialiteRepository: BaccalaureatSpecialiteRepository,
    private val voeuRepository: VoeuRepository,
    private val domaineRepository: DomaineRepository,
    private val interetRepository: InteretRepository,
    private val metierRepository: MetierRepository,
    private val formationRepository: FormationRepository,
    private val eleveRepository: EleveRepository,
) {
    @Transactional(readOnly = false)
    @Throws(MonProjetSupBadRequestException::class)
    fun mettreAJourUnProfilEleve(
        miseAJourDuProfil: ModificationProfilEleve,
        profilActuel: ProfilEleve,
    ): ProfilEleve.AvecProfilExistant {
        val profilInitial =
            when (profilActuel) {
                is ProfilEleve.SansCompte -> eleveRepository.creerUnEleve(profilActuel.id)
                is ProfilEleve.AvecProfilExistant -> profilActuel
            }
        verifierBaccalaureatEtSesSpecialites(miseAJourDuProfil, profilInitial)
        verifierDomaines(miseAJourDuProfil.domainesInterets)
        verifierCentresInterets(miseAJourDuProfil.centresInterets)
        val nouveauxMetiersFavoris = verifierMetiers(miseAJourDuProfil.metiersFavoris)
        val formationsFavoritesCorbeille =
            verifierFormations(miseAJourDuProfil.formationsFavorites, miseAJourDuProfil.corbeilleFormations, profilInitial)
        val voeuxFavoris = verifierVoeux(miseAJourDuProfil.voeuxFavoris)

        val nouvellesFormations = formationsFavoritesCorbeille.first ?: profilInitial.formationsFavorites
        val nouveauxVoeux =
            calculerNouveauxVoeux(
                profilInitial.voeuxFavoris,
                voeuxFavoris,
                nouvellesFormations?.map { it.idFormation }.orEmpty().toSet(),
            )

        val profilEleveAMettreAJour =
            ProfilEleve.AvecProfilExistant(
                id = profilActuel.id,
                situation = miseAJourDuProfil.situation ?: profilInitial.situation,
                classe = miseAJourDuProfil.classe ?: profilInitial.classe,
                baccalaureat = miseAJourDuProfil.baccalaureat ?: profilInitial.baccalaureat,
                specialites = miseAJourDuProfil.specialites ?: profilInitial.specialites,
                domainesInterets = miseAJourDuProfil.domainesInterets ?: profilInitial.domainesInterets,
                centresInterets = miseAJourDuProfil.centresInterets ?: profilInitial.centresInterets,
                metiersFavoris = nouveauxMetiersFavoris ?: profilInitial.metiersFavoris,
                dureeEtudesPrevue = miseAJourDuProfil.dureeEtudesPrevue ?: profilInitial.dureeEtudesPrevue,
                alternance = miseAJourDuProfil.alternance ?: profilInitial.alternance,
                communesFavorites = miseAJourDuProfil.communesFavorites ?: profilInitial.communesFavorites,
                formationsFavorites = nouvellesFormations,
                corbeilleFormations = formationsFavoritesCorbeille.second ?: profilInitial.corbeilleFormations,
                compteParcoursupLie = profilInitial.compteParcoursupLie,
                voeuxFavoris = nouveauxVoeux.sortedBy { it.idVoeu },
            )
        return if (profilEleveAMettreAJour != profilInitial) {
            eleveRepository.mettreAJourUnProfilEleve(profilEleveAMettreAJour)
            profilEleveAMettreAJour
        } else {
            profilInitial
        }
    }

    private fun calculerNouveauxVoeux(
        voeuxFavorisActuels: List<VoeuFavori>,
        voeuxMisAjourOuNull: List<VoeuFavori>?,
        formationsFavorites: Set<String>,
    ): List<VoeuFavori> {
        return voeuxMisAjourOuNull?.let { voeuxMisAjour ->

            val voeuxActuelsParFormation = voeuRepository.recupererVoeux(voeuxFavorisActuels.map { it.idVoeu })

            val voeuxMisAjourIds =
                voeuxMisAjour
                    .filter { !it.estFavoriParcoursup }
                    .map { it.idVoeu }
                    .toSet()

            val voeuxSupprimesIds =
                voeuxFavorisActuels
                    .filter { !it.estFavoriParcoursup && !voeuxMisAjourIds.contains(it.idVoeu) }
                    .map { it.idVoeu }
                    .toSet()

            val voeuxAConserverIds =
                voeuxActuelsParFormation.asSequence()
                    .filter { it.key in formationsFavorites }
                    .flatMap { it.value }
                    .filter { it.id !in voeuxSupprimesIds }
                    .map { it.id }.distinct().toList()
            val voeuxConserves = voeuxFavorisActuels.filter { it.estFavoriParcoursup || it.idVoeu in voeuxAConserverIds }
            val voeuxConservesIds = voeuxConserves.map { it.idVoeu }.toSet()
            val voeuxAjoutes =
                voeuxMisAjourIds
                    .filter { it !in voeuxConservesIds }
                    .filter { it !in voeuxSupprimesIds }
                    .map { VoeuFavori(it, false) }

            return (voeuxConserves + voeuxAjoutes).sortedBy { it.idVoeu }
        } ?: voeuxFavorisActuels
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierFormations(
        formationsFavorites: List<FormationFavorite>?,
        corbeilleFormations: List<String>?,
        profilInitial: ProfilEleve.AvecProfilExistant,
    ): Pair<List<FormationFavorite>?, List<String>?> {
        val idFormations = formationsFavorites?.map { it.idFormation }
        var newFormationsFavorites = formationsFavorites
        var newCorbeilleFormations = corbeilleFormations

        when {
            idFormations?.distinct()?.size != idFormations?.size -> {
                throw MonProjetSupBadRequestException(
                    code = "FORMATIONS_FAVORITES_EN_DOUBLE",
                    msg = "Une des formations favorites est présentes plusieurs fois",
                )
            }

            corbeilleFormations?.distinct()?.size != corbeilleFormations?.size -> {
                throw MonProjetSupBadRequestException(
                    code = "FORMATIONS_CORBEILLE_EN_DOUBLE",
                    msg = "Une des formations à la corbeille est présentes plusieurs fois",
                )
            }

            !idFormations.isNullOrEmpty() && !corbeilleFormations.isNullOrEmpty() -> {
                if (idFormations.aUneValeurCommune(corbeilleFormations)) {
                    throw MonProjetSupBadRequestException(
                        code = "CONFLIT_FORMATION_FAVORITE_A_LA_CORBEILLE",
                        msg = "Une ou plusieurs des formations se trouvent à la fois à la corbeille et dans les favoris",
                    )
                }
            }

            !idFormations.isNullOrEmpty() && corbeilleFormations == null -> {
                if (idFormations.aUneValeurCommune(profilInitial.corbeilleFormations)) {
                    throw MonProjetSupBadRequestException(
                        code = "CONFLIT_FORMATION_FAVORITE_A_LA_CORBEILLE",
                        msg = "Vous essayez d'ajouter une formation en favoris alors qu'elle se trouve actuellement à la corbeille",
                    )
                }
            }
        }
        if (!idFormations.isNullOrEmpty()) {
            val formationsInexistantes =
                formationRepository.recupererIdsFormationsInexistantes(ids = idFormations)
            if (formationsInexistantes.isNotEmpty()) {
                logger.warn(
                    "FORMATIONS_NON_RECONNUES",
                    "Les formations $formationsInexistantes envoyées n'existent pas",
                )
                newFormationsFavorites =
                    formationsFavorites.filterNot { formationsInexistantes.contains(it.idFormation) }
            }
        }
        if (!corbeilleFormations.isNullOrEmpty()) {
            if (corbeilleFormations.aUneValeurCommune(profilInitial.formationsFavorites?.map { it.idFormation })) {
                throw MonProjetSupBadRequestException(
                    code = "CONFLIT_FORMATION_FAVORITE_A_LA_CORBEILLE",
                    msg = "Vous essayez d'ajouter une formation à la corbeille alors qu'elle se trouve actuellement en favoris",
                )
            } else {
                val formationsInexistantes =
                    formationRepository.recupererIdsFormationsInexistantes(ids = corbeilleFormations)
                if (formationsInexistantes.isNotEmpty()) {
                    logger.warn(
                        "FORMATIONS_NON_RECONNUES",
                        "Les formations $formationsInexistantes envoyées n'existent pas",
                    )
                    newCorbeilleFormations =
                        corbeilleFormations.filterNot {
                            formationsInexistantes.contains(it)
                        }
                }
            }
        }
        return Pair(newFormationsFavorites, newCorbeilleFormations)
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierVoeux(voeux: List<VoeuFavori>?): List<VoeuFavori>? {
        if (voeux == null) {
            return null
        }
        if (voeux.isEmpty()) {
            return emptyList()
        }
        val voeuxInexistants = voeuRepository.recupererIdsVoeuxInexistants(voeux.map { itt -> itt.idVoeu })
        return if (voeuxInexistants.isNotEmpty()) {
            logger.warn(
                "VOEU_FAVORI_INEXISTANT",
                "Le ou les voeux favoris suivants ne sont pas connus : $voeuxInexistants",
            )
            voeux.filterNot { voeuxInexistants.contains(it.idVoeu) }
        } else {
            voeux
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierMetiers(metiersFavoris: List<String>?): List<String>? {
        if (metiersFavoris == null) {
            return null
        }
        if (metiersFavoris.isEmpty()) {
            return emptyList()
        }
        if (metiersFavoris.distinct().size != metiersFavoris.size) {
            throw MonProjetSupBadRequestException(
                "METIERS_FAVORIS_EN_DOUBLE",
                "Un ou plusieurs des métiers est en double",
            )
        } else {
            val metiersInexistants = metierRepository.recupererIdsMetiersInexistants(ids = metiersFavoris)
            return if (metiersInexistants.isNotEmpty()) {
                logger.warn(
                    "METIER_FAVORI_INEXISTANT",
                    "Le ou les métiers favoris suivants ne sont pas connus : $metiersInexistants",
                )
                metiersFavoris.filterNot { itt -> metiersInexistants.contains(itt) }
            } else {
                metiersFavoris
            }
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierCentresInterets(centresInterets: List<String>?) {
        centresInterets?.takeUnless { it.isEmpty() }?.let {
            val interetInexistants = interetRepository.recupererIdsCentresInteretsInexistants(ids = it)
            if (interetInexistants.isNotEmpty()) {
                throw MonProjetSupBadRequestException(
                    "CENTRES_INTERETS_NON_RECONNUS",
                    "Les centres d'intérêt $interetInexistants n'existent pas",
                )
            }
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierDomaines(domainesInterets: List<String>?) {
        domainesInterets?.takeUnless { it.isEmpty() }?.let {
            val domainesInexistants = domaineRepository.recupererIdsDomainesInexistants(ids = it)
            if (domainesInexistants.isNotEmpty()) {
                throw MonProjetSupBadRequestException("DOMAINES_NON_RECONNUS", "Les domaines $domainesInexistants n'existent pas")
            }
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierBaccalaureatEtSesSpecialites(
        miseAJourDuProfil: ModificationProfilEleve,
        ancienProfil: ProfilEleve.AvecProfilExistant,
    ) {
        if (miseAJourDuProfil.baccalaureat == null) {
            if (!miseAJourDuProfil.specialites.isNullOrEmpty()) {
                if (ancienProfil.baccalaureat == null) {
                    throw MonProjetSupBadRequestException(
                        "BACCALAUREAT_NULL",
                        "Veuillez mettre à jour le baccalaureat avant de mettre à jour ses spécialités",
                    )
                } else {
                    verifierSpecialitesEnAccordAvecBaccalaureat(ancienProfil.baccalaureat, miseAJourDuProfil.specialites)
                }
            }
        } else {
            if (!miseAJourDuProfil.specialites.isNullOrEmpty()) {
                verifierSpecialitesEnAccordAvecBaccalaureat(miseAJourDuProfil.baccalaureat, miseAJourDuProfil.specialites)
            } else if (miseAJourDuProfil.specialites?.isEmpty() == true) {
                verifierBaccalaureatExiste(miseAJourDuProfil.baccalaureat)
            } else {
                if (ancienProfil.specialites.isNullOrEmpty()) {
                    verifierBaccalaureatExiste(miseAJourDuProfil.baccalaureat)
                } else {
                    verifierSpecialitesEnAccordAvecBaccalaureat(miseAJourDuProfil.baccalaureat, ancienProfil.specialites)
                }
            }
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierSpecialitesEnAccordAvecBaccalaureat(
        idBaccalaureat: String,
        nouvellesSpecialites: List<String>,
    ) {
        val specialitesDuBaccalaureat =
            baccalaureatSpecialiteRepository.recupererLesIdsDesSpecialitesDUnBaccalaureat(idBaccalaureat)
        if (!specialitesDuBaccalaureat.containsAll(nouvellesSpecialites)) {
            throw MonProjetSupBadRequestException(
                "BACCALAUREAT_ET_SPECIALITES_NON_EN_ACCORD",
                "Une ou plus spécialité renvoyées ne font pas parties des spécialités du baccalaureat $idBaccalaureat. " +
                    "Spécialités possibles $specialitesDuBaccalaureat",
            )
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierBaccalaureatExiste(baccalaureat: String) {
        if (!baccalaureatRepository.verifierBaccalaureatExiste(baccalaureat)) {
            throw MonProjetSupBadRequestException(
                "BACCALAUREAT_NON_RECONNU",
                "Aucun baccalaureat avec l'id $baccalaureat",
            )
        }
    }
}
