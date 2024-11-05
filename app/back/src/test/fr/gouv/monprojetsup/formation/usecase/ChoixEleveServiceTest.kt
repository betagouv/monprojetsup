package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import fr.gouv.monprojetsup.metier.domain.port.MetierRepository
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie
import fr.gouv.monprojetsup.referentiel.domain.port.DomaineRepository
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ChoixEleveServiceTest {
    @Mock
    lateinit var domaineRepository: DomaineRepository

    @Mock
    lateinit var metierRepository: MetierRepository

    @Mock
    lateinit var interetRepository: InteretRepository

    @Mock
    lateinit var logger: MonProjetSupLogger

    @InjectMocks
    lateinit var choixEleveService: ChoixEleveService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Nested
    inner class RecupererChoixEleveParFormation {
        @Test
        fun `doit retourner les domaines, intérêts et métiers choisis et logguer les ids inconnus`() {
            // Given
            val metier397 = MetierCourt("MET.397", "analyste financier/ère")
            val metier103 = MetierCourt("MET.103", "ingénieur/e en expérimentation et production végétales")
            val centreInteret8 = InteretSousCategorie("ci8", "Créer quelque chose de mes mains", "\uD83E\uDE9B")
            val centreInteret14 = InteretSousCategorie("ci14", "Aider les autres", "\uD83E\uDEC2")
            val centreInteret17 = InteretSousCategorie("ci17", "Des sensations fortes", "\uD83D\uDD25")
            val domaine8 = Domaine("dom8", "Aménagement du territoire - urbanisme", "\uD83C\uDF04")
            val explications =
                mapOf(
                    "fl0001" to
                        ExplicationsSuggestionEtExemplesMetiers(
                            interetsDomainesMetiersChoisis = listOf("ci17", "ci14", "MET.103"),
                        ),
                    "fl0002" to
                        ExplicationsSuggestionEtExemplesMetiers(
                            interetsDomainesMetiersChoisis = listOf("idInconnu", "ci17"),
                        ),
                    "fl0004" to
                        ExplicationsSuggestionEtExemplesMetiers(
                            interetsDomainesMetiersChoisis = listOf("dom8", "ci17", "MET.397", "ci8", "MET.103"),
                        ),
                    "fl0003" to ExplicationsSuggestionEtExemplesMetiers(),
                    "fl0005" to null,
                )
            val interetsDomainesMetiersChoisis = listOf("ci17", "ci14", "MET.103", "idInconnu", "dom8", "MET.397", "ci8")
            given(domaineRepository.recupererLesDomaines(interetsDomainesMetiersChoisis)).willReturn(listOf(domaine8))
            given(interetRepository.recupererLesSousCategories(interetsDomainesMetiersChoisis)).willReturn(
                listOf(
                    centreInteret8,
                    centreInteret14,
                    centreInteret17,
                ),
            )
            given(metierRepository.recupererLesMetiersCourts(interetsDomainesMetiersChoisis)).willReturn(listOf(metier103, metier397))

            // When
            val resultat = choixEleveService.recupererChoixEleve(explicationsParFormation = explications)

            // Then
            assertThat(resultat).usingRecursiveComparison().isEqualTo(
                mapOf(
                    "fl0001" to
                        ExplicationsSuggestionDetaillees.ChoixEleve(
                            interetsChoisis = listOf(centreInteret17, centreInteret14),
                            metiersChoisis = listOf(metier103),
                        ),
                    "fl0002" to
                        ExplicationsSuggestionDetaillees.ChoixEleve(
                            interetsChoisis = listOf(centreInteret17),
                        ),
                    "fl0004" to
                        ExplicationsSuggestionDetaillees.ChoixEleve(
                            domainesChoisis = listOf(domaine8),
                            interetsChoisis = listOf(centreInteret17, centreInteret8),
                            metiersChoisis = listOf(metier397, metier103),
                        ),
                    "fl0003" to ExplicationsSuggestionDetaillees.ChoixEleve(),
                    "fl0005" to ExplicationsSuggestionDetaillees.ChoixEleve(),
                ),
            )
            then(logger).should()
                .warn("ID_EXPLICATION_NON_RECONNU", "L'id idInconnu n'est ni un métier, ni un domaine, ni un centre d'intérêt")
        }
    }

    @Nested
    inner class RecupererChoixEleve {
        @Test
        fun `doit retourner les domaines, intérêts et métiers choisis et logguer les ids inconnus`() {
            // Given
            val metier397 = MetierCourt("MET.397", "analyste financier/ère")
            val metier103 = MetierCourt("MET.103", "ingénieur/e en expérimentation et production végétales")
            val centreInteret8 = InteretSousCategorie("ci8", "Créer quelque chose de mes mains", "\uD83E\uDE9B")
            val centreInteret17 = InteretSousCategorie("ci17", "Des sensations fortes", "\uD83D\uDD25")
            val domaine8 = Domaine("dom8", "Aménagement du territoire - urbanisme", "\uD83C\uDF04")
            val interetsDomainesMetiersChoisis = listOf("dom8", "ci17", "idInconnu", "MET.397", "ci8", "MET.103")
            val explications = ExplicationsSuggestionEtExemplesMetiers(interetsDomainesMetiersChoisis = interetsDomainesMetiersChoisis)
            given(domaineRepository.recupererLesDomaines(interetsDomainesMetiersChoisis)).willReturn(listOf(domaine8))
            given(interetRepository.recupererLesSousCategories(interetsDomainesMetiersChoisis)).willReturn(
                listOf(
                    centreInteret17,
                    centreInteret8,
                ),
            )
            given(metierRepository.recupererLesMetiersCourts(interetsDomainesMetiersChoisis)).willReturn(listOf(metier103, metier397))

            // When
            val resultat = choixEleveService.recupererChoixEleve(explications = explications)

            // Then
            assertThat(resultat).usingRecursiveComparison().isEqualTo(
                ExplicationsSuggestionDetaillees.ChoixEleve(
                    interetsChoisis = listOf(centreInteret17, centreInteret8),
                    domainesChoisis = listOf(domaine8),
                    metiersChoisis = listOf(metier103, metier397),
                ),
            )
            then(logger).should()
                .warn("ID_EXPLICATION_NON_RECONNU", "L'id idInconnu n'est ni un métier, ni un domaine, ni un centre d'intérêt")
        }
    }
}
