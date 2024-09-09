package fr.gouv.monprojetsup.metier.usecase

import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RecupererMetiersServiceTest {
    @Mock
    private lateinit var metierRepository: MetierRepository

    @InjectMocks
    private lateinit var recupererMetiersService: RecupererMetiersService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `doit retourner la liste des métiers`() {
        // Given
        val idsMetier = listOf("MET_356", "MET_355", "MET_358")
        val metiers =
            listOf(
                Metier(
                    id = "MET_356",
                    nom = "ingénieur / ingénieure matériaux",
                    descriptif =
                        "<p>L'ingénieur matériaux intervient de la conception à l'utilisation des matériaux. À la pointe de " +
                            "l'innovation, cet expert met ses compétences au service d'un bureau d'études, d'une entreprise " +
                            "industrielle ou d'un organisme de recherche.</p>",
                    liens =
                        listOf(
                            Lien(
                                nom = "Management et ingénierie études, recherche et développement industriel",
                                url = "https://candidat.pole-emploi.fr/marche-du-travail/fichemetierrome?codeRome=H1206",
                            ),
                            Lien(
                                nom = "ingénieur / ingénieure matériaux",
                                url = "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.356",
                            ),
                        ),
                    formations =
                        listOf(
                            FormationCourte(id = "fl1", nom = "CPGE MPSI"),
                            FormationCourte(id = "fl7", nom = "BUT Informatique"),
                        ),
                ),
                Metier(
                    id = "MET_355",
                    nom = "responsable de laboratoire de contrôle en chimie",
                    descriptif =
                        "<p> Dernière étape avant la mise sur le marché d'un produit issu de l'industrie de la chimie : son " +
                            "contrôle. Sous la direction du responsable de laboratoire de contrôle, des tests sont effectués pour " +
                            "évaluer sa qualité et sa conformité aux normes. </p>",
                    liens = emptyList(),
                    formations = emptyList(),
                ),
                Metier(
                    id = "MET_358",
                    nom = "pilote d'hélicoptère",
                    descriptif =
                        "<p>Le pilote d'hélicoptère, militaire ou civil, participe à des missions de combat ou de secours. Il " +
                            "est capable de se poser n'importe où et d'évoluer en montagne. Sang-froid et ténacité sont nécessaires " +
                            "pour exercer ce métier.</p>",
                    liens =
                        listOf(
                            Lien(
                                nom = "pilote d'hélicoptère",
                                url = "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.358",
                            ),
                            Lien(
                                nom = "Pilotage et navigation technique aérienne",
                                url = "https://candidat.pole-emploi.fr/marche-du-travail/fichemetierrome?codeRome=N2102",
                            ),
                        ),
                    formations =
                        listOf(
                            FormationCourte(id = "fl3", nom = "CAP Pâtisserie"),
                            FormationCourte(id = "fl1000", nom = "BPJEPS"),
                            FormationCourte(id = "fl17", nom = "L1 - Mathématique"),
                        ),
                ),
            )
        given(metierRepository.recupererLesMetiersDetailles(idsMetier)).willReturn(metiers)

        // When
        val resultat = recupererMetiersService.recupererMetiers(idsMetier)

        // Then
        assertThat(resultat).isEqualTo(metiers)
    }
}
