package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetIllegalStateErrorException
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupNotFoundException
import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.recherche.domain.entity.Formation
import fr.gouv.monprojetsup.recherche.domain.entity.FormationDetaillee
import fr.gouv.monprojetsup.recherche.domain.entity.Metier
import fr.gouv.monprojetsup.recherche.domain.entity.MetierDetaille
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
                    pointsAttendus = emptyList(),
                    formationsAssociees = listOf("fl0010", "fl0012"),
                    liens = emptyList(),
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
                                liens = emptyList(),
                            ),
                        ),
                ),
            )
        }

        @Test
        @Sql("classpath:formation_metier.sql")
        fun `Quand la formation est présente dans une formation associée, doit retourner la formation qui groupe`() {
            // Given
            val idFormation = "fl0010"

            // When
            val result = formationBDDRepository.recupererUneFormationAvecSesMetiers(idFormation)

            // Then
            assertThat(result).usingRecursiveComparison().isEqualTo(
                FormationDetaillee(
                    id = "fl0001",
                    nom = "CAP Fleuriste",
                    descriptifGeneral =
                        "Le CAP Fleuriste est un diplôme de niveau 3 qui permet d acquérir les compétences nécessaires " +
                            "pour exercer le métier de fleuriste. La formation dure 2 ans et est accessible après la classe de 3ème. " +
                            "Elle comprend des enseignements généraux (français, mathématiques, histoire-géographie, etc.) et des " +
                            "enseignements professionnels (botanique, art floral, techniques de vente, etc.). Le CAP Fleuriste permet " +
                            "d exercer le métier de fleuriste en boutique, en grande surface, en jardinerie ou " +
                            "en atelier de composition florale.",
                    descriptifAttendus =
                        "Il est attendu des candidats de démontrer une solide compréhension des techniques de base de " +
                            "la floristerie, y compris la composition florale, la reconnaissance des plantes et des fleurs, ainsi que " +
                            "les soins et l'entretien des végétaux.",
                    descriptifDiplome =
                        "Le Certificat d'Aptitude Professionnelle (CAP) est un diplôme national de niveau 3 du système " +
                            "éducatif français, qui atteste l'acquisition d'une qualification professionnelle dans un métier précis. " +
                            "Il est généralement obtenu après une formation de deux ans suivant la fin du collège et s'adresse " +
                            "principalement aux élèves souhaitant entrer rapidement dans la vie active.",
                    descriptifConseils =
                        "Nous vous conseillons de développer une sensibilité artistique et de rester informé des " +
                            "tendances actuelles en matière de design floral pour exceller dans ce domaine.",
                    pointsAttendus = emptyList(),
                    formationsAssociees = listOf("fl0010", "fl0012"),
                    liens = emptyList(),
                    metiers =
                        listOf(
                            MetierDetaille(
                                id = "MET001",
                                nom = "Fleuriste",
                                descriptif =
                                    "Le fleuriste est un artisan qui confectionne et vend des bouquets, des compositions " +
                                        "florales, des plantes et des accessoires de décoration. Il peut également " +
                                        "être amené à conseiller ses clients sur le choix des fleurs et des plantes " +
                                        "en fonction de l occasion et de leur budget. Le fleuriste peut travailler en " +
                                        "boutique, en grande surface, en jardinerie ou en atelier de composition florale.",
                                liens = emptyList(),
                            ),
                            MetierDetaille(
                                id = "MET002",
                                nom = "Fleuriste événementiel",
                                descriptif =
                                    "Le fleuriste événementiel est un artisan qui confectionne et vend des bouquets, des " +
                                        "compositions florales, des plantes et des accessoires de décoration pour des " +
                                        "événements particuliers (mariages, baptêmes, anniversaires, réceptions, etc.). " +
                                        "Il peut également être amené à conseiller ses clients sur le choix des fleurs et " +
                                        "des plantes en fonction de l occasion et de leur budget. Le fleuriste événementiel " +
                                        "peut travailler en boutique, en grande surface, en jardinerie ou en atelier de " +
                                        "composition florale.",
                                liens = emptyList(),
                            ),
                        ),
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
                    pointsAttendus = emptyList(),
                    formationsAssociees = listOf("fl0005"),
                    liens = emptyList(),
                    metiers = emptyList(),
                ),
            )
        }

        @Test
        @Sql("classpath:formation_metier.sql")
        fun `Quand la formations n'existe pas, doit throw une erreur`() {
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
        fun `Quand la formations est présente dans plusieurs formations associés, doit throw une erreur`() {
            // Given
            val idFormation = "fl0012"

            // When & Then
            assertThatThrownBy {
                formationBDDRepository.recupererUneFormationAvecSesMetiers(idFormation)
            }.isEqualTo(
                MonProjetIllegalStateErrorException(
                    code = "RECHERCHE_FORMATION",
                    msg = "La formation fl0012 existe plusieurs fois entre id et dans les formations équivalentes",
                ),
            )
        }

        @Test
        @Sql("classpath:formation_metier.sql")
        fun `Quand la formations est à la fois présente en tant que formation associée et entité, doit throw une erreur`() {
            // Given
            val idFormation = "fl0005"

            // When & Then
            assertThatThrownBy {
                formationBDDRepository.recupererUneFormationAvecSesMetiers(idFormation)
            }.isEqualTo(
                MonProjetIllegalStateErrorException(
                    code = "RECHERCHE_FORMATION",
                    msg = "La formation fl0005 existe plusieurs fois entre id et dans les formations équivalentes",
                ),
            )
        }
    }
}
