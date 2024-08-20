package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.Commune
import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.formation.domain.port.TripletAffectationRepository
import fr.gouv.monprojetsup.formation.entity.Communes.GRENOBLE
import fr.gouv.monprojetsup.formation.entity.Communes.LYON
import fr.gouv.monprojetsup.formation.entity.Communes.MARSEILLE
import fr.gouv.monprojetsup.formation.entity.Communes.MONTREUIL
import fr.gouv.monprojetsup.formation.entity.Communes.PARIS15EME
import fr.gouv.monprojetsup.formation.entity.Communes.PARIS19EME
import fr.gouv.monprojetsup.formation.entity.Communes.PARIS5EME
import fr.gouv.monprojetsup.formation.entity.Communes.RENNES
import fr.gouv.monprojetsup.formation.entity.Communes.SAINT_MALO
import fr.gouv.monprojetsup.formation.entity.Communes.STRASBOURG
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.BDDMockito.then
import org.mockito.BDDMockito.times
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.slf4j.Logger

class RecupererTripletAffectationDUneFormationServiceTest {
    @Mock
    lateinit var tripletAffectationRepository: TripletAffectationRepository

    @Mock
    lateinit var logger: Logger

    @InjectMocks
    lateinit var recupererTripletAffectationDUneFormationService: RecupererTripletAffectationDUneFormationService

