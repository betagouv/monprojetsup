package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.eleve.domain.entity.FormationFavorite
import fr.gouv.monprojetsup.eleve.domain.entity.ModificationProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFavori
import fr.gouv.monprojetsup.eleve.domain.port.EleveRepository
import fr.gouv.monprojetsup.eleve.entity.CommunesFavorites
import fr.gouv.monprojetsup.formation.domain.entity.Voeu
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.VoeuRepository
import fr.gouv.monprojetsup.formation.entity.CommunesCourtes
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatRepository
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatSpecialiteRepository
import fr.gouv.monprojetsup.referentiel.domain.port.DomaineRepository
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.only
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MiseAJourEleveServiceTest {
    @Mock
    private lateinit var baccalaureatRepository: BaccalaureatRepository

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
    private lateinit var miseAJourEleveService: MiseAJourEleveService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    private val profilEleve =
        ProfilEleve.AvecProfilExistant(
            id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15",
            situation = SituationAvanceeProjetSup.AUCUNE_IDEE,
            classe = ChoixNiveau.SECONDE,
            baccalaureat = "Général",
            specialites = listOf("4", "1006"),
            domainesInterets = listOf("animaux", "agroequipement"),
            centresInterets = listOf("linguistique", "voyage"),
            metiersFavoris = listOf("MET001"),
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
            voeuxFavoris = listOf(VoeuFavori("ta1", true), VoeuFavori("ta2", false)),
        )

    private val profilVide = ProfilEleve.AvecProfilExistant(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")
    private val modificationProfilEleveVide = ModificationProfilEleve()

    @Nested
    inner class ErreursBaccalaureatEtSpecialites {
        @Test
        fun `si baccalaureat null et spécialités ni nulles ni vides mais n'existent pas, doit throw BadRequestException`() {
            // Given
            val nouveauProfil =
                ModificationProfilEleve(
                    situation = null,
                    classe = null,
                    baccalaureat = null,
                    specialites = listOf("inconnu", "inconnu_2"),
                    domainesInterets = null,
                    centresInterets = null,
                    metiersFavoris = null,
                    dureeEtudesPrevue = null,
                    alternance = null,
                    communesFavorites = null,
                    formationsFavorites = null,
                    moyenneGenerale = null,
                )
            given(baccalaureatSpecialiteRepository.recupererLesIdsDesSpecialitesDUnBaccalaureat(idBaccalaureat = "Général"))
                .willReturn(listOf("5", "7", "1008", "2003"))

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilEleve,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage(
                    "Une ou plus spécialité renvoyées ne font pas parties des spécialités du baccalaureat Général. " +
                        "Spécialités possibles [5, 7, 1008, 2003]",
                )
        }

        @Test
        fun `si baccalaureat null et spécialités ni nulles ni vides mais baccalaureat en base null, doit throw BadRequestException`() {
            // Given
            val nouveauProfil = modificationProfilEleveVide.copy(specialites = listOf("5", "7"))

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("Veuillez mettre à jour le baccalaureat avant de mettre à jour ses spécialités")
        }

        @Test
        fun `si baccalaureat non null et spécialités vides mais bac n'existe pas, doit throw BadRequestException`() {
            // Given
            val nouveauProfil =
                modificationProfilEleveVide.copy(
                    baccalaureat = "Baccalaureat inconnu",
                    specialites = emptyList(),
                )
            given(baccalaureatRepository.verifierBaccalaureatExiste(id = "Baccalaureat inconnu")).willReturn(false)

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("Aucun baccalaureat avec l'id Baccalaureat inconnu")
        }

        @Test
        fun `si baccalaureat non null et spécialités null et en base null mais bac n'existe pas, doit throw BadRequestException`() {
            // Given
            val nouveauProfil =
                modificationProfilEleveVide.copy(
                    baccalaureat = "Baccalaureat inconnu",
                    specialites = null,
                )
            given(baccalaureatRepository.verifierBaccalaureatExiste("Baccalaureat inconnu")).willReturn(false)

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("Aucun baccalaureat avec l'id Baccalaureat inconnu")
        }

        @Test
        fun `si baccalaureat non null et spécialités null et en base vide mais bac n'existe pas, doit throw BadRequestException`() {
            // Given
            val nouveauProfil =
                modificationProfilEleveVide.copy(
                    baccalaureat = "Baccalaureat inconnu",
                    specialites = null,
                )
            given(baccalaureatRepository.verifierBaccalaureatExiste("Baccalaureat inconnu")).willReturn(false)

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide.copy(specialites = emptyList()),
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("Aucun baccalaureat avec l'id Baccalaureat inconnu")
        }

        @Test
        fun `si baccalaureat non null, spécialités null, en base non null-vide mais bac n'existe pas, doit throw BadRequestException`() {
            // Given
            val nouveauProfil =
                modificationProfilEleveVide.copy(
                    baccalaureat = "Baccalaureat inconnu",
                    specialites = null,
                )
            given(baccalaureatSpecialiteRepository.recupererLesIdsDesSpecialitesDUnBaccalaureat(idBaccalaureat = "Baccalaureat inconnu"))
                .willReturn(emptyList())

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide.copy(specialites = listOf("4", "1006")),
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage(
                    "Une ou plus spécialité renvoyées ne font pas parties des spécialités du baccalaureat Baccalaureat inconnu. " +
                        "Spécialités possibles []",
                )
        }

        @Test
        fun `si baccalaureat non null, spécialités null, en base non null-vide mais une spécialite absente, throw BadRequestException`() {
            // Given
            val nouveauProfil =
                modificationProfilEleveVide.copy(
                    baccalaureat = "Pro",
                    specialites = null,
                )
            given(baccalaureatSpecialiteRepository.recupererLesIdsDesSpecialitesDUnBaccalaureat(idBaccalaureat = "Pro"))
                .willReturn(listOf("5", "1006", "2003", "120"))

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide.copy(baccalaureat = "Général", specialites = listOf("4", "1006")),
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage(
                    "Une ou plus spécialité renvoyées ne font pas parties des spécialités du baccalaureat Pro. " +
                        "Spécialités possibles [5, 1006, 2003, 120]",
                )
        }

        @Test
        fun `si baccalaureat non null, spécialités non null-vide mais bac n'existe pas, doit throw BadRequestException`() {
            // Given
            val nouveauProfil =
                modificationProfilEleveVide.copy(
                    baccalaureat = "Baccalaureat inconnu",
                    specialites = listOf("5", "2003"),
                )
            given(baccalaureatSpecialiteRepository.recupererLesIdsDesSpecialitesDUnBaccalaureat(idBaccalaureat = "Baccalaureat inconnu"))
                .willReturn(emptyList())

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage(
                    "Une ou plus spécialité renvoyées ne font pas parties des spécialités du baccalaureat Baccalaureat inconnu. " +
                        "Spécialités possibles []",
                )
        }

        @Test
        fun `si baccalaureat non null, spécialités non null-vide mais une spécialite absente, doit throw BadRequestException`() {
            // Given
            val nouveauProfil =
                modificationProfilEleveVide.copy(
                    baccalaureat = "Pro",
                    specialites = listOf("5", "2006"),
                )
            given(baccalaureatSpecialiteRepository.recupererLesIdsDesSpecialitesDUnBaccalaureat(idBaccalaureat = "Pro"))
                .willReturn(listOf("5", "1006", "2003", "120"))

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage(
                    "Une ou plus spécialité renvoyées ne font pas parties des spécialités du baccalaureat Pro. " +
                        "Spécialités possibles [5, 1006, 2003, 120]",
                )
        }
    }

    @Nested
    inner class ErreurNonPresents {
        @Test
        fun `si un des métiers favoris n'existe pas, doit throw BadRequestException`() {
            // Given
            val metiersFavoris = listOf("MET_INCONNU", "MET001")
            val nouveauProfil = modificationProfilEleveVide.copy(metiersFavoris = metiersFavoris)
            given(metierRepository.recupererIdsMetiersInexistants(metiersFavoris)).willReturn(listOf("MET_INCONNU"))

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("Les métiers [MET_INCONNU] n'existent pas")
        }

        @Test
        fun `si un des métiers favoris est envoyé en double, doit throw BadRequestException`() {
            // Given
            val metiersFavoris = listOf("MET001", "MET001", "MET004")
            val nouveauProfil = modificationProfilEleveVide.copy(metiersFavoris = metiersFavoris)

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("Un ou plusieurs des métiers est en double")
            then(metierRepository).shouldHaveNoInteractions()
        }

        @Test
        fun `si un des domaines n'existe pas, doit throw BadRequestException`() {
            // Given
            val domaines = listOf("inconnu", "animaux")
            val nouveauProfil = modificationProfilEleveVide.copy(domainesInterets = domaines)
            given(domaineRepository.recupererIdsDomainesInexistants(domaines)).willReturn(listOf("inconnu"))

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("Les domaines [inconnu] n'existent pas")
        }

        @Test
        fun `si un des centre d'intérêt n'existe pas, doit throw BadRequestException`() {
            // Given
            val interets = listOf("inconnu", "linguistique", "voyage")
            val nouveauProfil = modificationProfilEleveVide.copy(centresInterets = interets)
            given(interetRepository.recupererIdsCentresInteretsInexistants(interets)).willReturn(listOf("inconnu"))

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("Les centres d'intérêt [inconnu] n'existent pas")
        }
    }

    @Nested
    inner class ErreurFormations {
        @Test
        fun `si une des formations favorites ou de la corbeille n'existe pas, doit throw BadRequestException`() {
            // Given
            val formationsFavorites =
                listOf(
                    FormationFavorite(
                        idFormation = "flInconnue",
                        niveauAmbition = 1,
                        priseDeNote = null,
                    ),
                    FormationFavorite(
                        idFormation = "fl0001",
                        niveauAmbition = 3,
                        priseDeNote = "Ma formation préférée",
                    ),
                )
            val corbeilleFormations = listOf("fl5678")
            val nouveauProfil =
                modificationProfilEleveVide.copy(
                    formationsFavorites = formationsFavorites,
                    corbeilleFormations = corbeilleFormations,
                    voeuxFavoris = emptyList(), // listOf(VoeuFavori("ta1", true), VoeuFavori("ta2", false)),
                )
            given(
                formationRepository.recupererIdsFormationsInexistantes(
                    ids =
                        listOf(
                            "flInconnue",
                            "fl0001",
                        ) + corbeilleFormations,
                ),
            ).willReturn(listOf("flInconnue"))

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilEleve,
                )
            }.isInstanceOf(
                MonProjetSupBadRequestException::class.java,
            ).hasMessage("Les formations [flInconnue] envoyées n'existent pas")
        }

        @Test
        fun `si une des formations favorites n'existe pas, doit throw BadRequestException`() {
            // Given
            val formationsFavorites =
                listOf(
                    FormationFavorite(
                        idFormation = "flInconnue",
                        niveauAmbition = 1,
                        priseDeNote = null,
                    ),
                    FormationFavorite(
                        idFormation = "fl0001",
                        niveauAmbition = 3,
                        priseDeNote = "Ma formation préférée",
                    ),
                )
            val nouveauProfil = modificationProfilEleveVide.copy(formationsFavorites = formationsFavorites)
            given(
                formationRepository.recupererIdsFormationsInexistantes(ids = listOf("flInconnue", "fl0001")),
            ).willReturn(listOf("flInconnue"))

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilEleve,
                )
            }.isInstanceOf(
                MonProjetSupBadRequestException::class.java,
            ).hasMessage("Les formations [flInconnue] envoyées n'existent pas")
        }

        @Test
        fun `si une des formations à la corbeille n'existe pas, doit throw BadRequestException`() {
            // Given
            val corbeilleFormations = listOf("flInconnue", "fl1234", "fl5678")
            val nouveauProfil = modificationProfilEleveVide.copy(corbeilleFormations = corbeilleFormations)
            given(formationRepository.recupererIdsFormationsInexistantes(corbeilleFormations)).willReturn(listOf("flInconnue"))

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilEleve,
                )
            }.isInstanceOf(
                MonProjetSupBadRequestException::class.java,
            ).hasMessage("Les formations [flInconnue] envoyées n'existent pas")
        }

        @Test
        fun `si une des formations favorites est commune avec une de la corbeille, doit throw BadRequestException`() {
            // Given
            val formationsFavorites =
                listOf(
                    FormationFavorite(
                        idFormation = "flInconnue",
                        niveauAmbition = 1,
                        priseDeNote = null,
                    ),
                    FormationFavorite(
                        idFormation = "fl0001",
                        niveauAmbition = 3,
                        priseDeNote = "Ma formation préférée",
                    ),
                )
            val corbeilleFormations = listOf("fl5678", "fl0001")
            val nouveauProfil =
                modificationProfilEleveVide.copy(
                    formationsFavorites = formationsFavorites,
                    corbeilleFormations = corbeilleFormations,
                )
            given(
                formationRepository.recupererIdsFormationsInexistantes(
                    ids =
                        listOf(
                            "flInconnue",
                            "fl0001",
                        ) + corbeilleFormations,
                ),
            ).willReturn(listOf("flInconnue"))

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilEleve,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("Une ou plusieurs des formations se trouvent à la fois à la corbeille et dans les favoris")
        }

        @Test
        fun `si une des formations favorites se trouve déjà dans la corbeille, doit throw BadRequestException`() {
            // Given
            val formationsFavorites =
                listOf(
                    FormationFavorite(
                        idFormation = "fl1234",
                        niveauAmbition = 1,
                        priseDeNote = null,
                    ),
                    FormationFavorite(
                        idFormation = "fl0001",
                        niveauAmbition = 3,
                        priseDeNote = "Ma formation préférée",
                    ),
                )
            val nouveauProfil = modificationProfilEleveVide.copy(formationsFavorites = formationsFavorites)

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilEleve,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("Vous essayez d'ajouter une formation en favoris alors qu'elle se trouve actuellement à la corbeille")
            then(formationRepository).shouldHaveNoInteractions()
        }

        @Test
        fun `si une des formations à la corbeille se trouve déjà dans les favoris, doit throw BadRequestException`() {
            // Given
            val corbeilleFormations = listOf("fl0010", "fl1234", "fl5678")
            val nouveauProfil = modificationProfilEleveVide.copy(corbeilleFormations = corbeilleFormations)

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilEleve,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("Vous essayez d'ajouter une formation à la corbeille alors qu'elle se trouve actuellement en favoris")
            then(formationRepository).shouldHaveNoInteractions()
        }

        @Test
        fun `si essaye d'inserer 2 fois ou plus la même formation dans les favoris, doit throw BadRequestException`() {
            // Given
            val formationsFavorites =
                listOf(
                    FormationFavorite(
                        idFormation = "fl1",
                        niveauAmbition = 1,
                        priseDeNote = null,
                    ),
                    FormationFavorite(
                        idFormation = "fl1",
                        niveauAmbition = 3,
                        priseDeNote = "Ma formation préférée",
                    ),
                )
            val nouveauProfil = modificationProfilEleveVide.copy(formationsFavorites = formationsFavorites)

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilEleve,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("Une des formations favorites est présentes plusieurs fois")
            then(formationRepository).shouldHaveNoInteractions()
        }

        @Test
        fun `si essaye d'inserer 2 fois ou plus la même formation dans la corbeille, doit throw BadRequestException`() {
            // Given
            val corbeilleFormations = listOf("fl1", "fl1", "fl2")
            val nouveauProfil = modificationProfilEleveVide.copy(corbeilleFormations = corbeilleFormations)

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilEleve,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("Une des formations à la corbeille est présentes plusieurs fois")
            then(formationRepository).shouldHaveNoInteractions()
        }
    }

    @Nested
    inner class ErreurVoeu {
        @Test
        fun `si un des voeux n'est pas présent da,ns la liste des possibilités, doit throw BadRequestException`() {
            // Given
            given(formationRepository.recupererIdsFormationsInexistantes(ids = listOf("fl1", "fl3"))).willReturn(
                emptyList(),
            )
            given(
                voeuRepository.recupererIdsVoeuxInexistants(
                    listOf(
                        "ta1",
                        "tainconnu",
                    ),
                ),
            ).willReturn(listOf("tainconnu"))

            val nouveauProfil =
                modificationProfilEleveVide.copy(
                    formationsFavorites =
                        listOf(
                            FormationFavorite(
                                idFormation = "fl1",
                                niveauAmbition = 3,
                                priseDeNote = null,
                            ),
                            FormationFavorite(
                                idFormation = "fl3",
                                niveauAmbition = 1,
                                priseDeNote = "Ma prise de note",
                            ),
                        ),
                    voeuxFavoris =
                        listOf(
                            VoeuFavori("ta1", true),
                            VoeuFavori("tainconnu", false),
                        ),
                )

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage(
                    "Le ou les voeux favoris suivants ne sont pas connus : [tainconnu]",
                )
        }
    }

    @Nested
    inner class ErreurMoyenneGenerale {
        @Test
        fun `si la moyenne envoyée est strictement inferieure à 0, doit throw BadRequestException`() {
            // Given
            val nouveauProfil = modificationProfilEleveVide.copy(moyenneGenerale = -0.5f)

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("La moyenne générale -0.5 n'est pas dans l'intervalle 0 et 20")
        }

        @Test
        fun `si la moyenne envoyée est strictement supérieure à 20, doit throw BadRequestException`() {
            // Given
            val nouveauProfil = modificationProfilEleveVide.copy(moyenneGenerale = 20.5f)

            // When & Then
            assertThatThrownBy {
                miseAJourEleveService.mettreAJourUnProfilEleve(
                    miseAJourDuProfil = nouveauProfil,
                    profilActuel = profilVide,
                )
            }.isInstanceOf(MonProjetSupBadRequestException::class.java)
                .hasMessage("La moyenne générale 20.5 n'est pas dans l'intervalle 0 et 20")
        }
    }

    @Nested
    inner class CasNominaux {
        @Test
        fun `quand toutes les valeurs sont à null, ne doit rien faire`() {
            // When
            miseAJourEleveService.mettreAJourUnProfilEleve(
                miseAJourDuProfil = modificationProfilEleveVide,
                profilActuel = profilEleve,
            )

            // Then
            then(baccalaureatRepository).shouldHaveNoInteractions()
            then(baccalaureatSpecialiteRepository).shouldHaveNoInteractions()
            then(domaineRepository).shouldHaveNoInteractions()
            then(interetRepository).shouldHaveNoInteractions()
            then(metierRepository).shouldHaveNoInteractions()
            then(formationRepository).shouldHaveNoInteractions()
            then(voeuRepository).shouldHaveNoInteractions()
            then(eleveRepository).shouldHaveNoInteractions()
        }

        @Test
        fun `quand les listes sont à vides, doit les mettre à jour sans appeler les repo`() {
            // Given
            val nouveauProfil =
                ModificationProfilEleve(
                    situation = null,
                    classe = null,
                    baccalaureat = null,
                    specialites = emptyList(),
                    domainesInterets = emptyList(),
                    centresInterets = emptyList(),
                    metiersFavoris = emptyList(),
                    dureeEtudesPrevue = null,
                    alternance = null,
                    communesFavorites = emptyList(),
                    formationsFavorites = emptyList(),
                    moyenneGenerale = null,
                )

            // When
            miseAJourEleveService.mettreAJourUnProfilEleve(
                miseAJourDuProfil = nouveauProfil,
                profilActuel = profilEleve,
            )

            // Then
            then(baccalaureatRepository).shouldHaveNoInteractions()
            then(baccalaureatSpecialiteRepository).shouldHaveNoInteractions()
            then(domaineRepository).shouldHaveNoInteractions()
            then(interetRepository).shouldHaveNoInteractions()
            then(metierRepository).shouldHaveNoInteractions()
            then(voeuRepository).shouldHaveNoInteractions()
            then(formationRepository).shouldHaveNoInteractions()
            val profilAMettreAJour =
                profilEleve.copy(
                    specialites = emptyList(),
                    domainesInterets = emptyList(),
                    centresInterets = emptyList(),
                    metiersFavoris = emptyList(),
                    communesFavorites = emptyList(),
                    formationsFavorites = emptyList(),
                )
            then(eleveRepository).should().mettreAJourUnProfilEleve(profilAMettreAJour)
        }

        @Test
        fun `quand toutes les valeurs sont okay, doit tout mettre à jour`() {
            // Given
            val modificationProfilEleve =
                ModificationProfilEleve(
                    situation = SituationAvanceeProjetSup.QUELQUES_PISTES,
                    classe = ChoixNiveau.PREMIERE,
                    baccalaureat = "Pro",
                    specialites = listOf("5", "1008"),
                    domainesInterets = listOf("agroequipement"),
                    centresInterets = listOf("linguistique", "etude"),
                    metiersFavoris = listOf("MET004"),
                    dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                    alternance = ChoixAlternance.PAS_INTERESSE,
                    communesFavorites = listOf(CommunesFavorites.PARIS15EME),
                    formationsFavorites =
                        listOf(
                            FormationFavorite(
                                idFormation = "fl0011",
                                niveauAmbition = 2,
                                priseDeNote = null,
                            ),
                            FormationFavorite(
                                idFormation = "fl0015",
                                niveauAmbition = 2,
                                priseDeNote = null,
                            ),
                        ),
                    moyenneGenerale = 14.5f,
                    corbeilleFormations = listOf("fl0013"),
                    voeuxFavoris =
                        listOf(
                            VoeuFavori("ta1", true),
                            VoeuFavori("ta2", false),
                        ),
                )
            given(domaineRepository.recupererIdsDomainesInexistants(ids = listOf("agroequipement"))).willReturn(
                emptyList(),
            )
            given(
                interetRepository.recupererIdsCentresInteretsInexistants(
                    ids =
                        listOf(
                            "linguistique",
                            "etude",
                        ),
                ),
            ).willReturn(emptyList())
            given(metierRepository.recupererIdsMetiersInexistants(ids = listOf("MET004"))).willReturn(emptyList())
            given(
                formationRepository.recupererIdsFormationsInexistantes(ids = listOf("fl0011", "fl0015", "fl0013")),
            ).willReturn(emptyList())
            given(voeuRepository.recupererLesVoeuxDeFormations(listOf("fl0011"), true)).willReturn(
                mapOf(
                    "fl0011" to
                        listOf(
                            Voeu(
                                id = "ta1",
                                nom = "Nom ta1",
                                commune = CommunesCourtes.MARSEILLE,
                                latitude = 43.300000,
                                longitude = 5.400000,
                            ),
                            Voeu(
                                id = "ta2",
                                nom = "Nom ta2",
                                commune = CommunesCourtes.PARIS15EME,
                                longitude = 2.2885659,
                                latitude = 48.851227,
                            ),
                            Voeu(
                                id = "ta20",
                                nom = "Nom ta20",
                                commune = CommunesCourtes.CAEN,
                                latitude = 49.183334,
                                longitude = -0.350000,
                            ),
                        ),
                ),
            )
            given(baccalaureatSpecialiteRepository.recupererLesIdsDesSpecialitesDUnBaccalaureat(idBaccalaureat = "Pro"))
                .willReturn(listOf("5", "7", "1008", "2003"))

            // When
            miseAJourEleveService.mettreAJourUnProfilEleve(
                miseAJourDuProfil = modificationProfilEleve,
                profilActuel = profilEleve,
            )

            // Then
            val nouveauProfil =
                ProfilEleve.AvecProfilExistant(
                    id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15",
                    situation = SituationAvanceeProjetSup.QUELQUES_PISTES,
                    classe = ChoixNiveau.PREMIERE,
                    baccalaureat = "Pro",
                    specialites = listOf("5", "1008"),
                    domainesInterets = listOf("agroequipement"),
                    centresInterets = listOf("linguistique", "etude"),
                    metiersFavoris = listOf("MET004"),
                    dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                    alternance = ChoixAlternance.PAS_INTERESSE,
                    communesFavorites = listOf(CommunesFavorites.PARIS15EME),
                    formationsFavorites =
                        listOf(
                            FormationFavorite(
                                idFormation = "fl0011",
                                niveauAmbition = 2,
                                priseDeNote = null,
                            ),
                            FormationFavorite(
                                idFormation = "fl0015",
                                niveauAmbition = 2,
                                priseDeNote = null,
                            ),
                        ),
                    moyenneGenerale = 14.5f,
                    corbeilleFormations = listOf("fl0013"),
                    compteParcoursupLie = true,
                    voeuxFavoris =
                        listOf(
                            VoeuFavori("ta1", true),
                            VoeuFavori("ta2", false),
                        ),
                )
            then(baccalaureatRepository).shouldHaveNoInteractions()
            then(eleveRepository).should(only()).mettreAJourUnProfilEleve(nouveauProfil)
        }

        @Test
        fun `quand la moyenne est à -1, doit la mettre à jour`() {
            // Given
            val modificationProfilEleve = ModificationProfilEleve(moyenneGenerale = -1.0f)

            // When
            miseAJourEleveService.mettreAJourUnProfilEleve(
                miseAJourDuProfil = modificationProfilEleve,
                profilActuel = profilEleve,
            )

            // Then
            val nouveauProfil =
                ProfilEleve.AvecProfilExistant(
                    id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15",
                    situation = SituationAvanceeProjetSup.AUCUNE_IDEE,
                    classe = ChoixNiveau.SECONDE,
                    baccalaureat = "Général",
                    specialites = listOf("4", "1006"),
                    domainesInterets = listOf("animaux", "agroequipement"),
                    centresInterets = listOf("linguistique", "voyage"),
                    metiersFavoris = listOf("MET001"),
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
                    moyenneGenerale = -1.0f,
                    corbeilleFormations = listOf("fl1234", "fl5678"),
                    compteParcoursupLie = true,
                    voeuxFavoris = listOf(VoeuFavori("ta1", true), VoeuFavori("ta2", false)),
                )
            then(baccalaureatRepository).shouldHaveNoInteractions()
            then(baccalaureatSpecialiteRepository).shouldHaveNoInteractions()
            then(domaineRepository).shouldHaveNoInteractions()
            then(interetRepository).shouldHaveNoInteractions()
            then(metierRepository).shouldHaveNoInteractions()
            then(formationRepository).shouldHaveNoInteractions()
            then(eleveRepository).should(only()).mettreAJourUnProfilEleve(nouveauProfil)
        }

        @Test
        fun `quand le baccalaureat existe, doit le mettre à jour`() {
            // Given
            val nouveauProfil =
                modificationProfilEleveVide.copy(
                    baccalaureat = "Pro",
                    specialites = emptyList(),
                )
            given(baccalaureatRepository.verifierBaccalaureatExiste(id = "Pro")).willReturn(true)

            // When
            miseAJourEleveService.mettreAJourUnProfilEleve(
                miseAJourDuProfil = nouveauProfil,
                profilActuel = profilEleve,
            )

            // Then
            val profilAMettreAJour = profilEleve.copy(baccalaureat = "Pro", specialites = emptyList())
            then(eleveRepository).should(only()).mettreAJourUnProfilEleve(profilAMettreAJour)
        }
    }

    data class ScenarioCasNominalModifVoeux(
        val nomScenario: String,
        val formations: List<String>,
        val voeuxFavorisActuels: List<VoeuFavori>,
        val modifVoeux: List<VoeuFavori>,
        val modifFormations: List<String>? = null,
        val nouveauxVoeuxFavoris: List<VoeuFavori>,
    )

    companion object {
        private val scenariosNominauxModifsVoeux =
            listOf(
                ScenarioCasNominalModifVoeux(
                    nomScenario = "quand un nouveau voeu marqué comme non FavoriParcoursup est ajouté, il doit apparaître ",
                    formations = listOf("fl0010", "fl0012"),
                    voeuxFavorisActuels =
                        listOf(
                            VoeuFavori("tafl0010", true),
                            VoeuFavori("tafl0012", false),
                        ),
                    modifVoeux =
                        listOf(
                            VoeuFavori("ta", false),
                            VoeuFavori("tafl0010", true),
                            VoeuFavori("tafl0012", false),
                        ),
                    nouveauxVoeuxFavoris =
                        listOf(
                            VoeuFavori("ta", false),
                            VoeuFavori("tafl0010", true),
                            VoeuFavori("tafl0012", false),
                        ),
                ),
                ScenarioCasNominalModifVoeux(
                    nomScenario = "quand un nouveau voeu marqué comme  FavoriParcoursup est ajouté, il est ignoré ",
                    formations = listOf("fl0010", "fl0012"),
                    voeuxFavorisActuels =
                        listOf(
                            VoeuFavori("tafl0010", true),
                            VoeuFavori("tafl0012", false),
                        ),
                    modifVoeux =
                        listOf(
                            VoeuFavori("ta", true),
                            VoeuFavori("tafl0010", true),
                            VoeuFavori("tafl0012", false),
                        ),
                    nouveauxVoeuxFavoris =
                        listOf(
                            VoeuFavori("tafl0010", true),
                            VoeuFavori("tafl0012", false),
                        ),
                ),
                ScenarioCasNominalModifVoeux(
                    nomScenario =
                        """
                    |quand un voeu est supprimé, 
                    |et que ce n'est pas un favori parcoursup, 
                    |ce voeu doit disparaitre des favoris
                        """.trimMargin(),
                    formations = listOf("fl0010", "fl0012"),
                    voeuxFavorisActuels =
                        listOf(
                            VoeuFavori("ta", false),
                            VoeuFavori("tafl0010", true),
                            VoeuFavori("tafl0012", false),
                        ),
                    modifVoeux =
                        listOf(
                            VoeuFavori("tafl0010", true),
                            VoeuFavori("tafl0012", false),
                        ),
                    nouveauxVoeuxFavoris =
                        listOf(
                            VoeuFavori("tafl0010", true),
                            VoeuFavori("tafl0012", false),
                        ),
                ),
                ScenarioCasNominalModifVoeux(
                    nomScenario =
                        """
                    |quand un voeu est supprimé, 
                    |et que c'est un favori parcoursup, 
                    |ce voeu ne doit pas disparaitre des favoris
                        """.trimMargin(),
                    formations = listOf("fl0010", "fl0012"),
                    voeuxFavorisActuels =
                        listOf(
                            VoeuFavori("ta", true),
                            VoeuFavori("tafl0010", true),
                            VoeuFavori("tafl0012", false),
                        ),
                    modifVoeux =
                        listOf(
                            VoeuFavori("tafl0010", true),
                            VoeuFavori("tafl0012", false),
                        ),
                    nouveauxVoeuxFavoris =
                        listOf(
                            VoeuFavori("ta", true),
                            VoeuFavori("tafl0010", true),
                            VoeuFavori("tafl0012", false),
                        ),
                ),
                ScenarioCasNominalModifVoeux(
                    nomScenario =
                        """
                    |quand une formation est supprimée, 
                    |les voeux associés qui n'apparaissent
                    |pas dans une autre formation et ne sont pas des favoris Psup
                    |sont supprimés
                        """.trimMargin(),
                    formations = listOf("fl0010", "fl0012"),
                    voeuxFavorisActuels =
                        listOf(
                            VoeuFavori("ta", false),
                            VoeuFavori("tafl0010", false),
                            VoeuFavori("tafl0012", false),
                        ),
                    modifVoeux =
                        listOf(
                            VoeuFavori("ta", false),
                            VoeuFavori("tafl0010", false),
                        ),
                    modifFormations = listOf("fl0010"),
                    nouveauxVoeuxFavoris =
                        listOf(
                            VoeuFavori("ta", false),
                            VoeuFavori("tafl0010", false),
                        ),
                ),
                ScenarioCasNominalModifVoeux(
                    nomScenario =
                        """
                    |quand une formation est supprimée, 
                    |les voeux associés qui n'apparaissent
                    |pas dans une autre formation mais sont des favoris psup 
                    |ne sont pas supprimés
                        """.trimMargin(),
                    formations = listOf("fl0010", "fl0012"),
                    voeuxFavorisActuels =
                        listOf(
                            VoeuFavori("ta", false),
                            VoeuFavori("tafl0010", false),
                            VoeuFavori("tafl0012", true),
                        ),
                    modifVoeux =
                        listOf(
                            VoeuFavori("ta", false),
                            VoeuFavori("tafl0010", false),
                        ),
                    modifFormations = listOf("fl0010"),
                    nouveauxVoeuxFavoris =
                        listOf(
                            VoeuFavori("ta", false),
                            VoeuFavori("tafl0010", false),
                            VoeuFavori("tafl0012", true),
                        ),
                ),
            )

        @JvmStatic
        fun provideArgumentsForScenariosModifVoeux(): List<Arguments> {
            return scenariosNominauxModifsVoeux.map { Arguments.of(it) }
        }
    }

    private val formationsIds = listOf("fl0010", "fl0012")
    private val voeuxMaps =
        formationsIds.map {
            it to
                listOf(
                    Voeu(
                        id = "ta",
                        nom = "Nom ta",
                        commune = CommunesCourtes.MARSEILLE,
                        latitude = 43.300000,
                        longitude = 5.400000,
                    ),
                    Voeu(
                        id = "ta$it",
                        nom = "Nom ta$it",
                        commune = CommunesCourtes.MARSEILLE,
                        latitude = 43.300000,
                        longitude = 5.400000,
                    ),
                )
        }.toMap()

    @ParameterizedTest
    @MethodSource("provideArgumentsForScenariosModifVoeux")
    fun `scénarios de modifications de voeux`(scenario: ScenarioCasNominalModifVoeux) {
        val voeuxRequetés = scenario.voeuxFavorisActuels.map { it.idVoeu }
        val voeuxMappés =
            voeuxMaps
                .filterKeys { it in scenario.formations }
                .map { it.key to it.value.filter { v -> v.id in voeuxRequetés } }
                .toMap()

        // Given
        given(voeuRepository.recupererVoeux(voeuxRequetés)).willReturn(voeuxMappés)
        val profilActuel =
            profilEleve.copy(
                id = scenario.nomScenario,
                formationsFavorites =
                    scenario.formations.map { idFormation ->
                        FormationFavorite(
                            idFormation = idFormation,
                            niveauAmbition = 1,
                            priseDeNote = null,
                        )
                    },
                voeuxFavoris = scenario.voeuxFavorisActuels,
            )
        val miseAJourProfil =
            if (scenario.modifFormations != null) {
                modificationProfilEleveVide.copy(
                    formationsFavorites =
                        scenario.modifFormations.map { idFormation ->
                            FormationFavorite(
                                idFormation = idFormation,
                                niveauAmbition = 1,
                                priseDeNote = null,
                            )
                        },
                    voeuxFavoris = scenario.modifVoeux,
                )
            } else {
                modificationProfilEleveVide.copy(
                    voeuxFavoris = scenario.modifVoeux,
                    formationsFavorites = profilActuel.formationsFavorites,
                )
            }
        // When
        miseAJourEleveService.mettreAJourUnProfilEleve(
            miseAJourDuProfil = miseAJourProfil,
            profilActuel = profilActuel,
        )
        // Then
        val nouveauProfil =
            profilActuel.copy(
                formationsFavorites = miseAJourProfil.formationsFavorites,
                voeuxFavoris = scenario.nouveauxVoeuxFavoris,
            )
        if (profilActuel == nouveauProfil) {
            then(eleveRepository).shouldHaveNoInteractions()
        } else {
            then(eleveRepository).should(only()).mettreAJourUnProfilEleve(nouveauProfil)
        }
    }
}
