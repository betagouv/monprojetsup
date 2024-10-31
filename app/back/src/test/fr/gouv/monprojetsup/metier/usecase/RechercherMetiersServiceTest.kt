package fr.gouv.monprojetsup.metier.usecase

import fr.gouv.monprojetsup.commun.recherche.usecase.FiltrerRechercheBuilder
import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import fr.gouv.monprojetsup.metier.domain.entity.ResultatRechercheMetierCourt
import fr.gouv.monprojetsup.metier.domain.entity.ResultatRechercheMetierCourt.ScoreMot
import fr.gouv.monprojetsup.metier.domain.port.RechercheMetierRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RechercherMetiersServiceTest {
    @Mock
    private lateinit var rechercheMetierRepository: RechercheMetierRepository

    @Mock
    private lateinit var filtrerRechercheBuilder: FiltrerRechercheBuilder

    @InjectMocks
    private lateinit var rechercherMetiersService: RechercherMetiersService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `si une seul mot recherché, doit renvoyer les résultats en les triant par score`() {
        // Given
        val metiersPourPolicier =
            listOf(
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.191", "officier/ère de police"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = false,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 45,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.598", "ingénieur/e de la police technique et scientifique"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = false,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 45,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.786", "commissaire de police"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = false,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 45,
                        ),
                ),
            )

        given(rechercheMetierRepository.rechercherMetiersCourts(motRecherche = "policier")).willReturn(metiersPourPolicier)
        given(filtrerRechercheBuilder.filtrerMotsRecherches("policier", 2)).willReturn(listOf("policier"))

        // When
        val resultat =
            rechercherMetiersService.rechercherMetiersTriesParScores(
                recherche = "policier",
                tailleMinimumRecherche = 2,
            )

        // Then
        val attendu =
            listOf(
                MetierCourt(id = "MET.191", nom = "officier/ère de police"),
                MetierCourt(id = "MET.598", nom = "ingénieur/e de la police technique et scientifique"),
                MetierCourt(id = "MET.786", nom = "commissaire de police"),
            )
        assertThat(resultat).isEqualTo(attendu)
    }

    @Test
    fun `si plusieurs mots recherchés, doit renvoyer les résultats communs en les triant par score`() {
        // Given
        val recherche = "hôtesse de l'air"
        given(filtrerRechercheBuilder.filtrerMotsRecherches(recherche, 2)).willReturn(listOf("hôtesse", "air"))

        val metiersPourHotesse =
            listOf(
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.2", "directeur/trice d'hôtel"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = false,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 40,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.285", "hôte/esse d'accueil"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 44,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.400", "pilote de ligne"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 7,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.513", "personnel de hall d'hôtel de luxe"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = false,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 40,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.654", "maître/esse d'hôtel"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = false,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 40,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.7821", "agent/e d'opérations aériennes"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
            )
        given(rechercheMetierRepository.rechercherMetiersCourts(motRecherche = "hôtesse")).willReturn(metiersPourHotesse)
        val metiersPourAir =
            listOf(
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.223", "climatologue"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.225", "ingénieur/e analyste de l'air"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = true,
                            prefixDansLabel = true,
                            similariteLabelDecoupe = 100,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.25", "styliste "),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.400", "pilote de ligne"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.452", "régisseur/euse de spectacles"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.474", "ingénieur/e électronicien/ne des systèmes de la sécurité aérienne (IESSA)"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 8,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.50", "ouvrier/ère forestier/ère"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.61", "ingénieur/e en génie climatique"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.683", "officier/ère de l'armée de l'air"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = true,
                            prefixDansLabel = true,
                            similariteLabelDecoupe = 100,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.705", "bûcheron/ne"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.725", "ingénieur/e fluides, énergies, réseaux, environnement"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.747", "technicien/ne de mesure de la pollution "),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.7821", "agent/e d'opérations aériennes"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 11,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.793", "agent/e arboricole"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 11,
                        ),
                ),
            )
        given(rechercheMetierRepository.rechercherMetiersCourts(motRecherche = "air")).willReturn(metiersPourAir)

        // When
        val resultat =
            rechercherMetiersService.rechercherMetiersTriesParScores(
                recherche = recherche,
                tailleMinimumRecherche = 2,
            )

        // Then
        val attendu =
            listOf(
                MetierCourt("MET.7821", "agent/e d'opérations aériennes"),
                MetierCourt("MET.400", "pilote de ligne"),
            )
        assertThat(resultat).isEqualTo(attendu)
    }

    @Test
    fun `doit renvoyer les résultats en mettant en premier prefix et enfin par similarité label avec bonus dans descriptif`() {
        // Given
        val metiersPourClimat =
            listOf(
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.107", "chargé/e d'affaires en génie climatique"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = true,
                            similariteLabelDecoupe = 50,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.161", "architecte"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.164", "météorologiste"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.182", "ingénieur/e du BTP"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.223", "climatologue"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = true,
                            similariteLabelDecoupe = 43,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.475", "géochimiste"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.500", "ingénieur/e efficacité énergétique du bâtiment"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.61", "ingénieur/e en génie climatique"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = true,
                            similariteLabelDecoupe = 50,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.632", "expert/e bilan carbone"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 7,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.82", "juriste d'entreprise"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.83", "technicien/ne de maintenance en génie climatique"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = true,
                            similariteLabelDecoupe = 50,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.873", "technicien/ne thermicien/ne"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.902", "BIM manager"),
                    score =
                        ScoreMot(
                            motDansLeDescriptif = true,
                            labelContientMot = false,
                            prefixDansLabel = false,
                            similariteLabelDecoupe = 0,
                        ),
                ),
            )

        given(rechercheMetierRepository.rechercherMetiersCourts(motRecherche = "climat")).willReturn(metiersPourClimat)
        given(filtrerRechercheBuilder.filtrerMotsRecherches("climat", 2)).willReturn(listOf("climat"))

        // When
        val resultat =
            rechercherMetiersService.rechercherMetiersTriesParScores(
                recherche = "climat",
                tailleMinimumRecherche = 2,
            )

        // Then
        val attendu =
            listOf(
                MetierCourt(id = "MET.107", nom = "chargé/e d'affaires en génie climatique"),
                MetierCourt(id = "MET.223", nom = "climatologue"),
                MetierCourt(id = "MET.61", nom = "ingénieur/e en génie climatique"),
                MetierCourt(id = "MET.83", nom = "technicien/ne de maintenance en génie climatique"),
                MetierCourt(id = "MET.632", nom = "expert/e bilan carbone"),
                MetierCourt(id = "MET.161", nom = "architecte"),
                MetierCourt(id = "MET.164", nom = "météorologiste"),
                MetierCourt(id = "MET.182", nom = "ingénieur/e du BTP"),
                MetierCourt(id = "MET.475", nom = "géochimiste"),
                MetierCourt(id = "MET.500", nom = "ingénieur/e efficacité énergétique du bâtiment"),
                MetierCourt(id = "MET.82", nom = "juriste d'entreprise"),
                MetierCourt(id = "MET.873", nom = "technicien/ne thermicien/ne"),
                MetierCourt(id = "MET.902", nom = "BIM manager"),
            )
        assertThat(resultat).isEqualTo(attendu)
    }

    @Test
    fun `doit renvoyer les résultats en mettant en premier ceux avec le mot puis par similarité label avec bonus dans descriptif`() {
        // Given
        val metiersPourClimat =
            listOf(
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.589", "biologiste en environnement"),
                    score = ScoreMot(true, true, true, 100),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.202", "responsable assurance qualité"),
                    score = ScoreMot(true, false, false, 5),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.284", "chargé/e de valorisation de la recherche"),
                    score = ScoreMot(true, false, false, 0),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.413", "océanologue"),
                    score = ScoreMot(true, false, false, 9),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.424", "botaniste"),
                    score = ScoreMot(true, false, false, 23),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.48", "zoologiste"),
                    score = ScoreMot(true, false, false, 46),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.486", "responsable de plate-forme biotechnologique"),
                    score = ScoreMot(true, false, false, 27),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.699", "chercheur/euse en biologie"),
                    score = ScoreMot(true, false, false, 53),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.701", "microbiologiste"),
                    score = ScoreMot(true, false, false, 50),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.702", "histologiste"),
                    score = ScoreMot(true, false, false, 43),
                ),
                ResultatRechercheMetierCourt(
                    metier = MetierCourt("MET.771", "technicien/ne d''analyses biomédicales"),
                    score = ScoreMot(true, false, false, 14),
                ),
            )

        given(rechercheMetierRepository.rechercherMetiersCourts(motRecherche = "climat")).willReturn(metiersPourClimat)
        given(filtrerRechercheBuilder.filtrerMotsRecherches("climat", 2)).willReturn(listOf("climat"))

        // When
        val resultat =
            rechercherMetiersService.rechercherMetiersTriesParScores(
                recherche = "climat",
                tailleMinimumRecherche = 2,
            )

        // Then
        val attendu =
            listOf(
                MetierCourt(id = "MET.589", nom = "biologiste en environnement"),
                MetierCourt(id = "MET.699", nom = "chercheur/euse en biologie"),
                MetierCourt(id = "MET.701", nom = "microbiologiste"),
                MetierCourt(id = "MET.48", nom = "zoologiste"),
                MetierCourt(id = "MET.702", nom = "histologiste"),
                MetierCourt(id = "MET.486", nom = "responsable de plate-forme biotechnologique"),
                MetierCourt(id = "MET.424", nom = "botaniste"),
                MetierCourt(id = "MET.771", nom = "technicien/ne d''analyses biomédicales"),
                MetierCourt(id = "MET.413", nom = "océanologue"),
                MetierCourt(id = "MET.202", nom = "responsable assurance qualité"),
                MetierCourt(id = "MET.284", nom = "chargé/e de valorisation de la recherche"),
            )
        assertThat(resultat).isEqualTo(attendu)
    }
}
