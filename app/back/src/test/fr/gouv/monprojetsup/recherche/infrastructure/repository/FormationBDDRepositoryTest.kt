package fr.gouv.monprojetsup.recherche.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.recherche.domain.entity.Formation
import fr.gouv.monprojetsup.recherche.domain.entity.Metier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class FormationBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var formationBDDRepository: FormationBDDRepository

    @Test
    @Sql("classpath:formation_metier.sql")
    fun `recupererLesFormationsAvecLeursMetiers - doit retourner les formations avec leurs métiers associés`() {
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
    fun `recupererLesFormationsAvecLeursMetiers - quand la formation n'a pas de métiers associés, doit retourner la liste vide`() {
        // Given
        val idsFormations = listOf("fl0004")

        // When
        val result = formationBDDRepository.recupererLesFormationsAvecLeursMetiers(idsFormations)

        // Then
        assertThat(result).isEqualTo(mapOf(Formation(id = "fl0004", nom = "L1 - Histoire") to emptyList<Metier>()))
    }
}
