package fr.gouv.monprojetsup.recherche.infrastructure.dto

import fr.gouv.monprojetsup.recherche.domain.entity.ProfilEleve
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class ProfilDTOTest {
    companion object {
        private val profilEleveSeconde =
            ProfilEleve(
                id = "a08266d7-7eca-4198-a753-9e6b168c277f",
                classe = "seconde",
                bac = "Générale",
                dureeEtudesPrevue = "options_ouvertes",
                alternance = "pas_interesse",
                villesPreferees = listOf("Paris", "Marseille"),
                specialites = listOf("1056", "1054"),
                centresInterets = listOf("T_ROME_2092381917", "T_IDEO2_4812"),
                moyenneGenerale = 10.5f,
                metiersChoisis = listOf("MET_123", "MET_456"),
                formationsChoisies = listOf("fl1234", "fl5678"),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
            )
        private val profilDTOSeconde =
            ProfilDTO(
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
                classe = "seconde_sthr",
                bac = "P",
                dureeEtudesPrevue = "courte",
                alternance = "indifferent",
                villesPreferees = listOf("Lyon", "Caen"),
                specialites = listOf("1053", "1055"),
                centresInterets = emptyList(),
                moyenneGenerale = 19.5f,
                metiersChoisis = listOf("MET_001", "MET_004"),
                formationsChoisies = emptyList(),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
            )
        private val profilDTOSecondeSTHR =
            ProfilDTO(
                classe = "secSTHR",
                bac = "P",
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
                classe = "seconde_tmd",
                bac = "PA",
                dureeEtudesPrevue = "longue",
                alternance = "interesse",
                villesPreferees = emptyList(),
                specialites = emptyList(),
                centresInterets = listOf("T_IDEO2_4813", "T_ROME_2092381918"),
                moyenneGenerale = null,
                metiersChoisis = emptyList(),
                formationsChoisies = listOf("fl0001", "fl0005"),
                domainesInterets = emptyList(),
            )
        private val profilDTOSecondeTMD =
            ProfilDTO(
                classe = "secTMD",
                bac = "PA",
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
                classe = "premiere",
                bac = "S2TMD",
                dureeEtudesPrevue = "aucune_idee",
                alternance = "tres_interesse",
                villesPreferees = listOf("Lyon", "Paris"),
                specialites = listOf("1045"),
                centresInterets = emptyList(),
                moyenneGenerale = 4.9f,
                metiersChoisis = emptyList(),
                formationsChoisies = emptyList(),
                domainesInterets = emptyList(),
            )
        private val profilDTOPremiere =
            ProfilDTO(
                classe = "prem",
                bac = "S2TMD",
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
                classe = "terminale",
                bac = "NC",
                dureeEtudesPrevue = "options_ouvertes",
                alternance = "pas_interesse",
                villesPreferees = listOf("Paris", "Marseille"),
                specialites = listOf("1056", "1054"),
                centresInterets = null,
                moyenneGenerale = 10.5f,
                metiersChoisis = null,
                formationsChoisies = listOf("fl1234", "fl5678"),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
            )
        private val profilDTOTerminal =
            ProfilDTO(
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
                classe = null,
                bac = null,
                dureeEtudesPrevue = null,
                alternance = null,
                villesPreferees = null,
                specialites = null,
                centresInterets = null,
                moyenneGenerale = null,
                metiersChoisis = null,
                formationsChoisies = null,
                domainesInterets = null,
            )
        private val profilDTONull =
            ProfilDTO(
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
    fun `to - doit créer le ProfilDTO attendu`(
        entree: ProfilEleve,
        dtoAttendu: ProfilDTO,
    ) {
        // When
        val resultat = ProfilDTO(entree, listOf("SVT", "Maths"))

        // Then
        assertThat(resultat).usingRecursiveAssertion().isEqualTo(dtoAttendu)
    }
}
