package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.Constantes.NOTE_MAXIMALE
import fr.gouv.monprojetsup.commun.Constantes.NOTE_MINIMALE
import fr.gouv.monprojetsup.commun.Constantes.NOTE_NON_REPONSE
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.utilitaires.aUneValeurCommune
import fr.gouv.monprojetsup.eleve.domain.entity.ModificationProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFormation
import fr.gouv.monprojetsup.eleve.domain.port.EleveRepository
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatRepository
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatSpecialiteRepository
import fr.gouv.monprojetsup.referentiel.domain.port.DomaineRepository
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import org.springframework.stereotype.Service

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
    @Throws(MonProjetSupBadRequestException::class)
    fun mettreAJourUnProfilEleve(
        miseAJourDuProfil: ModificationProfilEleve,
        profilActuel: ProfilEleve,
    ) {
        val profilInitial =
            when (profilActuel) {
                is ProfilEleve.SansCompte -> eleveRepository.creerUnEleve(profilActuel.id)
                is ProfilEleve.AvecProfilExistant -> profilActuel
            }
        verifierBaccalaureatEtSesSpecialites(miseAJourDuProfil, profilInitial)
        verifierDomaines(miseAJourDuProfil.domainesInterets)
        verifierCentresInterets(miseAJourDuProfil.centresInterets)
        verifierMetiers(miseAJourDuProfil.metiersFavoris)
        verifierFormations(miseAJourDuProfil.formationsFavorites, miseAJourDuProfil.corbeilleFormations, profilInitial)
        verifierVoeuxFormations(miseAJourDuProfil.formationsFavorites)
        verifierLaMoyenneGenerale(miseAJourDuProfil.moyenneGenerale)
        val profilEleveAMettreAJour =
            ProfilEleve.AvecProfilExistant(
                id = profilActuel.id,
                situation = miseAJourDuProfil.situation ?: profilInitial.situation,
                classe = miseAJourDuProfil.classe ?: profilInitial.classe,
                baccalaureat = miseAJourDuProfil.baccalaureat ?: profilInitial.baccalaureat,
                specialites = miseAJourDuProfil.specialites ?: profilInitial.specialites,
                domainesInterets = miseAJourDuProfil.domainesInterets ?: profilInitial.domainesInterets,
                centresInterets = miseAJourDuProfil.centresInterets ?: profilInitial.centresInterets,
                metiersFavoris = miseAJourDuProfil.metiersFavoris ?: profilInitial.metiersFavoris,
                dureeEtudesPrevue = miseAJourDuProfil.dureeEtudesPrevue ?: profilInitial.dureeEtudesPrevue,
                alternance = miseAJourDuProfil.alternance ?: profilInitial.alternance,
                communesFavorites = miseAJourDuProfil.communesFavorites ?: profilInitial.communesFavorites,
                formationsFavorites = miseAJourDuProfil.formationsFavorites ?: profilInitial.formationsFavorites,
                moyenneGenerale = miseAJourDuProfil.moyenneGenerale ?: profilInitial.moyenneGenerale,
                corbeilleFormations = miseAJourDuProfil.corbeilleFormations ?: profilInitial.corbeilleFormations,
                compteParcoursupLie = profilInitial.compteParcoursupLie,
            )
        if (profilEleveAMettreAJour != profilInitial) {
            eleveRepository.mettreAJourUnProfilEleve(profilEleveAMettreAJour)
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierFormations(
        voeuxDeFormations: List<VoeuFormation>?,
        corbeilleFormations: List<String>?,
        profilInitial: ProfilEleve.AvecProfilExistant,
    ) {
        val formationsFavorites = voeuxDeFormations?.map { it.idFormation }
        if (formationsFavorites?.distinct()?.size != formationsFavorites?.size) {
            throw MonProjetSupBadRequestException(
                code = "FORMATIONS_FAVORITES_EN_DOUBLE",
                msg = "Une des formations favorites est présentes plusieurs fois",
            )
        } else if (corbeilleFormations?.distinct()?.size != corbeilleFormations?.size) {
            throw MonProjetSupBadRequestException(
                code = "FORMATIONS_CORBEILLE_EN_DOUBLE",
                msg = "Une des formations à la corbeille est présentes plusieurs fois",
            )
        } else if (!formationsFavorites.isNullOrEmpty() && !corbeilleFormations.isNullOrEmpty()) {
            if (formationsFavorites.aUneValeurCommune(corbeilleFormations)) {
                throw MonProjetSupBadRequestException(
                    code = "CONFLIT_FORMATION_FAVORITE_A_LA_CORBEILLE",
                    msg = "Une ou plusieurs des formations se trouvent à la fois à la corbeille et dans les favoris",
                )
            } else {
                val formationsInexistantes =
                    formationRepository.recupererIdsFormationsInexistantes(
                        ids = formationsFavorites + corbeilleFormations,
                    )
                if (formationsInexistantes.isNotEmpty()) {
                    throw MonProjetSupBadRequestException(
                        "FORMATIONS_NON_RECONNUES",
                        "Les formations $formationsInexistantes envoyées n'existent pas",
                    )
                }
            }
        } else if (!formationsFavorites.isNullOrEmpty()) {
            if (formationsFavorites.aUneValeurCommune(profilInitial.corbeilleFormations)) {
                throw MonProjetSupBadRequestException(
                    code = "CONFLIT_FORMATION_FAVORITE_A_LA_CORBEILLE",
                    msg = "Vous essayez d'ajouter une formation en favoris alors qu'elle se trouve actuellement à la corbeille",
                )
            } else {
                val formationsInexistantes = formationRepository.recupererIdsFormationsInexistantes(ids = formationsFavorites)
                if (formationsInexistantes.isNotEmpty()) {
                    throw MonProjetSupBadRequestException(
                        "FORMATIONS_NON_RECONNUES",
                        "Les formations $formationsInexistantes envoyées n'existent pas",
                    )
                }
            }
        } else if (!corbeilleFormations.isNullOrEmpty()) {
            if (corbeilleFormations.aUneValeurCommune(profilInitial.formationsFavorites?.map { it.idFormation })) {
                throw MonProjetSupBadRequestException(
                    code = "CONFLIT_FORMATION_FAVORITE_A_LA_CORBEILLE",
                    msg = "Vous essayez d'ajouter une formation à la corbeille alors qu'elle se trouve actuellement en favoris",
                )
            } else {
                val formationsInexistantes = formationRepository.recupererIdsFormationsInexistantes(ids = corbeilleFormations)
                if (formationsInexistantes.isNotEmpty()) {
                    throw MonProjetSupBadRequestException(
                        "FORMATIONS_NON_RECONNUES",
                        "Les formations $formationsInexistantes envoyées n'existent pas",
                    )
                }
            }
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierVoeuxFormations(voeuxDeFormations: List<VoeuFormation>?) {
        voeuxDeFormations?.mapNotNull {
            if (it.voeuxChoisis.isNotEmpty()) it.idFormation else null
        }?.takeUnless { it.isEmpty() }?.let { idsFormations ->
            val voeux = voeuRepository.recupererLesVoeuxDeFormations(idsFormations, obsoletesInclus = true)
            voeuxDeFormations.forEach { voeu ->
                if (voeu.voeuxChoisis.isNotEmpty()) {
                    val voeuDuVoeu = voeux[voeu.idFormation]?.map { it.id }
                    if (voeuDuVoeu?.containsAll(voeu.voeuxChoisis) != true) {
                        throw MonProjetSupBadRequestException(
                            code = "VOEU_IMPOSSIBLE_POUR_FORMATION_FAVORITE",
                            msg =
                                "Pour la formation ${voeu.idFormation} présente dans les formations favorites " +
                                    "comporte un ou plusieurs voeux ne correspondant pas à une de ses possibilités : $voeuDuVoeu",
                        )
                    }
                }
            }
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierMetiers(metiersFavoris: List<String>?) {
        metiersFavoris?.takeUnless { it.isEmpty() }?.let {
            if (it.distinct().size != it.size) {
                throw MonProjetSupBadRequestException("METIERS_FAVORITES_EN_DOUBLE", "Un ou plusieurs des métiers est en double")
            } else {
                val metiersInexistants = metierRepository.recupererIdsMetiersInexistants(ids = it)
                if (metiersInexistants.isNotEmpty()) {
                    throw MonProjetSupBadRequestException("METIERS_NON_RECONNUS", "Les métiers $metiersInexistants n'existent pas")
                }
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

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierLaMoyenneGenerale(moyenneGenerale: Float?) {
        moyenneGenerale?.let {
            if (it > NOTE_MAXIMALE || it < NOTE_MINIMALE) {
                if (it != NOTE_NON_REPONSE) {
                    throw MonProjetSupBadRequestException(
                        code = "ERREUR_MOYENNE_GENERALE",
                        msg = "La moyenne générale $it n'est pas dans l'intervalle ${NOTE_MINIMALE.toInt()} et ${NOTE_MAXIMALE.toInt()}",
                    )
                }
            }
        }
    }
}
