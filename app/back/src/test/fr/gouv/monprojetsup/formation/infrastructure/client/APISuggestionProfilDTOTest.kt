package fr.gouv.monprojetsup.formation.infrastructure.client

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.FormationFavorite
import fr.gouv.monprojetsup.eleve.entity.CommunesFavorites
import fr.gouv.monprojetsup.formation.infrastructure.dto.APISuggestionProfilDTO
import fr.gouv.monprojetsup.formation.infrastructure.dto.ChoixDTO
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class APISuggestionProfilDTOTest {
    @ParameterizedTest
    @MethodSource("testsProfileDTO")
    fun `doit créer le ProfilDTO attendu`(
        entree: ProfilEleve.AvecProfilExistant,
        dtoAttendu: APISuggestionProfilDTO,
    ) {
        // When
        val resultat = APISuggestionProfilDTO(profilEleve = entree)

        // Then
        assertThat(resultat).isEqualTo(dtoAttendu)
    }

    companion object {
        private val unProfil =
            ProfilEleve.AvecProfilExistant(
                id = "adcf627c-36dd-4df5-897b-159443a6d49c",
                situation = SituationAvanceeProjetSup.PROJET_PRECIS,
                classe = ChoixNiveau.TERMINALE,
                baccalaureat = "Générale",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.INDIFFERENT,
                alternance = ChoixAlternance.PAS_INTERESSE,
                communesFavorites = listOf(CommunesFavorites.PARIS15EME),
                specialites = listOf("mat1001", "mat1049"),
                centresInterets = listOf("chiffres_jongler", "aider_autres"),
                moyenneGenerale = 14f,
                metiersFavoris = listOf("MET_123", "MET_456"),
                formationsFavorites =
                    listOf(
                        FormationFavorite(
                            idFormation = "fl1234",
                            niveauAmbition = 1,
                            priseDeNote = null,
                        ),
                        FormationFavorite(
                            idFormation = "fl5678",
                            niveauAmbition = 3,
                            priseDeNote = "Ma formation préférée",
                        ),
                    ),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
                corbeilleFormations = listOf("fl0002"),
                compteParcoursupLie = false,
                voeuxFavoris = emptyList(),
            )
        private val unProfilDTO =
            APISuggestionProfilDTO(
                classe = "term",
                baccalaureat = "Générale",
                duree = "indiff",
                alternance = "D",
                preferencesGeographiques = listOf("75115"),
                specialites =
                    listOf(
                        "mat1001",
                        "mat1049",
                    ),
                interets =
                    listOf(
                        "chiffres_jongler",
                        "aider_autres",
                        "T_ITM_1054",
                        "T_ITM_1534",
                        "T_ITM_1248",
                        "T_ITM_1351",
                    ),
                moyenneGenerale = "28",
                choix =
                    listOf(
                        ChoixDTO.FavorisChoixDTO(id = "MET_123"),
                        ChoixDTO.FavorisChoixDTO(id = "MET_456"),
                        ChoixDTO.FavorisChoixDTO(id = "fl1234"),
                        ChoixDTO.FavorisChoixDTO(id = "fl5678"),
                        ChoixDTO.CorbeilleChoixDTO(id = "fl0002"),
                    ),
            )

        private val profilEleveSeconde =
            ProfilEleve.AvecProfilExistant(
                id = "a08266d7-7eca-4198-a753-9e6b168c277f",
                situation = SituationAvanceeProjetSup.PROJET_PRECIS,
                classe = ChoixNiveau.SECONDE,
                baccalaureat = "Générale",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.INDIFFERENT,
                alternance = ChoixAlternance.PAS_INTERESSE,
                communesFavorites = listOf(CommunesFavorites.PARIS15EME, CommunesFavorites.MARSEILLE),
                specialites = listOf("mat1001", "mat1049"),
                centresInterets = listOf("chiffres_jongler", "aider_autres"),
                moyenneGenerale = 10.5f,
                metiersFavoris = listOf("MET_123", "MET_456"),
                formationsFavorites =
                    listOf(
                        FormationFavorite(
                            idFormation = "fl1234",
                            niveauAmbition = 1,
                            priseDeNote = null,
                        ),
                        FormationFavorite(
                            idFormation = "fl5678",
                            niveauAmbition = 3,
                            priseDeNote = "Ma formation préférée",
                        ),
                    ),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
                corbeilleFormations = listOf("fl0001"),
                compteParcoursupLie = true,
                voeuxFavoris = emptyList(),
            )
        private val profilDTOSeconde =
            APISuggestionProfilDTO(
                classe = "sec",
                baccalaureat = "Générale",
                duree = "indiff",
                alternance = "D",
                preferencesGeographiques = listOf("75115", "13055"),
                specialites =
                    listOf(
                        "mat1001",
                        "mat1049",
                    ),
                interets =
                    listOf(
                        "chiffres_jongler",
                        "aider_autres",
                        "T_ITM_1054",
                        "T_ITM_1534",
                        "T_ITM_1248",
                        "T_ITM_1351",
                    ),
                moyenneGenerale = "21",
                choix =
                    listOf(
                        ChoixDTO.FavorisChoixDTO(id = "MET_123"),
                        ChoixDTO.FavorisChoixDTO(id = "MET_456"),
                        ChoixDTO.FavorisChoixDTO(id = "fl1234"),
                        ChoixDTO.FavorisChoixDTO(id = "fl5678"),
                        ChoixDTO.CorbeilleChoixDTO(id = "fl0001"),
                    ),
            )
        private val profilEleveSecondeSTHR =
            ProfilEleve.AvecProfilExistant(
                id = "915a5cf7-cf93-43f5-98db-39d6b4b0b8b7",
                situation = SituationAvanceeProjetSup.AUCUNE_IDEE,
                classe = ChoixNiveau.SECONDE,
                baccalaureat = "STHR",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.COURTE,
                alternance = ChoixAlternance.INDIFFERENT,
                communesFavorites = listOf(CommunesFavorites.LYON, CommunesFavorites.CAEN),
                specialites = listOf("1053", "1055"),
                centresInterets = emptyList(),
                moyenneGenerale = 19.5f,
                metiersFavoris = listOf("MET_001", "MET_004"),
                formationsFavorites = emptyList(),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
                corbeilleFormations = emptyList(),
                compteParcoursupLie = false,
                voeuxFavoris = emptyList(),
            )
        private val profilDTOSecondeSTHR =
            APISuggestionProfilDTO(
                classe = "sec",
                baccalaureat = "STHR",
                duree = "court",
                alternance = "C",
                preferencesGeographiques = listOf("69123", "14118"),
                specialites =
                    listOf(
                        "1053",
                        "1055",
                    ),
                interets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
                moyenneGenerale = "39",
                choix =
                    listOf(
                        ChoixDTO.FavorisChoixDTO(id = "MET_001"),
                        ChoixDTO.FavorisChoixDTO(id = "MET_004"),
                    ),
            )
        private val profilEleveSecondeTMD =
            ProfilEleve.AvecProfilExistant(
                id = "6d8aca7a-846c-4b42-b909-f1f8c8ab1e6a",
                situation = SituationAvanceeProjetSup.QUELQUES_PISTES,
                classe = ChoixNiveau.SECONDE,
                baccalaureat = "S2TMD",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.LONGUE,
                alternance = ChoixAlternance.INTERESSE,
                communesFavorites = emptyList(),
                specialites = emptyList(),
                centresInterets = listOf("rechercher_experiences", "chiffres_jongler"),
                moyenneGenerale = null,
                metiersFavoris = emptyList(),
                formationsFavorites =
                    listOf(
                        FormationFavorite(
                            idFormation = "fl0001",
                            niveauAmbition = 1,
                            priseDeNote = null,
                        ),
                        FormationFavorite(
                            idFormation = "fl0005",
                            niveauAmbition = 3,
                            priseDeNote = "Ma formation préférée",
                        ),
                    ),
                domainesInterets = emptyList(),
                corbeilleFormations = emptyList(),
                compteParcoursupLie = true,
                voeuxFavoris = emptyList(),
            )
        private val profilDTOSecondeTMD =
            APISuggestionProfilDTO(
                classe = "sec",
                baccalaureat = "S2TMD",
                duree = "long",
                alternance = "B",
                preferencesGeographiques = emptyList(),
                specialites = emptyList(),
                interets = listOf("rechercher_experiences", "chiffres_jongler"),
                moyenneGenerale = null,
                choix =
                    listOf(
                        ChoixDTO.FavorisChoixDTO(id = "fl0001"),
                        ChoixDTO.FavorisChoixDTO(id = "fl0005"),
                    ),
            )
        private val profilElevePremiere =
            ProfilEleve.AvecProfilExistant(
                id = "93de7b80-d43e-4357-90ea-28a44beed8f7",
                situation = SituationAvanceeProjetSup.AUCUNE_IDEE,
                classe = ChoixNiveau.PREMIERE,
                baccalaureat = "PA",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.AUCUNE_IDEE,
                alternance = ChoixAlternance.TRES_INTERESSE,
                communesFavorites = listOf(CommunesFavorites.LYON, CommunesFavorites.PARIS15EME),
                specialites = listOf("1045"),
                centresInterets = emptyList(),
                moyenneGenerale = -1.0f,
                metiersFavoris = emptyList(),
                formationsFavorites = emptyList(),
                domainesInterets = emptyList(),
                corbeilleFormations = emptyList(),
                compteParcoursupLie = false,
                voeuxFavoris = emptyList(),
            )
        private val profilDTOPremiere =
            APISuggestionProfilDTO(
                classe = "prem",
                baccalaureat = "PA",
                duree = "",
                alternance = "A",
                preferencesGeographiques = listOf("69123", "75115"),
                specialites = listOf("1045"),
                interets = emptyList(),
                moyenneGenerale = null,
                choix = emptyList(),
            )

        private val profilEleveTerminal =
            ProfilEleve.AvecProfilExistant(
                id = "de8c0c9c-a683-4f2f-9d1f-ccd5be89dd8c",
                situation = SituationAvanceeProjetSup.PROJET_PRECIS,
                classe = ChoixNiveau.TERMINALE,
                baccalaureat = "NC",
                dureeEtudesPrevue = ChoixDureeEtudesPrevue.INDIFFERENT,
                alternance = ChoixAlternance.PAS_INTERESSE,
                communesFavorites = listOf(CommunesFavorites.PARIS15EME, CommunesFavorites.MARSEILLE),
                specialites = listOf("mat1001", "mat1049"),
                centresInterets = null,
                moyenneGenerale = 4.9f,
                metiersFavoris = null,
                formationsFavorites =
                    listOf(
                        FormationFavorite(
                            idFormation = "fl1234",
                            niveauAmbition = 1,
                            priseDeNote = null,
                        ),
                        FormationFavorite(
                            idFormation = "fl5678",
                            niveauAmbition = 3,
                            priseDeNote = "Ma formation préférée",
                        ),
                    ),
                domainesInterets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
                corbeilleFormations = listOf("fl0012"),
                compteParcoursupLie = true,
                voeuxFavoris = emptyList(),
            )
        private val profilDTOTerminal =
            APISuggestionProfilDTO(
                classe = "term",
                baccalaureat = "",
                duree = "indiff",
                alternance = "D",
                preferencesGeographiques = listOf("75115", "13055"),
                specialites =
                    listOf(
                        "mat1001",
                        "mat1049",
                    ),
                interets = listOf("T_ITM_1054", "T_ITM_1534", "T_ITM_1248", "T_ITM_1351"),
                moyenneGenerale = "9",
                choix =
                    listOf(
                        ChoixDTO.FavorisChoixDTO(id = "fl1234"),
                        ChoixDTO.FavorisChoixDTO(id = "fl5678"),
                        ChoixDTO.CorbeilleChoixDTO(id = "fl0012"),
                    ),
            )
        private val profilEleveNull =
            ProfilEleve.AvecProfilExistant(
                id = "3e72892b-b6bb-4d5e-b349-81c2adfd292b",
                situation = SituationAvanceeProjetSup.QUELQUES_PISTES,
                classe = null,
                baccalaureat = null,
                dureeEtudesPrevue = null,
                alternance = null,
                communesFavorites = null,
                specialites = null,
                centresInterets = null,
                moyenneGenerale = null,
                metiersFavoris = null,
                formationsFavorites = null,
                domainesInterets = null,
                corbeilleFormations = emptyList(),
                compteParcoursupLie = false,
                voeuxFavoris = emptyList(),
            )
        private val profilDTONull =
            APISuggestionProfilDTO(
                classe = null,
                baccalaureat = null,
                duree = null,
                alternance = null,
                preferencesGeographiques = null,
                specialites = null,
                interets = emptyList(),
                moyenneGenerale = null,
                choix = emptyList(),
            )

        @JvmStatic
        fun testsProfileDTO(): Stream<Arguments> =
            Stream.of(
                Arguments.of(unProfil, unProfilDTO),
                Arguments.of(profilEleveSeconde, profilDTOSeconde),
                Arguments.of(profilEleveSecondeSTHR, profilDTOSecondeSTHR),
                Arguments.of(profilEleveSecondeTMD, profilDTOSecondeTMD),
                Arguments.of(profilElevePremiere, profilDTOPremiere),
                Arguments.of(profilEleveTerminal, profilDTOTerminal),
                Arguments.of(profilEleveNull, profilDTONull),
            )
    }
}
