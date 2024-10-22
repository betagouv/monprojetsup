package fr.gouv.monprojetsup.metier.usecase

import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import fr.gouv.monprojetsup.metier.domain.port.RechercheMetierRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.inOrder
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.MockitoAnnotations

class RechercherMetiersServiceTest {
    @Mock
    private lateinit var rechercheMetierRepository: RechercheMetierRepository

    @InjectMocks
    private lateinit var rechercherMetiersService: RechercherMetiersService

    private val rechercheLongue = "[pique-nique] (37°) 37$ *émmêlées} ? TesT@exàmple.com"

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `doit retourner la liste des formations sans doublons`() {
        // Given
        val metiersPourPique =
            listOf(
                MetierCourt(id = "MET_4321", nom = "Piqueur d'herbe"),
                MetierCourt(id = "MET_4322", nom = "Piquelogue"),
            )
        given(rechercheMetierRepository.rechercherMetiersCourts(motRecherche = "pique")).willReturn(metiersPourPique)

        val metiersPourNique =
            listOf(
                MetierCourt(id = "MET_4325", nom = "Technicien électronique"),
                MetierCourt(id = "MET_354", nom = "traducteur/trice technique"),
                MetierCourt(id = "MET_357", nom = "électromécanicien/ne en remontées mécaniques"),
                MetierCourt(id = "MET_348", nom = "chargé / chargée d'affaires en génie mécanique"),
                MetierCourt(id = "MET_300", nom = "attaché / attachée de recherche clinique"),
                MetierCourt(id = "MET_306", nom = "dessinateur / dessinatrice en construction mécanique"),
                MetierCourt(id = "MET_499", nom = "ingénieur/e méthodes mécaniques"),
                MetierCourt(id = "MET_401", nom = "ingénieur / ingénieure en mécanique"),
                MetierCourt(id = "MET_7830", nom = "Adjoint / adjointe technique des établissements d'enseignement"),
                MetierCourt(id = "MET_598", nom = "ingénieur / ingénieure de la police technique et scientifique"),
                MetierCourt(id = "MET_4326", nom = "Testeur technique mécanique"),
            )
        given(rechercheMetierRepository.rechercherMetiersCourts(motRecherche = "nique")).willReturn(metiersPourNique)

        val metiersPourEmmelees = emptyList<MetierCourt>()
        given(rechercheMetierRepository.rechercherMetiersCourts(motRecherche = "émmêlées")).willReturn(metiersPourEmmelees)

        val metiersPourTest =
            listOf(
                MetierCourt(id = "MET_84", nom = "testeur / testeuse en informatique"),
                MetierCourt(id = "MET_4326", nom = "Testeur technique mécanique"),
            )
        given(rechercheMetierRepository.rechercherMetiersCourts(motRecherche = "TesT")).willReturn(metiersPourTest)

        val metiersPourExample = emptyList<MetierCourt>()
        given(rechercheMetierRepository.rechercherMetiersCourts(motRecherche = "exàmple")).willReturn(metiersPourExample)

        // When
        val resultat =
            rechercherMetiersService.rechercherMetiersTriesParScores(
                recherche = rechercheLongue,
                tailleMinimumRecherche = 4,
            )

        // Then
        val attendu =
            listOf(
                MetierCourt(id = "MET_4326", nom = "Testeur technique mécanique"),
                MetierCourt(id = "MET_4321", nom = "Piqueur d'herbe"),
                MetierCourt(id = "MET_4322", nom = "Piquelogue"),
                MetierCourt(id = "MET_4325", nom = "Technicien électronique"),
                MetierCourt(id = "MET_354", nom = "traducteur/trice technique"),
                MetierCourt(id = "MET_357", nom = "électromécanicien/ne en remontées mécaniques"),
                MetierCourt(id = "MET_348", nom = "chargé / chargée d'affaires en génie mécanique"),
                MetierCourt(id = "MET_300", nom = "attaché / attachée de recherche clinique"),
                MetierCourt(id = "MET_306", nom = "dessinateur / dessinatrice en construction mécanique"),
                MetierCourt(id = "MET_499", nom = "ingénieur/e méthodes mécaniques"),
                MetierCourt(id = "MET_401", nom = "ingénieur / ingénieure en mécanique"),
                MetierCourt(id = "MET_7830", nom = "Adjoint / adjointe technique des établissements d'enseignement"),
                MetierCourt(id = "MET_598", nom = "ingénieur / ingénieure de la police technique et scientifique"),
                MetierCourt(id = "MET_84", nom = "testeur / testeuse en informatique"),
            )
        assertThat(resultat).isEqualTo(attendu)
    }

    @Test
    fun `ne doit pas appeler le repository pour les mots de moins de 4 caractères`() {
        // When
        rechercherMetiersService.rechercherMetiersTriesParScores(
            recherche = rechercheLongue,
            tailleMinimumRecherche = 4,
        )

        // Then
        val inOrder = inOrder(rechercheMetierRepository)
        inOrder.verify(rechercheMetierRepository).rechercherMetiersCourts("pique")
        inOrder.verify(rechercheMetierRepository).rechercherMetiersCourts("nique")
        inOrder.verify(rechercheMetierRepository).rechercherMetiersCourts("émmêlées")
        inOrder.verify(rechercheMetierRepository).rechercherMetiersCourts("TesT")
        inOrder.verify(rechercheMetierRepository).rechercherMetiersCourts("exàmple")
        verifyNoMoreInteractions(rechercheMetierRepository)
    }

    @Test
    fun `ne doit pas appeler le repository plusieurs fois pour le même mot`() {
        // When
        rechercherMetiersService.rechercherMetiersTriesParScores(
            recherche = rechercheLongue,
            tailleMinimumRecherche = 2,
        )

        // Then
        then(rechercheMetierRepository).should(times(1)).rechercherMetiersCourts(motRecherche = "37")
    }
}
