package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecVoeuxAuxAlentours
import fr.gouv.monprojetsup.formation.domain.entity.CommuneAvecVoeuxAuxAlentours.VoeuAvecDistance
import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil
import fr.gouv.monprojetsup.formation.domain.entity.SuggestionsPourUnProfil.FormationAvecSonAffinite
import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.entity.Communes.CAEN
import fr.gouv.monprojetsup.formation.entity.Communes.LYON
import fr.gouv.monprojetsup.formation.entity.Communes.MARSEILLE
import fr.gouv.monprojetsup.formation.entity.Communes.PARIS15EME
import fr.gouv.monprojetsup.formation.entity.Communes.PARIS5EME
import fr.gouv.monprojetsup.formation.entity.Communes.SAINT_MALO
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class RecupererFormationsServiceTest {
    @Mock
    lateinit var formationRepository: FormationRepository

    @Mock
    lateinit var recupererTripletAffectationDUneFormationService: RecupererTripletAffectationDUneFormationService

    @Mock
    lateinit var recupererTripletAffectationDesCommunesFavoritesService: RecupererTripletAffectationDesCommunesFavoritesService

    @Mock
    lateinit var critereAnalyseCandidatureService: CritereAnalyseCandidatureService

    @Mock
    lateinit var recupererExplicationsEtExemplesMetiersPourFormationService: RecupererExplicationsEtExemplesMetiersPourFormationService

    @Mock
    lateinit var statistiquesDesAdmisPourFormationsService: StatistiquesDesAdmisPourFormationsService

    @Mock
    lateinit var metiersTriesParProfilBuilder: MetiersTriesParProfilBuilder

    @Mock
    lateinit var calculDuTauxDAffiniteBuilder: CalculDuTauxDAffiniteBuilder

    @InjectMocks
    lateinit var recupererFormationsService: RecupererFormationsService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `doit renvoyers les fiches formations pour un profil`() {
        // Given
        val profilEleve = mock(ProfilEleve.Identifie::class.java)
        given(profilEleve.baccalaureat).willReturn("Général")
        given(profilEleve.classe).willReturn(ChoixNiveau.TERMINALE)
        given(profilEleve.communesFavorites).willReturn(listOf(SAINT_MALO))
        val formationAvecSonAffinite1 = mock(FormationAvecSonAffinite::class.java)
        val formationAvecSonAffinite2 = mock(FormationAvecSonAffinite::class.java)
        val formationAvecSonAffinite3 = mock(FormationAvecSonAffinite::class.java)
        val formationsAvecAffinites = listOf(formationAvecSonAffinite1, formationAvecSonAffinite2, formationAvecSonAffinite3)
        val affinitesFormationEtMetier = mock(SuggestionsPourUnProfil::class.java)
        val metier234 = mock(Metier::class.java)
        val metier123 = mock(Metier::class.java)
        val metier534 = mock(Metier::class.java)
        val metiersTriesParAffinites = listOf("MET_234", "MET_123", "MET_534")
        given(affinitesFormationEtMetier.formations).willReturn(formationsAvecAffinites)
        given(affinitesFormationEtMetier.metiersTriesParAffinites).willReturn(metiersTriesParAffinites)
        val idsFormations = listOf("fl0001", "fl0002", "fl0003")
        val lien1 = mock(Lien::class.java)
        val lien2 = mock(Lien::class.java)
        val formations =
            listOf(
                Formation(
                    id = "fl0001",
                    nom = "L1 - Mathématique",
                    descriptifGeneral = "Descriptif general fl0001",
                    descriptifAttendus = "Descriptif attendus fl0001",
                    descriptifDiplome = "Descriptif diplome fl0001",
                    descriptifConseils = "Descriptif conseils fl0001",
                    formationsAssociees = listOf("fl0004", "fl0003"),
                    liens = listOf(lien1, lien2),
                    valeurCriteresAnalyseCandidature = listOf(0, 6, 14, 68, 12),
                ),
                Formation(
                    id = "fl0003",
                    nom = "CAP Patisserie",
                    descriptifGeneral = "Descriptif general fl0003",
                    descriptifAttendus = "Descriptif attendus fl0003",
                    descriptifDiplome = "Descriptif diplome fl0003",
                    descriptifConseils = "Descriptif conseils fl0003",
                    formationsAssociees = emptyList(),
                    liens = emptyList(),
                    valeurCriteresAnalyseCandidature = listOf(10, 0, 40, 20, 30),
                ),
            )
        given(formationRepository.recupererLesFormationsAvecLeursMetiers(idsFormations)).willReturn(formations)
        val critereAnalyseCandidatureFL0001 = mock(CritereAnalyseCandidature::class.java)
        val critere2AnalyseCandidatureFL0001 = mock(CritereAnalyseCandidature::class.java)
        val critereAnalyseCandidatureFL0003 = mock(CritereAnalyseCandidature::class.java)
        val criteres =
            mapOf(
                "fl0001" to listOf(critereAnalyseCandidatureFL0001, critere2AnalyseCandidatureFL0001),
                "fl0003" to listOf(critereAnalyseCandidatureFL0003),
            )
        given(critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formations)).willReturn(
            criteres,
        )
        val statistiqueDesAdmisFL0001 = mock(StatistiquesDesAdmis::class.java)
        val statistiqueDesAdmisFL0003 = mock(StatistiquesDesAdmis::class.java)
        val statistiquesDesAdmis =
            mapOf(
                "fl0001" to statistiqueDesAdmisFL0001,
                "fl0003" to statistiqueDesAdmisFL0003,
            )
        given(
            statistiquesDesAdmisPourFormationsService.recupererStatistiquesAdmisDeFormations(
                idBaccalaureat = "Général",
                idsFormations = listOf("fl0001", "fl0003"),
                classe = ChoixNiveau.TERMINALE,
            ),
        ).willReturn(statistiquesDesAdmis)
        val explicationsFL0001 = mock(ExplicationsSuggestionDetaillees::class.java)
        val explicationsFL0003 = mock(ExplicationsSuggestionDetaillees::class.java)

        val exemplesMetiersFL0001 = listOf(metier534, metier123)
        val exemplesMetiersFL0003 = listOf(metier534, metier234)
        val explications =
            mapOf("fl0001" to Pair(explicationsFL0001, exemplesMetiersFL0001), "fl0003" to Pair(explicationsFL0003, exemplesMetiersFL0003))
        given(
            recupererExplicationsEtExemplesMetiersPourFormationService.recupererExplicationsEtExemplesDeMetiers(
                profilEleve,
                listOf("fl0001", "fl0003"),
            ),
        ).willReturn(explications)
        val tripletFormationFL0001 =
            listOf(
                TripletAffectation(id = "ta1", nom = "Nom du ta1", commune = PARIS15EME),
                TripletAffectation(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO),
                TripletAffectation(id = "ta6", nom = "Nom du ta6", commune = MARSEILLE),
            )
        val tripletFormationFL0003 =
            listOf(
                TripletAffectation(id = "ta10", nom = "Nom du ta10", commune = LYON),
                TripletAffectation(id = "ta3", nom = "Nom du ta3", commune = PARIS5EME),
                TripletAffectation(id = "ta11", nom = "Nom du ta11", commune = LYON),
                TripletAffectation(id = "ta32", nom = "Nom du ta32", commune = CAEN),
                TripletAffectation(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO),
                TripletAffectation(id = "ta7", nom = "Nom du ta7", commune = MARSEILLE),
            )
        val tripletsAffectationsDesFormations = mapOf("fl0001" to tripletFormationFL0001, "fl0003" to tripletFormationFL0003)
        given(
            recupererTripletAffectationDUneFormationService.recupererTripletAffectationTriesParAffinites(
                idsFormations = listOf("fl0001", "fl0003"),
                profilEleve = profilEleve,
            ),
        ).willReturn(tripletsAffectationsDesFormations)

        val tripletsAffectationParCommunesFavoritesFL0001 =
            listOf(
                CommuneAvecVoeuxAuxAlentours(
                    commune = SAINT_MALO,
                    distances =
                        listOf(
                            VoeuAvecDistance(voeu = TripletAffectation(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO), km = 0),
                        ),
                ),
            )
        val tripletsAffectationParCommunesFavoritesFL0003 =
            listOf(
                CommuneAvecVoeuxAuxAlentours(
                    commune = SAINT_MALO,
                    distances =
                        listOf(
                            VoeuAvecDistance(voeu = TripletAffectation(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO), km = 0),
                            VoeuAvecDistance(voeu = TripletAffectation(id = "ta32", nom = "Nom du ta32", commune = CAEN), km = 120),
                        ),
                ),
            )
        val tripletsAffectationParCommunesFavorites =
            mapOf(
                "fl0001" to tripletsAffectationParCommunesFavoritesFL0001,
                "fl0003" to tripletsAffectationParCommunesFavoritesFL0003,
            )
        given(
            recupererTripletAffectationDesCommunesFavoritesService.recupererVoeuxAutoursDeCommmunes(
                listOf(SAINT_MALO),
                tripletsAffectationsDesFormations,
            ),
        ).willReturn(
            tripletsAffectationParCommunesFavorites,
        )
        given(
            calculDuTauxDAffiniteBuilder.calculDuTauxDAffinite(
                formationAvecLeurAffinite = formationsAvecAffinites,
                idFormation = "fl0001",
            ),
        ).willReturn(17)
        given(
            calculDuTauxDAffiniteBuilder.calculDuTauxDAffinite(
                formationAvecLeurAffinite = formationsAvecAffinites,
                idFormation = "fl0003",
            ),
        ).willReturn(87)
        given(
            metiersTriesParProfilBuilder.trierMetiersParAffinites(
                metiers = exemplesMetiersFL0001,
                idsMetierTriesParAffinite = metiersTriesParAffinites,
            ),
        ).willReturn(listOf(metier123, metier534))
        given(
            metiersTriesParProfilBuilder.trierMetiersParAffinites(
                metiers = exemplesMetiersFL0003,
                idsMetierTriesParAffinite = metiersTriesParAffinites,
            ),
        ).willReturn(listOf(metier234, metier534))

        // When
        val resultat = recupererFormationsService.recupererFichesFormationPourProfil(profilEleve, affinitesFormationEtMetier, idsFormations)

        // Then
        val ficheFormationFl0001 =
            FicheFormation.FicheFormationPourProfil(
                id = "fl0001",
                nom = "L1 - Mathématique",
                descriptifGeneral = "Descriptif general fl0001",
                descriptifAttendus = "Descriptif attendus fl0001",
                descriptifDiplome = "Descriptif diplome fl0001",
                descriptifConseils = "Descriptif conseils fl0001",
                formationsAssociees = listOf("fl0004", "fl0003"),
                liens = listOf(lien1, lien2),
                criteresAnalyseCandidature = listOf(critereAnalyseCandidatureFL0001, critere2AnalyseCandidatureFL0001),
                statistiquesDesAdmis = statistiqueDesAdmisFL0001,
                tauxAffinite = 17,
                metiersTriesParAffinites = listOf(metier123, metier534),
                tripletsAffectation = tripletFormationFL0001,
                tripletsAffectationParCommunesFavorites = tripletsAffectationParCommunesFavoritesFL0001,
                explications = explicationsFL0001,
            )
        val ficheFormationFl0003 =
            FicheFormation.FicheFormationPourProfil(
                id = "fl0003",
                nom = "CAP Patisserie",
                descriptifGeneral = "Descriptif general fl0003",
                descriptifAttendus = "Descriptif attendus fl0003",
                descriptifDiplome = "Descriptif diplome fl0003",
                descriptifConseils = "Descriptif conseils fl0003",
                formationsAssociees = emptyList(),
                liens = emptyList(),
                criteresAnalyseCandidature = listOf(critereAnalyseCandidatureFL0003),
                statistiquesDesAdmis = statistiqueDesAdmisFL0003,
                tauxAffinite = 87,
                metiersTriesParAffinites = listOf(metier234, metier534),
                tripletsAffectation = tripletFormationFL0003,
                tripletsAffectationParCommunesFavorites = tripletsAffectationParCommunesFavoritesFL0003,
                explications = explicationsFL0003,
            )
        assertThat(resultat).usingRecursiveComparison().isEqualTo(listOf(ficheFormationFl0001, ficheFormationFl0003))
    }

    @Test
    fun `si la formation n'est pas renvoyé par les autres services, doit mettre des valeurs par défaut`() {
        // Given
        val profilEleve = mock(ProfilEleve.Identifie::class.java)
        given(profilEleve.baccalaureat).willReturn("Général")
        given(profilEleve.classe).willReturn(ChoixNiveau.TERMINALE)
        given(profilEleve.communesFavorites).willReturn(listOf(CAEN))
        val formationAvecSonAffinite1 = mock(FormationAvecSonAffinite::class.java)
        val formationAvecSonAffinite2 = mock(FormationAvecSonAffinite::class.java)
        val formationAvecSonAffinite3 = mock(FormationAvecSonAffinite::class.java)
        val formationsAvecAffinites = listOf(formationAvecSonAffinite1, formationAvecSonAffinite2, formationAvecSonAffinite3)
        val affinitesFormationEtMetier = mock(SuggestionsPourUnProfil::class.java)
        val metier534 = mock(Metier::class.java)
        val metiersTriesParAffinites = listOf("MET_234", "MET_123", "MET_534")
        given(affinitesFormationEtMetier.formations).willReturn(formationsAvecAffinites)
        given(affinitesFormationEtMetier.metiersTriesParAffinites).willReturn(metiersTriesParAffinites)
        val idsFormations = listOf("fl0001", "fl0002", "fl0003")
        val lien1 = mock(Lien::class.java)
        val lien2 = mock(Lien::class.java)
        val formations =
            listOf(
                Formation(
                    id = "fl0001",
                    nom = "L1 - Mathématique",
                    descriptifGeneral = "Descriptif general fl0001",
                    descriptifAttendus = "Descriptif attendus fl0001",
                    descriptifDiplome = "Descriptif diplome fl0001",
                    descriptifConseils = "Descriptif conseils fl0001",
                    formationsAssociees = listOf("fl0004", "fl0003"),
                    liens = listOf(lien1, lien2),
                    valeurCriteresAnalyseCandidature = listOf(0, 6, 14, 68, 12),
                ),
                Formation(
                    id = "fl0003",
                    nom = "CAP Patisserie",
                    descriptifGeneral = "Descriptif general fl0003",
                    descriptifAttendus = "Descriptif attendus fl0003",
                    descriptifDiplome = "Descriptif diplome fl0003",
                    descriptifConseils = "Descriptif conseils fl0003",
                    formationsAssociees = emptyList(),
                    liens = emptyList(),
                    valeurCriteresAnalyseCandidature = listOf(10, 0, 40, 20, 30),
                ),
            )
        given(formationRepository.recupererLesFormationsAvecLeursMetiers(idsFormations)).willReturn(formations)
        val critereAnalyseCandidatureFL0001 = mock(CritereAnalyseCandidature::class.java)
        val critere2AnalyseCandidatureFL0001 = mock(CritereAnalyseCandidature::class.java)
        val criteres =
            mapOf(
                "fl0001" to listOf(critereAnalyseCandidatureFL0001, critere2AnalyseCandidatureFL0001),
            )
        given(critereAnalyseCandidatureService.recupererCriteresAnalyseCandidature(formations)).willReturn(
            criteres,
        )
        val statistiqueDesAdmisFL0001 = mock(StatistiquesDesAdmis::class.java)
        val statistiquesDesAdmis =
            mapOf(
                "fl0001" to statistiqueDesAdmisFL0001,
            )
        given(
            statistiquesDesAdmisPourFormationsService.recupererStatistiquesAdmisDeFormations(
                idBaccalaureat = "Général",
                idsFormations = listOf("fl0001", "fl0003"),
                classe = ChoixNiveau.TERMINALE,
            ),
        ).willReturn(statistiquesDesAdmis)
        val exemplesMetiersFL0001 = listOf(metier534)
        val explicationsFL0001 = mock(ExplicationsSuggestionDetaillees::class.java)
        val explications = mapOf("fl0001" to Pair(explicationsFL0001, exemplesMetiersFL0001))
        given(
            recupererExplicationsEtExemplesMetiersPourFormationService.recupererExplicationsEtExemplesDeMetiers(
                profilEleve,
                listOf("fl0001", "fl0003"),
            ),
        ).willReturn(explications)
        val tripletFormationFL0001 =
            listOf(
                TripletAffectation(id = "ta1", nom = "Nom du ta1", commune = PARIS15EME),
                TripletAffectation(id = "ta17", nom = "Nom du ta17", commune = SAINT_MALO),
                TripletAffectation(id = "ta6", nom = "Nom du ta6", commune = MARSEILLE),
            )
        val tripletsAffectationsDesFormations = mapOf("fl0001" to tripletFormationFL0001)
        given(
            recupererTripletAffectationDUneFormationService.recupererTripletAffectationTriesParAffinites(
                idsFormations = listOf("fl0001", "fl0003"),
                profilEleve = profilEleve,
            ),
        ).willReturn(tripletsAffectationsDesFormations)
        val tripletsAffectationParCommunesFavoritesFL0001 =
            listOf(
                CommuneAvecVoeuxAuxAlentours(
                    commune = CAEN,
                    distances =
                        listOf(
                            VoeuAvecDistance(
                                voeu = TripletAffectation(id = "ta17", nom = "Nom du ta17", commune = CAEN),
                                km = 0,
                            ),
                            VoeuAvecDistance(
                                voeu = TripletAffectation(id = "ta7", nom = "Nom du ta7", commune = SAINT_MALO),
                                km = 120,
                            ),
                        ),
                ),
            )
        given(
            recupererTripletAffectationDesCommunesFavoritesService.recupererVoeuxAutoursDeCommmunes(
                listOf(CAEN),
                tripletsAffectationsDesFormations,
            ),
        ).willReturn(
            mapOf("fl0001" to tripletsAffectationParCommunesFavoritesFL0001),
        )
        given(
            calculDuTauxDAffiniteBuilder.calculDuTauxDAffinite(
                formationAvecLeurAffinite = formationsAvecAffinites,
                idFormation = "fl0001",
            ),
        ).willReturn(17)
        given(
            calculDuTauxDAffiniteBuilder.calculDuTauxDAffinite(
                formationAvecLeurAffinite = formationsAvecAffinites,
                idFormation = "fl0003",
            ),
        ).willReturn(0)
        given(
            metiersTriesParProfilBuilder.trierMetiersParAffinites(
                metiers = listOf(metier534),
                idsMetierTriesParAffinite = metiersTriesParAffinites,
            ),
        ).willReturn(listOf(metier534))
        given(
            metiersTriesParProfilBuilder.trierMetiersParAffinites(
                metiers = emptyList(),
                idsMetierTriesParAffinite = metiersTriesParAffinites,
            ),
        ).willReturn(emptyList())

        // When
        val resultat = recupererFormationsService.recupererFichesFormationPourProfil(profilEleve, affinitesFormationEtMetier, idsFormations)

        // Then
        val ficheFormationFl0001 =
            FicheFormation.FicheFormationPourProfil(
                id = "fl0001",
                nom = "L1 - Mathématique",
                descriptifGeneral = "Descriptif general fl0001",
                descriptifAttendus = "Descriptif attendus fl0001",
                descriptifDiplome = "Descriptif diplome fl0001",
                descriptifConseils = "Descriptif conseils fl0001",
                formationsAssociees = listOf("fl0004", "fl0003"),
                liens = listOf(lien1, lien2),
                criteresAnalyseCandidature = listOf(critereAnalyseCandidatureFL0001, critere2AnalyseCandidatureFL0001),
                statistiquesDesAdmis = statistiqueDesAdmisFL0001,
                tauxAffinite = 17,
                metiersTriesParAffinites = listOf(metier534),
                tripletsAffectation = tripletFormationFL0001,
                tripletsAffectationParCommunesFavorites = tripletsAffectationParCommunesFavoritesFL0001,
                explications = explicationsFL0001,
            )
        val ficheFormationFl0003 =
            FicheFormation.FicheFormationPourProfil(
                id = "fl0003",
                nom = "CAP Patisserie",
                descriptifGeneral = "Descriptif general fl0003",
                descriptifAttendus = "Descriptif attendus fl0003",
                descriptifDiplome = "Descriptif diplome fl0003",
                descriptifConseils = "Descriptif conseils fl0003",
                formationsAssociees = emptyList(),
                liens = emptyList(),
                criteresAnalyseCandidature = emptyList(),
                statistiquesDesAdmis = null,
                tauxAffinite = 0,
                metiersTriesParAffinites = emptyList(),
                tripletsAffectation = emptyList(),
                tripletsAffectationParCommunesFavorites = emptyList(),
                explications = null,
            )
        assertThat(resultat).usingRecursiveComparison().isEqualTo(listOf(ficheFormationFl0001, ficheFormationFl0003))
    }
}
