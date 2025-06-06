package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.Specialite
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class BaccalaureatSpecialiteBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var baccalaureatJPARepository: BaccalaureatJPARepository

    @Autowired
    lateinit var baccalaureatSpecialiteJPARepository: BaccalaureatSpecialiteJPARepository

    @Autowired
    lateinit var specialiteJPARepository: SpecialiteJPARepository

    lateinit var baccalaureatSpecialiteBDDRepository: BaccalaureatSpecialiteBDDRepository

    @BeforeEach
    fun setup() {
        baccalaureatSpecialiteBDDRepository =
            BaccalaureatSpecialiteBDDRepository(
                baccalaureatJPARepository,
                baccalaureatSpecialiteJPARepository,
                specialiteJPARepository,
            )
    }

    @Nested
    inner class RecupererLesBaccalaureatsAvecLeursSpecialites {
        val speSI = Specialite(id = "4", label = "Sciences de l'ingénieur")
        val speBio = Specialite(id = "5", label = "Biologie/Ecologie")
        val speEcoHotelier = Specialite(id = "1006", label = "Economie et gestion hôtelière")
        val speSAE = Specialite(id = "1008", label = "Enseignement scientifique alimentation - environnement")
        val speRHCom = Specialite(id = "1009", label = "Ressources humaines et communication")
        val speDroitEco = Specialite(id = "1038", label = "Droit et Economie")
        val spePCMath = Specialite(id = "1040", label = "Physique-Chimie et Mathématiques")
        val speEPS = Specialite(id = "1095", label = "Éducation Physique, Pratiques Et Culture Sportives")

        @Test
        @Sql("classpath:baccalaureat_specialite.sql")
        fun `Doit retourner tous les baccalaureats avec leurs spécialités`() {
            // When
            val resultat = baccalaureatSpecialiteBDDRepository.recupererLesBaccalaureatsAvecLeursSpecialites()

            // Then
            val attendu =
                mapOf(
                    Baccalaureat(
                        id = "Professionnel",
                        nom = "Série Pro",
                        idExterne = "P",
                        idCarteParcoursup = "3",
                    ) to listOf(speSI, speBio, speEcoHotelier, speSAE, speRHCom, speDroitEco, speEPS),
                    Baccalaureat(
                        id = "Général",
                        nom = "Série Générale",
                        idExterne = "Générale",
                        idCarteParcoursup = "1",
                    ) to listOf(speSI, speBio, speDroitEco, spePCMath),
                    Baccalaureat(
                        id = "PA",
                        nom = "Bac Pro Agricole",
                        idExterne = "PA",
                        idCarteParcoursup = "3",
                    ) to emptyList(),
                )
            assertThat(resultat).isEqualTo(attendu)
        }
    }

    @Nested
    inner class RecupererLesIdsDesSpecialitesDUnBaccalaureat {
        @Test
        @Sql("classpath:baccalaureat_specialite.sql")
        fun `Doit retourner tous les ids des spécialitées d'un baccalaureat`() {
            // Given
            val idBaccalaureat = "Professionnel"

            // When
            val resultat = baccalaureatSpecialiteBDDRepository.recupererLesIdsDesSpecialitesDUnBaccalaureat(idBaccalaureat).toSet()

            // Then
            val attendu = setOf("4", "5", "1006", "1008", "1009", "1038", "1095")
            assertThat(resultat).isEqualTo(attendu)
        }

        @Test
        @Sql("classpath:baccalaureat_specialite.sql")
        fun `Si pas des spécialités pour un bac, doit retourner vide`() {
            // Given
            val idBaccalaureat = "PA"

            // When
            val resultat = baccalaureatSpecialiteBDDRepository.recupererLesIdsDesSpecialitesDUnBaccalaureat(idBaccalaureat)

            // Then
            assertThat(resultat).isEqualTo(emptyList<String>())
        }

        @Test
        @Sql("classpath:baccalaureat_specialite.sql")
        fun `Si le baccalaureat n'existe pas, doit retourner vide`() {
            // Given
            val idBaccalaureat = "baccalaureat_inconnu"

            // When
            val resultat = baccalaureatSpecialiteBDDRepository.recupererLesIdsDesSpecialitesDUnBaccalaureat(idBaccalaureat)

            // Then
            assertThat(resultat).isEqualTo(emptyList<String>())
        }
    }
}
