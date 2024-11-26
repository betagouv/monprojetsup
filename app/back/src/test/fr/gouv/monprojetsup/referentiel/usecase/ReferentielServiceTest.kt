package fr.gouv.monprojetsup.referentiel.usecase

import fr.gouv.monprojetsup.commun.Constantes.ANNEE_DONNEES_PARCOURSUP
import fr.gouv.monprojetsup.formation.domain.port.FrequencesCumuleesDesMoyenneDesAdmisRepository
import fr.gouv.monprojetsup.referentiel.domain.entity.AdmissionsParcoursup
import fr.gouv.monprojetsup.referentiel.domain.entity.AdmissionsParcoursup.PourcentagesPourChaqueMoyenneParBaccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.AdmissionsParcoursup.PourcentagesPourChaqueMoyenneParBaccalaureat.PourcentagesMoyenne
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.CategorieDomaine
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretCategorie
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie
import fr.gouv.monprojetsup.referentiel.domain.entity.Referentiel
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import fr.gouv.monprojetsup.referentiel.domain.entity.Specialite
import fr.gouv.monprojetsup.referentiel.domain.port.BaccalaureatSpecialiteRepository
import fr.gouv.monprojetsup.referentiel.domain.port.DomaineRepository
import fr.gouv.monprojetsup.referentiel.domain.port.InteretRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ReferentielServiceTest {
    @Mock
    lateinit var baccalaureatSpecialiteRepository: BaccalaureatSpecialiteRepository

    @Mock
    lateinit var interetRepository: InteretRepository

    @Mock
    lateinit var domaineRepository: DomaineRepository

    @Mock
    lateinit var frequencesCumuleesDesMoyenneDesAdmisRepository: FrequencesCumuleesDesMoyenneDesAdmisRepository

    @InjectMocks
    lateinit var referentielService: ReferentielService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `Doit appeler les repository et renvoyer l'objet`() {
        val baccalaureatsAvecSpecialites =
            mapOf(
                baccalaureatPro to
                    listOf(
                        Specialite(id = "4", label = "Sciences de l'ingénieur"),
                        Specialite(id = "1006", label = "Economie et gestion hôtelière"),
                    ),
                baccalaureatGeneral to
                    listOf(
                        Specialite(id = "4", label = "Sciences de l'ingénieur"),
                        Specialite(id = "1040", label = "Physique-Chimie et Mathématiques"),
                    ),
            )
        given(baccalaureatSpecialiteRepository.recupererLesBaccalaureatsAvecLeursSpecialites()).willReturn(
            baccalaureatsAvecSpecialites,
        )

        val toutesLesCategoriesEtSousCategoriesDInteret =
            mapOf(
                InteretCategorie(
                    id = "decouvrir_monde",
                    nom = "Découvrir le monde",
                    emoji = "🌎",
                ) to
                    listOf(
                        InteretSousCategorie(
                            id = "voyage",
                            nom = "Voyager",
                            emoji = "🚅",
                            description =
                                "Pour travailler dans le tourisme, l’hôtellerie, les transports, ou encore pour " +
                                    "organiser des voyages et des séjours.",
                        ),
                        InteretSousCategorie(
                            id = "linguistique",
                            nom = "Apprendre de nouvelles langues",
                            emoji = "🇬🇧",
                            description = null,
                        ),
                    ),
                InteretCategorie(
                    id = "rechercher",
                    nom = "Découvrir, enquêter et rechercher",
                    emoji = "\uD83E\uDDD0",
                ) to emptyList(),
            )
        given(interetRepository.recupererToutesLesCategoriesEtLeursSousCategoriesDInterets()).willReturn(
            toutesLesCategoriesEtSousCategoriesDInteret,
        )

        val categorieDomaineAvecLeursDomaines =
            mapOf(
                CategorieDomaine(
                    id = "agriculture_alimentaire",
                    nom = "Agriculture et Alimentation",
                    emoji = "🥕",
                ) to
                    listOf(
                        Domaine(
                            id = "animaux",
                            nom = "Soins aux animaux",
                            emoji = "\uD83D\uDC2E",
                            description =
                                "Pour travailler dans les élevages ou la pêche, mais aussi apprendre à soigner " +
                                    "les animaux, les nourrir et assurer leur bien-être.",
                        ),
                        Domaine(id = "agroequipement", nom = "Agroéquipement", emoji = "\uD83D\uDE9C", description = null),
                    ),
                CategorieDomaine(
                    id = "commerce",
                    nom = "Commerce",
                    emoji = "\uD83C\uDFE2",
                ) to emptyList(),
            )
        given(domaineRepository.recupererTousLesDomainesEtLeursCategories()).willReturn(
            categorieDomaineAvecLeursDomaines,
        )
        val frequencesCumuleesParBacs =
            mapOf(
                baccalaureatGeneral to generale,
                baccalaureatNC to nonCommunique,
                baccalaureatPro to pro,
                baccalaureatSTL to stl,
            )
        given(frequencesCumuleesDesMoyenneDesAdmisRepository.recupererFrequencesCumuleesParBacs(ANNEE_DONNEES_PARCOURSUP)).willReturn(
            frequencesCumuleesParBacs,
        )

        // When
        val resultat = referentielService.recupererReferentiel()

        // Then
        val attendu =
            Referentiel(
                situations =
                    listOf(
                        SituationAvanceeProjetSup.AUCUNE_IDEE,
                        SituationAvanceeProjetSup.QUELQUES_PISTES,
                        SituationAvanceeProjetSup.PROJET_PRECIS,
                    ),
                baccalaureatsAvecLeursSpecialites = baccalaureatsAvecSpecialites,
                choixNiveau =
                    listOf(
                        ChoixNiveau.SECONDE,
                        ChoixNiveau.PREMIERE,
                        ChoixNiveau.TERMINALE,
                    ),
                choixAlternance =
                    listOf(
                        ChoixAlternance.PAS_INTERESSE,
                        ChoixAlternance.INDIFFERENT,
                        ChoixAlternance.INTERESSE,
                        ChoixAlternance.TRES_INTERESSE,
                    ),
                choixDureeEtudesPrevue =
                    listOf(
                        ChoixDureeEtudesPrevue.INDIFFERENT,
                        ChoixDureeEtudesPrevue.COURTE,
                        ChoixDureeEtudesPrevue.LONGUE,
                        ChoixDureeEtudesPrevue.AUCUNE_IDEE,
                    ),
                categoriesDInteretsAvecLeursSousCategories = toutesLesCategoriesEtSousCategoriesDInteret,
                categoriesDomaineAvecLeursDomaines = categorieDomaineAvecLeursDomaines,
                admissionsParcoursup =
                    AdmissionsParcoursup(
                        annee = ANNEE_DONNEES_PARCOURSUP,
                        parBaccalaureat =
                            listOf(
                                PourcentagesPourChaqueMoyenneParBaccalaureat(
                                    baccalaureat = baccalaureatGeneral,
                                    pourcentages =
                                        listOf(
                                            PourcentagesMoyenne(note = 0.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 0.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 1.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 1.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 2.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 2.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 3.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 3.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 4.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 4.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 5.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 5.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 6.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 6.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 7.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 7.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 8.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 8.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 9.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 9.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 10.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 2),
                                            PourcentagesMoyenne(note = 10.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 4),
                                            PourcentagesMoyenne(note = 11.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 7),
                                            PourcentagesMoyenne(note = 11.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 12),
                                            PourcentagesMoyenne(note = 12.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 19),
                                            PourcentagesMoyenne(note = 12.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 28),
                                            PourcentagesMoyenne(note = 13.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 37),
                                            PourcentagesMoyenne(note = 13.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 47),
                                            PourcentagesMoyenne(note = 14.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 57),
                                            PourcentagesMoyenne(note = 14.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 66),
                                            PourcentagesMoyenne(note = 15.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 75),
                                            PourcentagesMoyenne(note = 15.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 82),
                                            PourcentagesMoyenne(note = 16.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 88),
                                            PourcentagesMoyenne(note = 16.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 93),
                                            PourcentagesMoyenne(note = 17.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 96),
                                            PourcentagesMoyenne(note = 17.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 98),
                                            PourcentagesMoyenne(note = 18.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                            PourcentagesMoyenne(note = 18.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                            PourcentagesMoyenne(note = 19.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                            PourcentagesMoyenne(note = 19.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                        ),
                                ),
                                PourcentagesPourChaqueMoyenneParBaccalaureat(
                                    baccalaureat = baccalaureatNC,
                                    pourcentages =
                                        listOf(
                                            PourcentagesMoyenne(note = 0.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 0.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 1.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 1.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 2.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 2.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 3.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 3.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 4.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 4.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 5.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 5.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 6.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 6.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 7.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 7.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 8.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 8.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 9.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 9.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 1),
                                            PourcentagesMoyenne(note = 10.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 3),
                                            PourcentagesMoyenne(note = 10.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 6),
                                            PourcentagesMoyenne(note = 11.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 10),
                                            PourcentagesMoyenne(note = 11.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 17),
                                            PourcentagesMoyenne(note = 12.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 25),
                                            PourcentagesMoyenne(note = 12.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 34),
                                            PourcentagesMoyenne(note = 13.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 44),
                                            PourcentagesMoyenne(note = 13.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 55),
                                            PourcentagesMoyenne(note = 14.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 64),
                                            PourcentagesMoyenne(note = 14.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 73),
                                            PourcentagesMoyenne(note = 15.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 80),
                                            PourcentagesMoyenne(note = 15.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 86),
                                            PourcentagesMoyenne(note = 16.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 91),
                                            PourcentagesMoyenne(note = 16.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 95),
                                            PourcentagesMoyenne(note = 17.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 97),
                                            PourcentagesMoyenne(note = 17.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 98),
                                            PourcentagesMoyenne(note = 18.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                            PourcentagesMoyenne(note = 18.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                            PourcentagesMoyenne(note = 19.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                            PourcentagesMoyenne(note = 19.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                        ),
                                ),
                                PourcentagesPourChaqueMoyenneParBaccalaureat(
                                    baccalaureat = baccalaureatPro,
                                    pourcentages =
                                        listOf(
                                            PourcentagesMoyenne(note = 0.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 0.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 1.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 1.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 2.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 2.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 3.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 3.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 4.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 4.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 5.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 5.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 6.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 6.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 7.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 7.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 8.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 8.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 9.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 1),
                                            PourcentagesMoyenne(note = 9.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 2),
                                            PourcentagesMoyenne(note = 10.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 4),
                                            PourcentagesMoyenne(note = 10.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 8),
                                            PourcentagesMoyenne(note = 11.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 13),
                                            PourcentagesMoyenne(note = 11.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 21),
                                            PourcentagesMoyenne(note = 12.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 31),
                                            PourcentagesMoyenne(note = 12.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 42),
                                            PourcentagesMoyenne(note = 13.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 53),
                                            PourcentagesMoyenne(note = 13.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 65),
                                            PourcentagesMoyenne(note = 14.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 75),
                                            PourcentagesMoyenne(note = 14.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 83),
                                            PourcentagesMoyenne(note = 15.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 90),
                                            PourcentagesMoyenne(note = 15.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 94),
                                            PourcentagesMoyenne(note = 16.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 97),
                                            PourcentagesMoyenne(note = 16.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                            PourcentagesMoyenne(note = 17.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                            PourcentagesMoyenne(note = 17.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                            PourcentagesMoyenne(note = 18.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                            PourcentagesMoyenne(note = 18.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                            PourcentagesMoyenne(note = 19.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                            PourcentagesMoyenne(note = 19.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                        ),
                                ),
                                PourcentagesPourChaqueMoyenneParBaccalaureat(
                                    baccalaureat = baccalaureatSTL,
                                    pourcentages =
                                        listOf(
                                            PourcentagesMoyenne(note = 0.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 0.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 1.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 1.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 2.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 2.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 3.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 3.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 4.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 4.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 5.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 5.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 6.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 6.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 7.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 7.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 8.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 8.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 0),
                                            PourcentagesMoyenne(note = 9.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 1),
                                            PourcentagesMoyenne(note = 9.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 3),
                                            PourcentagesMoyenne(note = 10.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 7),
                                            PourcentagesMoyenne(note = 10.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 12),
                                            PourcentagesMoyenne(note = 11.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 19),
                                            PourcentagesMoyenne(note = 11.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 28),
                                            PourcentagesMoyenne(note = 12.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 38),
                                            PourcentagesMoyenne(note = 12.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 48),
                                            PourcentagesMoyenne(note = 13.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 59),
                                            PourcentagesMoyenne(note = 13.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 69),
                                            PourcentagesMoyenne(note = 14.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 78),
                                            PourcentagesMoyenne(note = 14.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 85),
                                            PourcentagesMoyenne(note = 15.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 90),
                                            PourcentagesMoyenne(note = 15.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 94),
                                            PourcentagesMoyenne(note = 16.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 97),
                                            PourcentagesMoyenne(note = 16.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 98),
                                            PourcentagesMoyenne(note = 17.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                            PourcentagesMoyenne(note = 17.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                            PourcentagesMoyenne(note = 18.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 99),
                                            PourcentagesMoyenne(note = 18.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                            PourcentagesMoyenne(note = 19.0f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                            PourcentagesMoyenne(note = 19.5f, pourcentageAdmisAyantCetteMoyenneOuMoins = 100),
                                        ),
                                ),
                            ),
                    ),
            )
        assertThat(resultat).isEqualTo(attendu)
    }

    companion object {
        private val baccalaureatGeneral =
            Baccalaureat(id = "Générale", nom = "Bac Général", idExterne = "Générale", idCarteParcoursup = "1")
        private val baccalaureatNC = Baccalaureat(id = "NC", nom = "Non-communiqué", idExterne = "NC", idCarteParcoursup = "0")
        private val baccalaureatPro = Baccalaureat(id = "P", nom = "Bac Professionnel", idExterne = "P", idCarteParcoursup = "3")
        private val baccalaureatSTL = Baccalaureat(id = "STL", nom = "Bac STL", idExterne = "STL", idCarteParcoursup = "2")

        private val generale =
            listOf(
                2,
                3,
                3,
                3,
                4,
                4,
                4,
                5,
                7,
                11,
                16,
                23,
                33,
                47,
                78,
                136,
                267,
                597,
                1270,
                2933,
                6389,
                12866,
                23667,
                39745,
                61086,
                87054,
                115920,
                146223,
                176436,
                204853,
                230804,
                253011,
                271340,
                285271,
                295345,
                301601,
                304990,
                306229,
                306512,
                306535,
            )
        private val stl =
            listOf(
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                1,
                2,
                4,
                6,
                12,
                20,
                38,
                85,
                180,
                334,
                580,
                916,
                1338,
                1789,
                2260,
                2772,
                3259,
                3669,
                3995,
                4225,
                4404,
                4534,
                4606,
                4647,
                4663,
                4668,
                4669,
                4669,
                4669,
            )
        private val pro =
            listOf(
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                3,
                4,
                5,
                8,
                12,
                18,
                34,
                58,
                133,
                295,
                617,
                1248,
                2483,
                4507,
                7510,
                11725,
                17048,
                23086,
                29347,
                35690,
                41213,
                45780,
                49299,
                51663,
                53172,
                53964,
                54300,
                54455,
                54493,
                54506,
                54506,
                54506,
            )
        private val nonCommunique =
            listOf(
                4,
                5,
                5,
                5,
                6,
                6,
                6,
                8,
                14,
                20,
                27,
                43,
                69,
                106,
                191,
                347,
                714,
                1586,
                3367,
                7321,
                15048,
                28393,
                49396,
                78858,
                116536,
                160156,
                206445,
                252989,
                297022,
                336530,
                370760,
                398610,
                420419,
                436377,
                447389,
                454087,
                457603,
                458879,
                459174,
                459198,
            )
    }
}
