package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.AffinitesPourProfil
import fr.gouv.monprojetsup.formation.domain.entity.AffinitesPourProfil.FormationAvecSonAffinite
import fr.gouv.monprojetsup.formation.domain.entity.Formation
import fr.gouv.monprojetsup.formation.domain.entity.FormationPourProfil
import fr.gouv.monprojetsup.formation.domain.entity.Metier
import fr.gouv.monprojetsup.formation.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.formation.domain.port.FormationRepository
import fr.gouv.monprojetsup.formation.domain.port.SuggestionHttpClient
import fr.gouv.monprojetsup.formation.domain.port.TripletAffectationRepository
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations

class SuggestionsFormationsServiceTest {
    @Mock
    lateinit var suggestionHttpClient: SuggestionHttpClient

    @Mock
    lateinit var formationRepository: FormationRepository

    @Mock
    lateinit var tripletAffectationRepository: TripletAffectationRepository

    @InjectMocks
    lateinit var suggestionsFormationsService: SuggestionsFormationsService

    private val metiersTriesParAffinites =
        listOf(
            "MET_611",
            "MET_610",
            "MET_613",
            "MET_628",
            "MET_627",
            "MET_620",
            "MET_861",
            "MET_864",
            "MET_863",
            "MET_866",
            "MET_865",
            "MET_623",
            "MET_867",
            "MET_625",
            "MET_831",
            "MET_606",
            "MET_605",
            "MET_602",
            "MET_603",
            "MET_815",
            "MET_31",
            "MET_32",
            "MET_35",
            "MET_813",
            "MET_51",
            "MET_50",
            "MET_826",
            "MET_827",
            "MET_44",
            "MET_43",
            "MET_19",
            "MET_11",
            "MET_10",
            "MET_27",
            "MET_22",
            "MET_801",
            "MET_196",
            "MET_195",
            "MET_192",
            "MET_172",
            "MET_171",
            "MET_174",
            "MET_179",
            "MET_182",
            "MET_9",
            "MET_156",
            "MET_397",
            "MET_158",
            "MET_157",
            "MET_160",
            "MET_162",
            "MET_165",
            "MET_164",
            "MET_370",
            "MET_371",
            "MET_149",
            "MET_592",
            "MET_591",
            "MET_351",
            "MET_593",
            "MET_595",
            "MET_356",
            "MET_118",
            "MET_117",
            "MET_361",
            "MET_362",
            "MET_125",
            "MET_124",
            "MET_126",
            "MET_571",
            "MET_333",
            "MET_575",
            "MET_580",
            "MET_340",
            "MET_101",
            "MET_342",
            "MET_100",
            "MET_103",
            "MET_346",
            "MET_104",
            "MET_349",
            "MET_107",
            "MET_348",
            "MET_106",
            "MET_793",
            "MET_795",
            "MET_553",
            "MET_797",
            "MET_555",
            "MET_317",
            "MET_563",
            "MET_322",
            "MET_569",
            "MET_326",
            "MET_770",
            "MET_775",
            "MET_538",
            "MET_541",
            "MET_540",
            "MET_300",
            "MET_303",
            "MET_547",
            "MET_788",
            "MET_306",
            "MET_548",
            "MET_518",
            "MET_512",
            "MET_514",
            "MET_757",
            "MET_760",
            "MET_528",
            "MET_764",
            "MET_730",
            "MET_741",
            "MET_500",
            "MET_726",
            "MET_728",
            "MET_721",
            "MET_935",
            "MET_939",
            "MET_930",
            "MET_932",
            "MET_931",
            "MET_934",
            "MET_947",
            "MET_705",
            "MET_949",
            "MET_948",
            "MET_943",
            "MET_944",
            "MET_910",
            "MET_911",
            "MET_925",
            "MET_920",
            "MET_903",
            "MET_902",
            "MET_909",
            "MET_293",
            "MET_292",
            "MET_295",
            "MET_297",
            "MET_296",
            "MET_290",
            "MET_275",
            "MET_277",
            "MET_286",
            "MET_289",
            "MET_491",
            "MET_492",
            "MET_250",
            "MET_499",
            "MET_498",
            "MET_264",
            "MET_263",
            "MET_266",
            "MET_265",
            "MET_231",
            "MET_230",
            "MET_234",
            "MET_480",
            "MET_481",
            "MET_242",
            "MET_483",
            "MET_244",
            "MET_243",
            "MET_489",
            "MET_82",
            "MET_81",
            "MET_211",
        )
    private val formations =
        listOf(
            FormationAvecSonAffinite(
                "fl240",
                0.5448393f,
            ),
            FormationAvecSonAffinite(
                "fr22",
                0.7782054f,
            ),
            FormationAvecSonAffinite(
                "fl2110",
                0.3333385f,
            ),
            FormationAvecSonAffinite(
                "fl2016",
                0.7217561f,
            ),
            FormationAvecSonAffinite(
                "fl252",
                0.7125898f,
            ),
            FormationAvecSonAffinite(
                "fl2118",
                0.7103791f,
            ),
            FormationAvecSonAffinite(
                "fl680003",
                0.6735823f,
            ),
            FormationAvecSonAffinite(
                "fl2009",
                0.7486587f,
            ),
            FormationAvecSonAffinite(
                "fl2046",
                0.6638471f,
            ),
            FormationAvecSonAffinite(
                "fl2022",
                0.6206682f,
            ),
            FormationAvecSonAffinite(
                "fl2040",
                0.5962649f,
            ),
            FormationAvecSonAffinite(
                "fl2032",
                0.5958909f,
            ),
            FormationAvecSonAffinite(
                "fr83",
                0.5900792f,
            ),
            FormationAvecSonAffinite(
                "fl2044",
                0.5842652f,
            ),
            FormationAvecSonAffinite(
                "fl2090",
                0.5719057f,
            ),
            FormationAvecSonAffinite(
                "fl840010",
                0.5644857f,
            ),
            FormationAvecSonAffinite(
                "fl2033",
                0.5587129f,
            ),
            FormationAvecSonAffinite(
                "fl2037",
                0.548797f,
            ),
            FormationAvecSonAffinite(
                "fl2041",
                0.5469723f,
            ),
            FormationAvecSonAffinite(
                "fl2034",
                0.538966f,
            ),
            FormationAvecSonAffinite(
                "fl2073",
                0.5299478f,
            ),
            FormationAvecSonAffinite(
                "fl2018",
                0.5652516f,
            ),
            FormationAvecSonAffinite(
                "fl2035",
                0.5214914f,
            ),
            FormationAvecSonAffinite(
                "fl810007",
                0.5210825f,
            ),
            FormationAvecSonAffinite(
                "fl2014",
                0.5209211f,
            ),
            FormationAvecSonAffinite(
                "fl2023",
                0.5203177f,
            ),
            FormationAvecSonAffinite(
                "fl2029",
                0.520241f,
            ),
            FormationAvecSonAffinite(
                "fl2010",
                0.5200685f,
            ),
            FormationAvecSonAffinite(
                "fl2028",
                0.5198961f,
            ),
            FormationAvecSonAffinite(
                "fl393",
                0.519804f,
            ),
            FormationAvecSonAffinite(
                "fl663",
                0.5197131f,
            ),
            FormationAvecSonAffinite(
                "fl2027",
                0.5196401f,
            ),
            FormationAvecSonAffinite(
                "fl2024",
                0.5192161f,
            ),
            FormationAvecSonAffinite(
                "fl2050",
                0.5190555f,
            ),
            FormationAvecSonAffinite(
                "fl270",
                0.5157492f,
            ),
            FormationAvecSonAffinite(
                "fl2019",
                0.5145695f,
            ),
            FormationAvecSonAffinite(
                "fl52",
                0.513774f,
            ),
            FormationAvecSonAffinite(
                "fl13",
                0.5077645f,
            ),
            FormationAvecSonAffinite(
                "fl41",
                0.5070587f,
            ),
            FormationAvecSonAffinite(
                "fl2096",
                0.49477f,
            ),
            FormationAvecSonAffinite(
                "fl2051",
                0.4817011f,
            ),
            FormationAvecSonAffinite(
                "fl54",
                0.4754353f,
            ),
            FormationAvecSonAffinite(
                "fl2017",
                0.473114f,
            ),
            FormationAvecSonAffinite(
                "fl872",
                0.4708512f,
            ),
            FormationAvecSonAffinite(
                "fl2089",
                0.4567504f,
            ),
            FormationAvecSonAffinite(
                "fl210",
                0.4429564f,
            ),
            FormationAvecSonAffinite(
                "fl2100",
                0.4402275f,
            ),
            FormationAvecSonAffinite(
                "fl2042",
                0.4338807f,
            ),
            FormationAvecSonAffinite(
                "fl2012",
                0.4016404f,
            ),
            FormationAvecSonAffinite(
                "fl680002",
                0.9f,
            ),
            FormationAvecSonAffinite(
                "fl31",
                0.4326786f,
            ),
            FormationAvecSonAffinite(
                "fl242",
                0.4325488f,
            ),
            FormationAvecSonAffinite(
                "fl2112",
                0.427363f,
            ),
            FormationAvecSonAffinite(
                "fl659",
                0.3776005f,
            ),
            FormationAvecSonAffinite(
                "fl2013",
                0.4189669f,
            ),
            FormationAvecSonAffinite(
                "fl2092",
                0.4169147f,
            ),
            FormationAvecSonAffinite(
                "fl2043",
                0.4090678f,
            ),
            FormationAvecSonAffinite(
                "fl2060",
                0.3869467f,
            ),
            FormationAvecSonAffinite(
                "fl2061",
                0.3795127f,
            ),
            FormationAvecSonAffinite(
                "fl2020",
                0.3793568f,
            ),
            FormationAvecSonAffinite(
                "fl810022",
                0.3787828f,
            ),
            FormationAvecSonAffinite(
                "fl434",
                0.3785562f,
            ),
            FormationAvecSonAffinite(
                "fl830",
                0.3785463f,
            ),
            FormationAvecSonAffinite(
                "fl810006",
                0.3782891f,
            ),
            FormationAvecSonAffinite(
                "fl660009",
                0.3781019f,
            ),
            FormationAvecSonAffinite(
                "fl10434",
                0.378074f,
            ),
            FormationAvecSonAffinite(
                "fl653",
                0.378003f,
            ),
            FormationAvecSonAffinite(
                "fl660008",
                0.3779076f,
            ),
            FormationAvecSonAffinite(
                "fl649",
                0.3778561f,
            ),
            FormationAvecSonAffinite(
                "fl660010",
                0.3776218f,
            ),
            FormationAvecSonAffinite(
                "fl836",
                0.3775837f,
            ),
            FormationAvecSonAffinite(
                "fl660003",
                0.3775475f,
            ),
            FormationAvecSonAffinite(
                "fl665",
                0.3775119f,
            ),
            FormationAvecSonAffinite(
                "fl660007",
                0.3774604f,
            ),
            FormationAvecSonAffinite(
                "fl672",
                0.3774106f,
            ),
            FormationAvecSonAffinite(
                "fl10411",
                0.3773751f,
            ),
            FormationAvecSonAffinite(
                "fl467",
                0.3773626f,
            ),
            FormationAvecSonAffinite(
                "fl10409",
                0.3748815f,
            ),
            FormationAvecSonAffinite(
                "fl810001",
                0.3656532f,
            ),
            FormationAvecSonAffinite(
                "fl810502",
                0.3656008f,
            ),
            FormationAvecSonAffinite(
                "fl660006",
                0.3651972f,
            ),
            FormationAvecSonAffinite(
                "fl810003",
                0.3649447f,
            ),
            FormationAvecSonAffinite(
                "fl660011",
                0.3642214f,
            ),
            FormationAvecSonAffinite(
                "fl840007",
                0.3588819f,
            ),
            FormationAvecSonAffinite(
                "fl2002",
                0.3555425f,
            ),
            FormationAvecSonAffinite(
                "fl2091",
                0.3541359f,
            ),
            FormationAvecSonAffinite(
                "fl810019",
                0.3427746f,
            ),
            FormationAvecSonAffinite(
                "fl810011",
                0.3372925f,
            ),
            FormationAvecSonAffinite(
                "fl2047",
                0.3303166f,
            ),
            FormationAvecSonAffinite(
                "fl840012",
                0.3296169f,
            ),
            FormationAvecSonAffinite(
                "fl660002",
                0.3264019f,
            ),
            FormationAvecSonAffinite(
                "fl840011",
                0.4197874f,
            ),
        )

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    private val affinitesFormationEtMetier =
        AffinitesPourProfil(
            metiersTriesParAffinites = metiersTriesParAffinites,
            formations = formations,
        )

