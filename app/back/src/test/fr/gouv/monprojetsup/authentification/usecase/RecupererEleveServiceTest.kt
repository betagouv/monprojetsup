package fr.gouv.monprojetsup.authentification.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.FormationFavorite
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFavori
import fr.gouv.monprojetsup.eleve.domain.port.EleveRepository
import fr.gouv.monprojetsup.eleve.entity.CommunesFavorites
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatSpecialiteRepository
import fr.gouv.monprojetsup.referentiel.domain.port.DomaineRepository
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RecupererEleveServiceTest {
    @Mock
    private lateinit var baccalaureatSpecialiteRepository: BaccalaureatSpecialiteRepository

    @Mock
    private lateinit var voeuRepository: VoeuRepository

    @Mock
    private lateinit var domaineRepository: DomaineRepository

    @Mock
    private lateinit var interetRepository: InteretRepository

    @Mock
    private lateinit var metierRepository: MetierRepository

    @Mock
    private lateinit var formationRepository: FormationRepository

    @Mock
    private lateinit var eleveRepository: EleveRepository

    @InjectMocks
    private lateinit var recupererEleveService: RecupererEleveService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        given(
            baccalaureatSpecialiteRepository.recupererUnBaccalaureatEtLesIdsDeSesSpecialites("Général"),
        ).willReturn(Pair("Général", listOf("mat1050", "mat719")))
        given(
            formationRepository.recupererIdsFormationsInexistantes(listOf("fl0010", "fl0012", "fl1234", "fl5678")),
        ).willReturn(emptyList())
        given(voeuRepository.recupererIdsVoeuxInexistants(listOf("ta1", "ta2"))).willReturn(emptyList())
        given(interetRepository.recupererIdsCentresInteretsInexistants(listOf("ci12", "ci13", "ci15"))).willReturn(emptyList())
        given(domaineRepository.recupererIdsDomainesInexistants(listOf("dom4", "dom5", "dom7", "dom8"))).willReturn(emptyList())
        given(metierRepository.recupererIdsMetiersInexistants(listOf("MET.116", "MET.188"))).willReturn(emptyList())
    }

    private val profilEleve =
        ProfilEleve.AvecProfilExistant(
            id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15",
            situation = SituationAvanceeProjetSup.AUCUNE_IDEE,
            classe = ChoixNiveau.SECONDE,
            baccalaureat = "Général",
            specialites = listOf("mat1050", "mat719"),
            domainesInterets = listOf("dom4", "dom5", "dom7", "dom8"),
            centresInterets = listOf("ci12", "ci13", "ci15"),
            metiersFavoris = listOf("MET.116", "MET.188"),
            dureeEtudesPrevue = ChoixDureeEtudesPrevue.COURTE,
            alternance = ChoixAlternance.INDIFFERENT,
            communesFavorites = listOf(CommunesFavorites.PARIS15EME, CommunesFavorites.MARSEILLE),
            formationsFavorites =
                listOf(
                    FormationFavorite(
                        idFormation = "fl0010",
                        niveauAmbition = 1,
                        priseDeNote = null,
                    ),
                    FormationFavorite(
                        idFormation = "fl0012",
                        niveauAmbition = 3,
                        priseDeNote = "Ma formation préférée",
                    ),
                ),
            moyenneGenerale = 10.5f,
            corbeilleFormations = listOf("fl1234", "fl5678"),
            compteParcoursupLie = true,
            voeuxFavoris =
                listOf(
                    VoeuFavori("ta1", true),
                    VoeuFavori("ta77", false),
                ),
        )

    @Nested
    inner class FiltrerBaccalaureatEtSesSpecialites {
        @Test
        fun `si le baccalauréat est null mais pas les spécialités, doit les remettre à null et ne pas appeler le repo`() {
            // Given
            val profil = profilEleve.copy(baccalaureat = null)
            given(eleveRepository.recupererUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")).willReturn(profil)

            // When
            val resultat = recupererEleveService.recupererEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            val attendu =
                profilEleve.copy(
                    baccalaureat = null,
                    specialites = null,
                )
            assertThat(resultat).isEqualTo(attendu)
            then(baccalaureatSpecialiteRepository).shouldHaveNoInteractions()
        }

        @Test
        fun `si le baccalauréat n'existe pas, doit les remettre à null avec ses spécialités`() {
            // Given
            val profil = profilEleve.copy(baccalaureat = "baccalauréatInconnu")
            given(eleveRepository.recupererUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")).willReturn(profil)
            given(baccalaureatSpecialiteRepository.recupererUnBaccalaureatEtLesIdsDeSesSpecialites("baccalauréatInconnu")).willReturn(null)

            // When
            val resultat = recupererEleveService.recupererEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            val attendu =
                profilEleve.copy(
                    baccalaureat = null,
                    specialites = null,
                )
            assertThat(resultat).isEqualTo(attendu)
        }

        @Test
        fun `si le baccalauréat existe mais des spécialités ne sont pas reconnues, doit les filtrer`() {
            // Given
            val profil =
                profilEleve.copy(
                    baccalaureat = "P",
                    specialites = listOf("mat1046", "mat1047", "mat1048", "mat1049", "mat282", "mat1050", "mat1051", "mat1052", "mat1053"),
                )

            given(eleveRepository.recupererUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")).willReturn(profil)
            given(baccalaureatSpecialiteRepository.recupererUnBaccalaureatEtLesIdsDeSesSpecialites("P")).willReturn(
                Pair(
                    "P",
                    listOf("mat1047", "mat1051", "mat1060"),
                ),
            )

            // When
            val resultat = recupererEleveService.recupererEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            val attendu =
                profilEleve.copy(
                    baccalaureat = "P",
                    specialites = listOf("mat1047", "mat1051"),
                )
            assertThat(resultat).isEqualTo(attendu)
        }
    }

    @Nested
    inner class FiltrerFormationsEtVoeux {
        @Test
        fun `si formations favorites et voeux favoris nuls et la corbeille vide, doit ne pas modifier et ne pas appeler le repo`() {
            // Given
            val profil = profilEleve.copy(formationsFavorites = null, voeuxFavoris = emptyList(), corbeilleFormations = emptyList())
            given(eleveRepository.recupererUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")).willReturn(profil)

            // When
            val resultat = recupererEleveService.recupererEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            assertThat(resultat).isEqualTo(profil)
            then(formationRepository).shouldHaveNoInteractions()
            then(voeuRepository).shouldHaveNoInteractions()
        }

        @Test
        fun `si des formations dans favoris et corbeille n'existent pas, doit les filtrer`() {
            // Given
            val profil =
                profilEleve.copy(
                    formationsFavorites =
                        listOf(
                            FormationFavorite("fl0001", niveauAmbition = 0, priseDeNote = null),
                            FormationFavorite("fl0002", niveauAmbition = 0, priseDeNote = null),
                            FormationFavorite("flInconnue", niveauAmbition = 0, priseDeNote = null),
                        ),
                    corbeilleFormations = listOf("flInconnue2", "flInconnue", "test", "fl0003"),
                )
            given(eleveRepository.recupererUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")).willReturn(profil)
            given(
                formationRepository.recupererIdsFormationsInexistantes(
                    listOf("fl0001", "fl0002", "flInconnue", "flInconnue2", "test", "fl0003"),
                ),
            ).willReturn(
                listOf("flInconnue", "flInconnue2", "test"),
            )

            // When
            val resultat = recupererEleveService.recupererEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            val attendu =
                profilEleve.copy(
                    formationsFavorites =
                        listOf(
                            FormationFavorite("fl0001", niveauAmbition = 0, priseDeNote = null),
                            FormationFavorite("fl0002", niveauAmbition = 0, priseDeNote = null),
                        ),
                    corbeilleFormations = listOf("fl0003"),
                )
            assertThat(resultat).isEqualTo(attendu)
        }

        @Test
        fun `si des voeux n'existent pas, doit les filtrer`() {
            // Given
            val profil =
                profilEleve.copy(
                    formationsFavorites =
                        listOf(
                            FormationFavorite(
                                "fl0001",
                                niveauAmbition = 0,
                                priseDeNote = null,
                            ),
                            FormationFavorite("fl0002", niveauAmbition = 0, priseDeNote = null),
                            FormationFavorite(
                                "fl0003",
                                niveauAmbition = 0,
                                priseDeNote = null,
                            ),
                        ),
                    corbeilleFormations = listOf("fl0004"),
                    voeuxFavoris =
                        listOf(
                            VoeuFavori("ta26660", true),
                            VoeuFavori("ta37607", true),
                            VoeuFavori("taInconnu", true),
                            VoeuFavori("ta31045", true),
                            VoeuFavori("ta31042", true),
                        ),
                )
            given(eleveRepository.recupererUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")).willReturn(profil)
            given(
                formationRepository.recupererIdsFormationsInexistantes(listOf("fl0001", "fl0002", "fl0003", "fl0004")),
            ).willReturn(emptyList())
            given(
                voeuRepository.recupererIdsVoeuxInexistants(
                    listOf(
                        "ta26660",
                        "ta37607",
                        "taInconnu",
                        "ta31045",
                        "ta31042",
                    ),
                ),
            ).willReturn(
                listOf("taInconnu"),
            )

            // When
            val resultat = recupererEleveService.recupererEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            val attendu =
                profilEleve.copy(
                    formationsFavorites =
                        listOf(
                            FormationFavorite(
                                "fl0001",
                                niveauAmbition = 0,
                                priseDeNote = null,
                            ),
                            FormationFavorite("fl0002", niveauAmbition = 0, priseDeNote = null),
                            FormationFavorite(
                                "fl0003",
                                niveauAmbition = 0,
                                priseDeNote = null,
                            ),
                        ),
                    corbeilleFormations = listOf("fl0004"),
                    voeuxFavoris =
                        listOf(
                            VoeuFavori("ta26660", true),
                            VoeuFavori("ta37607", true),
                            VoeuFavori("ta31045", true),
                            VoeuFavori("ta31042", true),
                        ),
                )
            assertThat(resultat).isEqualTo(attendu)
        }
    }

    @Nested
    inner class FiltrerCentresInteret {
        @Test
        fun `si les centres d'intérêts sont null, doit ne pas modifier et ne pas appeler le repo`() {
            // Given
            val profil = profilEleve.copy(centresInterets = null)
            given(eleveRepository.recupererUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")).willReturn(profil)

            // When
            val resultat = recupererEleveService.recupererEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            assertThat(resultat).isEqualTo(profil)
            then(interetRepository).shouldHaveNoInteractions()
        }

        @Test
        fun `si des centres d'intérêts n'existent pas, doit les filtrer`() {
            // Given
            val profil = profilEleve.copy(centresInterets = listOf("ci10", "ci13", "ci29"))
            given(eleveRepository.recupererUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")).willReturn(profil)
            given(interetRepository.recupererIdsCentresInteretsInexistants(listOf("ci10", "ci13", "ci29"))).willReturn(listOf("ci13"))

            // When
            val resultat = recupererEleveService.recupererEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            val attendu = profilEleve.copy(centresInterets = listOf("ci10", "ci29"))
            assertThat(resultat).isEqualTo(attendu)
        }
    }

    @Nested
    inner class FiltrerDomaines {
        @Test
        fun `si les domaines d'intérêts sont null, doit ne pas modifier et ne pas appeler le repo`() {
            // Given
            val profil = profilEleve.copy(domainesInterets = null)
            given(eleveRepository.recupererUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")).willReturn(profil)

            // When
            val resultat = recupererEleveService.recupererEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            assertThat(resultat).isEqualTo(profil)
            then(domaineRepository).shouldHaveNoInteractions()
        }

        @Test
        fun `si des domaines d'intérêts n'existent pas, doit les filtrer`() {
            // Given
            val profil = profilEleve.copy(domainesInterets = listOf("dom12", "dom7", "dom25"))
            given(eleveRepository.recupererUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")).willReturn(profil)
            given(domaineRepository.recupererIdsDomainesInexistants(listOf("dom12", "dom7", "dom25"))).willReturn(listOf("dom7"))

            // When
            val resultat = recupererEleveService.recupererEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            val attendu = profilEleve.copy(domainesInterets = listOf("dom12", "dom25"))
            assertThat(resultat).isEqualTo(attendu)
        }
    }

    @Nested
    inner class FiltrerMetiers {
        @Test
        fun `si les métiers sont null, doit ne pas modifier et ne pas appeler le repo`() {
            // Given
            val profil = profilEleve.copy(metiersFavoris = null)
            given(eleveRepository.recupererUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")).willReturn(profil)

            // When
            val resultat = recupererEleveService.recupererEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            assertThat(resultat).isEqualTo(profil)
            then(metierRepository).shouldHaveNoInteractions()
        }

        @Test
        fun `si des métiers n'existent pas, doit les filtrer`() {
            // Given
            val profil = profilEleve.copy(metiersFavoris = listOf("MET.14", "MET.152", "MET.168"))
            given(eleveRepository.recupererUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")).willReturn(profil)
            given(
                metierRepository.recupererIdsMetiersInexistants(listOf("MET.14", "MET.152", "MET.168")),
            ).willReturn(listOf("MET.14", "MET.168"))

            // When
            val resultat = recupererEleveService.recupererEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            val attendu = profilEleve.copy(metiersFavoris = listOf("MET.152"))
            assertThat(resultat).isEqualTo(attendu)
        }
    }

    @Nested
    inner class CasNominal {
        @Test
        fun `quand toutes les valeurs sont okay, doit renvoyer le profil tel quel `() {
            // Given
            given(eleveRepository.recupererUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")).willReturn(profilEleve)

            // When
            val resultat = recupererEleveService.recupererEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            assertThat(resultat).isEqualTo(profilEleve)
        }

        @Test
        fun `quand élève sans compte, doit le retourner`() {
            // Given
            val profilSansCompte = ProfilEleve.SansCompte("3a24c5c6-7583-4c27-957c-feba8f4682df")
            given(eleveRepository.recupererUnEleve(id = "3a24c5c6-7583-4c27-957c-feba8f4682df")).willReturn(profilSansCompte)

            // When
            val resultat = recupererEleveService.recupererEleve(id = "3a24c5c6-7583-4c27-957c-feba8f4682df")

            // Then
            assertThat(resultat).isEqualTo(profilSansCompte)
        }
    }
}
