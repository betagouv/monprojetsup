package fr.gouv.monprojetsup.eleve.usecase

import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve
import fr.gouv.monprojetsup.eleve.domain.entity.VoeuFormation
import fr.gouv.monprojetsup.eleve.domain.port.CompteParcoursupRepository
import fr.gouv.monprojetsup.formation.domain.entity.TripletAffectation
import fr.gouv.monprojetsup.formation.domain.port.TripletAffectationRepository
import fr.gouv.monprojetsup.formation.entity.Communes
import fr.gouv.monprojetsup.parcoursup.domain.entity.FavorisParcoursup
import fr.gouv.monprojetsup.parcoursup.infrastructure.client.ParcoursupApiHttpClient
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixAlternance
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixDureeEtudesPrevue
import fr.gouv.monprojetsup.referentiel.domain.entity.ChoixNiveau
import fr.gouv.monprojetsup.referentiel.domain.entity.SituationAvanceeProjetSup
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.UUID

class MiseAJourFavorisParcoursupServiceTest {
    @Mock
    lateinit var compteParcoursupRepository: CompteParcoursupRepository

    @Mock
    lateinit var parcoursupApiHttpClient: ParcoursupApiHttpClient

    @Mock
    lateinit var tripletAffectationRepository: TripletAffectationRepository

    @InjectMocks
    lateinit var miseAJourFavorisParcoursupService: MiseAJourFavorisParcoursupService

    @BeforeEach
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `quand l'élève n'a pas connecté son compte parcoursup, ne pas appeler le repo de triplets d'affectation ni l'api parcoursup`() {
        // Given
        val profil =
            creerProfilIdentifie(
                formationsFavorites =
                    listOf(
                        VoeuFormation(
                            idFormation = "fl0010",
                            niveauAmbition = 1,
                            tripletsAffectationsChoisis = emptyList(),
                            priseDeNote = null,
                        ),
                        VoeuFormation(
                            idFormation = "fl0012",
                            niveauAmbition = 3,
                            tripletsAffectationsChoisis = listOf("ta1", "ta2"),
                            priseDeNote = "Mon voeu préféré",
                        ),
                    ),
            )
        given(compteParcoursupRepository.recupererIdCompteParcoursup(idEleve)).willReturn(null)

        // When
        val resultat = miseAJourFavorisParcoursupService.mettreAJourFavorisParcoursup(profil)

        // Then
        assertThat(resultat).isEqualTo(profil)
        then(parcoursupApiHttpClient).shouldHaveNoInteractions()
        then(tripletAffectationRepository).shouldHaveNoInteractions()
    }

    @Test
    fun `quand pas de formations favorites sur parcoursup, doit renvoyer le profil et ne pas appeler le repo de triplets d'affectation`() {
        // Given
        val profil =
            creerProfilIdentifie(
                formationsFavorites =
                    listOf(
                        VoeuFormation(
                            idFormation = "fl0010",
                            niveauAmbition = 1,
                            tripletsAffectationsChoisis = emptyList(),
                            priseDeNote = null,
                        ),
                        VoeuFormation(
                            idFormation = "fl0012",
                            niveauAmbition = 3,
                            tripletsAffectationsChoisis = listOf("ta1", "ta2"),
                            priseDeNote = "Mon voeu préféré",
                        ),
                    ),
            )
        given(compteParcoursupRepository.recupererIdCompteParcoursup(idEleve)).willReturn(510)
        given(parcoursupApiHttpClient.recupererLesTripletsAffectationSelectionnesSurParcoursup(510)).willReturn(emptyList())

        // When
        val resultat = miseAJourFavorisParcoursupService.mettreAJourFavorisParcoursup(profil)

        // Then
        assertThat(resultat).isEqualTo(profil)
    }

    @Test
    fun `quand déjà à jour, alors doit renvoyer le profil d'entrée et ne pas appeler le repo de triplets d'affectation`() {
        // Given
        val profil =
            creerProfilIdentifie(
                formationsFavorites =
                    listOf(
                        VoeuFormation(
                            idFormation = "fl0010",
                            niveauAmbition = 1,
                            tripletsAffectationsChoisis = emptyList(),
                            priseDeNote = null,
                        ),
                        VoeuFormation(
                            idFormation = "fl0012",
                            niveauAmbition = 3,
                            tripletsAffectationsChoisis = listOf("ta1", "ta2"),
                            priseDeNote = "Mon voeu préféré",
                        ),
                    ),
            )
        given(compteParcoursupRepository.recupererIdCompteParcoursup(idEleve)).willReturn(510)
        val voeuxParcoursup = listOf(FavorisParcoursup("ta2", null, 0))
        given(parcoursupApiHttpClient.recupererLesTripletsAffectationSelectionnesSurParcoursup(510)).willReturn(voeuxParcoursup)
        given(tripletAffectationRepository.recupererTripletsAffectation(listOf("ta2"))).willReturn(
            mapOf(
                "fl0012" to
                    listOf(
                        TripletAffectation(
                            id = "ta2",
                            nom = "Mon affecation 2",
                            commune = Communes.PARIS15EME,
                        ),
                    ),
            ),
        )

        // When
        val resultat = miseAJourFavorisParcoursupService.mettreAJourFavorisParcoursup(profil)

        // Then
        assertThat(resultat).isEqualTo(profil)
    }

