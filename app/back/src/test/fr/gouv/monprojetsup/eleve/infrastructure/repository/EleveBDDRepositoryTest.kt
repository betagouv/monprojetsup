package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.eleve.domain.entity.FormationFavorite
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFavori
import fr.gouv.monprojetsup.eleve.entity.CommunesFavorites
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.then
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class EleveBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var eleveJPARepository: EleveJPARepository

    @Mock
    lateinit var logger: MonProjetSupLogger

    lateinit var eleveBDDRepository: EleveBDDRepository

    @BeforeEach
    fun setup() {
        eleveBDDRepository = EleveBDDRepository(eleveJPARepository, logger)
    }

    private val profil0f88 =
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
            corbeilleFormations = listOf("fl0001", "fl0002"),
            compteParcoursupLie = true,
            voeuxFavoris = listOf(VoeuFavori("ta15974", true), VoeuFavori("ta17831", false)),
        )

    private val profil129f =
        ProfilEleve.AvecProfilExistant(
            id = "129f6d9c-0f6f-4fa4-8107-75b7cb129889",
            situation = SituationAvanceeProjetSup.QUELQUES_PISTES,
            classe = ChoixNiveau.TERMINALE,
            baccalaureat = "Professionnel",
            specialites = emptyList(),
            domainesInterets = listOf("animaux", "agroequipement"),
            centresInterets = listOf("linguistique", "voyage"),
            metiersFavoris = listOf("MET002"),
            dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
            alternance = ChoixAlternance.TRES_INTERESSE,
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
            corbeilleFormations = listOf("fl0001", "fl0002"),
            compteParcoursupLie = false,
            voeuxFavoris = listOf(VoeuFavori("ta15974", true), VoeuFavori("ta17831", false)),
        )

    @Nested
    inner class RecupererUnEleve {
        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `Quand l'élève existe et qu'il a un compte parcoursup, doit retourner son profil`() {
            // Given
            val id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15"

            // When
            val result = eleveBDDRepository.recupererUnEleve(id = id)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(profil0f88)
        }

        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `Quand l'élève existe mais n'a pas lié son compte parcoursup, doit retourner son profil avec compte lié à false`() {
            // Given
            val id = "129f6d9c-0f6f-4fa4-8107-75b7cb129889"

            // When
            val result = eleveBDDRepository.recupererUnEleve(id = id)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(profil129f)
        }

        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `Quand l'élève n'existe pas, doit retourner un profil inconnu`() {
            // Given
            val id = "45fdce8e-0717-4848-9a0c-505dea093b8c"

            // When
            val result = eleveBDDRepository.recupererUnEleve(id = id)

            // Then
            assertThat(result).isEqualTo(ProfilEleve.SansCompte(id))
        }
    }

    @Nested
    inner class CreerUnEleve {
        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `Quand l'élève n'existe pas, doit retourner son profil`() {
            // Given
            val id = "45fdce8e-0717-4848-9a0c-505dea093b8c"

            // When
            val result = eleveBDDRepository.creerUnEleve(id = id)

            // Then
            val attendu = ProfilEleve.AvecProfilExistant(id = id)
            assertThat(result).isEqualTo(attendu)
        }

        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `Quand l'élève existe, doit le retourner et logguer un warning`() {
            // Given
            val id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15"

            // When
            val result = eleveBDDRepository.creerUnEleve(id = id)

            // Then
            assertThat(result).isEqualTo(profil0f88)
            then(logger).should()
                .warn(
                    type = "ID_ELEVE_EXISTE_DEJA",
                    message = "L'élève 0f88ddd1-62ef-436e-ad3f-cf56d5d14c15 a voulu être crée alors qu'il existe déjà en base",
                )
        }
    }

    @Nested
    inner class MettreAJourUnProfilEleve {
        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `Quand l'élève existe, doit mettre à jour ses données qui ne sont pas à nulles`() {
            // Given
            val id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15"
            val nouveauProfil = ProfilEleve.AvecProfilExistant(id = id)

            // When
            eleveBDDRepository.mettreAJourUnProfilEleve(profilEleve = nouveauProfil)

            // Then
            val resultat = eleveJPARepository.findById(id).orElseThrow().toProfilEleve()
            assertThat(resultat).usingRecursiveAssertion().isEqualTo(nouveauProfil)
        }

        @Test
        @Sql("classpath:comptes_parcoursup.sql")
        fun `Quand l'élève n'existe pas, doit throw une erreur`() {
            // Given
            val profilInconnu =
                ProfilEleve.AvecProfilExistant(
                    id = "871a33a9-fd55-4d9d-9211-22edf3c3d1e5",
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
                    corbeilleFormations = listOf("fl0001", "fl0002"),
                    compteParcoursupLie = false,
                    voeuxFavoris = listOf(VoeuFavori("ta15974", true), VoeuFavori("ta17831", false)),
                )

            // When & Then
            assertThatThrownBy {
                eleveBDDRepository.mettreAJourUnProfilEleve(profilEleve = profilInconnu)
            }.isInstanceOf(MonProjetSupNotFoundException::class.java)
                .hasMessage("L'élève 871a33a9-fd55-4d9d-9211-22edf3c3d1e5 n'a pas été crée en base")
        }
    }
}
