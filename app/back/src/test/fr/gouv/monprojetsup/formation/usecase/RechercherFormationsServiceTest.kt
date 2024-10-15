package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.ResultatRechercheFormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.ResultatRechercheFormationCourte.ScoreMot
import fr.gouv.monprojetsup.formation.domain.port.RechercheFormationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.inOrder
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.MockitoAnnotations

class RechercherFormationsServiceTest {
    @Mock
    private lateinit var rechercheFormationRepository: RechercheFormationRepository

    @InjectMocks
    private lateinit var rechercherFormationsService: RechercherFormationsService

    private val rechercheLongue = "[^12  ma*réchèrche 1%peu Toùt!peTit peu&lôngue a b c === ×÷\\ h^&e l l`.,|o w]{+o r l d'"

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Nested
    inner class FiltrerMotsRecherches {
        @Test
        fun `ne doit pas appeler le repository pour les mots alpha numérique`() {
            // When
            rechercherFormationsService.rechercheLesFormationsAvecLeurScoreCorrespondantes(
                recherche = rechercheLongue,
                tailleMinimumRecherche = 2,
            )

            // Then
            val inOrder = inOrder(rechercheFormationRepository)
            inOrder.verify(rechercheFormationRepository).rechercherUneFormation("12")
            inOrder.verify(rechercheFormationRepository).rechercherUneFormation("réchèrche")
            inOrder.verify(rechercheFormationRepository).rechercherUneFormation("peu")
            inOrder.verify(rechercheFormationRepository).rechercherUneFormation("Toùt")
            inOrder.verify(rechercheFormationRepository).rechercherUneFormation("peTit")
            inOrder.verify(rechercheFormationRepository).rechercherUneFormation("lôngue")
            verifyNoMoreInteractions(rechercheFormationRepository)
        }

        @Test
        fun `ne doit pas appeler le repository pour les mots de moins de 2 caractères`() {
            // When
            rechercherFormationsService.rechercheLesFormationsAvecLeurScoreCorrespondantes(
                recherche = rechercheLongue,
                tailleMinimumRecherche = 2,
            )

            // Then
            then(rechercheFormationRepository).should(never()).rechercherUneFormation(motRecherche = "1")
            then(rechercheFormationRepository).should(never()).rechercherUneFormation(motRecherche = "a")
            then(rechercheFormationRepository).should(never()).rechercherUneFormation(motRecherche = "b")
            then(rechercheFormationRepository).should(never()).rechercherUneFormation(motRecherche = "c")
        }

        @Test
        fun `ne doit pas appeler le repository pour les mots vides`() {
            // Given
            val rechercheAvecMotsVide =
                "ma recherche avec ma liste de mots vides le la les aux un une des du des en sur sous dans chez par pour sans contre entre parmi vers derrière devant après avant autour et ou mais donc ni car que quand comme puisque quoique mon mes ton ta tes son sa ses notre nos votre vos leur leurs ce cet cette ces qui que quoi dont lequel laquelle lesquels lesquelles"

            // When
            rechercherFormationsService.rechercheLesFormationsAvecLeurScoreCorrespondantes(
                recherche = rechercheAvecMotsVide,
                tailleMinimumRecherche = 2,
            )

            // Then
            val inOrder = inOrder(rechercheFormationRepository)
            inOrder.verify(rechercheFormationRepository).rechercherUneFormation("recherche")
            inOrder.verify(rechercheFormationRepository).rechercherUneFormation("liste")
            inOrder.verify(rechercheFormationRepository).rechercherUneFormation("mots")
            inOrder.verify(rechercheFormationRepository).rechercherUneFormation("vides")
            verifyNoMoreInteractions(rechercheFormationRepository)
        }

        @Test
        fun `ne doit pas appeler le repository plusieurs fois pour le même mot`() {
            // When
            rechercherFormationsService.rechercheLesFormationsAvecLeurScoreCorrespondantes(
                recherche = rechercheLongue,
                tailleMinimumRecherche = 2,
            )

            // Then
            then(rechercheFormationRepository).should(times(1)).rechercherUneFormation(motRecherche = "peu")
        }
    }

