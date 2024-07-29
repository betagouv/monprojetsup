package fr.gouv.monprojetsup.eleve.infrastructure.repository

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
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
        ProfilEleve(
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
            communesFavorites = listOf(Communes.PARIS, Communes.MARSEILLE),
            formationsFavorites = listOf("fl0010", "fl0012"),
            moyenneGenerale = 10.5f,
        )

    @Nested
    inner class RecupererUnEleve {
        @Test
        @Sql("classpath:profil_eleve.sql")
        fun `Quand l'élève existe, doit retourner son profil`() {
            // When
            val result = eleveBDDRepository.recupererUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            assertThat(result).usingRecursiveAssertion().isEqualTo(profil0f88)
        }

        @Test
        @Sql("classpath:profil_eleve.sql")
        fun `Quand l'élève n'existe pas, doit throw MonProjetSupNotFoundException`() {
            // When & Then
            assertThatThrownBy {
                eleveBDDRepository.recupererUnEleve(id = "45fdce8e-0717-4848-9a0c-505dea093b8c")
            }.isInstanceOf(MonProjetSupNotFoundException::class.java)
                .hasMessage("L'élève n'a pas de compte")
        }
    }

    @Nested
    inner class CreerUnEleve {
        @Test
        @Sql("classpath:profil_eleve.sql")
        fun `Quand l'élève n'existe pas, doit retourner son profil`() {
            // When
            val result = eleveBDDRepository.creerUnEleve(id = "45fdce8e-0717-4848-9a0c-505dea093b8c")

            // Then
            val attendu =
                ProfilEleve(
                    id = "45fdce8e-0717-4848-9a0c-505dea093b8c",
                    situation = null,
                    classe = null,
                    baccalaureat = null,
                    specialites = null,
                    domainesInterets = null,
                    centresInterets = null,
                    metiersFavoris = null,
                    dureeEtudesPrevue = null,
                    alternance = null,
                    communesFavorites = null,
                    formationsFavorites = null,
                    moyenneGenerale = null,
                )
            assertThat(result).isEqualTo(attendu)
        }

        @Test
        @Sql("classpath:profil_eleve.sql")
        fun `Quand l'élève existe, doit le retourner et logguer un warning`() {
            // When
            val result = eleveBDDRepository.creerUnEleve(id = "0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")

            // Then
            assertThat(result).isEqualTo(profil0f88)
            then(logger).should().warn("L'élève 0f88ddd1-62ef-436e-ad3f-cf56d5d14c15 a voulu être crée alors qu'il existe déjà en base")
        }
    }
}
