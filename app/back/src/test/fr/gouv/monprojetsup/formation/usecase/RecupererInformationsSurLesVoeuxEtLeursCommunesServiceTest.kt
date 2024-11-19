package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecIdsVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecIdsVoeuxAuxAlentours.VoeuAvecDistance
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.InformationsSurLesVoeuxEtLeursCommunes
import fr.gouv.monprojetsup.formation.domain.entity.Voeu
import fr.gouv.monprojetsup.formation.domain.port.CommunesAvecVoeuxAuxAlentoursRepository
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
import fr.gouv.monprojetsup.formation.entity.Communes.AJACCIO
import fr.gouv.monprojetsup.formation.entity.Communes.BASTIA
import fr.gouv.monprojetsup.formation.entity.Communes.FORT_DE_FRANCE
import fr.gouv.monprojetsup.formation.entity.Communes.GRENOBLE
import fr.gouv.monprojetsup.formation.entity.Communes.LYON
import fr.gouv.monprojetsup.formation.entity.Communes.MARSEILLE
import fr.gouv.monprojetsup.formation.entity.Communes.MONTREUIL
import fr.gouv.monprojetsup.formation.entity.Communes.PARIS15EME
import fr.gouv.monprojetsup.formation.entity.Communes.PARIS19EME
import fr.gouv.monprojetsup.formation.entity.Communes.PARIS5EME
import fr.gouv.monprojetsup.formation.entity.Communes.RENNES
import fr.gouv.monprojetsup.formation.entity.Communes.SAINT_MALO
import fr.gouv.monprojetsup.formation.entity.Communes.SAINT_PIERRE
import fr.gouv.monprojetsup.formation.entity.Communes.STRASBOURG
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.mock
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RecupererInformationsSurLesVoeuxEtLeursCommunesServiceTest {
    @Mock
    lateinit var voeuRepository: VoeuRepository

    @Mock
    private lateinit var communesAvecVoeuxAuxAlentoursRepository: CommunesAvecVoeuxAuxAlentoursRepository

    @InjectMocks
    lateinit var recupererInformationsSurLesVoeuxEtLeursCommunesService: RecupererInformationsSurLesVoeuxEtLeursCommunesService

    @Mock
    private lateinit var profilEleve: ProfilEleve.AvecProfilExistant

    private val id = "f859056f-1f3b-49d4-96c8-2a93b925fbaa"

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        given(profilEleve.id).willReturn(id)
    }

    @Nested
    inner class RecupererVoeuxPourUneFormation {
        @Test
        fun `les voeux doivent être retournés tels quels`() {
            // Given
            val ta1 = mock(Voeu::class.java)
            val ta2 = mock(Voeu::class.java)
            val ta3 = mock(Voeu::class.java)
            given(voeuRepository.recupererLesVoeuxDUneFormation(idFormation = "fl2016", obsoletesInclus = true)).willReturn(
                listOf(ta2, ta1, ta3),
            )

            // When
            val result =
                recupererInformationsSurLesVoeuxEtLeursCommunesService.recupererVoeux(
                    idFormation = "fl2016",
                    obsoletesInclus = true,
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
            given(
                voeuRepository.recupererLesVoeuxDeFormations(idsFormations = listOf("fl2016", "fl2017", "fl2018"), true),
            ).willReturn(voeux)

            // When
            val result =
                recupererInformationsSurLesVoeuxEtLeursCommunesService.recupererVoeux(
                    idsFormations = listOf("fl2016", "fl2017", "fl2018"),
                    obsoletesInclus = true,
                )

            // Then
            assertThat(result).isEqualTo(voeux)
        }
    }

    @Nested
    inner class RecupererInformationsSurLesVoeuxEtLeursCommunesPourUneFormation {
        private val voeux =
            listOf(
                Voeu(id = "ta6", nom = "Nom du ta6", commune = FORT_DE_FRANCE),
                Voeu(id = "ta17", nom = "Nom du ta17", commune = RENNES),
                Voeu(id = "ta10", nom = "Nom du ta10", commune = LYON),
                Voeu(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                Voeu(id = "ta32", nom = "Nom du ta32", commune = PARIS15EME),
                Voeu(id = "ta11", nom = "Nom du ta11", commune = LYON),
                Voeu(id = "ta7", nom = "Nom du ta7", commune = PARIS19EME),
                Voeu(id = "ta80", nom = "Nom du ta80", commune = BASTIA),
            )

        @Test
        fun `les communes doivent être ordonnées par affinités en plaçant en premier les communes favorites puis par distance`() {
            // Given
            val communes = listOf(LYON, SAINT_MALO, PARIS5EME, GRENOBLE)
            given(profilEleve.communesFavorites).willReturn(communes)
            given(voeuRepository.recupererLesVoeuxDUneFormation(idFormation = "fl2016", obsoletesInclus = true)).willReturn(voeux)
            val communesAvecIdsVoeuxAuxAlentours =
                listOf(
                    CommuneAvecIdsVoeuxAuxAlentours(
                        commune = LYON,
                        distances =
                            listOf(
                                VoeuAvecDistance(idVoeu = "ta11", km = 0),
                                VoeuAvecDistance(idVoeu = "ta10", km = 0),
                            ),
                    ),
                    CommuneAvecIdsVoeuxAuxAlentours(
                        commune = SAINT_MALO,
                        distances = listOf(VoeuAvecDistance(idVoeu = "ta17", km = 77)),
                    ),
                    CommuneAvecIdsVoeuxAuxAlentours(
                        commune = PARIS5EME,
                        distances =
                            listOf(
                                VoeuAvecDistance(idVoeu = "ta3", km = 0),
                                VoeuAvecDistance(idVoeu = "ta32", km = 2),
                                VoeuAvecDistance(idVoeu = "ta7", km = 4),
                            ),
                    ),
                    CommuneAvecIdsVoeuxAuxAlentours(commune = GRENOBLE, distances = emptyList()),
                )
            given(communesAvecVoeuxAuxAlentoursRepository.recupererVoeuxAutoursDeCommmune(communes)).willReturn(
                communesAvecIdsVoeuxAuxAlentours,
            )

            // When
            val result =
                recupererInformationsSurLesVoeuxEtLeursCommunesService.recupererInformationsSurLesVoeuxEtLeursCommunes(
                    idFormation = "fl2016",
                    profilEleve = profilEleve,
                    obsoletesInclus = true,
                )

            // Then
            val voeuxTries =
                listOf(
                    Voeu(id = "ta11", nom = "Nom du ta11", commune = LYON),
                    Voeu(id = "ta10", nom = "Nom du ta10", commune = LYON),
                    Voeu(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                    Voeu(id = "ta32", nom = "Nom du ta32", commune = PARIS15EME),
                    Voeu(id = "ta7", nom = "Nom du ta7", commune = PARIS19EME),
                    Voeu(id = "ta17", nom = "Nom du ta17", commune = RENNES),
                    Voeu(id = "ta6", nom = "Nom du ta6", commune = FORT_DE_FRANCE),
                    Voeu(id = "ta80", nom = "Nom du ta80", commune = BASTIA),
                )
            val voeuxParCommunesFavorites =
                listOf(
                    CommuneAvecVoeuxAuxAlentours(
                        commune = LYON,
                        distances =
                            listOf(
                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                    voeu = Voeu(id = "ta11", nom = "Nom du ta11", commune = LYON),
                                    km = 0,
                                ),
                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                    voeu = Voeu(id = "ta10", nom = "Nom du ta10", commune = LYON),
                                    km = 0,
                                ),
                            ),
                    ),
                    CommuneAvecVoeuxAuxAlentours(
                        commune = SAINT_MALO,
                        distances =
                            listOf(
                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                    voeu = Voeu(id = "ta17", nom = "Nom du ta17", commune = RENNES),
                                    km = 77,
                                ),
                            ),
                    ),
                    CommuneAvecVoeuxAuxAlentours(
                        commune = PARIS5EME,
                        distances =
                            listOf(
                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                    voeu = Voeu(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                                    km = 0,
                                ),
                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                    voeu = Voeu(id = "ta32", nom = "Nom du ta32", commune = PARIS15EME),
                                    km = 2,
                                ),
                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                    voeu = Voeu(id = "ta7", nom = "Nom du ta7", commune = PARIS19EME),
                                    km = 4,
                                ),
                            ),
                    ),
                    CommuneAvecVoeuxAuxAlentours(commune = GRENOBLE, distances = emptyList()),
                )
            val attendu =
                InformationsSurLesVoeuxEtLeursCommunes(
                    voeux = voeuxTries,
                    communesTriees = listOf(LYON, PARIS5EME, PARIS15EME, PARIS19EME, RENNES, FORT_DE_FRANCE, BASTIA),
                    voeuxParCommunesFavorites = voeuxParCommunesFavorites,
                )
            assertThat(result).isEqualTo(attendu)
        }

        @Test
        fun `si la liste des communes favorites est vide, doit retourner la liste telle quelle`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(emptyList())
            given(voeuRepository.recupererLesVoeuxDUneFormation(idFormation = "fl2016", obsoletesInclus = true)).willReturn(voeux)

            // When
            val result =
                recupererInformationsSurLesVoeuxEtLeursCommunesService.recupererInformationsSurLesVoeuxEtLeursCommunes(
                    idFormation = "fl2016",
                    profilEleve = profilEleve,
                    obsoletesInclus = true,
                )

            // Then
            then(communesAvecVoeuxAuxAlentoursRepository).shouldHaveNoInteractions()
            val attendu =
                InformationsSurLesVoeuxEtLeursCommunes(
                    voeux = voeux,
                    communesTriees = listOf(FORT_DE_FRANCE, RENNES, LYON, PARIS5EME, PARIS15EME, PARIS19EME, BASTIA),
                    voeuxParCommunesFavorites = emptyList(),
                )
            assertThat(result).isEqualTo(attendu)
        }

        @Test
        fun `si la liste des communes favorites est nulle, doit retourner la liste telle quelle`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(null)
            given(voeuRepository.recupererLesVoeuxDUneFormation(idFormation = "fl2016", obsoletesInclus = false)).willReturn(voeux)

            // When
            val result =
                recupererInformationsSurLesVoeuxEtLeursCommunesService.recupererInformationsSurLesVoeuxEtLeursCommunes(
                    idFormation = "fl2016",
                    profilEleve = profilEleve,
                    obsoletesInclus = false,
                )

            // Then
            then(communesAvecVoeuxAuxAlentoursRepository).shouldHaveNoInteractions()
            val attendu =
                InformationsSurLesVoeuxEtLeursCommunes(
                    voeux = voeux,
                    communesTriees = listOf(FORT_DE_FRANCE, RENNES, LYON, PARIS5EME, PARIS15EME, PARIS19EME, BASTIA),
                    voeuxParCommunesFavorites = emptyList(),
                )
            assertThat(result).isEqualTo(attendu)
        }
    }

    @Nested
    inner class RecupererInformationsSurLesVoeuxEtLeursCommunesPourDesFormations {
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
                        Voeu(id = "ta30", nom = "Nom du ta30", commune = AJACCIO),
                        Voeu(id = "ta7", nom = "Nom du ta7", commune = STRASBOURG),
                        Voeu(id = "ta11", nom = "Nom du ta11", commune = LYON),
                    ),
                "fl2016" to
                    listOf(
                        Voeu(id = "ta4", nom = "Nom du ta4", commune = MARSEILLE),
                        Voeu(id = "ta14", nom = "Nom du ta14", commune = FORT_DE_FRANCE),
                        Voeu(id = "ta15", nom = "Nom du ta15", commune = SAINT_PIERRE),
                    ),
                "fl252" to
                    listOf(
                        Voeu(id = "ta8", nom = "Nom du ta8", commune = PARIS19EME),
                        Voeu(id = "ta5", nom = "Nom du ta5", commune = RENNES),
                        Voeu(id = "ta50", nom = "Nom du ta50", commune = BASTIA),
                        Voeu(id = "ta9", nom = "Nom du ta9", commune = MONTREUIL),
                    ),
            )

        @BeforeEach
        fun setUp() {
            given(voeuRepository.recupererLesVoeuxDeFormations(idsFormation, true)).willReturn(voeux)
            given(profilEleve.id).willReturn(id)
        }

        @Test
        fun `les communes doivent être ordonnées par affinités en plaçant en premier les communes favorites puis par distance`() {
            // Given
            val communesFavorites = listOf(PARIS15EME, GRENOBLE, RENNES, FORT_DE_FRANCE, AJACCIO)
            given(profilEleve.communesFavorites).willReturn(communesFavorites)
            val communesAvecIdsVoeuxAuxAlentours =
                listOf(
                    CommuneAvecIdsVoeuxAuxAlentours(
                        commune = PARIS15EME,
                        distances =
                            listOf(
                                VoeuAvecDistance(idVoeu = "ta1", km = 0),
                                VoeuAvecDistance(idVoeu = "ta32", km = 0),
                                VoeuAvecDistance(idVoeu = "ta3", km = 1),
                                VoeuAvecDistance(idVoeu = "ta8", km = 3),
                            ),
                    ),
                    CommuneAvecIdsVoeuxAuxAlentours(
                        commune = GRENOBLE,
                        distances = emptyList(),
                    ),
                    CommuneAvecIdsVoeuxAuxAlentours(
                        commune = RENNES,
                        distances =
                            listOf(
                                VoeuAvecDistance(idVoeu = "ta17", km = 77),
                                VoeuAvecDistance(idVoeu = "ta5", km = 0),
                            ),
                    ),
                    CommuneAvecIdsVoeuxAuxAlentours(
                        commune = FORT_DE_FRANCE,
                        distances =
                            listOf(
                                VoeuAvecDistance(idVoeu = "ta14", km = 0),
                            ),
                    ),
                    CommuneAvecIdsVoeuxAuxAlentours(
                        commune = AJACCIO,
                        distances =
                            listOf(
                                VoeuAvecDistance(idVoeu = "ta30", km = 0),
                                VoeuAvecDistance(idVoeu = "ta50", km = 150),
                            ),
                    ),
                )
            given(
                communesAvecVoeuxAuxAlentoursRepository.recupererVoeuxAutoursDeCommmune(communesFavorites),
            ).willReturn(communesAvecIdsVoeuxAuxAlentours)

            // When
            val result =
                recupererInformationsSurLesVoeuxEtLeursCommunesService.recupererInformationsSurLesVoeuxEtLeursCommunes(
                    idsFormations = idsFormation,
                    profilEleve = profilEleve,
                    obsoletesInclus = true,
                )

            // Then
            val attendu =
                mapOf(
                    "fl680002" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta1", nom = "Nom du ta1", commune = PARIS15EME),
                                    Voeu(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO),
                                    Voeu(id = "ta6", nom = "Nom du ta6", commune = MARSEILLE),
                                ),
                            communesTriees = listOf(PARIS15EME, SAINT_MALO, MARSEILLE),
                            voeuxParCommunesFavorites =
                                listOf(
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = PARIS15EME,
                                        distances =
                                            listOf(
                                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                                    voeu = Voeu(id = "ta1", nom = "Nom du ta1", commune = PARIS15EME),
                                                    km = 0,
                                                ),
                                            ),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = GRENOBLE,
                                        distances = emptyList(),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = RENNES,
                                        distances =
                                            listOf(
                                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                                    voeu = Voeu(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO),
                                                    km = 77,
                                                ),
                                            ),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = FORT_DE_FRANCE,
                                        distances = emptyList(),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = AJACCIO,
                                        distances = emptyList(),
                                    ),
                                ),
                        ),
                    "fr22" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta2", nom = "Nom du ta2", commune = LYON),
                                ),
                            communesTriees = listOf(LYON),
                            voeuxParCommunesFavorites =
                                listOf(
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = PARIS15EME,
                                        distances = emptyList(),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = GRENOBLE,
                                        distances = emptyList(),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = RENNES,
                                        distances = emptyList(),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = FORT_DE_FRANCE,
                                        distances = emptyList(),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = AJACCIO,
                                        distances = emptyList(),
                                    ),
                                ),
                        ),
                    "fl2009" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta30", nom = "Nom du ta30", commune = AJACCIO),
                                    Voeu(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                                    Voeu(id = "ta10", nom = "Nom du ta10", commune = LYON),
                                    Voeu(id = "ta7", nom = "Nom du ta7", commune = STRASBOURG),
                                    Voeu(id = "ta11", nom = "Nom du ta11", commune = LYON),
                                ),
                            communesTriees = listOf(AJACCIO, PARIS5EME, LYON, STRASBOURG),
                            voeuxParCommunesFavorites =
                                listOf(
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = PARIS15EME,
                                        distances =
                                            listOf(
                                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                                    voeu = Voeu(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                                                    km = 1,
                                                ),
                                            ),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = GRENOBLE,
                                        distances = emptyList(),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = RENNES,
                                        distances = emptyList(),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = FORT_DE_FRANCE,
                                        distances = emptyList(),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = AJACCIO,
                                        distances =
                                            listOf(
                                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                                    voeu = Voeu(id = "ta30", nom = "Nom du ta30", commune = AJACCIO),
                                                    km = 0,
                                                ),
                                            ),
                                    ),
                                ),
                        ),
                    "fl2016" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta14", nom = "Nom du ta14", commune = FORT_DE_FRANCE),
                                    Voeu(id = "ta4", nom = "Nom du ta4", commune = MARSEILLE),
                                    Voeu(id = "ta15", nom = "Nom du ta15", commune = SAINT_PIERRE),
                                ),
                            communesTriees = listOf(FORT_DE_FRANCE, MARSEILLE, SAINT_PIERRE),
                            voeuxParCommunesFavorites =
                                listOf(
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = PARIS15EME,
                                        distances = emptyList(),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = GRENOBLE,
                                        distances = emptyList(),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = RENNES,
                                        distances = emptyList(),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = FORT_DE_FRANCE,
                                        distances =
                                            listOf(
                                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                                    voeu = Voeu(id = "ta14", nom = "Nom du ta14", commune = FORT_DE_FRANCE),
                                                    km = 0,
                                                ),
                                            ),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = AJACCIO,
                                        distances = emptyList(),
                                    ),
                                ),
                        ),
                    "fl252" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta5", nom = "Nom du ta5", commune = RENNES),
                                    Voeu(id = "ta8", nom = "Nom du ta8", commune = PARIS19EME),
                                    Voeu(id = "ta50", nom = "Nom du ta50", commune = BASTIA),
                                    Voeu(id = "ta9", nom = "Nom du ta9", commune = MONTREUIL),
                                ),
                            communesTriees = listOf(RENNES, PARIS19EME, BASTIA, MONTREUIL),
                            voeuxParCommunesFavorites =
                                listOf(
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = PARIS15EME,
                                        distances =
                                            listOf(
                                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                                    voeu = Voeu(id = "ta8", nom = "Nom du ta8", commune = PARIS19EME),
                                                    km = 3,
                                                ),
                                            ),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = GRENOBLE,
                                        distances = emptyList(),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = RENNES,
                                        distances =
                                            listOf(
                                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                                    voeu = Voeu(id = "ta5", nom = "Nom du ta5", commune = RENNES),
                                                    km = 0,
                                                ),
                                            ),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = FORT_DE_FRANCE,
                                        distances = emptyList(),
                                    ),
                                    CommuneAvecVoeuxAuxAlentours(
                                        commune = AJACCIO,
                                        distances =
                                            listOf(
                                                CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance(
                                                    voeu = Voeu(id = "ta50", nom = "Nom du ta50", commune = BASTIA),
                                                    km = 150,
                                                ),
                                            ),
                                    ),
                                ),
                        ),
                )
            assertThat(result).isEqualTo(attendu)
        }

        @Test
        fun `si la liste des communes favorites est vide, doit retourner la liste telle quelle`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(emptyList())

            // When
            val result =
                recupererInformationsSurLesVoeuxEtLeursCommunesService.recupererInformationsSurLesVoeuxEtLeursCommunes(
                    idsFormations = idsFormation,
                    profilEleve = profilEleve,
                    obsoletesInclus = true,
                )

            // Then
            val attendu =
                mapOf(
                    "fl680002" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta6", nom = "Nom du ta6", commune = MARSEILLE),
                                    Voeu(id = "ta1", nom = "Nom du ta1", commune = PARIS15EME),
                                    Voeu(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO),
                                ),
                            communesTriees = listOf(MARSEILLE, PARIS15EME, SAINT_MALO),
                            voeuxParCommunesFavorites = emptyList(),
                        ),
                    "fr22" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta2", nom = "Nom du ta2", commune = LYON),
                                ),
                            communesTriees = listOf(LYON),
                            voeuxParCommunesFavorites = emptyList(),
                        ),
                    "fl2009" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta10", nom = "Nom du ta10", commune = LYON),
                                    Voeu(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                                    Voeu(id = "ta30", nom = "Nom du ta30", commune = AJACCIO),
                                    Voeu(id = "ta7", nom = "Nom du ta7", commune = STRASBOURG),
                                    Voeu(id = "ta11", nom = "Nom du ta11", commune = LYON),
                                ),
                            communesTriees = listOf(LYON, PARIS5EME, AJACCIO, STRASBOURG),
                            voeuxParCommunesFavorites = emptyList(),
                        ),
                    "fl2016" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta4", nom = "Nom du ta4", commune = MARSEILLE),
                                    Voeu(id = "ta14", nom = "Nom du ta14", commune = FORT_DE_FRANCE),
                                    Voeu(id = "ta15", nom = "Nom du ta15", commune = SAINT_PIERRE),
                                ),
                            communesTriees = listOf(MARSEILLE, FORT_DE_FRANCE, SAINT_PIERRE),
                            voeuxParCommunesFavorites = emptyList(),
                        ),
                    "fl252" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta8", nom = "Nom du ta8", commune = PARIS19EME),
                                    Voeu(id = "ta5", nom = "Nom du ta5", commune = RENNES),
                                    Voeu(id = "ta50", nom = "Nom du ta50", commune = BASTIA),
                                    Voeu(id = "ta9", nom = "Nom du ta9", commune = MONTREUIL),
                                ),
                            communesTriees = listOf(PARIS19EME, RENNES, BASTIA, MONTREUIL),
                            voeuxParCommunesFavorites = emptyList(),
                        ),
                )
            assertThat(result).isEqualTo(attendu)
        }

        @Test
        fun `si la liste des communes favorites est nulle, doit retourner la liste telle quelle`() {
            // Given
            given(profilEleve.communesFavorites).willReturn(null)

            // When
            val result =
                recupererInformationsSurLesVoeuxEtLeursCommunesService.recupererInformationsSurLesVoeuxEtLeursCommunes(
                    idsFormations = idsFormation,
                    profilEleve = profilEleve,
                    obsoletesInclus = true,
                )

            // Then
            val attendu =
                mapOf(
                    "fl680002" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta6", nom = "Nom du ta6", commune = MARSEILLE),
                                    Voeu(id = "ta1", nom = "Nom du ta1", commune = PARIS15EME),
                                    Voeu(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO),
                                ),
                            communesTriees = listOf(MARSEILLE, PARIS15EME, SAINT_MALO),
                            voeuxParCommunesFavorites = emptyList(),
                        ),
                    "fr22" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta2", nom = "Nom du ta2", commune = LYON),
                                ),
                            communesTriees = listOf(LYON),
                            voeuxParCommunesFavorites = emptyList(),
                        ),
                    "fl2009" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta10", nom = "Nom du ta10", commune = LYON),
                                    Voeu(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                                    Voeu(id = "ta30", nom = "Nom du ta30", commune = AJACCIO),
                                    Voeu(id = "ta7", nom = "Nom du ta7", commune = STRASBOURG),
                                    Voeu(id = "ta11", nom = "Nom du ta11", commune = LYON),
                                ),
                            communesTriees = listOf(LYON, PARIS5EME, AJACCIO, STRASBOURG),
                            voeuxParCommunesFavorites = emptyList(),
                        ),
                    "fl2016" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta4", nom = "Nom du ta4", commune = MARSEILLE),
                                    Voeu(id = "ta14", nom = "Nom du ta14", commune = FORT_DE_FRANCE),
                                    Voeu(id = "ta15", nom = "Nom du ta15", commune = SAINT_PIERRE),
                                ),
                            communesTriees = listOf(MARSEILLE, FORT_DE_FRANCE, SAINT_PIERRE),
                            voeuxParCommunesFavorites = emptyList(),
                        ),
                    "fl252" to
                        InformationsSurLesVoeuxEtLeursCommunes(
                            voeux =
                                listOf(
                                    Voeu(id = "ta8", nom = "Nom du ta8", commune = PARIS19EME),
                                    Voeu(id = "ta5", nom = "Nom du ta5", commune = RENNES),
                                    Voeu(id = "ta50", nom = "Nom du ta50", commune = BASTIA),
                                    Voeu(id = "ta9", nom = "Nom du ta9", commune = MONTREUIL),
                                ),
                            communesTriees = listOf(PARIS19EME, RENNES, BASTIA, MONTREUIL),
                            voeuxParCommunesFavorites = emptyList(),
                        ),
                )
            assertThat(result).isEqualTo(attendu)
        }
    }
}
