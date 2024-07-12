package fr.gouv.monprojetsup.formation.infrastructure.dto

import fr.gouv.monprojetsup.eleve.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.entity.Communes
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class APISuggestionProfilDTOTest {
    companion object {
        private val profilEleveSeconde =
            ProfilEleve(
                id = "a08266d7-7eca-4198-a753-9e6b168c277f",
                classe = ChoixNiveau.SECONDE,
                bac = "Générale",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.INDIFFERENT,
                alternance = ChoixAlternance.PAS_INTERESSE,
                communesPreferees = listOf(Communes.PARIS, Communes.MARSEILLE),
                specialites = listOf("1056", "1054"),
                centresInterets = listOf("T_ROME_2092381917", "T_IDEO2_4812"),
                moyenneGenerale = 10.5f,
                metiersChoisis = listOf("MET_123", "MET_456"),
                formationsChoisies = listOf("fl1234", "fl5678"),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
            )
        private val profilDTOSeconde =
            APISuggestionProfilDTO(
                classe = "sec",
                bac = "Générale",
                duree = "indiff",
                alternance = "D",
                preferencesGeographiques = listOf("Paris", "Marseille"),
                specialites = listOf("SVT", "Maths"),
                interets =
                    listOf(
                        "T_ROME_2092381917",
                        "T_IDEO2_4812",
                        "T_ITM_1054",
                        "T_ITM_1534",
                        "T_ITM_1248",
                        "T_ITM_1351",
                    ),
                moyenneGenerale = "10.5",
                choix =
                    listOf(
                        SuggestionDTO("MET_123", 1, null),
                        SuggestionDTO("MET_456", 1, null),
                        SuggestionDTO("fl1234", 1, null),
                        SuggestionDTO("fl5678", 1, null),
                    ),
            )
        private val profilEleveSecondeSTHR =
            ProfilEleve(
                id = "915a5cf7-cf93-43f5-98db-39d6b4b0b8b7",
                classe = ChoixNiveau.SECONDE,
                bac = "STHR",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.COURTE,
                alternance = ChoixAlternance.INDIFFERENT,
                communesPreferees = listOf(Communes.LYON, Communes.CAEN),
                specialites = listOf("1053", "1055"),
                centresInterets = emptyList(),
                moyenneGenerale = 19.5f,
                metiersChoisis = listOf("MET_001", "MET_004"),
                formationsChoisies = emptyList(),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
            )
        private val profilDTOSecondeSTHR =
            APISuggestionProfilDTO(
                classe = "sec",
                bac = "STHR",
                duree = "court",
                alternance = "C",
                preferencesGeographiques = listOf("Lyon", "Caen"),
                specialites = listOf("SVT", "Maths"),
                interets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
                moyenneGenerale = "19.5",
                choix =
                    listOf(
                        SuggestionDTO("MET_001", 1, null),
                        SuggestionDTO("MET_004", 1, null),
                    ),
            )
        private val profilEleveSecondeTMD =
            ProfilEleve(
                id = "6d8aca7a-846c-4b42-b909-f1f8c8ab1e6a",
                classe = ChoixNiveau.SECONDE,
                bac = "S2TMD",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                alternance = ChoixAlternance.INTERESSE,
                communesPreferees = emptyList(),
                specialites = emptyList(),
                centresInterets = listOf("T_IDEO2_4813", "T_ROME_2092381918"),
                moyenneGenerale = null,
                metiersChoisis = emptyList(),
                formationsChoisies = listOf("fl0001", "fl0005"),
                domainesInterets = emptyList(),
            )
        private val profilDTOSecondeTMD =
            APISuggestionProfilDTO(
                classe = "sec",
                bac = "S2TMD",
                duree = "long",
                alternance = "B",
                preferencesGeographiques = emptyList(),
                specialites = listOf("SVT", "Maths"),
                interets = listOf("T_IDEO2_4813", "T_ROME_2092381918"),
                moyenneGenerale = null,
                choix =
                    listOf(
                        SuggestionDTO("fl0001", 1, null),
                        SuggestionDTO("fl0005", 1, null),
                    ),
            )
        private val profilElevePremiere =
            ProfilEleve(
                id = "93de7b80-d43e-4357-90ea-28a44beed8f7",
                classe = ChoixNiveau.PREMIERE,
                bac = "PA",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.AUCUNE_IDEE,
                alternance = ChoixAlternance.TRES_INTERESSE,
                communesPreferees = listOf(Communes.LYON, Communes.PARIS),
                specialites = listOf("1045"),
                centresInterets = emptyList(),
                moyenneGenerale = 4.9f,
                metiersChoisis = emptyList(),
                formationsChoisies = emptyList(),
                domainesInterets = emptyList(),
            )
        private val profilDTOPremiere =
            APISuggestionProfilDTO(
                classe = "prem",
                bac = "PA",
                duree = "",
                alternance = "A",
                preferencesGeographiques = listOf("Lyon", "Paris"),
                specialites = listOf("SVT", "Maths"),
                interets = emptyList(),
                moyenneGenerale = "4.9",
                choix = emptyList(),
            )

        private val profilEleveTerminal =
            ProfilEleve(
                id = "de8c0c9c-a683-4f2f-9d1f-ccd5be89dd8c",
                classe = ChoixNiveau.TERMINALE,
                bac = "NC",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.INDIFFERENT,
                alternance = ChoixAlternance.PAS_INTERESSE,
                communesPreferees = listOf(Communes.PARIS, Communes.MARSEILLE),
                specialites = listOf("1056", "1054"),
                centresInterets = null,
                moyenneGenerale = 10.5f,
                metiersChoisis = null,
                formationsChoisies = listOf("fl1234", "fl5678"),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
            )
        private val profilDTOTerminal =
            APISuggestionProfilDTO(
                classe = "term",
                bac = "",
                duree = "indiff",
                alternance = "D",
                preferencesGeographiques = listOf("Paris", "Marseille"),
                specialites = listOf("SVT", "Maths"),
                interets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
                moyenneGenerale = "10.5",
                choix =
                    listOf(
                        SuggestionDTO("fl1234", 1, null),
                        SuggestionDTO("fl5678", 1, null),
                    ),
            )
        private val profilEleveNull =
            ProfilEleve(
                id = "unknown",
                classe = ChoixNiveau.NON_RENSEIGNE,
                bac = null,
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.NON_RENSEIGNE,
                alternance = ChoixAlternance.NON_RENSEIGNE,
                communesPreferees = null,
                specialites = null,
                centresInterets = null,
                moyenneGenerale = null,
                metiersChoisis = null,
                formationsChoisies = null,
                domainesInterets = null,
            )
        private val profilDTONull =
            APISuggestionProfilDTO(
                classe = "",
                bac = null,
                duree = "",
                alternance = "",
                preferencesGeographiques = null,
                specialites = listOf("SVT", "Maths"),
                interets = emptyList(),
                moyenneGenerale = null,
                choix = emptyList(),
            )

        @JvmStatic
        fun testsProfileDTO(): Stream<Arguments> =
            Stream.of(
                Arguments.of(profilEleveSeconde, profilDTOSeconde),
                Arguments.of(profilEleveSecondeSTHR, profilDTOSecondeSTHR),
                Arguments.of(profilEleveSecondeTMD, profilDTOSecondeTMD),
                Arguments.of(profilElevePremiere, profilDTOPremiere),
                Arguments.of(profilEleveTerminal, profilDTOTerminal),
                Arguments.of(profilEleveNull, profilDTONull),
            )
    }

    @ParameterizedTest
    @MethodSource("testsProfileDTO")
    fun `doit créer le ProfilDTO attendu`(
        entree: ProfilEleve,
        dtoAttendu: APISuggestionProfilDTO,
    ) {
        // When
        val resultat = APISuggestionProfilDTO(entree, listOf("SVT", "Maths"))

        // Then
        assertThat(resultat).usingRecursiveAssertion().isEqualTo(dtoAttendu)
    }
}
