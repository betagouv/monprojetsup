package fr.gouv.monprojetsup.referentiel.usecase

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
                Baccalaureat(
                    id = "Professionnel",
                    nom = "Série Pro",
                    idExterne = "P",
                ) to
                    listOf(
                        Specialite(id = "4", label = "Sciences de l'ingénieur"),
                        Specialite(id = "1006", label = "Economie et gestion hôtelière"),
                    ),
                Baccalaureat(
                    id = "Général",
                    nom = "Série Générale",
                    idExterne = "Générale",
                ) to
                    listOf(
                        Specialite(id = "4", label = "Sciences de l'ingénieur"),
                        Specialite(id = "1040", label = "Physique-Chimie et Mathématiques"),
                    ),
            )
        given(baccalaureatSpecialiteRepository.recupererLesBaccalaureatsAvecLeursSpecialites()).willReturn(baccalaureatsAvecSpecialites)

        val toutesLesCategoriesEtSousCategoriesDInteret =
            mapOf(
                InteretCategorie(id = "decouvrir_monde", nom = "Découvrir le monde", emoji = "🌎") to
                    listOf(
                        InteretSousCategorie(id = "voyage", nom = "Voyager", emoji = "🚅"),
                        InteretSousCategorie(id = "linguistique", nom = "Apprendre de nouvelles langues", emoji = "🇬🇧"),
                    ),
                InteretCategorie(id = "rechercher", nom = "Découvrir, enquêter et rechercher", emoji = "\uD83E\uDDD0") to emptyList(),
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
                        Domaine(id = "animaux", nom = "Soins aux animaux", emoji = "\uD83D\uDC2E"),
                        Domaine(id = "agroequipement", nom = "Agroéquipement", emoji = "\uD83D\uDE9C"),
                    ),
                CategorieDomaine(
                    id = "commerce",
                    nom = "Commerce",
                    emoji = "\uD83C\uDFE2",
                ) to emptyList(),
            )
        given(domaineRepository.recupererTousLesDomainesEtLeursCategories()).willReturn(categorieDomaineAvecLeursDomaines)

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
                        ChoixNiveau.NON_RENSEIGNE,
                    ),
                choixAlternance =
                    listOf(
                        ChoixAlternance.PAS_INTERESSE,
                        ChoixAlternance.INDIFFERENT,
                        ChoixAlternance.INTERESSE,
                        ChoixAlternance.TRES_INTERESSE,
                        ChoixAlternance.NON_RENSEIGNE,
                    ),
                choixDureeEtudesPrevue =
                    listOf(
                        ChoixDureeEtudesPrevue.INDIFFERENT,
                        ChoixDureeEtudesPrevue.COURTE,
                        ChoixDureeEtudesPrevue.LONGUE,
                        ChoixDureeEtudesPrevue.AUCUNE_IDEE,
                        ChoixDureeEtudesPrevue.NON_RENSEIGNE,
                    ),
                categoriesDInteretsAvecLeursSousCategories = toutesLesCategoriesEtSousCategoriesDInteret,
                categoriesDomaineAvecLeursDomaines = categorieDomaineAvecLeursDomaines,
            )
        assertThat(resultat).isEqualTo(attendu)
    }
}