    @Mock
    private lateinit var profilEleve: ProfilEleve.Identifie

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        given(profilEleve.id).willReturn("idEleve")
    }

    @Nested
    inner class RecupererNomCommunesTriesParAffinitesPourFormations {
        private val idsFormation =
            listOf(
                "fl680002",
                "fr22",
                "fl2009",
                "fl2016",
                "fl252",
            )

        private val tripletsAffectation =
            mapOf(
                "fl680002" to
                    listOf(
                        TripletAffectation(id = "ta6", nom = "Nom du ta6", commune = MARSEILLE),
                        TripletAffectation(id = "ta1", nom = "Nom du ta1", commune = PARIS15EME),
                        TripletAffectation(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO),
                    ),
                "fr22" to
                    listOf(
                        TripletAffectation(id = "ta2", nom = "Nom du ta2", commune = LYON),
                    ),
                "fl2009" to
                    listOf(
                        TripletAffectation(id = "ta10", nom = "Nom du ta10", commune = LYON),
                        TripletAffectation(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                        TripletAffectation(id = "ta7", nom = "Nom du ta7", commune = STRASBOURG),
                        TripletAffectation(id = "ta11", nom = "Nom du ta11", commune = LYON),
                    ),
                "fl2016" to
                    listOf(
                        TripletAffectation(id = "ta4", nom = "Nom du ta4", commune = MARSEILLE),
                    ),
                "fl252" to
                    listOf(
                        TripletAffectation(id = "ta8", nom = "Nom du ta8", commune = PARIS19EME),
                        TripletAffectation(id = "ta5", nom = "Nom du ta5", commune = RENNES),
                        TripletAffectation(id = "ta9", nom = "Nom du ta9", commune = MONTREUIL),
                    ),
            )

        @BeforeEach
        fun setUp() {
            given(tripletAffectationRepository.recupererLesTripletsAffectationDeFormations(idsFormation)).willReturn(tripletsAffectation)
            given(profilEleve.id).willReturn("idEleve")
        }

        @Test
        fun `les communes doivent être ordonnées par affinités en mettant en premiers les villes exactes, puis les départements`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(listOf(PARIS15EME, RENNES))

            // When
            val result =
                recupererTripletAffectationDUneFormationService.recupererTripletAffectationTriesParAffinites(
                    idsFormation,
                    profilEleve,
                )

            // Then
            assertThat(result).isEqualTo(
                mapOf(
                    "fl680002" to
                        listOf(
                            TripletAffectation(id = "ta1", nom = "Nom du ta1", commune = PARIS15EME),
                            TripletAffectation(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO),
                            TripletAffectation(id = "ta6", nom = "Nom du ta6", commune = MARSEILLE),
                        ),
                    "fr22" to
                        listOf(
                            TripletAffectation(id = "ta2", nom = "Nom du ta2", commune = LYON),
                        ),
                    "fl2009" to
                        listOf(
                            TripletAffectation(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                            TripletAffectation(id = "ta10", nom = "Nom du ta10", commune = LYON),
                            TripletAffectation(id = "ta7", nom = "Nom du ta7", commune = STRASBOURG),
                            TripletAffectation(id = "ta11", nom = "Nom du ta11", commune = LYON),
                        ),
                    "fl2016" to
                        listOf(
                            TripletAffectation(id = "ta4", nom = "Nom du ta4", commune = MARSEILLE),
                        ),
                    "fl252" to
                        listOf(
                            TripletAffectation(id = "ta5", nom = "Nom du ta5", commune = RENNES),
                            TripletAffectation(id = "ta8", nom = "Nom du ta8", commune = PARIS19EME),
                            TripletAffectation(id = "ta9", nom = "Nom du ta9", commune = MONTREUIL),
                        ),
                ),
            )
        }

        @Test
        fun `si la liste des communes favorites est vide, doit retourner la liste telle quelle`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(emptyList())
            val idsFormation =
                listOf(
                    "fl680002",
                    "fr22",
                    "fl2009",
                    "fl2016",
                    "fl252",
                )

            // When
            val result =
                recupererTripletAffectationDUneFormationService.recupererTripletAffectationTriesParAffinites(
                    idsFormation,
                    profilEleve,
                )

            // Then
            assertThat(result).isEqualTo(tripletsAffectation)
        }

        @Test
        fun `si la liste des communes favorites est nulle, doit retourner la liste telle quelle`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(null)

            // When
            val result =
                recupererTripletAffectationDUneFormationService.recupererTripletAffectationTriesParAffinites(
                    idsFormation,
                    profilEleve,
                )

            // Then
            assertThat(result).isEqualTo(tripletsAffectation)
        }

        @Test
        fun `si une des communes favorites a un code commune non valable, doit logguer un warning`() {
            // Given
            val communeInconnue = Commune(codeInsee = "Paris", nom = "Paris", latitude = 0.0, longitude = 0.0)
            given(profilEleve.communesFavorites).willReturn(listOf(communeInconnue, RENNES))

            // When
            recupererTripletAffectationDUneFormationService.recupererTripletAffectationTriesParAffinites(idsFormation, profilEleve)

            // Then
            then(logger).should(times(1))
                .warn("La commune Paris présente dans le profil de l'élève idEleve a un code commune non standard : Paris")
        }

        @Test
        fun `si une des communes d'un triplet d'affectation a un code commune non valable, doit logguer un warning`() {
            // Given
            val communeInconnue = Commune(codeInsee = "1", nom = "Paris", latitude = 0.0, longitude = 0.0)
            given(tripletAffectationRepository.recupererLesTripletsAffectationDeFormations(idsFormation)).willReturn(
                mapOf(
                    "fl0001" to
                        listOf(
                            TripletAffectation(id = "ta6", nom = "Nom du ta6", commune = MARSEILLE),
                            TripletAffectation(id = "ta1", nom = "Nom du ta1", commune = communeInconnue),
                            TripletAffectation(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO),
                        ),
                ),
            )
            given(profilEleve.communesFavorites).willReturn(listOf(PARIS15EME, RENNES))

            // When
            recupererTripletAffectationDUneFormationService.recupererTripletAffectationTriesParAffinites(idsFormation, profilEleve)

            // Then
            then(logger).should(times(1)).warn("La commune du triplet d'affectation ta1 a un code commune non standard : 1")
        }
    }

    @Nested
    inner class RecupererNomCommunesTriesParAffinitesPourFormation {
        private val tripletsAffectation =
            listOf(
                TripletAffectation(id = "ta17", nom = "Nom du ta17", commune = STRASBOURG),
                TripletAffectation(id = "ta10", nom = "Nom du ta10", commune = LYON),
                TripletAffectation(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                TripletAffectation(id = "ta32", nom = "Nom du ta32", commune = PARIS15EME),
                TripletAffectation(id = "ta11", nom = "Nom du ta11", commune = LYON),
                TripletAffectation(id = "ta7", nom = "Nom du ta7", commune = MARSEILLE),
            )

        @Test
        fun `les communes doivent être ordonnées par affinités en mettant en premiers les villes exactes, puis les départements`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(listOf(LYON, GRENOBLE, PARIS5EME))
            given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation = "fl2016")).willReturn(
                tripletsAffectation,
            )

            // When
            val result =
                recupererTripletAffectationDUneFormationService.recupererTripletAffectationTriesParAffinites(
                    idFormation = "fl2016",
                    profilEleve = profilEleve,
                )

            // Then
            assertThat(result).isEqualTo(
                listOf(
                    TripletAffectation(id = "ta10", nom = "Nom du ta10", commune = LYON),
                    TripletAffectation(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                    TripletAffectation(id = "ta11", nom = "Nom du ta11", commune = LYON),
                    TripletAffectation(id = "ta32", nom = "Nom du ta32", commune = PARIS15EME),
                    TripletAffectation(id = "ta17", nom = "Nom du ta17", commune = STRASBOURG),
                    TripletAffectation(id = "ta7", nom = "Nom du ta7", commune = MARSEILLE),
                ),
            )
        }

        @Test
        fun `si la liste des communes favorites est vide, doit retourner la liste telle quelle`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(emptyList())
            given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation = "fl2016")).willReturn(
                tripletsAffectation,
            )

            // When
            val result =
                recupererTripletAffectationDUneFormationService.recupererTripletAffectationTriesParAffinites(
                    idFormation = "fl2016",
                    profilEleve = profilEleve,
                )

            // Then
            assertThat(result).isEqualTo(tripletsAffectation)
        }

        @Test
        fun `si la liste des communes favorites est nulle, doit retourner la liste telle quelle`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(null)
            given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation = "fl2016")).willReturn(
                tripletsAffectation,
            )

            // When
            val result =
                recupererTripletAffectationDUneFormationService.recupererTripletAffectationTriesParAffinites(
                    idFormation = "fl2016",
                    profilEleve = profilEleve,
                )

            // Then
            assertThat(result).isEqualTo(tripletsAffectation)
        }
    }

    @Nested
    inner class RecupererNomCommunes {
        @Test
        fun `les communes doivent être retournées telles quelles`() {
            // Given
            val ta1 = mock(TripletAffectation::class.java)
            val ta2 = mock(TripletAffectation::class.java)
            val ta3 = mock(TripletAffectation::class.java)
            given(tripletAffectationRepository.recupererLesTripletsAffectationDUneFormation(idFormation = "fl2016")).willReturn(
                listOf(ta2, ta1, ta3),
            )

            // When
            val result =
                recupererTripletAffectationDUneFormationService.recupererTripletsAffectations(
                    idFormation = "fl2016",
                )

            // Then
            assertThat(result).isEqualTo(listOf(ta2, ta1, ta3))
        }
    }
}