    @Nested
    inner class CalculerScores {
        @Test
        fun `doit retourner la liste des formations sans doublons avec les scores additionnés`() {
            // Given
            val formationsPour12 =
                listOf(
                    ResultatRechercheFormationCourte(
                        formation = FormationCourte(id = "fl3", nom = "CAP Pâtisserie"),
                        scoreLabel = null,
                        scoreMotClef = null,
                    ),
                )
            given(rechercheFormationRepository.rechercherUneFormation("12")).willReturn(formationsPour12)

            val formationsPourRecherche =
                listOf(
                    ResultatRechercheFormationCourte(
                        formation = FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                        scoreLabel =
                            ScoreMot(
                                mot = "réchèrche",
                                motExact = true,
                                motExactPresentDebutPhrase = false,
                                motExactPresentFin = false,
                                motExactMilieu = false,
                                sequencePresenteMot = true,
                                pourcentageMot = 100,
                            ),
                        scoreMotClef = null,
                    ),
                    ResultatRechercheFormationCourte(
                        formation = FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
                        scoreLabel =
                            ScoreMot(
                                mot = "réchèrche",
                                motExact = false,
                                motExactPresentDebutPhrase = true,
                                motExactPresentFin = false,
                                motExactMilieu = false,
                                sequencePresenteMot = true,
                                pourcentageMot = 100,
                            ),
                        scoreMotClef =
                            ScoreMot(
                                mot = "réchèrche",
                                motExact = false,
                                motExactPresentDebutPhrase = false,
                                motExactPresentFin = false,
                                motExactMilieu = false,
                                sequencePresenteMot = false,
                                pourcentageMot = 43,
                            ),
                    ),
                )
            given(rechercheFormationRepository.rechercherUneFormation("réchèrche")).willReturn(formationsPourRecherche)

            val formationsPourPeu =
                listOf(
                    ResultatRechercheFormationCourte(
                        formation = FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                        scoreLabel = null,
                        scoreMotClef =
                            ScoreMot(
                                mot = "peu",
                                motExact = true,
                                motExactPresentDebutPhrase = false,
                                motExactPresentFin = false,
                                motExactMilieu = false,
                                sequencePresenteMot = true,
                                pourcentageMot = 100,
                            ),
                    ),
                    ResultatRechercheFormationCourte(
                        formation = FormationCourte(id = "fl3", nom = "CAP Pâtisserie"),
                        scoreLabel = null,
                        scoreMotClef =
                            ScoreMot(
                                mot = "peu",
                                motExact = false,
                                motExactPresentDebutPhrase = false,
                                motExactPresentFin = false,
                                motExactMilieu = false,
                                sequencePresenteMot = true,
                                pourcentageMot = 50,
                            ),
                    ),
                    ResultatRechercheFormationCourte(
                        formation = FormationCourte(id = "fl17", nom = "L1 - Mathématique"),
                        scoreLabel = null,
                        scoreMotClef =
                            ScoreMot(
                                mot = "peu",
                                motExact = false,
                                motExactPresentDebutPhrase = false,
                                motExactPresentFin = true,
                                motExactMilieu = false,
                                sequencePresenteMot = true,
                                pourcentageMot = 77,
                            ),
                    ),
                    ResultatRechercheFormationCourte(
                        formation = FormationCourte(id = "fl1000", nom = "BPJEPS"),
                        scoreLabel = null,
                        scoreMotClef =
                            ScoreMot(
                                mot = "peu",
                                motExact = false,
                                motExactPresentDebutPhrase = false,
                                motExactPresentFin = false,
                                motExactMilieu = true,
                                sequencePresenteMot = true,
                                pourcentageMot = 76,
                            ),
                    ),
                )
            given(rechercheFormationRepository.rechercherUneFormation("peu")).willReturn(formationsPourPeu)

            val formationsPourTout = emptyList<ResultatRechercheFormationCourte>()
            given(rechercheFormationRepository.rechercherUneFormation("Toùt")).willReturn(formationsPourTout)

            val formationsPourPetit =
                listOf(
                    ResultatRechercheFormationCourte(
                        formation = FormationCourte(id = "fl17", nom = "L1 - Mathématique"),
                        scoreLabel = null,
                        scoreMotClef =
                            ScoreMot(
                                mot = "peTit",
                                motExact = false,
                                motExactPresentDebutPhrase = false,
                                motExactPresentFin = true,
                                motExactMilieu = false,
                                sequencePresenteMot = false,
                                pourcentageMot = 80,
                            ),
                    ),
                    ResultatRechercheFormationCourte(
                        formation = FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
                        scoreLabel = null,
                        scoreMotClef =
                            ScoreMot(
                                mot = "peTit",
                                motExact = false,
                                motExactPresentDebutPhrase = false,
                                motExactPresentFin = true,
                                motExactMilieu = false,
                                sequencePresenteMot = false,
                                pourcentageMot = 30,
                            ),
                    ),
                )
            given(rechercheFormationRepository.rechercherUneFormation("peTit")).willReturn(formationsPourPetit)

            val formationsPourLongue =
                listOf(
                    ResultatRechercheFormationCourte(
                        formation = FormationCourte(id = "fl20", nom = "CAP Boulangerie"),
                        scoreLabel =
                            ScoreMot(
                                mot = "lôngue",
                                motExact = false,
                                motExactPresentDebutPhrase = true,
                                motExactPresentFin = false,
                                motExactMilieu = false,
                                sequencePresenteMot = true,
                                pourcentageMot = 100,
                            ),
                        scoreMotClef = null,
                    ),
                    ResultatRechercheFormationCourte(
                        formation = FormationCourte(id = "fl17", nom = "L1 - Mathématique"),
                        scoreLabel =
                            ScoreMot(
                                mot = "lôngue",
                                motExact = false,
                                motExactPresentDebutPhrase = false,
                                motExactPresentFin = true,
                                motExactMilieu = false,
                                sequencePresenteMot = true,
                                pourcentageMot = 100,
                            ),
                        scoreMotClef = null,
                    ),
                    ResultatRechercheFormationCourte(
                        formation = FormationCourte(id = "fl10", nom = "DUT Informatique"),
                        scoreLabel =
                            ScoreMot(
                                mot = "lôngue",
                                motExact = false,
                                motExactPresentDebutPhrase = false,
                                motExactPresentFin = false,
                                motExactMilieu = true,
                                sequencePresenteMot = true,
                                pourcentageMot = 100,
                            ),
                        scoreMotClef = null,
                    ),
                    ResultatRechercheFormationCourte(
                        formation = FormationCourte(id = "fl18", nom = "L1 - Littérature"),
                        scoreLabel =
                            ScoreMot(
                                mot = "lôngue",
                                motExact = false,
                                motExactPresentDebutPhrase = false,
                                motExactPresentFin = false,
                                motExactMilieu = false,
                                sequencePresenteMot = true,
                                pourcentageMot = 83,
                            ),
                        scoreMotClef = null,
                    ),
                    ResultatRechercheFormationCourte(
                        formation = FormationCourte(id = "fl21", nom = "L1 - Science de la vie"),
                        scoreLabel =
                            ScoreMot(
                                mot = "lôngue",
                                motExact = false,
                                motExactPresentDebutPhrase = false,
                                motExactPresentFin = false,
                                motExactMilieu = false,
                                sequencePresenteMot = false,
                                pourcentageMot = 37,
                            ),
                        scoreMotClef = null,
                    ),
                )
            given(rechercheFormationRepository.rechercherUneFormation("lôngue")).willReturn(formationsPourLongue)

            // When
            val resultat =
                rechercherFormationsService.rechercheLesFormationsAvecLeurScoreCorrespondantes(
                    recherche = rechercheLongue,
                    tailleMinimumRecherche = 2,
                )

            // Then
            val attendu =
                mapOf(
                    FormationCourte(id = "fl3", nom = "CAP Pâtisserie") to 0 + (0.83 * 50).toInt(),
                    FormationCourte(id = "fl1", nom = "L1 - Psychologie") to 150 + 85,
                    FormationCourte(id = "fl7", nom = "L1 - Philosophie") to 130 + (0.84 * 30).toInt(),
                    FormationCourte(id = "fl17", nom = "L1 - Mathématique") to (0.84 * 77).toInt() + (0.84 * 80).toInt() + 130,
                    FormationCourte(id = "fl1000", nom = "BPJEPS") to (76 * 0.84).toInt(),
                    FormationCourte(id = "fl20", nom = "CAP Boulangerie") to 130,
                    FormationCourte(id = "fl10", nom = "DUT Informatique") to 130,
                    FormationCourte(id = "fl18", nom = "L1 - Littérature") to 110,
                    FormationCourte(id = "fl21", nom = "L1 - Science de la vie") to 37,
                )
            assertThat(resultat).isEqualTo(attendu)
        }
    }
}
