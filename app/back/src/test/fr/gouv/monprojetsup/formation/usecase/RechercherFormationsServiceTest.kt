package fr.gouv.monprojetsup.formation.usecase

import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.port.RechercheFormationRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.atMostOnce
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.MockitoAnnotations

class RechercherFormationsServiceTest {
    @Mock
    private lateinit var rechercheFormationRepository: RechercheFormationRepository

    @InjectMocks
    private lateinit var rechercherFormationsService: RechercherFormationsService

    private val rechercheLongue = "12  ma*réchèrche 1%peu Toùt!peTit peu&lôngue ==="

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
        val formationsPourRecherche =
            listOf(
                FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
            )
        given(rechercheFormationRepository.rechercherUneFormation("recherche")).willReturn(formationsPourRecherche)
        val formationsPourPeu =
            listOf(
                FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                FormationCourte(id = "fl3", nom = "CAP Pâtisserie"),
                FormationCourte(id = "fl1000", nom = "BPJEPS"),
            )
        given(rechercheFormationRepository.rechercherUneFormation("peu")).willReturn(formationsPourPeu)
        val formationsPourTout = emptyList<FormationCourte>()
        given(rechercheFormationRepository.rechercherUneFormation("tout")).willReturn(formationsPourTout)
        val formationsPourPetit =
            listOf(
                FormationCourte(id = "fl17", nom = "L1 - Mathématique"),
                FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
            )
        given(rechercheFormationRepository.rechercherUneFormation("petit")).willReturn(formationsPourPetit)
        val formationsPourLongue =
            listOf(
                FormationCourte(id = "fl20", nom = "CAP Boulangerie"),
                FormationCourte(id = "fl10", nom = "DUT Informatique"),
                FormationCourte(id = "fl18", nom = "L1 - Littérature"),
            )
        given(rechercheFormationRepository.rechercherUneFormation("longue")).willReturn(formationsPourLongue)
    }

    @Test
    fun `doit retourner la liste des formations sans doublons`() {
        // When
        val resultat = rechercherFormationsService.rechercheLesFormationsCorrespondantes(rechercheLongue)

        // Then
        val attendu =
            listOf(
                FormationCourte(id = "fl1", nom = "L1 - Psychologie"),
                FormationCourte(id = "fl7", nom = "L1 - Philosophie"),
                FormationCourte(id = "fl3", nom = "CAP Pâtisserie"),
                FormationCourte(id = "fl1000", nom = "BPJEPS"),
                FormationCourte(id = "fl17", nom = "L1 - Mathématique"),
                FormationCourte(id = "fl20", nom = "CAP Boulangerie"),
                FormationCourte(id = "fl10", nom = "DUT Informatique"),
                FormationCourte(id = "fl18", nom = "L1 - Littérature"),
            )
        assertThat(resultat).isEqualTo(attendu)
    }

    @Test
    fun `ne doit pas appeler le repository pour les mots de moins de 2 caractères`() {
        // When
        rechercherFormationsService.rechercheLesFormationsCorrespondantes(rechercheLongue)

        // Then
        then(rechercheFormationRepository).should(never()).rechercherUneFormation("1")
    }

    @Test
    fun `ne doit pas appeler le repository plusieurs fois pour le même mot`() {
        // When
        rechercherFormationsService.rechercheLesFormationsCorrespondantes(rechercheLongue)

        // Then
        then(rechercheFormationRepository).should(atMostOnce()).rechercherUneFormation("peu")
    }
}
