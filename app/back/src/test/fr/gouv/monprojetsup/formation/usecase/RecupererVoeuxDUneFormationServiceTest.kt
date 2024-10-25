package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.Commune
import fr.gouv.monprojetsup.formation.domain.entity.Voeu
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
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
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
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

class RecupererVoeuxDUneFormationServiceTest {
    @Mock
    lateinit var voeuRepository: VoeuRepository

    @Mock
    lateinit var logger: MonProjetSupLogger

    @InjectMocks
    lateinit var recupererVoeuxDUneFormationService: RecupererVoeuxDUneFormationService

    @Mock
    private lateinit var profilEleve: ProfilEleve.AvecProfilExistant

    private val id = "f859056f-1f3b-49d4-96c8-2a93b925fbaa"

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        given(profilEleve.id).willReturn(id)
    }

    @Nested
    inner class RecupererVoeuxTriesParAffinitesPourDesFormations {
        private val idsFormation =
            listOf(
                "fl680002",
                "fr22",
                "fl2009",
                "fl2016",
                "fl252",
            )

        private val voeux =
            mapOf(
                "fl680002" to
                    listOf(
                        Voeu(id = "ta6", nom = "Nom du ta6", commune = MARSEILLE),
                        Voeu(id = "ta1", nom = "Nom du ta1", commune = PARIS15EME),
                        Voeu(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO),
                    ),
                "fr22" to
                    listOf(
                        Voeu(id = "ta2", nom = "Nom du ta2", commune = LYON),
                    ),
                "fl2009" to
                    listOf(
                        Voeu(id = "ta10", nom = "Nom du ta10", commune = LYON),
                        Voeu(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                        Voeu(id = "ta7", nom = "Nom du ta7", commune = STRASBOURG),
                        Voeu(id = "ta11", nom = "Nom du ta11", commune = LYON),
                    ),
                "fl2016" to
                    listOf(
                        Voeu(id = "ta4", nom = "Nom du ta4", commune = MARSEILLE),
                    ),
                "fl252" to
                    listOf(
                        Voeu(id = "ta8", nom = "Nom du ta8", commune = PARIS19EME),
                        Voeu(id = "ta5", nom = "Nom du ta5", commune = RENNES),
                        Voeu(id = "ta9", nom = "Nom du ta9", commune = MONTREUIL),
                    ),
            )

        @BeforeEach
        fun setUp() {
            given(voeuRepository.recupererLesVoeuxDeFormations(idsFormation)).willReturn(voeux)
            given(profilEleve.id).willReturn(id)
        }

        @Test
        fun `les communes doivent être ordonnées par affinités en mettant en premiers les villes exactes, puis les départements`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(listOf(PARIS15EME, RENNES))

            // When
            val result =
                recupererVoeuxDUneFormationService.recupererVoeuxTriesParAffinites(
                    idsFormation,
                    profilEleve,
                )

            // Then
            assertThat(result).isEqualTo(
                mapOf(
                    "fl680002" to
                        listOf(
                            Voeu(id = "ta1", nom = "Nom du ta1", commune = PARIS15EME),
                            Voeu(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO),
                            Voeu(id = "ta6", nom = "Nom du ta6", commune = MARSEILLE),
                        ),
                    "fr22" to
                        listOf(
                            Voeu(id = "ta2", nom = "Nom du ta2", commune = LYON),
                        ),
                    "fl2009" to
                        listOf(
                            Voeu(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                            Voeu(id = "ta10", nom = "Nom du ta10", commune = LYON),
                            Voeu(id = "ta7", nom = "Nom du ta7", commune = STRASBOURG),
                            Voeu(id = "ta11", nom = "Nom du ta11", commune = LYON),
                        ),
                    "fl2016" to
                        listOf(
                            Voeu(id = "ta4", nom = "Nom du ta4", commune = MARSEILLE),
                        ),
                    "fl252" to
                        listOf(
                            Voeu(id = "ta5", nom = "Nom du ta5", commune = RENNES),
                            Voeu(id = "ta8", nom = "Nom du ta8", commune = PARIS19EME),
                            Voeu(id = "ta9", nom = "Nom du ta9", commune = MONTREUIL),
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
                recupererVoeuxDUneFormationService.recupererVoeuxTriesParAffinites(
                    idsFormation,
                    profilEleve,
                )

            // Then
            assertThat(result).isEqualTo(voeux)
        }

        @Test
        fun `si la liste des communes favorites est nulle, doit retourner la liste telle quelle`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(null)

            // When
            val result =
                recupererVoeuxDUneFormationService.recupererVoeuxTriesParAffinites(
                    idsFormation,
                    profilEleve,
                )

            // Then
            assertThat(result).isEqualTo(voeux)
        }

        @Test
        fun `si une des communes favorites a un code commune non valable, doit logguer un warning`() {
            // Given
            val communeInconnue = Commune(codeInsee = "Paris", nom = "Paris", latitude = 0.0, longitude = 0.0)
            given(profilEleve.communesFavorites).willReturn(listOf(communeInconnue, RENNES))

            // When
            recupererVoeuxDUneFormationService.recupererVoeuxTriesParAffinites(idsFormation, profilEleve)

            // Then
            then(logger).should(times(1))
                .warn(
                    type = "ERREUR_TRI_VOEUX",
                    message =
                        "La commune Paris présente dans le profil de l'élève f859056f-1f3b-49d4-96c8-2a93b925fbaa " +
                            "a un code commune non standard : Paris",
                )
        }

        @Test
        fun `si une des communes d'un voeu a un code commune non valable, doit logguer un warning`() {
            // Given
            val communeInconnue = Commune(codeInsee = "1", nom = "Paris", latitude = 0.0, longitude = 0.0)
            given(voeuRepository.recupererLesVoeuxDeFormations(idsFormation)).willReturn(
                mapOf(
                    "fl0001" to
                        listOf(
                            Voeu(id = "ta6", nom = "Nom du ta6", commune = MARSEILLE),
                            Voeu(id = "ta1", nom = "Nom du ta1", commune = communeInconnue),
                            Voeu(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO),
                        ),
                ),
            )
            given(profilEleve.communesFavorites).willReturn(listOf(PARIS15EME, RENNES))

            // When
            recupererVoeuxDUneFormationService.recupererVoeuxTriesParAffinites(idsFormation, profilEleve)

            // Then
            then(logger).should(times(1))
                .warn(type = "ERREUR_TRI_VOEUX", message = "La commune du voeu ta1 a un code commune non standard : 1")
        }
    }

    @Nested
    inner class RecupererVoeuxTriesParAffinitesPourUneFormation {
        private val voeux =
            listOf(
                Voeu(id = "ta17", nom = "Nom du ta17", commune = STRASBOURG),
                Voeu(id = "ta10", nom = "Nom du ta10", commune = LYON),
                Voeu(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                Voeu(id = "ta32", nom = "Nom du ta32", commune = PARIS15EME),
                Voeu(id = "ta11", nom = "Nom du ta11", commune = LYON),
                Voeu(id = "ta7", nom = "Nom du ta7", commune = MARSEILLE),
            )

        @Test
        fun `les communes doivent être ordonnées par affinités en mettant en premiers les communes exactes, puis les départements`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(listOf(LYON, GRENOBLE, PARIS5EME))
            given(voeuRepository.recupererLesVoeuxDUneFormation(idFormation = "fl2016")).willReturn(
                voeux,
            )

            // When
            val result =
                recupererVoeuxDUneFormationService.recupererVoeuxTriesParAffinites(
                    idFormation = "fl2016",
                    profilEleve = profilEleve,
                )

            // Then
            assertThat(result).isEqualTo(
                listOf(
                    Voeu(id = "ta10", nom = "Nom du ta10", commune = LYON),
                    Voeu(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                    Voeu(id = "ta11", nom = "Nom du ta11", commune = LYON),
                    Voeu(id = "ta32", nom = "Nom du ta32", commune = PARIS15EME),
                    Voeu(id = "ta17", nom = "Nom du ta17", commune = STRASBOURG),
                    Voeu(id = "ta7", nom = "Nom du ta7", commune = MARSEILLE),
                ),
            )
        }

        @Test
        fun `si la liste des communes favorites est vide, doit retourner la liste telle quelle`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(emptyList())
            given(voeuRepository.recupererLesVoeuxDUneFormation(idFormation = "fl2016")).willReturn(
                voeux,
            )

            // When
            val result =
                recupererVoeuxDUneFormationService.recupererVoeuxTriesParAffinites(
                    idFormation = "fl2016",
                    profilEleve = profilEleve,
                )

            // Then
            assertThat(result).isEqualTo(voeux)
        }

        @Test
        fun `si la liste des communes favorites est nulle, doit retourner la liste telle quelle`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(null)
            given(voeuRepository.recupererLesVoeuxDUneFormation(idFormation = "fl2016")).willReturn(
                voeux,
            )

            // When
            val result =
                recupererVoeuxDUneFormationService.recupererVoeuxTriesParAffinites(
                    idFormation = "fl2016",
                    profilEleve = profilEleve,
                )

            // Then
            assertThat(result).isEqualTo(voeux)
        }
    }

    @Nested
    inner class RecupererVoeuxPourUneFormation {
        @Test
        fun `les voeux doivent être retournés tels quels`() {
            // Given
            val ta1 = mock(Voeu::class.java)
            val ta2 = mock(Voeu::class.java)
            val ta3 = mock(Voeu::class.java)
            given(voeuRepository.recupererLesVoeuxDUneFormation(idFormation = "fl2016")).willReturn(
                listOf(ta2, ta1, ta3),
            )

            // When
            val result =
                recupererVoeuxDUneFormationService.recupererVoeux(
                    idFormation = "fl2016",
                )

            // Then
            assertThat(result).isEqualTo(listOf(ta2, ta1, ta3))
        }
    }

    @Nested
    inner class RecupererVoeuxPourDesFormations {
        @Test
        fun `les voeux doivent être retournés tels quels`() {
            // Given
            val ta1 = mock(Voeu::class.java)
            val ta2 = mock(Voeu::class.java)
            val ta3 = mock(Voeu::class.java)
            val voeux =
                mapOf(
                    "fl2016" to listOf(ta2, ta1, ta3),
                    "fl2017" to emptyList(),
                    "fl2018" to listOf(ta1, ta2),
                )
            given(voeuRepository.recupererLesVoeuxDeFormations(idsFormations = listOf("fl2016", "fl2017", "fl2018"))).willReturn(voeux)

            // When
            val result = recupererVoeuxDUneFormationService.recupererVoeux(idsFormations = listOf("fl2016", "fl2017", "fl2018"))

            // Then
            assertThat(result).isEqualTo(voeux)
        }
    }
}
