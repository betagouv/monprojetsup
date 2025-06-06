package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.port.TraceRepository
import fr.gouv.monprojetsup.referentiel.infrastructure.repository.BaccalaureatSpecialiteBDDRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RecupererProgressionService(
    private val traceRepository: TraceRepository,
    private val baccalaureatSpecialiteBDDRepository: BaccalaureatSpecialiteBDDRepository,
) {
    @Transactional(readOnly = true)
    fun recupererProgression(eleve: ProfilEleve.AvecProfilExistant): Int {
        // 1. je complète mon profil à 100%
        // 2. j'ai lu 3 fiches
        // 3. >= 1 favori
        // 4. >= 3 favoris
        // 5. >= eval niveau ambition
        // 6. >= favoris Parcoursup
        val specialitesSelectionnablesParCandidat =
            eleve.baccalaureat?.let {
                baccalaureatSpecialiteBDDRepository.recupererLesIdsDesSpecialitesDUnBaccalaureat(
                    it,
                )
            }
        if (!eleve.estProfilComplet(specialitesSelectionnablesParCandidat)) {
            return 0
        }
        val nbFiches = traceRepository.getNbFichesLues(eleve.id)
        if (nbFiches < 3) {
            return 1
        }
        if (!eleve.aAuMoinsUnFavoriMPS()) {
            return 2
        }
        if (!eleve.aAuMoinsTroisFavorisMPS()) {
            return 3
        }
        if (!eleve.aEvalueSonNiveauAmbition()) {
            return 4
        }
        if (!eleve.aDesFavorisParcoursup()) {
            return 5
        }
        return 6
    }
}
