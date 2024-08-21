package fr.gouv.monprojetsup.commun.application.controller

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.MonProjetSupTestConfiguration
import fr.gouv.monprojetsup.configuration.SecuriteConfiguration
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFormation
import fr.gouv.monprojetsup.eleve.domain.port.EleveRepository
import fr.gouv.monprojetsup.formation.entity.Communes
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import org.junit.jupiter.api.BeforeEach
import org.mockito.BDDMockito.given
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration

@ContextConfiguration(classes = [MonProjetSupTestConfiguration::class])
@Import(value = [SecuriteConfiguration::class])
abstract class ControllerTest {
    @MockBean
    lateinit var eleveRepository: EleveRepository

    @BeforeEach
    fun setup() {
        given(eleveRepository.recupererUnEleve(id = "adcf627c-36dd-4df5-897b-159443a6d49c")).willReturn(unProfil)
    }

    val unProfil =
        ProfilEleve.Identifie(
            id = "adcf627c-36dd-4df5-897b-159443a6d49c",
            situation = SituationAvanceeProjetSup.AUCUNE_IDEE,
            classe = ChoixNiveau.TERMINALE,
            baccalaureat = "Générale",
            dureeEtudesPrevue = ChoixDureeEtudesPrevue.INDIFFERENT,
            alternance = ChoixAlternance.PAS_INTERESSE,
            communesFavorites = listOf(Communes.PARIS15EME),
            specialites = listOf("1056", "1054"),
            centresInterets = listOf("T_ROME_2092381917", "T_IDEO2_4812"),
            moyenneGenerale = 14f,
            metiersFavoris = listOf("MET_123", "MET_456"),
            formationsFavorites =
                listOf(
                    VoeuFormation(
                        idFormation = "fl1234",
                        niveauAmbition = 1,
                        tripletsAffectationsChoisis = emptyList(),
                        priseDeNote = null,
                    ),
                    VoeuFormation(
                        idFormation = "fl5678",
                        niveauAmbition = 3,
                        tripletsAffectationsChoisis = listOf("ta1", "ta2"),
                        priseDeNote = "Mon voeu préféré",
                    ),
                ),
            domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
            corbeilleFormations = listOf("fl0010", "fl0012"),
        )
}
