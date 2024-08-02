package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ModificationProfilEleve
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.eleve.domain.port.EleveRepository
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
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
                is ProfilEleve.Inconnu -> eleveRepository.creerUnEleve(profilActuel.id)
                is ProfilEleve.Identifie -> profilActuel
            }
        verifierBaccalaureatEtSesSpecialites(miseAJourDuProfil, profilInitial)
        verifierDomaines(miseAJourDuProfil.domainesInterets)
        verifierCentresInterets(miseAJourDuProfil.centresInterets)
        verifierMetiers(miseAJourDuProfil.metiersFavoris)
        verifierFormations(miseAJourDuProfil.formationsFavorites)
        verifierLaMoyenneGenerale(miseAJourDuProfil.moyenneGenerale)
        val profilEleveAMettreAJour =
            ProfilEleve.Identifie(
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
            )
        if (profilEleveAMettreAJour != profilInitial) {
            eleveRepository.mettreAJourUnProfilEleve(profilEleveAMettreAJour)
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierFormations(formationsFavorites: List<String>?) {
        formationsFavorites?.takeUnless { it.isEmpty() }?.let {
            if (!formationRepository.verifierFormationsExistent(ids = it)) {
                throw MonProjetSupBadRequestException("FORMATIONS_NON_RECONNUES", "Une ou plusieurs des formations n'existent pas")
            }
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierMetiers(metiersFavoris: List<String>?) {
        metiersFavoris?.takeUnless { it.isEmpty() }?.let {
            if (!metierRepository.verifierMetiersExistent(ids = it)) {
                throw MonProjetSupBadRequestException("METIERS_NON_RECONNUS", "Un ou plusieurs des métiers n'existent pas")
            }
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierCentresInterets(centresInterets: List<String>?) {
        centresInterets?.takeUnless { it.isEmpty() }?.let {
            if (!interetRepository.verifierCentresInteretsExistent(ids = it)) {
                throw MonProjetSupBadRequestException(
                    "CENTRES_INTERETS_NON_RECONNUS",
                    "Un ou plusieurs des centres d'intérêt n'existent pas",
                )
            }
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierDomaines(domainesInterets: List<String>?) {
        domainesInterets?.takeUnless { it.isEmpty() }?.let {
            if (!domaineRepository.verifierDomainesExistent(ids = it)) {
                throw MonProjetSupBadRequestException("DOMAINES_NON_RECONNUS", "Un ou plusieurs des domaines n'existent pas")
            }
        }
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun verifierBaccalaureatEtSesSpecialites(
        miseAJourDuProfil: ModificationProfilEleve,
        ancienProfil: ProfilEleve.Identifie,
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
                throw MonProjetSupBadRequestException(
                    code = "ERREUR_MOYENNE_GENERALE",
                    msg = "La moyenne générale $it n'est pas dans l'intervalle $NOTE_MINIMALE et $NOTE_MAXIMALE",
                )
            }
        }
    }

    companion object {
        private const val NOTE_MINIMALE = 0
        private const val NOTE_MAXIMALE = 20
    }
}
