package fr.gouv.monprojetsup.eleve.domain.port

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import java.util.UUID
import kotlin.jvm.Throws

interface EleveRepository {
    fun recupererUnEleve(id: UUID): ProfilEleve

    fun creerUnEleve(id: UUID): ProfilEleve.Identifie

    @Throws(MonProjetSupNotFoundException::class)
    fun mettreAJourUnProfilEleve(profilEleve: ProfilEleve.Identifie)
}
