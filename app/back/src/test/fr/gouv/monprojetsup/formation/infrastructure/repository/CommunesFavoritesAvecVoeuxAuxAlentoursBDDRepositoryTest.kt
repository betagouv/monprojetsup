package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.eleve.entity.CommunesFavorites
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecIdsVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecIdsVoeuxAuxAlentours.VoeuAvecDistance
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.then
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class CommunesFavoritesAvecVoeuxAuxAlentoursBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    private lateinit var communesAvecVoeuxAuxAlentoursJPARepository: CommunesAvecVoeuxAuxAlentoursJPARepository

    @Mock
    private lateinit var logger: MonProjetSupLogger

    private lateinit var communesAvecVoeuxAuxAlentoursBDDRepository: CommunesAvecVoeuxAuxAlentoursBDDRepository

    @BeforeEach
    fun setup() {
        communesAvecVoeuxAuxAlentoursBDDRepository =
            CommunesAvecVoeuxAuxAlentoursBDDRepository(
                communesAvecVoeuxAuxAlentoursJPARepository = communesAvecVoeuxAuxAlentoursJPARepository,
                logger = logger,
            )
    }

    @Test
    @Sql("classpath:join_ville_voeu.sql")
    fun `doit retourner à vide pour les communes inconnues et logguer un warning`() {
        // Given
        val communes =
            listOf(CommunesFavorites.SAINT_MALO, CommunesFavorites.MONTREUIL, CommunesFavorites.CAEN, CommunesFavorites.PARIS5EME)

        // When
        val result = communesAvecVoeuxAuxAlentoursBDDRepository.recupererVoeuxAutoursDeCommmune(communes)

        // Then
        val attendu =
            listOf(
                CommuneAvecIdsVoeuxAuxAlentours(
                    CommunesFavorites.SAINT_MALO,
                    distances =
                        listOf(
                            VoeuAvecDistance("ta60", 66),
                            VoeuAvecDistance("ta77", 66),
                            VoeuAvecDistance("ta480", 66),
                        ),
                ),
                CommuneAvecIdsVoeuxAuxAlentours(CommunesFavorites.MONTREUIL, distances = emptyList()),
                CommuneAvecIdsVoeuxAuxAlentours(
                    CommunesFavorites.CAEN,
                    distances =
                        listOf(
                            VoeuAvecDistance("ta33", 52),
                            VoeuAvecDistance("ta256", 52),
                        ),
                ),
                CommuneAvecIdsVoeuxAuxAlentours(CommunesFavorites.PARIS5EME, distances = emptyList()),
            )
        assertThat(result).isEqualTo(attendu)
        then(logger).should().warn(
            type = "COMMUNE_NON_PRESENTE_EN_BDD",
            message = "La commune Paris (75105) n'est pas présente dans la table ref_join_ville_voeu",
            parametres = mapOf("nomCommune" to "Paris", "codeInsee" to "75105"),
        )
    }
}
