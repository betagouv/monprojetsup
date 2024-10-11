package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.ResultatRechercheFormationCourte
import fr.gouv.monprojetsup.formation.domain.port.RechercheFormationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations

class RechercherFormationsServiceTest {
    @Mock
    private lateinit var rechercheFormationRepository: RechercheFormationRepository

    @InjectMocks
    private lateinit var rechercherFormationsService: RechercherFormationsService

    private val rechercheLongue = "[^12  ma*réchèrche 1%peu Toùt!peTit peu&lôngue === ×÷\\ h^&e l l`.,|o w]{+o r l d'"

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        val formationsPourRecherche =
            listOf(
                ResultatRechercheFormationCourte(FormationCourte(id = "fl1", nom = "L1 - Psychologie"), null, null),
                ResultatRechercheFormationCourte(FormationCourte(id = "fl7", nom = "L1 - Philosophie"), null, null),
            )
        given(rechercheFormationRepository.rechercherUneFormation("réchèrche")).willReturn(formationsPourRecherche)
        val formationsPourPeu =
            listOf(
                ResultatRechercheFormationCourte(FormationCourte(id = "fl1", nom = "L1 - Psychologie"), null, null),
                ResultatRechercheFormationCourte(FormationCourte(id = "fl3", nom = "CAP Pâtisserie"), null, null),
                ResultatRechercheFormationCourte(FormationCourte(id = "fl17", nom = "L1 - Mathématique"), null, null),
                ResultatRechercheFormationCourte(FormationCourte(id = "fl1000", nom = "BPJEPS"), null, null),
            )
        given(rechercheFormationRepository.rechercherUneFormation("peu")).willReturn(formationsPourPeu)
        val formationsPourTout = emptyList<ResultatRechercheFormationCourte>()
        given(rechercheFormationRepository.rechercherUneFormation("Toùt")).willReturn(formationsPourTout)
        val formationsPourPetit =
            listOf(
                ResultatRechercheFormationCourte(FormationCourte(id = "fl17", nom = "L1 - Mathématique"), null, null),
                ResultatRechercheFormationCourte(FormationCourte(id = "fl7", nom = "L1 - Philosophie"), null, null),
            )
        given(rechercheFormationRepository.rechercherUneFormation("peTit")).willReturn(formationsPourPetit)
        val formationsPourLongue =
            listOf(
                ResultatRechercheFormationCourte(FormationCourte(id = "fl20", nom = "CAP Boulangerie"), null, null),
                ResultatRechercheFormationCourte(FormationCourte(id = "fl17", nom = "L1 - Mathématique"), null, null),
                ResultatRechercheFormationCourte(FormationCourte(id = "fl10", nom = "DUT Informatique"), null, null),
                ResultatRechercheFormationCourte(FormationCourte(id = "fl18", nom = "L1 - Littérature"), null, null),
            )
        given(rechercheFormationRepository.rechercherUneFormation("lôngue")).willReturn(formationsPourLongue)
    }

    @Test
    fun `doit retourner la liste des formations sans doublons`() {
        // When
        val resultat =
            rechercherFormationsService.rechercheLesFormationsCorrespondantes(
                recherche = rechercheLongue,
                tailleMinimumRecherche = 2,
            )

        // Then
        val attendu =
            listOf(
                FormationCourte(id = "fl17", nom = "L1 - Mathématique"),
                FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
                FormationCourte(id = "fl3", nom = "CAP Pâtisserie"),
                FormationCourte(id = "fl1000", nom = "BPJEPS"),
                FormationCourte(id = "fl20", nom = "CAP Boulangerie"),
                FormationCourte(id = "fl10", nom = "DUT Informatique"),
                FormationCourte(id = "fl18", nom = "L1 - Littérature"),
            )
        assertThat(resultat).isEqualTo(attendu)
    }

    @Test
    fun `ne doit pas appeler le repository pour les mots de moins de 2 caractères`() {
        // When
        rechercherFormationsService.rechercheLesFormationsCorrespondantes(
            recherche = rechercheLongue,
            tailleMinimumRecherche = 2,
        )

        // Then
        then(rechercheFormationRepository).should(never()).rechercherUneFormation(motRecherche = "1")
    }

    @Test
    fun `ne doit pas appeler le repository pour les mots vides`() {
        // When
        rechercherFormationsService.rechercheLesFormationsCorrespondantes(
            recherche = rechercheLongue,
            tailleMinimumRecherche = 2,
        )

        // Then
        then(rechercheFormationRepository).should(never()).rechercherUneFormation(motRecherche = "ma")
    }

    @Test
    fun `ne doit pas appeler le repository plusieurs fois pour le même mot`() {
        // When
        rechercherFormationsService.rechercheLesFormationsCorrespondantes(
            recherche = rechercheLongue,
            tailleMinimumRecherche = 2,
        )

        // Then
        then(rechercheFormationRepository).should(times(1)).rechercherUneFormation(motRecherche = "peu")
    }
}
