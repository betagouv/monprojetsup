package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFormation
import fr.gouv.monprojetsup.formation.entity.Communes
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
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class EleveBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var eleveJPARepository: EleveJPARepository

    @Mock
    lateinit var logger: Logger

    lateinit var eleveBDDRepository: EleveBDDRepository

    @BeforeEach
    fun setup() {
        eleveBDDRepository = EleveBDDRepository(eleveJPARepository, logger)
    }

    val profil0f88 =
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
            communesFavorites = listOf(Communes.PARIS15EME, Communes.MARSEILLE),
            formationsFavorites =
                listOf(
                    VoeuFormation(
                        idFormation = "fl0010",
                        niveauAmbition = 1,
                        voeuxChoisis = emptyList(),
                        priseDeNote = null,
                    ),
                    VoeuFormation(
                        idFormation = "fl0012",
                        niveauAmbition = 3,
                        voeuxChoisis = listOf("ta15974", "ta17831"),
                        priseDeNote = "Mon voeu préféré",
                    ),
                ),
            moyenneGenerale = 10.5f,
            corbeilleFormations = listOf("fl0001", "fl0002"),
        )

    @Nested
    inner class RecupererUnEleve {
        @Test
        @Sql("classpath:profil_eleve.sql")
        fun `Quand l'élève existe, doit retourner son profil`() {
            // Given
            val id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15"

            // When
            val result = eleveBDDRepository.recupererUnEleve(id = id)

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(profil0f88)
        }

        @Test
        @Sql("classpath:profil_eleve.sql")
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
        @Sql("classpath:profil_eleve.sql")
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
        @Sql("classpath:profil_eleve.sql")
        fun `Quand l'élève existe, doit le retourner et logguer un warning`() {
            // Given
            val id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15"

            // When
            val result = eleveBDDRepository.creerUnEleve(id = id)

            // Then
            assertThat(result).isEqualTo(profil0f88)
            then(logger).should()
                .warn("L'élève 0f88ddd1-62ef-436e-ad3f-cf56d5d14c15 a voulu être crée alors qu'il existe déjà en base")
        }
    }

    @Nested
    inner class MettreAJourUnProfilEleve {
        @Test
        @Sql("classpath:profil_eleve.sql")
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
        @Sql("classpath:profil_eleve.sql")
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
                    communesFavorites = listOf(Communes.PARIS15EME, Communes.MARSEILLE),
                    formationsFavorites =
                        listOf(
                            VoeuFormation(
                                idFormation = "fl0010",
                                niveauAmbition = 1,
                                voeuxChoisis = emptyList(),
                                priseDeNote = null,
                            ),
                            VoeuFormation(
                                idFormation = "fl0012",
                                niveauAmbition = 3,
                                voeuxChoisis = listOf("ta15974", "ta17831"),
                                priseDeNote = "Mon voeu préféré",
                            ),
                        ),
                    moyenneGenerale = 10.5f,
                    corbeilleFormations = listOf("fl0001", "fl0002"),
                )

            // When & Then
            assertThatThrownBy {
                eleveBDDRepository.mettreAJourUnProfilEleve(profilEleve = profilInconnu)
            }.isInstanceOf(MonProjetSupNotFoundException::class.java)
                .hasMessage("L'élève 871a33a9-fd55-4d9d-9211-22edf3c3d1e5 n'a pas été crée en base")
        }
    }
}
