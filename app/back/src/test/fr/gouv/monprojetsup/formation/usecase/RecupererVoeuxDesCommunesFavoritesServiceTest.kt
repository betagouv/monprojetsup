package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecIdsVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecIdsVoeuxAuxAlentours.VoeuAvecDistance
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.Voeu
import fr.gouv.monprojetsup.formation.domain.port.CommunesAvecVoeuxAuxAlentoursRepository
import fr.gouv.monprojetsup.formation.entity.Communes.CAEN
import fr.gouv.monprojetsup.formation.entity.Communes.MONTREUIL
import fr.gouv.monprojetsup.formation.entity.Communes.PARIS5EME
import fr.gouv.monprojetsup.formation.entity.Communes.RENNES
import fr.gouv.monprojetsup.formation.entity.Communes.SAINT_MALO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RecupererVoeuxDesCommunesFavoritesServiceTest {
    @Mock
    private lateinit var voeuxParVilleRepository: CommunesAvecVoeuxAuxAlentoursRepository

    @InjectMocks
    private lateinit var recupererVoeuxDesCommunesFavoritesService: RecupererVoeuxDesCommunesFavoritesService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Nested
    inner class RecupererVoeuxAutoursDeCommmunesDUneFormation {
        @Test
        fun `doit filtrer sur les voeux de la formation`() {
            // Given
            val communes = listOf(SAINT_MALO, MONTREUIL, CAEN, PARIS5EME)
            val voeuxDeLaFormation =
                listOf(
                    Voeu(id = "ta256", nom = "Nom du ta256", commune = RENNES),
                    Voeu(id = "ta33", nom = "Nom du ta33", commune = RENNES),
                    Voeu(id = "ta77", nom = "Nom du ta77", commune = RENNES),
                )

            val communeAvecIdsVoeuxAuxAlentours =
                listOf(
                    CommuneAvecIdsVoeuxAuxAlentours(
                        commune = SAINT_MALO,
                        distances =
                            listOf(
                                VoeuAvecDistance(idVoeu = "ta60", km = 66),
                                VoeuAvecDistance(idVoeu = "ta77", km = 66),
                                VoeuAvecDistance(idVoeu = "ta480", km = 66),
                            ),
                    ),
                    CommuneAvecIdsVoeuxAuxAlentours(commune = MONTREUIL, distances = emptyList()),
                    CommuneAvecIdsVoeuxAuxAlentours(
                        commune = CAEN,
                        distances =
                            listOf(
                                VoeuAvecDistance(idVoeu = "ta33", km = 52),
                                VoeuAvecDistance(idVoeu = "ta256", km = 52),
                            ),
                    ),
                    CommuneAvecIdsVoeuxAuxAlentours(commune = PARIS5EME, distances = emptyList()),
                )
            given(voeuxParVilleRepository.recupererVoeuxAutoursDeCommmune(communes)).willReturn(communeAvecIdsVoeuxAuxAlentours)

            // When
            val resultat =
                recupererVoeuxDesCommunesFavoritesService.recupererVoeuxAutoursDeCommmunes(
                    communes = communes,
                    voeuxDeLaFormation = voeuxDeLaFormation,
                )

            // Then
            val attendu =
                listOf(
                    CommuneAvecVoeuxAuxAlentours(
                        commune = SAINT_MALO,
                        distances =
                            listOf(
                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                    voeu = Voeu(id = "ta77", nom = "Nom du ta77", commune = RENNES),
                                    km = 66,
                                ),
                            ),
                    ),
                    CommuneAvecVoeuxAuxAlentours(MONTREUIL, distances = emptyList()),
                    CommuneAvecVoeuxAuxAlentours(
                        commune = CAEN,
                        distances =
                            listOf(
                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                    voeu = Voeu(id = "ta33", nom = "Nom du ta33", commune = RENNES),
                                    km = 52,
                                ),
                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                    voeu = Voeu(id = "ta256", nom = "Nom du ta256", commune = RENNES),
                                    km = 52,
                                ),
                            ),
                    ),
                    CommuneAvecVoeuxAuxAlentours(PARIS5EME, distances = emptyList()),
                )
            assertThat(resultat).isEqualTo(attendu)
        }
    }

    @Nested
    inner class RecupererVoeuxAutoursDeCommmunesDeFormations {
        @Test
        fun `doit filtrer sur les voeux des formations`() {
            // Given
            val communes = listOf(SAINT_MALO, MONTREUIL, CAEN, PARIS5EME)
            val voeuxDeLaFormation =
                mapOf(
                    "fl0001" to
                        listOf(
                            Voeu(id = "ta256", nom = "Nom du ta256", commune = RENNES),
                            Voeu(id = "ta33", nom = "Nom du ta33", commune = RENNES),
                            Voeu(id = "ta77", nom = "Nom du ta77", commune = RENNES),
                        ),
                    "fl0003" to emptyList(),
                    "fl0004" to
                        listOf(
                            Voeu(id = "ta480", nom = "Nom du ta480", commune = PARIS5EME),
                            Voeu(id = "ta256", nom = "Nom du ta256", commune = RENNES),
                        ),
                )

            val communeAvecIdsVoeuxAuxAlentours =
                listOf(
                    CommuneAvecIdsVoeuxAuxAlentours(
                        commune = SAINT_MALO,
                        distances =
                            listOf(
                                VoeuAvecDistance(idVoeu = "ta60", km = 66),
                                VoeuAvecDistance(idVoeu = "ta77", km = 66),
                                VoeuAvecDistance(idVoeu = "ta480", km = 66),
                            ),
                    ),
                    CommuneAvecIdsVoeuxAuxAlentours(commune = MONTREUIL, distances = emptyList()),
                    CommuneAvecIdsVoeuxAuxAlentours(
                        commune = CAEN,
                        distances =
                            listOf(
                                VoeuAvecDistance(idVoeu = "ta33", km = 52),
                                VoeuAvecDistance(idVoeu = "ta256", km = 52),
                                VoeuAvecDistance(idVoeu = "ta480", km = 12),
                            ),
                    ),
                    CommuneAvecIdsVoeuxAuxAlentours(commune = PARIS5EME, distances = emptyList()),
                )
            given(voeuxParVilleRepository.recupererVoeuxAutoursDeCommmune(communes)).willReturn(communeAvecIdsVoeuxAuxAlentours)

            // When
            val resultat =
                recupererVoeuxDesCommunesFavoritesService.recupererVoeuxAutoursDeCommmunes(
                    communes = communes,
                    voeuxDeLaFormation = voeuxDeLaFormation,
                )

            // Then
            val attendu =
                mapOf(
                    "fl0001" to
                        listOf(
                            CommuneAvecVoeuxAuxAlentours(
                                commune = SAINT_MALO,
                                distances =
                                    listOf(
                                        CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                            voeu = Voeu(id = "ta77", nom = "Nom du ta77", commune = RENNES),
                                            km = 66,
                                        ),
                                    ),
                            ),
                            CommuneAvecVoeuxAuxAlentours(MONTREUIL, distances = emptyList()),
                            CommuneAvecVoeuxAuxAlentours(
                                commune = CAEN,
                                distances =
                                    listOf(
                                        CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                            voeu = Voeu(id = "ta33", nom = "Nom du ta33", commune = RENNES),
                                            km = 52,
                                        ),
                                        CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                            voeu = Voeu(id = "ta256", nom = "Nom du ta256", commune = RENNES),
                                            km = 52,
                                        ),
                                    ),
                            ),
                            CommuneAvecVoeuxAuxAlentours(PARIS5EME, distances = emptyList()),
                        ),
                    "fl0003" to
                        listOf(
                            CommuneAvecVoeuxAuxAlentours(
                                commune = SAINT_MALO,
                                distances = emptyList(),
                            ),
                            CommuneAvecVoeuxAuxAlentours(
                                commune = MONTREUIL,
                                distances = emptyList(),
                            ),
                            CommuneAvecVoeuxAuxAlentours(
                                commune = CAEN,
                                distances = emptyList(),
                            ),
                            CommuneAvecVoeuxAuxAlentours(
                                commune = PARIS5EME,
                                distances = emptyList(),
                            ),
                        ),
                    "fl0004" to
                        listOf(
                            CommuneAvecVoeuxAuxAlentours(
                                commune = SAINT_MALO,
                                distances =
                                    listOf(
                                        CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                            voeu =
                                                Voeu(
                                                    id = "ta480",
                                                    nom = "Nom du ta480",
                                                    commune = PARIS5EME,
                                                ),
                                            km = 66,
                                        ),
                                    ),
                            ),
                            CommuneAvecVoeuxAuxAlentours(commune = MONTREUIL, distances = listOf()),
                            CommuneAvecVoeuxAuxAlentours(
                                commune = CAEN,
                                distances =
                                    listOf(
                                        CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                            voeu =
                                                Voeu(
                                                    id = "ta256",
                                                    nom = "Nom du ta256",
                                                    commune = RENNES,
                                                ),
                                            km = 52,
                                        ),
                                        CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                            voeu =
                                                Voeu(
                                                    id = "ta480",
                                                    nom = "Nom du ta480",
                                                    commune = PARIS5EME,
                                                ),
                                            km = 12,
                                        ),
                                    ),
                            ),
                            CommuneAvecVoeuxAuxAlentours(commune = PARIS5EME, distances = listOf()),
                        ),
                )
            assertThat(resultat).isEqualTo(attendu)
        }
    }
}
