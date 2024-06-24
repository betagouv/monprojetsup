package fr.gouv.monprojetsup.recherche.usecase

import fr.gouv.monprojetsup.recherche.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation.FicheFormationPourProfil.MoyenneGeneraleDesAdmis
import fr.gouv.monprojetsup.recherche.domain.entity.FicheFormation.FicheFormationPourProfil.MoyenneGeneraleDesAdmis.Centile
import fr.gouv.monprojetsup.recherche.domain.port.MoyenneGeneraleAdmisRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MoyenneGeneraleDesAdmisServiceTest {
    @Mock
    lateinit var moyenneGeneraleAdmisRepository: MoyenneGeneraleAdmisRepository

    @InjectMocks
    lateinit var moyenneGeneraleDesAdmisService: MoyenneGeneraleDesAdmisService

    private val frequencesCumulees =
        mapOf(
            "Général" to
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
            "STMG" to
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
            "STI2D" to
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

    private val centilesTousBacConfonduses =
        listOf(
            Centile(5, 12.5f),
            Centile(25, 14.5f),
            Centile(75, 17.5f),
            Centile(95, 18.5f),
        )

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        given(moyenneGeneraleAdmisRepository.recupererFrequencesCumuleesDeToutLesBacs("fl0001")).willReturn(
            frequencesCumulees,
        )
    }

    @Test
    fun `doit retourner la moyenne générale avec la gauche de l'intervalle pour la 1ere partie et la droite pour la 2nde`() {
        // Given
        val baccalaureat = Baccalaureat(id = "Général", idExterne = "Générale", nom = "Série Générale")

        // When
        val resultat =
            moyenneGeneraleDesAdmisService.recupererMoyenneGeneraleDesAdmisDUneFormation(
                baccalaureat = baccalaureat,
                idFormation = "fl0001",
            )

        // Then
        val attendu =
            MoyenneGeneraleDesAdmis(
                idBaccalaureat = "Général",
                nomBaccalaureat = "Série Générale",
                centiles =
                    listOf(
                        Centile(centile = 5, note = 13f),
                        Centile(centile = 25, note = 14.5f),
                        Centile(centile = 75, note = 17.5f),
                        Centile(centile = 95, note = 18.5f),
                    ),
            )
        assertThat(resultat).usingRecursiveComparison().isEqualTo(attendu)
    }

    @Test
    fun `si le baccalaureat est null, doit retourner pour tous les baccalauréats confondus`() {
        // When
        val resultat =
            moyenneGeneraleDesAdmisService.recupererMoyenneGeneraleDesAdmisDUneFormation(baccalaureat = null, idFormation = "fl0001")

        // Then
        val attendu = MoyenneGeneraleDesAdmis(idBaccalaureat = null, nomBaccalaureat = null, centiles = centilesTousBacConfonduses)
        assertThat(resultat).usingRecursiveComparison().isEqualTo(attendu)
    }

    @Test
    fun `si l'id du baccalaureat n'est pas dans la liste retournée, doit retourner pour tous les baccalauréats confondus`() {
        // Given
        val baccalaureat = Baccalaureat(id = "Pro", idExterne = "P", nom = "Bac Pro")

        // When
        val resultat =
            moyenneGeneraleDesAdmisService.recupererMoyenneGeneraleDesAdmisDUneFormation(
                baccalaureat = baccalaureat,
                idFormation = "fl0001",
            )

        // Then
        val attendu = MoyenneGeneraleDesAdmis(idBaccalaureat = null, nomBaccalaureat = null, centiles = centilesTousBacConfonduses)
        assertThat(resultat).usingRecursiveComparison().isEqualTo(attendu)
    }

    @Test
    fun `si moins de 30 admis pour le baccalaureat dans la formation, doit retourner pour tous les baccalauréats confondus`() {
        // Given
        val baccalaureat = Baccalaureat(id = "STMG", idExterne = "STMG", nom = "Série STMG")

        // When
        val resultat =
            moyenneGeneraleDesAdmisService.recupererMoyenneGeneraleDesAdmisDUneFormation(
                baccalaureat = baccalaureat,
                idFormation = "fl0001",
            )

        // Then
        val attendu = MoyenneGeneraleDesAdmis(idBaccalaureat = null, nomBaccalaureat = null, centiles = centilesTousBacConfonduses)
        assertThat(resultat).usingRecursiveComparison().isEqualTo(attendu)
    }

    @Test
    fun `si moins de 30 admis pour tous bac confondus, doit quand même retourner tous bac confondus`() {
        // Given
        val baccalaureat = Baccalaureat(id = "STMG", idExterne = "STMG", nom = "Série STMG")
        val frequencesAvecMoinsDe30AdmisAuTotal =
            mapOf(
                "STMG" to
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
            )
        given(moyenneGeneraleAdmisRepository.recupererFrequencesCumuleesDeToutLesBacs("fl0001")).willReturn(
            frequencesAvecMoinsDe30AdmisAuTotal,
        )
        // When
        val resultat =
            moyenneGeneraleDesAdmisService.recupererMoyenneGeneraleDesAdmisDUneFormation(
                baccalaureat = baccalaureat,
                idFormation = "fl0001",
            )

        // Then
        val attendu =
            MoyenneGeneraleDesAdmis(
                idBaccalaureat = null,
                nomBaccalaureat = null,
                centiles =
                    listOf(
                        Centile(centile = 5, note = 7.5f),
                        Centile(centile = 25, note = 11.5f),
                        Centile(centile = 75, note = 14.5f),
                        Centile(centile = 95, note = 16f),
                    ),
            )
        assertThat(resultat).usingRecursiveComparison().isEqualTo(attendu)
    }

    @Test
    fun `si la liste de fréquence cumulées est vide, alors doit retourner null`() {
        val baccalaureat = Baccalaureat(id = "STMG", idExterne = "STMG", nom = "Série STMG")
        val frequencesAvecMoinsDe30AdmisAuTotal =
            mapOf(
                "STMG" to emptyList<Int>(),
            )
        given(moyenneGeneraleAdmisRepository.recupererFrequencesCumuleesDeToutLesBacs("fl0001")).willReturn(
            frequencesAvecMoinsDe30AdmisAuTotal,
        )
        // When
        val resultat =
            moyenneGeneraleDesAdmisService.recupererMoyenneGeneraleDesAdmisDUneFormation(
                baccalaureat = baccalaureat,
                idFormation = "fl0001",
            )

        // Then
        assertThat(resultat).isNull()
    }

    @Test
    fun `si le retour du repository est vide, alors doit retourner null`() {
        val baccalaureat = Baccalaureat(id = "STMG", idExterne = "STMG", nom = "Série STMG")
        val frequencesAvecMoinsDe30AdmisAuTotal = emptyMap<String, List<Int>>()
        given(moyenneGeneraleAdmisRepository.recupererFrequencesCumuleesDeToutLesBacs("fl0001")).willReturn(
            frequencesAvecMoinsDe30AdmisAuTotal,
        )
        // When
        val resultat =
            moyenneGeneraleDesAdmisService.recupererMoyenneGeneraleDesAdmisDUneFormation(
                baccalaureat = baccalaureat,
                idFormation = "fl0001",
            )

        // Then
        assertThat(resultat).isNull()
    }
}
