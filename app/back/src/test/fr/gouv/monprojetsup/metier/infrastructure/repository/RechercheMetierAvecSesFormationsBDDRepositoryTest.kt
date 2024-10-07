package fr.gouv.monprojetsup.metier.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class RechercheMetierAvecSesFormationsBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var entityManager: EntityManager

    lateinit var rechercheMetierBDDRepository: RechercheMetierBDDRepository

    @BeforeEach
    fun setup() {
        rechercheMetierBDDRepository = RechercheMetierBDDRepository(entityManager)
    }

    @Test
    @Sql("classpath:recherche_metier.sql")
    fun `Si prof, renvoyer les métiers de professeur et professionnel`() {
        // Given
        val recherche = "prof"

        // When
        val result = rechercheMetierBDDRepository.rechercherMetiersCourts(recherche)

        // Then
        val attendu =
            listOf(
                MetierCourt(id = "MET_557", nom = "professeur des écoles / professeure des écoles"),
                MetierCourt(id = "MET_522", nom = "Professeur / professeure de sport"),
                MetierCourt(id = "MET_431", nom = "professeur / professeure de lycée professionnel"),
                MetierCourt(id = "MET_890", nom = "professeur-documentaliste / professeure-documentaliste"),
                MetierCourt(id = "MET_293", nom = "professeur / professeure de musique ou de danse"),
                MetierCourt(id = "MET_51", nom = "professeur / professeure de collège et de lycée"),
                MetierCourt(id = "MET_85", nom = "professeur/e de maths ou de physique-chimie"),
                MetierCourt(id = "MET_7834", nom = "Artiste graffeur professionnel / artiste graffeuse professionnelle"),
                MetierCourt(id = "MET_423", nom = "professeur / professeure dans l'enseignement agricole"),
                MetierCourt(id = "MET_43", nom = "professeur/e d'éducation physique et sportive"),
                MetierCourt(id = "MET_342", nom = "conseiller / conseillère en insertion sociale et professionnelle"),
                MetierCourt(
                    id = "MET_634",
                    nom =
                        "psychologue de l'éducation nationale spécialité éducation, " +
                            "développement et conseil en orientation scolaire et professionnelle",
                ),
            )
        assertThat(result).isEqualTo(attendu)
    }

    @Test
    @Sql("classpath:recherche_metier.sql")
    fun `Si cheval, renvoyer les métiers associés`() {
        // Given
        val recherche = "cheval"

        // When
        val result = rechercheMetierBDDRepository.rechercherMetiersCourts(recherche)

        // Then
        val attendu =
            listOf(
                MetierCourt(id = "MET_454", nom = "garde à cheval"),
                MetierCourt(id = "MET_471", nom = "entraîneur / entraîneuse de chevaux"),
                MetierCourt(id = "MET_682", nom = "maréchal-ferrant / maréchale-ferrante"),
                MetierCourt(id = "MET_155", nom = "lad-jockey, lad-driver"),
                MetierCourt(id = "MET_345", nom = "moniteur/trice d'activités équestres"),
                MetierCourt(id = "MET_19", nom = "palefrenier / palefrenière"),
                MetierCourt(id = "MET_98", nom = "sellier/ère"),
            )
        assertThat(result).isEqualTo(attendu)
    }

    @Test
    @Sql("classpath:recherche_metier.sql")
    fun `Si sport, renvoyer les métiers associés`() {
        // Given
        val recherche = "sport"

        // When
        val result = rechercheMetierBDDRepository.rechercherMetiersCourts(recherche)

        // Then
        val attendu =
            listOf(
                MetierCourt(id = "MET_392", nom = "ergonome du sport"),
                MetierCourt(id = "MET_140", nom = "gestionnaire du sport"),
                MetierCourt(id = "MET_522", nom = "Professeur / professeure de sport"),
                MetierCourt(id = "MET_385", nom = "journaliste sportive"),
                MetierCourt(id = "MET_481", nom = "entraîneur/euse sportif/ve"),
                MetierCourt(id = "MET_783", nom = "éducateur sportif / éducatrice sportive"),
                MetierCourt(id = "MET_820", nom = "chercheur/euse en biologie du sport"),
                MetierCourt(id = "MET_375", nom = "masseur-kinésithérapeute (sport)"),
                MetierCourt(id = "MET_135", nom = "vendeur/euse d'articles de sport"),
                MetierCourt(id = "MET_43", nom = "professeur/e d'éducation physique et sportive"),
                MetierCourt(id = "MET_530", nom = "animateur/trice handisport"),
                MetierCourt(id = "MET_526", nom = "designer transports"),
                MetierCourt(id = "MET_723", nom = "conseiller sportif / conseillère sportive en salle de remise en forme"),
                MetierCourt(id = "MET_808", nom = "animateur / animatrice d'activités physiques et sportives pour tous"),
                MetierCourt(id = "MET_569", nom = "éducateur/trice sportif/ve des activités aquatiques et de la natation"),
                MetierCourt(id = "MET_832", nom = "pisteur / pisteuse secouriste"),
                MetierCourt(id = "MET_132", nom = "Médiateur social / médiatrice sociale"),
            )
        assertThat(result).isEqualTo(attendu)
    }

    @Test
    @Sql("classpath:recherche_metier.sql")
    fun `Si psy, renvoyer les métiers associés`() {
        // Given
        val recherche = "psy"

        // When
        val result = rechercheMetierBDDRepository.rechercherMetiersCourts(recherche)

        // Then
        val attendu =
            listOf(
                MetierCourt(id = "MET_731", nom = "psychiatre"),
                MetierCourt(id = "MET_871", nom = "psychologue"),
                MetierCourt(id = "MET_279", nom = "psychanalyste"),
                MetierCourt(id = "MET_1117", nom = "psycholinguiste"),
                MetierCourt(id = "MET_876", nom = "psychomotricien / psychomotricienne"),
                MetierCourt(id = "MET_1199", nom = "psychologue du travail"),
                MetierCourt(id = "MET_7857", nom = "pédopsychiatre"),
                MetierCourt(id = "MET_1198", nom = "psychologue clinicien/ne"),
                MetierCourt(
                    id = "MET_361",
                    nom = "psychologue de l'Éducation nationale spécialité éducation, développement et apprentissages",
                ),
                MetierCourt(
                    id = "MET_634",
                    nom =
                        "psychologue de l'éducation nationale spécialité éducation, " +
                            "développement et conseil en orientation scolaire et professionnelle",
                ),
                MetierCourt(id = "MET_7856", nom = "gérontopsychiatre"),
            )
        assertThat(result).isEqualTo(attendu)
    }
}
