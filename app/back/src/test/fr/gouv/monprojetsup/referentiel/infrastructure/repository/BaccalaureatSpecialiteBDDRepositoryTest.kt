package fr.gouv.monprojetsup.referentiel.infrastructure.repository

import fr.gouv.monprojetsup.commun.infrastructure.repository.BDDRepositoryTest
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.Specialite
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.jdbc.Sql

class BaccalaureatSpecialiteBDDRepositoryTest : BDDRepositoryTest() {
    @Autowired
    lateinit var baccalaureatSpecialiteBDDRepository: BaccalaureatSpecialiteBDDRepository

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
                ) to listOf(speSI, speBio, speEcoHotelier, speSAE, speRHCom, speDroitEco, speEPS),
                Baccalaureat(
                    id = "Général",
                    nom = "Série Générale",
                    idExterne = "Générale",
                ) to listOf(speSI, speBio, speDroitEco, spePCMath),
                Baccalaureat(
                    id = "PA",
                    nom = "Bac Pro Agricole",
                    idExterne = "PA",
                ) to emptyList(),
            )
        assertThat(resultat).isEqualTo(attendu)
    }
}
