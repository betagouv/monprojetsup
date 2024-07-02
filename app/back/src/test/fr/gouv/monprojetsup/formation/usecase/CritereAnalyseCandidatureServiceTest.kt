package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.FormationDetaillee
import fr.gouv.monprojetsup.formation.domain.port.CriteresAnalyseCandidatureRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class CritereAnalyseCandidatureServiceTest {
    @Mock
    lateinit var criteresAnalyseCandidatureRepository: CriteresAnalyseCandidatureRepository

    @InjectMocks
    lateinit var critereAnalyseCandidatureService: CritereAnalyseCandidatureService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    private val critereAnalyseCandidatureFormation1 =
        listOf(
            CritereAnalyseCandidature(
                nom = "Compétences académiques",
                pourcentage = 0,
            ),
            CritereAnalyseCandidature(
                nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                pourcentage = 5,
            ),
            CritereAnalyseCandidature(
                nom = "Résultats académiques",
                pourcentage = 29,
            ),
            CritereAnalyseCandidature(
                nom = "Savoir-être",
                pourcentage = 63,
            ),
            CritereAnalyseCandidature(
                nom = "Motivation, connaissance",
                pourcentage = 3,
            ),
        )
    private val critereAnalyseCandidatureFormation2 =
        listOf(
            CritereAnalyseCandidature(
                nom = "Compétences académiques",
                pourcentage = 0,
            ),
            CritereAnalyseCandidature(
                nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                pourcentage = 0,
            ),
            CritereAnalyseCandidature(
                nom = "Résultats académiques",
                pourcentage = 0,
            ),
            CritereAnalyseCandidature(
                nom = "Savoir-être",
                pourcentage = 100,
            ),
            CritereAnalyseCandidature(
                nom = "Motivation, connaissance",
                pourcentage = 0,
            ),
        )

    @Test
    fun `pour une formation donnée, doit filtrer les critères à 0`() {
        // Given
        val valeurCriteresAnalyseCandidature = listOf(0, 5, 29, 63, 3)
        val formation = mock(FormationDetaillee::class.java)
        given(formation.valeurCriteresAnalyseCandidature).willReturn(valeurCriteresAnalyseCandidature)
        given(criteresAnalyseCandidatureRepository.recupererLesCriteresDUneFormation(valeurCriteresAnalyseCandidature)).willReturn(
            critereAnalyseCandidatureFormation1,
        )

        // When
        val resultat = critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formation)

        // Then
        val attendu =
            listOf(
                CritereAnalyseCandidature(
                    nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                    pourcentage = 5,
                ),
                CritereAnalyseCandidature(
                    nom = "Résultats académiques",
                    pourcentage = 29,
                ),
                CritereAnalyseCandidature(
                    nom = "Savoir-être",
                    pourcentage = 63,
                ),
                CritereAnalyseCandidature(
                    nom = "Motivation, connaissance",
                    pourcentage = 3,
                ),
            )
        assertThat(resultat).isEqualTo(attendu)
    }

    @Test
    fun `pour plusieurs formations données, doit filtrer leurs critères à 0`() {
        val valeurCriteresAnalyseCandidature1 = listOf(0, 5, 29, 63, 3)
        val formation1 = mock(FormationDetaillee::class.java)

        given(formation1.valeurCriteresAnalyseCandidature).willReturn(valeurCriteresAnalyseCandidature1)

        val valeurCriteresAnalyseCandidature2 = listOf(0, 5, 29, 63, 3)
        val formation2 = mock(FormationDetaillee::class.java)
        given(formation1.valeurCriteresAnalyseCandidature).willReturn(valeurCriteresAnalyseCandidature2)
        val formations = listOf(formation2, formation1)
        given(criteresAnalyseCandidatureRepository.recupererLesCriteresDeFormations(formations)).willReturn(
            mapOf(
                "fl2" to critereAnalyseCandidatureFormation2,
                "fl1" to critereAnalyseCandidatureFormation1,
            ),
        )

        // When
        val resultat = critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formations)

        // Then
        val attendu =
            mapOf(
                "fl2" to
                    listOf(
                        CritereAnalyseCandidature(
                            nom = "Savoir-être",
                            pourcentage = 100,
                        ),
                    ),
                "fl1" to
                    listOf(
                        CritereAnalyseCandidature(
                            nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                            pourcentage = 5,
                        ),
                        CritereAnalyseCandidature(
                            nom = "Résultats académiques",
                            pourcentage = 29,
                        ),
                        CritereAnalyseCandidature(
                            nom = "Savoir-être",
                            pourcentage = 63,
                        ),
                        CritereAnalyseCandidature(
                            nom = "Motivation, connaissance",
                            pourcentage = 3,
                        ),
                    ),
            )
        assertThat(resultat).isEqualTo(attendu)
    }
}
