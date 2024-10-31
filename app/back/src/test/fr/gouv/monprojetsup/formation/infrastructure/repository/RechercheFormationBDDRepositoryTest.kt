package fr.gouv.monprojetsup.formation.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class RechercheFormationBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var entityManager: EntityManager

    lateinit var rechercheFormationBDDRepository: RechercheFormationBDDRepository

    @BeforeEach
    fun setup() {
        rechercheFormationBDDRepository = RechercheFormationBDDRepository(entityManager)
    }

    @Test
    @Sql("classpath:recherche_formation.sql")
    fun `Si fleur, renvoyer les formations d'horticulture`() {
        // Given
        val recherche = "fleur"

        // When
        val resultat = rechercheFormationBDDRepository.rechercherUneFormation(recherche)

        // Then
        val attendu =
            listOf(
                FormationCourte(id = "fl0001", nom = "CAP Fleuriste"),
                FormationCourte(id = "fl0002", nom = "Bac pro Fleuriste"),
            )
        val formationsCourteResultat = resultat.map { it.formation }
        assertThat(formationsCourteResultat).isEqualTo(attendu)
    }

    @Test
    @Sql("classpath:recherche_formation.sql")
    fun `Si flere, renvoyer les formations d'horticulture`() {
        // Given
        val recherche = "flere"

        // When
        val resultat = rechercheFormationBDDRepository.rechercherUneFormation(recherche)

        // Then
        val attendu =
            listOf(
                FormationCourte(id = "fl0001", nom = "CAP Fleuriste"),
                FormationCourte(id = "fl0002", nom = "Bac pro Fleuriste"),
            )
        val formationsCourteResultat = resultat.map { it.formation }
        assertThat(formationsCourteResultat).isEqualTo(attendu)
    }

    @Test
    @Sql("classpath:recherche_formation.sql")
    fun `Si L1, renvoyer les formations de license`() {
        // Given
        val recherche = "L1"

        // When
        val resultat = rechercheFormationBDDRepository.rechercherUneFormation(recherche)

        // Then
        val attendu =
            listOf(
                FormationCourte(id = "fl0004", nom = "L1 - Histoire"),
                FormationCourte(id = "fl0005", nom = "L1 - Géographie"),
                FormationCourte(id = "fl0006", nom = "L1 - Histoire de l'art"),
                FormationCourte(id = "fl0013", nom = "L1 - Sciences sanitaires et sociales -  Accès Santé (LAS)"),
            )
        val formationsCourteResultat = resultat.map { it.formation }
        assertThat(formationsCourteResultat).isEqualTo(attendu)
    }

    @Test
    @Sql("classpath:recherche_formation.sql")
    fun `Si histoire, renvoyer les formations avec celles d'histoire dans le label en premier puis celle en mot clé`() {
        // Given
        val recherche = "histoire"

        // When
        val resultat = rechercheFormationBDDRepository.rechercherUneFormation(recherche)

        // Then
        val attendu =
            listOf(
                FormationCourte(id = "fl0004", nom = "L1 - Histoire"),
                FormationCourte(id = "fl0006", nom = "L1 - Histoire de l'art"),
                FormationCourte(id = "fl0005", nom = "L1 - Géographie"),
            )
        val formationsCourteResultat = resultat.map { it.formation }
        assertThat(formationsCourteResultat).isEqualTo(attendu)
    }

    @Test
    @Sql("classpath:recherche_formation.sql")
    fun `Si histoare, renvoyer les formations avec celles d'histoire dans le label en premier puis celle en mot clé`() {
        // Given
        val recherche = "histoare"

        // When
        val resultat = rechercheFormationBDDRepository.rechercherUneFormation(recherche)

        // Then
        val attendu =
            listOf(
                FormationCourte(id = "fl0004", nom = "L1 - Histoire"),
                FormationCourte(id = "fl0006", nom = "L1 - Histoire de l'art"),
                FormationCourte(id = "fl0005", nom = "L1 - Géographie"),
            )
        val formationsCourteResultat = resultat.map { it.formation }
        assertThat(formationsCourteResultat).isEqualTo(attendu)
    }

    @Test
    @Sql("classpath:recherche_formation.sql")
    fun `Si ist, ne doit pas renvoyer les formations correspondant avec histoire, fleuriste et distribution`() {
        // Given
        val recherche = "ist"

        // When
        val resultat = rechercheFormationBDDRepository.rechercherUneFormation(recherche)

        // Then
        val nonAttendu =
            listOf(
                FormationCourte(id = "fl0001", nom = "CAP Fleuriste"),
                FormationCourte(id = "fl0002", nom = "Bac pro Fleuriste"),
                FormationCourte(id = "fl0004", nom = "L1 - Histoire"),
                FormationCourte(id = "fl0006", nom = "L1 - Histoire de l'art"),
                FormationCourte(id = "fl0007", nom = "DEUST - Technicien en qualité et distribution des produits alimentaires"),
                FormationCourte(id = "fl0005", nom = "L1 - Géographie"),
            )
        val formationsCourteResultat = resultat.map { it.formation }
        assertThat(nonAttendu.none { formationsCourteResultat.contains(it) }).isTrue()
    }

    @Test
    @Sql("classpath:recherche_formation.sql")
    fun `Si LAS, doit retourner les prefix et ne pas retourner les formations avec classe`() {
        // Given
        val recherche = "las"

        // When
        val resultat = rechercheFormationBDDRepository.rechercherUneFormation(recherche)

        // Then
        val attendu =
            listOf(
                FormationCourte(id = "fl0013", nom = "L1 - Sciences sanitaires et sociales -  Accès Santé (LAS)"),
                FormationCourte(id = "fl0017", nom = "BTS - Laser : technologies et sciences de la lumière"),
            )
        val formationsCourteResultat = resultat.map { it.formation }
        assertThat(formationsCourteResultat).isEqualTo(attendu)
        val nonAttendu =
            listOf(
                FormationCourte(id = "fl0012", nom = "Classe préparatoire aux études supérieures - Cinéma audiovisuel"),
            )
        assertThat(nonAttendu.none { formationsCourteResultat.contains(it) }).isTrue()
    }

    @Test
    @Sql("classpath:recherche_formation.sql")
    fun `Si géographie, doit retourner seulement ceux non obsolètes`() {
        // Given
        val recherche = "géographie"

        // When
        val resultat = rechercheFormationBDDRepository.rechercherUneFormation(recherche)

        // Then
        val attendu =
            listOf(
                FormationCourte(id = "fl0005", nom = "L1 - Géographie"),
            )
        val formationsCourteResultat = resultat.map { it.formation }
        assertThat(formationsCourteResultat).isEqualTo(attendu)
    }
}
