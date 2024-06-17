package fr.gouv.monprojetsup.commun.infrastructure.repository

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.jdbc.JdbcTestUtils
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BDDRepositoryTest {
    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @AfterEach
    fun setUp() {
        JdbcTestUtils.deleteFromTables(
            jdbcTemplate,
            "join_metier_formation",
            "triplet_affectation",
            "interet",
            "interet_sous_categorie",
            "domaine",
            "moyenne_generale_admis",
            "repartition_admis",
            "specialite",
            "critere_analyse_candidature",
            "domaine_categorie",
            "interet_categorie",
            "metier",
            "formation",
            "baccalaureat",
        )
    }

    companion object {
        @JvmStatic
        private var postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:15-alpine")

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            postgres.start()
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            postgres.stop()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("flyway.url", postgres::getJdbcUrl)
            registry.add("flyway.user", postgres::getUsername)
            registry.add("flyway.password", postgres::getPassword)
        }
    }
}