    @Test
    fun `suggererFormations - quand demander les 5 premieres formations, alors les retourner classés par ordre d'affinité du profil`() {
        // Given
        val profilEleve =
            mock(ProfilEleve::class.java).apply {
                given(this.villesPreferees).willReturn(listOf("Paris"))
            }

        given(suggestionHttpClient.recupererLesAffinitees(profilEleve)).willReturn(affinitesFormationEtMetier)

        val formationsEtLeursMetiers =
            mapOf(
                Formation("fr22", "C.M.I - Cursus Master en Ingénierie") to
                    listOf(
                        Metier("MET_368", "ingénieur/e environnement et risques industriels"),
                        Metier("MET_325", "ingénieur / ingénieure nucléaire"),
                        Metier("MET_304", "ingénieur / ingénieure gaz"),
                        Metier("MET_460", "ingénieur / ingénieure en métrologie"),
                        Metier("MET_738", "ingénieur/e chimiste en développement analytique"),
                        Metier("MET_707", "ingénieur / ingénieure logiciel"),
                        Metier("MET_947", "ingénieur/e structures"),
                        Metier("MET_887", "ingénieur / ingénieure télécoms et réseaux"),
                        Metier("MET_75", "ingénieur électricien / ingénieure électricienne"),
                        Metier("MET_74", "ingénieur technico-commercial / ingénieure technico-commerciale"),
                        Metier("MET_95", "ingénieur / ingénieure brevets"),
                        Metier("MET_228", "ingénieur / ingénieure cloud computing"),
                    ),
                Formation("fl2009", "L1 - Humanités") to
                    listOf(
                        Metier("MET_342", "conseiller / conseillère en insertion sociale et professionnelle"),
                        Metier("MET_463", "écrivain / écrivaine"),
                        Metier("MET_45", "journaliste"),
                        Metier("MET_172", "documentaliste"),
                        Metier("MET_179", "démographe"),
                        Metier("MET_180", "sociologue"),
                    ),
                Formation("fl252", "EA-BAC5 - Paysage") to listOf(Metier("MET_158", "paysagiste")),
                Formation("fl2016", "L1 - Philosophie") to
                    listOf(
                        Metier(
                            "MET_477",
                            "<span>écrivain / écrivaine public</span>",
                        ),
                    ),
                Formation("fl680002", "Cycle pluridisciplinaire d'Études Supérieures - Sciences") to
                    listOf(
                        Metier("MET_459", "expert / experte en sécurité informatique"),
                        Metier("MET_620", "halieute"),
                        Metier("MET_829", "glaciologue"),
                        Metier("MET_203", "chef / cheffe de projet informatique"),
                    ),
            )
        val idsFormation =
            listOf(
                "fl680002",
                "fr22",
                "fl2009",
                "fl2016",
                "fl252",
            )
        given(formationRepository.recupererLesFormationsAvecLeursMetiers(idsFormation)).willReturn(
            formationsEtLeursMetiers,
        )

        val tripletsAffectation =
            mapOf(
                "fl252" to
                    listOf(
                        TripletAffectation("ta5", "Caen"),
                    ),
                "fr22" to
                    listOf(
                        TripletAffectation("ta2", "Lyon"),
                    ),
                "fl2016" to
                    listOf(
                        TripletAffectation("ta4", "Marseille"),
                    ),
                "fl2009" to
                    listOf(
                        TripletAffectation("ta3", "Paris"),
                        TripletAffectation("ta7", "Strasbourg"),
                    ),
                "fl680002" to
                    listOf(
                        TripletAffectation("ta1", "Paris"),
                        TripletAffectation("ta6", "Marseille"),
                    ),
            )
        given(tripletAffectationRepository.recupererLesTripletsAffectationDeFormations(idsFormation)).willReturn(
            tripletsAffectation,
        )

        // When
        val result = suggestionsFormationsService.suggererFormations(profilEleve, 0, 5)

        // Then
        assertThat(result).isEqualTo(
            listOf(
                FormationPourProfil(
                    id = "fl680002",
                    nom = "Cycle pluridisciplinaire d'Études Supérieures - Sciences",
                    tauxAffinite = 0.9f,
                    metiersTriesParAffinites =
                        listOf(
                            "expert / experte en sécurité informatique",
                            "glaciologue",
                            "chef / cheffe de projet informatique",
                            "halieute",
                        ),
                    communesTrieesParAffinites = listOf("Paris", "Marseille"),
                ),
                FormationPourProfil(
                    id = "fr22",
                    nom = "C.M.I - Cursus Master en Ingénierie",
                    tauxAffinite = 0.7782054f,
                    metiersTriesParAffinites =
                        listOf(
                            "ingénieur/e environnement et risques industriels",
                            "ingénieur / ingénieure nucléaire",
                            "ingénieur / ingénieure gaz",
                            "ingénieur / ingénieure en métrologie",
                            "ingénieur/e chimiste en développement analytique",
                            "ingénieur / ingénieure logiciel",
                            "ingénieur / ingénieure télécoms et réseaux",
                            "ingénieur électricien / ingénieure électricienne",
                            "ingénieur technico-commercial / ingénieure technico-commerciale",
                            "ingénieur / ingénieure brevets",
                            "ingénieur / ingénieure cloud computing",
                            "ingénieur/e structures",
                        ),
                    communesTrieesParAffinites = listOf("Lyon"),
                ),
                FormationPourProfil(
                    id = "fl2009",
                    nom = "L1 - Humanités",
                    tauxAffinite = 0.7486587f,
                    metiersTriesParAffinites =
                        listOf(
                            "écrivain / écrivaine",
                            "journaliste",
                            "sociologue",
                            "documentaliste",
                            "démographe",
                            "conseiller / conseillère en insertion sociale et professionnelle",
                        ),
                    communesTrieesParAffinites = listOf("Paris", "Strasbourg"),
                ),
                FormationPourProfil(
                    id = "fl2016",
                    nom = "L1 - Philosophie",
                    tauxAffinite = 0.7217561f,
                    metiersTriesParAffinites = listOf("<span>écrivain / écrivaine public</span>"),
                    communesTrieesParAffinites = listOf("Marseille"),
                ),
                FormationPourProfil(
                    id = "fl252",
                    nom = "EA-BAC5 - Paysage",
                    tauxAffinite = 0.7125898f,
                    metiersTriesParAffinites = listOf("paysagiste"),
                    communesTrieesParAffinites = listOf("Caen"),
                ),
            ),
        )
    }

