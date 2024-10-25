package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.commun.Constantes.ANNEE_DONNEES_PARCOURSUP
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis
import fr.gouv.monprojetsup.formation.domain.port.FrequencesCumuleesDesMoyenneDesAdmisRepository
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class StatistiquesDesAdmisPourFormationsServiceTest {
    @Mock
    lateinit var frequencesCumuleesDesMoyenneDesAdmisRepository: FrequencesCumuleesDesMoyenneDesAdmisRepository

    @Mock
    lateinit var statistiquesDesAdmisBuilder: StatistiquesDesAdmisBuilder

    @Mock
    lateinit var logger: MonProjetSupLogger

    @InjectMocks
    lateinit var moyenneGeneraleDesAdmisService: StatistiquesDesAdmisPourFormationsService

    private val frequencesCumulees =
        mapOf(
            Baccalaureat(id = "Générale", idExterne = "Général", nom = "Série Générale") to
                listOf(
                    0, // 0 - 0,5
                    0, // 0,5 - 1
                    0, // 1 - 1,5
                    0, // 1,5 - 2
                    0, // 2 - 2,5
                    0, // 2,5 - 3
                    0, // 3 - 3,5
                    0, // 3,5 - 4
                    0, // 4 - 4,5
                    0, // 4,5 - 5
                    0, // 5 - 5,5
                    0, // 5,5 - 6
                    0, // 6 - 6,5
                    0, // 6,5 - 7
                    0, // 7 - 7,5
                    0, // 7,5 - 8
                    0, // 8 - 8,5
                    0, // 8,5 - 9
                    0, // 9 - 9,5
                    0, // 9,5 - 10
                    6, // 10 - 10,5
                    24, // 10,5 - 11
                    49, // 11 - 11,5
                    77, // 11,5 - 12
                    174, // 12 - 12,5
                    292, // 12,5 - 13
                    500, // 13 - 13,5
                    685, // 13,5 - 14
                    1206, // 14 - 14,5
                    1700, // 14,5 - 15
                    2375, // 15 - 15,5
                    2845, // 15,5 - 16
                    3924, // 16 - 16,5
                    4755, // 16,5 - 17
                    5479, // 17 - 17,5
                    5893, // 17,5 - 18
                    6401, // 18 - 18,5
                    6612, // 18,5 - 19
                    6670, // 19 - 19,5
                    6677, // 19,5 - 20
                ),
            Baccalaureat(id = "STMG", idExterne = "STMG", nom = "Série STMG") to
                listOf(
                    0, // 0 - 0,5
                    0, // 0,5 - 1
                    0, // 1 - 1,5
                    0, // 1,5 - 2
                    0, // 2 - 2,5
                    0, // 2,5 - 3
                    0, // 3 - 3,5
                    0, // 3,5 - 4
                    0, // 4 - 4,5
                    0, // 4,5 - 5
                    0, // 5 - 5,5
                    0, // 5,5 - 6
                    0, // 6 - 6,5
                    0, // 6,5 - 7
                    0, // 7 - 7,5
                    1, // 7,5 - 8
                    1, // 8 - 8,5
                    1, // 8,5 - 9
                    1, // 9 - 9,5
                    1, // 9,5 - 10
                    1, // 10 - 10,5
                    2, // 10,5 - 11
                    2, // 11 - 11,5
                    4, // 11,5 - 12
                    4, // 12 - 12,5
                    8, // 12,5 - 13
                    8, // 13 - 13,5
                    11, // 13,5 - 14
                    12, // 14 - 14,5
                    13, // 14,5 - 15
                    14, // 15 - 15,5
                    15, // 15,5 - 16
                    15, // 16 - 16,5
                    15, // 16,5 - 17
                    15, // 17 - 17,5
                    15, // 17,5 - 18
                    15, // 18 - 18,5
                    15, // 18,5 - 19
                    15, // 19 - 19,5
                    15, // 19,5 - 20
                ),
            Baccalaureat(id = "STI2D", idExterne = "STI2D", nom = "Série STI2D") to
                listOf(
                    0, // 0 - 0,5
                    0, // 0,5 - 1
                    0, // 1 - 1,5
                    0, // 1,5 - 2
                    0, // 2 - 2,5
                    0, // 2,5 - 3
                    0, // 3 - 3,5
                    0, // 3,5 - 4
                    0, // 4 - 4,5
                    0, // 4,5 - 5
                    0, // 5 - 5,5
                    0, // 5,5 - 6
                    0, // 6 - 6,5
                    0, // 6,5 - 7
                    2, // 7 - 7,5
                    2, // 7,5 - 8
                    2, // 8 - 8,5
                    5, // 8,5 - 9
                    11, // 9 - 9,5
                    17, // 9,5 - 10
                    27, // 10 - 10,5
                    38, // 10,5 - 11
                    50, // 11 - 11,5
                    61, // 11,5 - 12
                    76, // 12 - 12,5
                    103, // 12,5 - 13
                    128, // 13 - 13,5
                    150, // 13,5 - 14
                    163, // 14 - 14,5
                    173, // 14,5 - 15
                    187, // 15 - 15,5
                    198, // 15,5 - 16
                    201, // 16 - 16,5
                    210, // 16,5 - 17
                    216, // 17 - 17,5
                    221, // 17,5 - 18
                    223, // 18 - 18,5
                    223, // 18,5 - 19
                    223, // 19 - 19,5
                    223, // 19,5 - 20
                ),
        )

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Nested
    inner class RecupererStatistiquesAdmisDUneFormation {
        @BeforeEach
        fun setup() {
            given(
                frequencesCumuleesDesMoyenneDesAdmisRepository.recupererFrequencesCumuleesDeTousLesBacs(
                    idFormation = "fl0001",
                    annee = ANNEE_DONNEES_PARCOURSUP,
                ),
            ).willReturn(
                frequencesCumulees,
            )
        }

        @Test
        fun `doit renvoyer ce que renvoie le builder`() {
            // Given
            val idBaccalaureat = "Général"
            val classe = ChoixNiveau.TERMINALE
            val statistiquesDesAdmis = mock(StatistiquesDesAdmis::class.java)
            given(statistiquesDesAdmisBuilder.creerStatistiquesDesAdmis(frequencesCumulees, idBaccalaureat, classe)).willReturn(
                statistiquesDesAdmis,
            )

            // When
            val resultat =
                moyenneGeneraleDesAdmisService.recupererStatistiquesAdmisDUneFormation(
                    idBaccalaureat = idBaccalaureat,
                    idFormation = "fl0001",
                    classe = classe,
                )

            // Then
            assertThat(resultat).isEqualTo(statistiquesDesAdmis)
        }
    }

    @Nested
    inner class RecupererStatistiquesAdmisDeFormations {
        lateinit var frequenceCumuleesMock: Map<Baccalaureat, List<Int>>

        private val idsFormations = listOf("fl0001", "fl0002", "fl0003", "fl0004")

        @BeforeEach
        fun setup() {
            frequenceCumuleesMock = mapOf(mock(Baccalaureat::class.java) to listOf(0, 1, 2, 3, 4, 5))
            given(
                frequencesCumuleesDesMoyenneDesAdmisRepository.recupererFrequencesCumuleesDeTousLesBacs(
                    idsFormations = idsFormations,
                    ANNEE_DONNEES_PARCOURSUP,
                ),
            ).willReturn(
                mapOf(
                    "fl0001" to frequencesCumulees,
                    "fl0002" to emptyMap(),
                    "fl0003" to frequenceCumuleesMock,
                ),
            )
        }

        @Test
        fun `doit renvoyer ce que renvoie le builder`() {
            // Given
            val idBaccalaureat = "Général"
            val classe = ChoixNiveau.TERMINALE
            val statistiquesDesAdmis1 = mock(StatistiquesDesAdmis::class.java)
            val statistiquesDesAdmis2 = mock(StatistiquesDesAdmis::class.java)
            val statistiquesDesAdmis3 = mock(StatistiquesDesAdmis::class.java)
            given(statistiquesDesAdmisBuilder.creerStatistiquesDesAdmis(frequencesCumulees, idBaccalaureat, classe)).willReturn(
                statistiquesDesAdmis1,
            )
            given(statistiquesDesAdmisBuilder.creerStatistiquesDesAdmis(emptyMap(), idBaccalaureat, classe)).willReturn(
                statistiquesDesAdmis2,
            )
            given(statistiquesDesAdmisBuilder.creerStatistiquesDesAdmis(frequenceCumuleesMock, idBaccalaureat, classe)).willReturn(
                statistiquesDesAdmis3,
            )

            // When
            val resultat =
                moyenneGeneraleDesAdmisService.recupererStatistiquesAdmisDeFormations(
                    idBaccalaureat,
                    idsFormations = idsFormations,
                    classe = classe,
                )

            // Then
            assertThat(resultat).isEqualTo(
                mapOf(
                    "fl0001" to statistiquesDesAdmis1,
                    "fl0002" to statistiquesDesAdmis2,
                    "fl0003" to statistiquesDesAdmis3,
                    "fl0004" to null,
                ),
            )
            then(logger).should().warn(
                type = "FORMATION_SANS_STATISTIQUE",
                message = "Les formations suivantes n'ont pas de statistiques : [fl0004]",
                mapOf("formationsSansStatistiques" to listOf("fl0004")),
            )
        }
    }
}
