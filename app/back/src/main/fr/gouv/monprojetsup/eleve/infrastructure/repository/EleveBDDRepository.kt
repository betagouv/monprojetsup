package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.eleve.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.port.EleveRepository
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import org.springframework.stereotype.Repository
import kotlin.jvm.Throws

@Repository
class EleveBDDRepository : EleveRepository {
    @Throws(MonProjetSupNotFoundException::class)
    override fun recupererUnEleve(id: String): ProfilEleve {
        return ProfilEleve(
            id = "6d8aca7a-846c-4b42-b909-f1f8c8ab1e6a",
            situation = SituationAvanceeProjetSup.QUELQUES_PISTES,
            classe = ChoixNiveau.SECONDE,
            baccalaureat = "S2TMD",
            dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
            alternance = ChoixAlternance.INTERESSE,
            communesFavorites = emptyList(),
            specialites = emptyList(),
            centresInterets = listOf("T_IDEO2_4813", "T_ROME_2092381918"),
            moyenneGenerale = null,
            metiersFavoris = emptyList(),
            formationsFavorites = listOf("fl0001", "fl0005"),
            domainesInterets = emptyList(),
        )
    }

    override fun creerUnEleve(id: String): ProfilEleve {
        return ProfilEleve(
            id = "6d8aca7a-846c-4b42-b909-f1f8c8ab1e6a",
            situation = SituationAvanceeProjetSup.QUELQUES_PISTES,
            classe = ChoixNiveau.SECONDE,
            baccalaureat = "S2TMD",
            dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
            alternance = ChoixAlternance.INTERESSE,
            communesFavorites = emptyList(),
            specialites = emptyList(),
            centresInterets = listOf("T_IDEO2_4813", "T_ROME_2092381918"),
            moyenneGenerale = null,
            metiersFavoris = emptyList(),
            formationsFavorites = listOf("fl0001", "fl0005"),
            domainesInterets = emptyList(),
        )
    }
}