    @Test
    fun `suggererFormations - les villes doivent être ordonnées par affinités et enlever les doublons`() {
        // Given
        val profilEleve =
            mock(ProfilEleve::class.java).apply {
                given(this.villesPreferees).willReturn(listOf("Paris", "Caen"))
            }

        given(suggestionHttpClient.recupererLesAffinitees(profilEleve)).willReturn(affinitesFormationEtMetier)

        val formationsEtLeursMetiers =
            mapOf(
                Formation("fl680002", "Cycle pluridisciplinaire d'Études Supérieures - Sciences") to
                    listOf(
                        Metier("MET_459", "expert / experte en sécurité informatique"),
                        Metier("MET_620", "halieute"),
                        Metier("MET_829", "glaciologue"),
                        Metier("MET_203", "chef / cheffe de projet informatique"),
                    ),
                Formation("fr22", "C.M.I - Cursus Master en Ingénierie") to
                    listOf(
                        Metier("MET_368", "ingénieur/e environnement et risques industriels"),
                        Metier("MET_325", "ingénieur / ingénieure nucléaire"),
                        Metier("MET_304", "ingénieur / ingénieure gaz"),
                        Metier("MET_460", "ingénieur / ingénieure en métrologie"),
                        Metier("MET_738", "ingénieur/e chimiste en développement analytique"),
                        Metier("MET_707", "ingénieur / ingénieure logiciel"),
                        Metier("MET_947", "ingénieur/e structures"),
                        Metier("MET_887", "ingénieur / ingénieure télécoms et réseaux"),
                        Metier("MET_75", "ingénieur électricien / ingénieure électricienne"),
                        Metier("MET_74", "ingénieur technico-commercial / ingénieure technico-commerciale"),
                        Metier("MET_95", "ingénieur / ingénieure brevets"),
                        Metier("MET_228", "ingénieur / ingénieure cloud computing"),
                    ),
                Formation("fl2009", "L1 - Humanités") to
                    listOf(
                        Metier("MET_342", "conseiller / conseillère en insertion sociale et professionnelle"),
                        Metier("MET_463", "écrivain / écrivaine"),
                        Metier("MET_45", "journaliste"),
                        Metier("MET_172", "documentaliste"),
                        Metier("MET_179", "démographe"),
                        Metier("MET_180", "sociologue"),
                    ),
                Formation("fl2016", "L1 - Philosophie") to
                    listOf(
                        Metier(
                            "MET_477",
                            "<span>écrivain / écrivaine public</span>",
                        ),
                    ),
                Formation("fl252", "EA-BAC5 - Paysage") to listOf(Metier("MET_158", "paysagiste")),
            )
        val idsFormation =
            listOf(
                "fl680002",
                "fr22",
                "fl2009",
                "fl2016",
                "fl252",
            )
        given(formationRepository.recupererLesFormationsAvecLeursMetiers(idsFormation)).willReturn(
            formationsEtLeursMetiers,
        )

        val tripletsAffectation =
            mapOf(
                "fl680002" to
                    listOf(
                        TripletAffectation("ta6", "Marseille"),
                        TripletAffectation("ta1", "Paris"),
                    ),
                "fr22" to
                    listOf(
                        TripletAffectation("ta2", "Lyon"),
                    ),
                "fl2009" to
                    listOf(
                        TripletAffectation("ta10", "Lyon"),
                        TripletAffectation("ta3", "Paris"),
                        TripletAffectation("ta7", "Strasbourg"),
                        TripletAffectation("ta11", "Lyon"),
                    ),
                "fl2016" to
                    listOf(
                        TripletAffectation("ta4", "Marseille"),
                    ),
                "fl252" to
                    listOf(
                        TripletAffectation("ta5", "Caen"),
                        TripletAffectation("ta8", "Paris"),
                        TripletAffectation("ta9", "Clermont-Ferrand"),
                    ),
            )
        given(tripletAffectationRepository.recupererLesTripletsAffectationDeFormations(idsFormation)).willReturn(
            tripletsAffectation,
        )

        // When
        val result = suggestionsFormationsService.suggererFormations(profilEleve, 0, 5)

        // Then
        assertThat(result[0].communesTrieesParAffinites).isEqualTo(listOf("Paris", "Marseille"))
        assertThat(result[1].communesTrieesParAffinites).isEqualTo(listOf("Lyon"))
        assertThat(result[2].communesTrieesParAffinites).isEqualTo(listOf("Paris", "Lyon", "Strasbourg"))
        assertThat(result[3].communesTrieesParAffinites).isEqualTo(listOf("Marseille"))
        assertThat(result[4].communesTrieesParAffinites).isEqualTo(listOf("Paris", "Caen", "Clermont-Ferrand"))
    }

