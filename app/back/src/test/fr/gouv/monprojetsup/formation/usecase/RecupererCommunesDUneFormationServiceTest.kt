package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.Commune
import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.formation.domain.port.TripletAffectationRepository
import fr.gouv.monprojetsup.formation.entity.Communes
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RecupererCommunesDUneFormationServiceTest {
    @Mock
    lateinit var tripletAffectationRepository: TripletAffectationRepository

    @InjectMocks
    lateinit var recupererCommunesDUneFormationService: RecupererCommunesDUneFormationService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Nested
    inner class RecupererNomCommunesTriesParAffinitesPourFormations {
        @Test
        fun `les communes doivent être ordonnées par affinités et enlever les doublons`() {
            // Given
            val communesFavorites = listOf(Communes.PARIS, Communes.CAEN)
            val idsFormation =
                listOf(
                    "fl680002",
                    "fr22",
                    "fl2009",
                    "fl2016",
                    "fl252",
                )
            val tripletsAffectation =
                mapOf(
                    "fl680002" to
                        listOf(
                            TripletAffectation("ta6", "Marseille"),
                            TripletAffectation("ta1", "Paris"),
                        ),
                    "fr22" to
                        listOf(
                            TripletAffectation("ta2", "Lyon"),
                        ),
                    "fl2009" to
                        listOf(
                            TripletAffectation("ta10", "Lyon"),
                            TripletAffectation("ta3", "Paris"),
                            TripletAffectation("ta7", "Strasbourg"),
                            TripletAffectation("ta11", "Lyon"),
                        ),
                    "fl2016" to
                        listOf(
                            TripletAffectation("ta4", "Marseille"),
                        ),
                    "fl252" to
                        listOf(
                            TripletAffectation("ta5", "Caen"),
                            TripletAffectation("ta8", "Paris"),
                            TripletAffectation("ta9", "Clermont-Ferrand"),
                        ),
                )
            given(tripletAffectationRepository.recupererLesTripletsAffectationDeFormations(idsFormation)).willReturn(
                tripletsAffectation,
            )

            // When
            val result = recupererCommunesDUneFormationService.recupererNomCommunesTriesParAffinites(idsFormation, communesFavorites)

            // Then
            assertThat(result).isEqualTo(
                mapOf(
                    "fl680002" to listOf("Paris", "Marseille"),
                    "fr22" to listOf("Lyon"),
                    "fl2009" to listOf("Paris", "Lyon", "Strasbourg"),
                    "fl2016" to listOf("Marseille"),
                    "fl252" to listOf("Paris", "Caen", "Clermont-Ferrand"),
                ),
            )
        }

        @Test
        fun `si la liste des communes favorites est vide, doit juste enlever les doublons`() {
            // Given
            val communesFavorites = emptyList<Commune>()
            val idsFormation =
                listOf(
                    "fl680002",
                    "fr22",
                    "fl2009",
                    "fl2016",
                    "fl252",
                )
            val tripletsAffectation =
                mapOf(
                    "fl680002" to
                        listOf(
                            TripletAffectation("ta6", "Marseille"),
                            TripletAffectation("ta1", "Paris"),
                        ),
                    "fr22" to
                        listOf(
                            TripletAffectation("ta2", "Lyon"),
                        ),
                    "fl2009" to
                        listOf(
                            TripletAffectation("ta10", "Lyon"),
                            TripletAffectation("ta3", "Paris"),
                            TripletAffectation("ta7", "Strasbourg"),
                            TripletAffectation("ta11", "Lyon"),
                        ),
                    "fl2016" to
                        listOf(
                            TripletAffectation("ta4", "Marseille"),
                        ),
                    "fl252" to
                        listOf(
                            TripletAffectation("ta5", "Caen"),
                            TripletAffectation("ta8", "Paris"),
                            TripletAffectation("ta9", "Clermont-Ferrand"),
                        ),
                )
            given(tripletAffectationRepository.recupererLesTripletsAffectationDeFormations(idsFormation)).willReturn(
                tripletsAffectation,
            )

            // When
            val result = recupererCommunesDUneFormationService.recupererNomCommunesTriesParAffinites(idsFormation, communesFavorites)

            // Then
            assertThat(result).isEqualTo(
                mapOf(
                    "fl680002" to listOf("Marseille", "Paris"),
                    "fr22" to listOf("Lyon"),
                    "fl2009" to listOf("Lyon", "Paris", "Strasbourg"),
                    "fl2016" to listOf("Marseille"),
                    "fl252" to listOf("Caen", "Paris", "Clermont-Ferrand"),
                ),
            )
        }

        @Test
        fun `si la liste des communes favorites est nulle, doit juste enlever les doublons`() {
            // Given
            val communesFavorites = null
            val idsFormation =
                listOf(
                    "fl680002",
                    "fr22",
                    "fl2009",
                    "fl2016",
                    "fl252",
                )
            val tripletsAffectation =
                mapOf(
                    "fl680002" to
                        listOf(
                            TripletAffectation("ta6", "Marseille"),
                            TripletAffectation("ta1", "Paris"),
                        ),
                    "fr22" to
                        listOf(
                            TripletAffectation("ta2", "Lyon"),
                        ),
                    "fl2009" to
                        listOf(
                            TripletAffectation("ta10", "Lyon"),
                            TripletAffectation("ta3", "Paris"),
                            TripletAffectation("ta7", "Strasbourg"),
                            TripletAffectation("ta11", "Lyon"),
                        ),
                    "fl2016" to
                        listOf(
                            TripletAffectation("ta4", "Marseille"),
                        ),
                    "fl252" to
                        listOf(
                            TripletAffectation("ta5", "Caen"),
                            TripletAffectation("ta8", "Paris"),
                            TripletAffectation("ta9", "Clermont-Ferrand"),
                        ),
                )
            given(tripletAffectationRepository.recupererLesTripletsAffectationDeFormations(idsFormation)).willReturn(
                tripletsAffectation,
            )

            // When
            val result = recupererCommunesDUneFormationService.recupererNomCommunesTriesParAffinites(idsFormation, communesFavorites)

            // Then
            assertThat(result).isEqualTo(
                mapOf(
                    "fl680002" to listOf("Marseille", "Paris"),
                    "fr22" to listOf("Lyon"),
                    "fl2009" to listOf("Lyon", "Paris", "Strasbourg"),
                    "fl2016" to listOf("Marseille"),
                    "fl252" to listOf("Caen", "Paris", "Clermont-Ferrand"),
                ),
            )
        }
    }

    @Nested
    inner class RecupererNomCommunesTriesParAffinitesPourFormation {
        @Test
        fun `les communes doivent être ordonnées par affinités et enlever les doublons`() {
            // Given
            val communesFavorites = listOf(Communes.LYON, Communes.GRENOBLE, Communes.PARIS)
            given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation = "fl2016")).willReturn(
                listOf(
                    TripletAffectation("ta3", "Paris"),
                    TripletAffectation("ta10", "Lyon"),
                    TripletAffectation("ta7", "Strasbourg"),
                    TripletAffectation("ta11", "Lyon"),
                ),
            )

            // When
            val result =
                recupererCommunesDUneFormationService.recupererNomCommunesTriesParAffinites(
                    idFormation = "fl2016",
                    communesFavorites = communesFavorites,
                )

            // Then
            assertThat(result).isEqualTo(listOf("Lyon", "Paris", "Strasbourg"))
        }

        @Test
        fun `si la liste des communes favorites est vide, doit juste enlever les doublons`() {
            // Given
            val communesFavorites = emptyList<Commune>()
            given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation = "fl2016")).willReturn(
                listOf(
                    TripletAffectation("ta3", "Paris"),
                    TripletAffectation("ta10", "Lyon"),
                    TripletAffectation("ta7", "Strasbourg"),
                    TripletAffectation("ta11", "Lyon"),
                ),
            )

            // When
            val result =
                recupererCommunesDUneFormationService.recupererNomCommunesTriesParAffinites(
                    idFormation = "fl2016",
                    communesFavorites = communesFavorites,
                )

            // Then
            assertThat(result).isEqualTo(listOf("Paris", "Lyon", "Strasbourg"))
        }

        @Test
        fun `si la liste des communes favorites est nulle, doit juste enlever les doublons`() {
            // Given
            val communesFavorites = null
            given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation = "fl2016")).willReturn(
                listOf(
                    TripletAffectation("ta10", "Lyon"),
                    TripletAffectation("ta3", "Paris"),
                    TripletAffectation("ta7", "Strasbourg"),
                    TripletAffectation("ta11", "Lyon"),
                ),
            )

            // When
            val result =
                recupererCommunesDUneFormationService.recupererNomCommunesTriesParAffinites(
                    idFormation = "fl2016",
                    communesFavorites = communesFavorites,
                )

            // Then
            assertThat(result).isEqualTo(listOf("Lyon", "Paris", "Strasbourg"))
        }
    }

    @Nested
    inner class RecupererNomCommunes {
        @Test
        fun `les communes doivent être retournées en enlevant les doublons`() {
            // Given
            given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation = "fl2016")).willReturn(
                listOf(
                    TripletAffectation("ta10", "Lyon"),
                    TripletAffectation("ta3", "Paris"),
                    TripletAffectation("ta7", "Strasbourg"),
                    TripletAffectation("ta11", "Lyon"),
                ),
            )

            // When
            val result =
                recupererCommunesDUneFormationService.recupererNomCommunes(
                    idFormation = "fl2016",
                )

            // Then
            assertThat(result).isEqualTo(listOf("Lyon", "Paris", "Strasbourg"))
        }
    }
}
