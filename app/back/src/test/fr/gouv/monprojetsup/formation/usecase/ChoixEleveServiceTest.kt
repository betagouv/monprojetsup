package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionEtExemplesMetiers
import fr.gouv.monprojetsup.logging.MonProjetSupLogger
import fr.gouv.monprojetsup.metier.domain.entity.MetierCourt
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie
import fr.gouv.monprojetsup.referentiel.domain.port.LabelsRepository
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
    lateinit var labelsRepository: LabelsRepository

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
            val centreInteret8 = InteretSousCategorie("ci8", "Créer quelque chose de mes mains", null, "\uD83E\uDE9B")
            val centreInteret14 = InteretSousCategorie("ci14", "Aider les autres", null, "\uD83E\uDEC2")
            val centreInteret17 = InteretSousCategorie("ci17", "Des sensations fortes", null, "\uD83D\uDD25")
            val domaine8 = Domaine("dom8", "Aménagement du territoire - urbanisme", null, "\uD83C\uDF04")
            val explications =
                mapOf(
                    "fl0001" to
                        ExplicationsSuggestionEtExemplesMetiers(
                            choix = listOf("ci17", "ci14", "MET.103"),
                        ),
                    "fl0002" to
                        ExplicationsSuggestionEtExemplesMetiers(
                            choix = listOf("idInconnu", "ci17"),
                        ),
                    "fl0004" to
                        ExplicationsSuggestionEtExemplesMetiers(
                            choix = listOf("dom8", "ci17", "MET.397", "ci8", "MET.103"),
                        ),
                    "fl0003" to ExplicationsSuggestionEtExemplesMetiers(),
                    "fl0005" to null,
                )
            given(
                labelsRepository.recupererLesLabels(
                    listOf("ci17", "ci14", "MET.103", "idInconnu", "dom8", "MET.397", "ci8"),
                ),
            ).willReturn(
                listOf(
                    centreInteret17.label,
                    centreInteret14.label,
                    metier103.label,
                    domaine8.label,
                    metier397.label,
                    centreInteret8.label,
                ),
            )

            // When
            val resultat = choixEleveService.recupererChoixEleve(explicationsParFormation = explications)

            // Then
            assertThat(resultat).usingRecursiveComparison().isEqualTo(
                mapOf(
                    "fl0001" to listOf(centreInteret17.label, centreInteret14.label) + metier103.label,
                    "fl0002" to listOf(centreInteret17.label),
                    "fl0004" to listOf(domaine8.label, centreInteret17.label, metier397.label, centreInteret8.label, metier103.label),
                    "fl0003" to emptyList(),
                    "fl0005" to emptyList(),
                ),
            )
            then(logger).should()
                .warn("ID_EXPLICATION_NON_RECONNU", "L'id idInconnu n'a pas de label")
        }
    }

    @Nested
    inner class RecupererChoixEleve {
        @Test
        fun `doit retourner les domaines, intérêts et métiers choisis et logguer les ids inconnus`() {
            // Given
            val metier397 = MetierCourt("MET.397", "analyste financier/ère")
            val metier103 = MetierCourt("MET.103", "ingénieur/e en expérimentation et production végétales")
            val centreInteret8 = InteretSousCategorie("ci8", "Créer quelque chose de mes mains", null, "\uD83E\uDE9B")
            val centreInteret17 = InteretSousCategorie("ci17", "Des sensations fortes", null, "\uD83D\uDD25")
            val domaine8 = Domaine("dom8", "Aménagement du territoire - urbanisme", null, "\uD83C\uDF04")
            val interetsDomainesMetiersChoisis = listOf("dom8", "ci17", "idInconnu", "MET.397", "ci8", "MET.103")
            val explications = ExplicationsSuggestionEtExemplesMetiers(choix = interetsDomainesMetiersChoisis)

            given(
                labelsRepository.recupererLesLabels(
                    interetsDomainesMetiersChoisis,
                ),
            ).willReturn(
                listOf(
                    domaine8.label,
                    centreInteret17.label,
                    centreInteret8.label,
                    metier397.label,
                    metier103.label,
                ),
            )

            // When
            val resultat = choixEleveService.recupererChoixEleve(explications = explications)

            // Then
            assertThat(resultat).usingRecursiveComparison().isEqualTo(
                listOf(domaine8.label) +
                    listOf(centreInteret17.label, centreInteret8.label) +
                    listOf(metier397.label, metier103.label),
            )
            then(logger).should()
                .warn("ID_EXPLICATION_NON_RECONNU", "L'id idInconnu n'a pas de label")
        }
    }
}