    @Test
    fun `suggererFormations - quand l'API suggestion nous retourne des listes vides, alors on doit les retourner une liste vide`() {
        // Given
        val profilEleve =
            mock(ProfilEleve::class.java).apply {
                given(this.formationsChoisies).willReturn(listOf("Paris"))
            }

        val affinitesFormationEtMetierVides =
            AffinitesPourProfil(
                metiersTriesParAffinites = emptyList(),
                formations = emptyList(),
            )
        given(suggestionHttpClient.recupererLesAffinitees(profilEleve)).willReturn(affinitesFormationEtMetierVides)
        given(formationRepository.recupererLesFormationsAvecLeursMetiers(emptyList())).willReturn(emptyMap())
        given(tripletAffectationRepository.recupererLesTripletsAffectationDeFormations(emptyList())).willReturn(emptyMap())

        // When
        val result = suggestionsFormationsService.suggererFormations(profilEleve, 0, 5)

        // Then
        assertThat(result).isEqualTo(emptyList<FormationPourProfil>())
    }

    @Test
    fun `suggererFormations - quand les indexs sont inversés, alors on doit throw une exception`() {
        // Given
        val profilEleve =
            mock(ProfilEleve::class.java).apply {
                given(this.formationsChoisies).willReturn(listOf("Paris"))
            }

        val affinitesFormationEtMetier =
            AffinitesPourProfil(
                metiersTriesParAffinites = metiersTriesParAffinites,
                formations = formations,
            )
        given(suggestionHttpClient.recupererLesAffinitees(profilEleve)).willReturn(affinitesFormationEtMetier)

        // When & Then
        assertThatThrownBy {
            suggestionsFormationsService.suggererFormations(profilEleve, 5, 0)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }
}