    @Test
    fun `alors doit mettre à jour les triplets d'affectation des formations non présnete et ne pas toucher les existantes`() {
        // Given
        val profil =
            creerProfilIdentifie(
                formationsFavorites =
                    listOf(
                        VoeuFormation(
                            idFormation = "fl0010",
                            niveauAmbition = 1,
                            tripletsAffectationsChoisis = emptyList(),
                            priseDeNote = null,
                        ),
                        VoeuFormation(
                            idFormation = "fl0012",
                            niveauAmbition = 3,
                            tripletsAffectationsChoisis = listOf("ta1", "ta2"),
                            priseDeNote = "Mon voeu préféré",
                        ),
                    ),
            )
        given(compteParcoursupRepository.recupererIdCompteParcoursup(idEleve)).willReturn(510)
        val voeuxParcoursup =
            listOf(
                FavorisParcoursup(idTripletAffectation = "ta1", commentaire = null, notation = 0),
                FavorisParcoursup(idTripletAffectation = "ta7", commentaire = null, notation = 0),
                FavorisParcoursup(idTripletAffectation = "ta18", commentaire = null, notation = 0),
                FavorisParcoursup(idTripletAffectation = "ta19", commentaire = null, notation = 0),
            )
        given(parcoursupApiHttpClient.recupererLesTripletsAffectationSelectionnesSurParcoursup(510)).willReturn(voeuxParcoursup)
        given(tripletAffectationRepository.recupererTripletsAffectation(listOf("ta1", "ta7", "ta18", "ta19"))).willReturn(
            mapOf(
                "fl0012" to
                    listOf(
                        TripletAffectation(
                            id = "ta1",
                            nom = "Mon affecation 1",
                            commune = Communes.MARSEILLE,
                        ),
                        TripletAffectation(
                            id = "ta18",
                            nom = "Mon affecation 18",
                            commune = Communes.SAINT_MALO,
                        ),
                    ),
                "fl0010" to
                    listOf(
                        TripletAffectation(
                            id = "ta7",
                            nom = "Mon affecation 7",
                            commune = Communes.PARIS15EME,
                        ),
                    ),
                "fl0753" to
                    listOf(
                        TripletAffectation(
                            id = "ta19",
                            nom = "Mon affecation 19",
                            commune = Communes.PARIS5EME,
                        ),
                    ),
            ),
        )

        // When
        val resultat = miseAJourFavorisParcoursupService.mettreAJourFavorisParcoursup(profil)

        // Then
        val attendu =
            creerProfilIdentifie(
                formationsFavorites =
                    listOf(
                        VoeuFormation(
                            idFormation = "fl0010",
                            niveauAmbition = 1,
                            tripletsAffectationsChoisis = listOf("ta7"),
                            priseDeNote = null,
                        ),
                        VoeuFormation(
                            idFormation = "fl0012",
                            niveauAmbition = 3,
                            tripletsAffectationsChoisis = listOf("ta1", "ta2", "ta18"),
                            priseDeNote = "Mon voeu préféré",
                        ),
                        VoeuFormation(
                            idFormation = "fl0753",
                            niveauAmbition = 0,
                            tripletsAffectationsChoisis = listOf("ta19"),
                            priseDeNote = null,
                        ),
                    ),
            )
        assertThat(resultat).isEqualTo(attendu)
    }

    private fun creerProfilIdentifie(formationsFavorites: List<VoeuFormation>) =
        ProfilEleve.Identifie(
            id = idEleve,
            situation = SituationAvanceeProjetSup.AUCUNE_IDEE,
            classe = ChoixNiveau.SECONDE,
            baccalaureat = "Général",
            specialites = listOf("4", "1006"),
            domainesInterets = listOf("animaux", "agroequipement"),
            centresInterets = listOf("linguistique", "voyage"),
            metiersFavoris = listOf("MET001"),
            dureeEtudesPrevue = ChoixDureeEtudesPrevue.COURTE,
            alternance = ChoixAlternance.INDIFFERENT,
            communesFavorites = listOf(Communes.PARIS15EME, Communes.MARSEILLE),
            formationsFavorites = formationsFavorites,
            moyenneGenerale = -1.0f,
            corbeilleFormations = listOf("fl1234", "fl5678"),
        )

    companion object {
        private val idEleve = UUID.fromString("0f88ddd1-62ef-436e-ad3f-cf56d5d14c15")
    }
}
