package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.entity.FormationDetaillee
import fr.gouv.monprojetsup.formation.domain.entity.Lien
import fr.gouv.monprojetsup.formation.domain.entity.Metier
import fr.gouv.monprojetsup.formation.domain.entity.MetierDetaille
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class FormationBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var formationBDDRepository: FormationBDDRepository

    @Nested
    inner class RecupererLesFormationsAvecLeursMetiers {
        @Test
        @Sql("classpath:formation_metier.sql")
        fun `Doit retourner les formations avec leurs métiers associés`() {
            // Given
            val idsFormations = listOf("fl0001", "fl0002", "fl0003")

            // When
            val result = formationBDDRepository.recupererLesFormationsAvecLeursMetiers(idsFormations)

            // Then
            assertThat(result).isEqualTo(
                mapOf(
                    Formation(id = "fl0001", nom = "CAP Fleuriste") to
                        listOf(
                            Metier(id = "MET001", nom = "Fleuriste"),
                            Metier(id = "MET002", nom = "Fleuriste événementiel"),
                        ),
                    Formation(id = "fl0002", nom = "Bac pro Fleuriste") to
                        listOf(
                            Metier(id = "MET001", nom = "Fleuriste"),
                            Metier(id = "MET002", nom = "Fleuriste événementiel"),
                        ),
                    Formation(id = "fl0003", nom = "ENSA") to listOf(Metier(id = "MET003", nom = "Architecte")),
                ),
            )
        }

        @Test
        @Sql("classpath:formation_metier.sql")
        fun `Quand la formation n'a pas de métiers associés, doit retourner la liste vide`() {
            // Given
            val idsFormations = listOf("fl0004")

            // When
            val result = formationBDDRepository.recupererLesFormationsAvecLeursMetiers(idsFormations)

            // Then
            assertThat(result).isEqualTo(mapOf(Formation(id = "fl0004", nom = "L1 - Histoire") to emptyList<Metier>()))
        }

        @Test
        @Sql("classpath:formation_metier.sql")
        fun `Quand les formations n'existent pas, doit retourner la map vide`() {
            // Given
            val idsFormations = listOf("fl0007", "fl0008", "fl0009")

            // When
            val result = formationBDDRepository.recupererLesFormationsAvecLeursMetiers(idsFormations)

            // Then
            assertThat(result).isEqualTo(emptyMap<Formation, List<Metier>>())
        }
    }

    @Nested
    inner class RecupererUneFormationAvecSesMetiers {
        @Test
        @Sql("classpath:formation_metier.sql")
        fun `Doit retourner la formation détaillée`() {
            // Given
            val idFormation = "fl0001"

            // When
            val result = formationBDDRepository.recupererUneFormationAvecSesMetiers(idFormation)

            // Then
            assertThat(result).usingRecursiveComparison().isEqualTo(
                FormationDetaillee(
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
                    metiers =
                        listOf(
                            MetierDetaille(
                                id = "MET001",
                                nom = "Fleuriste",
                                descriptif =
                                    "Le fleuriste est un artisan qui confectionne et vend des bouquets, des compositions " +
                                        "florales, des plantes et des accessoires de décoration. Il peut également être amené à " +
                                        "conseiller ses clients sur le choix des fleurs et des plantes en fonction de l occasion " +
                                        "et de leur budget. Le fleuriste peut travailler en boutique, en grande surface, en " +
                                        "jardinerie ou en atelier de composition florale.",
                                liens = emptyList(),
                            ),
                            MetierDetaille(
                                id = "MET002",
                                nom = "Fleuriste événementiel",
                                descriptif =
                                    "Le fleuriste événementiel est un artisan qui confectionne et vend des bouquets, " +
                                        "des compositions florales, des plantes et des accessoires de décoration pour des " +
                                        "événements particuliers (mariages, baptêmes, anniversaires, réceptions, etc.). " +
                                        "Il peut également être amené à conseiller ses clients sur le choix des fleurs et des plantes " +
                                        "en fonction de l occasion et de leur budget. Le fleuriste événementiel peut travailler " +
                                        "en boutique, en grande surface, en jardinerie ou en atelier de composition florale.",
                                liens =
                                    listOf(
                                        Lien(
                                            nom = "Voir la fiche Onisep",
                                            url = "https://www.onisep.fr/ressources/univers-metier/metiers/fleuriste",
                                        ),
                                        Lien(
                                            nom = "Voir la fiche HelloWork",
                                            url = "https://www.hellowork.com/fr-fr/metiers/fleuriste.html",
                                        ),
                                    ),
                            ),
                        ),
                ),
            )
        }

        @Test
        @Sql("classpath:formation_metier.sql")
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
        @Sql("classpath:formation_metier.sql")
        fun `Quand la formation n'a pas de métiers associés, doit retourner sa liste vide`() {
            // Given
            val idFormation = "fl0004"

            // When
            val result = formationBDDRepository.recupererUneFormationAvecSesMetiers(idFormation)

            // Then
            assertThat(result).isEqualTo(
                FormationDetaillee(
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
                    metiers = emptyList(),
                    valeurCriteresAnalyseCandidature = listOf(100, 0, 0, 0, 0),
                ),
            )
        }

        @Test
        @Sql("classpath:formation_metier.sql")
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
        @Sql("classpath:formation_metier.sql")
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
        @Sql("classpath:formation_metier.sql")
        fun `Quand la formations est à la fois présente en tant que formation associée et entité, doit retourner la formation`() {
            // Given
            val idFormation = "fl0005"

            // When
            val result = formationBDDRepository.recupererUneFormationAvecSesMetiers(idFormation)

            // Then
            assertThat(result).usingRecursiveComparison().isEqualTo(
                FormationDetaillee(
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
                    metiers = emptyList(),
                ),
            )
        }
    }

    @Nested
    inner class RecupererLesNomsDesFormations {
        @Test
        @Sql("classpath:formation_metier.sql")
        fun `Doit retourner les noms des formations en ignorant celles inconnues`() {
            // Given
            val idsFormations = listOf("fl0001", "idInconnu", "fl0003", "", "fl0002")

            // When
            val result = formationBDDRepository.recupererLesNomsDesFormations(idsFormations)

            // Then
            val attendu =
                listOf(
                    Formation(id = "fl0001", nom = "CAP Fleuriste"),
                    Formation(id = "fl0002", nom = "Bac pro Fleuriste"),
                    Formation(id = "fl0003", nom = "ENSA"),
                )
            assertThat(result).usingRecursiveComparison().isEqualTo(attendu)
        }
    }
}
