package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.FormationFavorite.Companion.NIVEAU_AMBITION_AMBITIEUX
import fr.gouv.monprojetsup.eleve.domain.entity.FormationFavorite.Companion.NIVEAU_AMBITION_PLAN_B
import fr.gouv.monprojetsup.eleve.domain.entity.FormationFavorite.Companion.NIVEAU_AMBITION_REALISTE
import fr.gouv.monprojetsup.eleve.domain.entity.Indicateurs
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RecupererIndicateursService() {
    @Transactional(readOnly = true)
    fun recupererIndicateurs(eleve: ProfilEleve.AvecProfilExistant): Indicateurs {
        return Indicateurs(
            formationsRealistes = eleve.formationsFavorites?.count { it.niveauAmbition == NIVEAU_AMBITION_REALISTE } ?: 0,
            formationsAmbitieuses = eleve.formationsFavorites?.count { it.niveauAmbition == NIVEAU_AMBITION_AMBITIEUX } ?: 0,
            formationsPlanB = eleve.formationsFavorites?.count { it.niveauAmbition == NIVEAU_AMBITION_PLAN_B } ?: 0,
        )
    }
}
