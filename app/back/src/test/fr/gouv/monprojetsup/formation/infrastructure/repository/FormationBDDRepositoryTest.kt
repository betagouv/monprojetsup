package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.then
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class FormationBDDRepositoryTest : BDDRepositoryTest() {
    @Mock
    lateinit var logger: MonProjetSupLogger

    @Autowired
    lateinit var formationJPARepository: FormationJPARepository

    @Autowired
    lateinit var formationDetailleeJPARepository: FormationDetailleeJPARepository

    lateinit var formationBDDRepository: FormationBDDRepository

    private val formationFL0001 =
        Formation(
            id = "fl0001",
            nom = "CAP Fleuriste",
            descriptifGeneral =
                "Le CAP Fleuriste est un diplôme de niveau 3 qui permet d acquérir les " +
                    "compétences nécessaires pour exercer le métier de fleuriste. La formation dure 2 ans et " +
                    "est accessible après la classe de 3ème. Elle comprend des enseignements généraux " +
                    "(français, mathématiques, histoire-géographie, etc.) et des enseignements professionnels " +
                    "(botanique, art floral, techniques de vente, etc.). Le CAP Fleuriste permet d exercer le " +
                    "métier de fleuriste en boutique, en grande surface, en jardinerie ou en atelier de composition florale.",
            descriptifAttendus =
                "Il est attendu des candidats de démontrer une solide compréhension des techniques " +
                    "de base de la floristerie, y compris la composition florale, la reconnaissance des plantes et des " +
                    "fleurs, ainsi que les soins et l'entretien des végétaux.",
            descriptifDiplome =
                "Le Certificat d'Aptitude Professionnelle (CAP) est un diplôme national de niveau 3 du " +
                    "système éducatif français, qui atteste l'acquisition d'une qualification professionnelle dans un " +
                    "métier précis. Il est généralement obtenu après une formation de deux ans suivant la fin du collège " +
                    "et s'adresse principalement aux élèves souhaitant entrer rapidement dans la vie active.",
            descriptifConseils =
                "Nous vous conseillons de développer une sensibilité artistique et de rester informé des " +
                    "tendances actuelles en matière de design floral pour exceller dans ce domaine.",
            formationsAssociees = listOf("fl0010", "fl0012"),
            liens =
                listOf(
                    Lien(
                        nom = "Voir la fiche Onisep",
                        url = "https://www.onisep.fr/ressources/univers-formation/formations/cap-fleuriste",
                    ),
                    Lien(
                        nom = "Voir la fiche France Travail",
                        url = "https://candidat.francetravail.fr/formations/detail/3139962/true",
                    ),
                ),
            valeurCriteresAnalyseCandidature = listOf(0, 50, 0, 50, 0),
            apprentissage = false,
        )

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        formationBDDRepository =
            FormationBDDRepository(
                formationJPARepository,
                formationDetailleeJPARepository,
                logger,
            )
    }

    @Nested
    inner class RecupererLesFormationsAvecLeursMetiers {
        @Test
        @Sql("classpath:formation.sql")
        fun `Doit retourner les formations avec leurs métiers associés dans l'ordre demandé`() {
            // Given
            val idsFormations = listOf("fl0002", "fl0001", "fl0004")

            // When
            val result = formationBDDRepository.recupererLesFormationsAvecLeursMetiers(idsFormations)

            // Then
            val formationFL0002 =
                Formation(
                    id = "fl0002",
                    nom = "Bac pro Fleuriste",
                    descriptifGeneral =
                        "Le Bac pro Fleuriste est un diplôme de niveau 4 qui permet d acquérir les compétences nécessaires pour exercer " +
                            "le métier de fleuriste. La formation dure 3 ans et est accessible après la classe de 3ème. Elle " +
                            "comprend des enseignements généraux (français, mathématiques, histoire-géographie, etc.) et des " +
                            "enseignements professionnels (botanique, art floral, techniques de vente, etc.). Le Bac pro Fleuriste " +
                            "permet d exercer le métier de fleuriste en boutique, en grande surface, en jardinerie ou en atelier de " +
                            "composition florale.",
                    descriptifAttendus =
                        "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de la floristerie, y " +
                            "compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que les soins et " +
                            "l'entretien des végétaux.",
                    descriptifDiplome =
                        "Le Baccalauréat Professionnel, communément appelé Bac Pro, est un diplôme national de niveau 4 du système " +
                            "éducatif français. Il est conçu pour préparer les élèves à une insertion rapide et réussie dans le " +
                            "monde du travail tout en leur offrant la possibilité de poursuivre leurs études supérieures s'ils le " +
                            "souhaitent. Le Bac Pro se prépare généralement en trois ans après la classe de troisième, ou en deux " +
                            "ans après l'obtention d'un Certificat d'Aptitude Professionnelle (CAP).",
                    descriptifConseils = null,
                    formationsAssociees = listOf("fl0012"),
                    liens = emptyList(),
                    valeurCriteresAnalyseCandidature = listOf(13, 50, 12, 5, 15),
                    apprentissage = true,
                )
            val formationFL0004 =
                Formation(
                    id = "fl0004",
                    nom = "L1 - Histoire",
                    descriptifGeneral =
                        "La licence se décline en une quarantaine de mentions, allant du droit, à l'informatique, en passant par les " +
                            "arts. Organisée en parcours types, définis par chaque université, la licence permet d'acquérir une " +
                            "culture générale solide, des compétences disciplinaires, transversales et linguistiques.",
                    descriptifAttendus = null,
                    descriptifDiplome = null,
                    descriptifConseils = "",
                    formationsAssociees = listOf("fl0005"),
                    liens =
                        listOf(
                            Lien(
                                nom = "Voir la fiche Onisep",
                                url = "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/licence-mention-histoire",
                            ),
                        ),
                    valeurCriteresAnalyseCandidature = listOf(100, 0, 0, 0, 0),
                    apprentissage = false,
                )
            assertThat(result).usingRecursiveComparison().isEqualTo(listOf(formationFL0002, formationFL0001, formationFL0004))
        }

        @Test
        @Sql("classpath:formation.sql")
        fun `Quand les formations n'existent pas, doit retourner la map vide et les logguer`() {
            // Given
            val idsFormations = listOf("fl0007", "fl0008", "fl0009")

            // When
            val result = formationBDDRepository.recupererLesFormationsAvecLeursMetiers(idsFormations)

            // Then
            assertThat(result).isEqualTo(emptyList<Formation>())
            then(
                logger,
            ).should().error(
                type = "FORMATION_ABSENTE_BDD",
                message = "La formation fl0007 n'est pas présente en base",
                parametres = mapOf("idFormationAbsente" to "fl0007"),
            )
            then(
                logger,
            ).should().error(
                type = "FORMATION_ABSENTE_BDD",
                message = "La formation fl0008 n'est pas présente en base",
                parametres = mapOf("idFormationAbsente" to "fl0008"),
            )
            then(
                logger,
            ).should().error(
                type = "FORMATION_ABSENTE_BDD",
                message = "La formation fl0009 n'est pas présente en base",
                parametres = mapOf("idFormationAbsente" to "fl0009"),
            )
            then(logger).shouldHaveNoMoreInteractions()
        }
    }

    @Nested
    inner class RecupererUneFormationAvecSesMetiers {
        @Test
        @Sql("classpath:formation.sql")
        fun `Doit retourner la formation détaillée`() {
            // Given
            val idFormation = "fl0001"

            // When
            val result = formationBDDRepository.recupererUneFormationAvecSesMetiers(idFormation)

            // Then
            assertThat(result).usingRecursiveComparison().isEqualTo(formationFL0001)
        }

        @Test
        @Sql("classpath:formation.sql")
        fun `Quand la formation est présente dans une formation associée, doit throw une exception NOT FOUND`() {
            // Given
            val idFormation = "fl0010"

            // When & Then
            assertThatThrownBy {
                formationBDDRepository.recupererUneFormationAvecSesMetiers(idFormation)
            }.isEqualTo(
                MonProjetSupNotFoundException(
                    code = "RECHERCHE_FORMATION",
                    msg = "La formation fl0010 n'existe pas",
                ),
            )
        }

        @Test
        @Sql("classpath:formation.sql")
        fun `Quand la formation n'a pas de métiers associés, doit retourner sa liste vide`() {
            // Given
            val idFormation = "fl0004"

            // When
            val result = formationBDDRepository.recupererUneFormationAvecSesMetiers(idFormation)

            // Then
            assertThat(result).isEqualTo(
                Formation(
                    id = "fl0004",
                    nom = "L1 - Histoire",
                    descriptifGeneral =
                        "La licence se décline en une quarantaine de mentions, allant du droit, à l'informatique, " +
                            "en passant par les arts. Organisée en parcours types, définis par chaque université, la licence " +
                            "permet d'acquérir une culture générale solide, des compétences disciplinaires, " +
                            "transversales et linguistiques.",
                    descriptifAttendus = null,
                    descriptifDiplome = null,
                    descriptifConseils = "",
                    formationsAssociees = listOf("fl0005"),
                    liens =
                        listOf(
                            Lien(
                                nom = "Voir la fiche Onisep",
                                url = "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/licence-mention-histoire",
                            ),
                        ),
                    valeurCriteresAnalyseCandidature = listOf(100, 0, 0, 0, 0),
                    apprentissage = false,
                ),
            )
        }

        @Test
        @Sql("classpath:formation.sql")
        fun `Quand la formations n'existe pas, doit throw une erreur NOT FOUND`() {
            // Given
            val idFormation = "fl0007"

            // When & Then
            assertThatThrownBy {
                formationBDDRepository.recupererUneFormationAvecSesMetiers(idFormation)
            }.isEqualTo(
                MonProjetSupNotFoundException(
                    code = "RECHERCHE_FORMATION",
                    msg = "La formation fl0007 n'existe pas",
                ),
            )
        }

        @Test
        @Sql("classpath:formation.sql")
        fun `Quand la formations est présente dans plusieurs formations associés, doit throw une erreur NOT FOUND`() {
            // Given
            val idFormation = "fl0012"

            // When & Then
            assertThatThrownBy {
                formationBDDRepository.recupererUneFormationAvecSesMetiers(idFormation)
            }.isEqualTo(
                MonProjetSupNotFoundException(
                    code = "RECHERCHE_FORMATION",
                    msg = "La formation fl0012 n'existe pas",
                ),
            )
        }

        @Test
        @Sql("classpath:formation.sql")
        fun `Quand la formations est à la fois présente en tant que formation associée et entité, doit retourner la formation`() {
            // Given
            val idFormation = "fl0005"

            // When
            val result = formationBDDRepository.recupererUneFormationAvecSesMetiers(idFormation)

            // Then
            assertThat(result).usingRecursiveComparison().isEqualTo(
                Formation(
                    id = "fl0005",
                    nom = "L1 - Géographie",
                    descriptifGeneral =
                        "La licence de géographie est un cursus universitaire qui explore les interactions entre les " +
                            "environnements naturels et les sociétés humaines. Elle couvre des domaines variés comme la cartographie, " +
                            "la géopolitique, et l'aménagement du territoire. Les diplômés peuvent poursuivre des carrières dans " +
                            "l'urbanisme, l'environnement, la recherche, et l'enseignement.",
                    descriptifAttendus = null,
                    descriptifDiplome = null,
                    descriptifConseils = "",
                    formationsAssociees = emptyList(),
                    liens =
                        listOf(
                            Lien(
                                nom = "Voir la fiche Onisep",
                                url = "https://www.onisep.fr/ressources/univers-formation/formations/post-bac/licence-mention-histoire",
                            ),
                        ),
                    valeurCriteresAnalyseCandidature = listOf(100, 0, 0, 0, 0),
                    apprentissage = true,
                ),
            )
        }
    }

    @Nested
    inner class RecupererLesNomsDesFormations {
        @Test
        @Sql("classpath:formation.sql")
        fun `Doit retourner les noms des formations dans l'ordre demandé en ignorant celles inconnues et les loggant`() {
            // Given
            val idsFormations = listOf("fl0001", "idInconnu", "fl0003", "", "fl0002")

            // When
            val result = formationBDDRepository.recupererLesNomsDesFormations(idsFormations)

            // Then
            val attendu =
                listOf(
                    FormationCourte(id = "fl0001", nom = "CAP Fleuriste"),
                    FormationCourte(id = "fl0003", nom = "ENSA"),
                    FormationCourte(id = "fl0002", nom = "Bac pro Fleuriste"),
                )
            assertThat(result).usingRecursiveComparison().isEqualTo(attendu)
            then(
                logger,
            ).should().error(
                type = "FORMATION_ABSENTE_BDD",
                message = "La formation idInconnu n'est pas présente en base",
                parametres = mapOf("idFormationAbsente" to "idInconnu"),
            )
            then(
                logger,
            ).should().error(
                type = "FORMATION_ABSENTE_BDD",
                message = "La formation  n'est pas présente en base",
                parametres = mapOf("idFormationAbsente" to ""),
            )
            then(logger).shouldHaveNoMoreInteractions()
        }
    }

    @Nested
    inner class RecupererIdsFormationsInexistantes {
        @Test
        @Sql("classpath:formation.sql")
        fun `si toutes les formations existent, renvoyer liste vide`() {
            // Given
            val idsFormations = listOf("fl0001", "fl0003", "fl0002")

            // When
            val result = formationBDDRepository.recupererIdsFormationsInexistantes(idsFormations)

            // Then
            assertThat(result).isEqualTo(emptyList<String>())
        }

        @Test
        @Sql("classpath:formation.sql")
        fun `si une des formations n'existe pas, renvoyer la liste de ces formations`() {
            // Given
            val idsFormations = listOf("fl0001", "idInconnu", "fl0003", "fl0002")

            // When
            val result = formationBDDRepository.recupererIdsFormationsInexistantes(idsFormations)

            // Then
            assertThat(result).isEqualTo(listOf("idInconnu"))
        }
    }
}
