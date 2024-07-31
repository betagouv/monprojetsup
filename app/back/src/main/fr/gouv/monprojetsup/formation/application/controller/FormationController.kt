package fr.gouv.monprojetsup.formation.application.controller

import fr.gouv.monprojetsup.authentification.application.controller.AuthentifieController
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilConnecte
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve.Identifie
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEleve.Inconnu
import fr.gouv.monprojetsup.authentification.domain.entity.ProfilEnseignant
import fr.gouv.monprojetsup.commun.erreur.domain.MonProjetSupBadRequestException
import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.formation.application.dto.FormationAvecExplicationsDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationCourteDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationsAvecExplicationsDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationsCourtesDTO
import fr.gouv.monprojetsup.formation.domain.entity.AffiniteSpecialite
import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationTypeBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.FormationCourte
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.MoyenneGeneraleDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.MoyenneGeneraleDesAdmis.Centile
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.RepartitionAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.RepartitionAdmis.TotalAdmisPourUnBaccalaureat
import fr.gouv.monprojetsup.formation.usecase.RechercherFormationsService
import fr.gouv.monprojetsup.formation.usecase.RecupererFormationService
import fr.gouv.monprojetsup.formation.usecase.RecupererFormationsService
import fr.gouv.monprojetsup.formation.usecase.SuggestionsFormationsService
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/formations")
@Tag(name = "Formation", description = "API des formations proposées sur MonProjetSup")
class FormationController(
    val suggestionsFormationsService: SuggestionsFormationsService,
    val recupererFormationService: RecupererFormationService,
    val recupererFormationsService: RecupererFormationsService,
    val rechercherFormation: RechercherFormationsService,
) : AuthentifieController() {
    @GetMapping("/suggestions")
    @Operation(summary = "Récupérer les suggestions de formations pour un profil d'élève")
    fun getSuggestionsFormations(): FormationsAvecExplicationsDTO {
        val formationsPourProfil: List<FicheFormation.FicheFormationPourProfil> =
            suggestionsFormationsService.suggererFormations(
                profilEleve = recupererEleveIdentifie(),
                deLIndex = 0,
                aLIndex = NOMBRE_FORMATIONS_SUGGEREES,
            )
        return FormationsAvecExplicationsDTO(
            formations = formationsPourProfil.map { FormationAvecExplicationsDTO(it) },
        )
    }

    @GetMapping("/{idformation}")
    fun getFormation(
        @PathVariable("idformation") idFormation: String,
    ): FormationAvecExplicationsDTO {
        val profil =
            when (val utilisateur = recupererUtilisateur()) {
                is Identifie -> utilisateur
                is Inconnu, ProfilEnseignant, ProfilConnecte -> null
            }
        val ficheFormation = recupererFormationService.recupererFormation(profilEleve = profil, idFormation = idFormation)
        return FormationAvecExplicationsDTO(ficheFormation)
    }

    @GetMapping("/recherche/succincte")
    fun getRechercheFormationSuccincte(
        @RequestParam recherche: String,
    ): FormationsCourtesDTO {
        val formationRecherchees = recupererLesFormationsAssocieesALaRecherche(recherche)
        return FormationsCourtesDTO(
            formationRecherchees.map { FormationCourteDTO(it) },
        )
    }

    @GetMapping("/recherche/detaillee")
    fun getRechercheFormationDetaillee(
        @RequestParam recherche: String,
    ): FormationsAvecExplicationsDTO {
        val formations = listOf(uneFormationMocke, uneFormationMocke2)
        return FormationsAvecExplicationsDTO(
            formations =
                formations.filter {
                    it.formation.nom.contains(recherche, true) ||
                        it.formation.descriptifFormation?.contains(recherche, true) == true ||
                        it.formation.descriptifAttendus?.contains(recherche, true) == true ||
                        it.formation.descriptifDiplome?.contains(recherche, true) == true ||
                        it.formation.descriptifConseils?.contains(recherche, true) == true
                },
        )
    }

    @GetMapping
    fun getFormations(
        @RequestParam ids: List<String>,
    ): FormationsAvecExplicationsDTO {
        val profilEleve = recupererEleveIdentifie()
        val toutesLesSuggestions = suggestionsFormationsService.recupererToutesLesSuggestionsPourUnProfil(profilEleve)
        val formations = recupererFormationsService.recupererFichesFormationPourProfil(profilEleve, toutesLesSuggestions, ids)
        return FormationsAvecExplicationsDTO(formations = formations.map { FormationAvecExplicationsDTO(it) })
    }

    @Throws(MonProjetSupBadRequestException::class)
    private fun recupererLesFormationsAssocieesALaRecherche(recherche: String): List<FormationCourte> {
        if (recherche.length > TAILLE_MAXIMAL_RECHERCHE) {
            throw MonProjetSupBadRequestException(
                code = "REQUETE_TROP_LONGUE",
                msg = "La taille de la requête dépasse la taille maximale de $TAILLE_MAXIMAL_RECHERCHE caractères",
            )
        } else if (recherche.length < TAILLE_MINIMUM_RECHERCHE) {
            throw MonProjetSupBadRequestException(
                code = "REQUETE_TROP_COURTE",
                msg = "La taille de la requête est trop courte. Elle doit faire au moins $TAILLE_MINIMUM_RECHERCHE caractères",
            )
        }
        val formationRecherchees = rechercherFormation.rechercheLesFormationsCorrespondantes(recherche)
        return formationRecherchees
    }

    companion object {
        private const val NOMBRE_FORMATIONS_SUGGEREES = 30
        const val TAILLE_MINIMUM_RECHERCHE = 2
        private const val TAILLE_MAXIMAL_RECHERCHE = 50

        private val uneFormationMocke2 =
            FormationAvecExplicationsDTO(
                ficheFormation =
                    FicheFormation.FicheFormationPourProfil(
                        id = "fl810007",
                        nom = "BUT - Génie mécanique et productique",
                        descriptifGeneral =
                            "Que vas -tu apprendre ? Tu pourras gérer la mise sur le marché d 'un produit industriel, " +
                                "depuis sa conception à l' organisation des lignes de production . C 'est donc une formation large qui " +
                                "mêle les statistiques, la science des matériaux, les mathématiques appliquées, mais aussi une " +
                                "formation technologique poussée. En 2ème année, tu pourras choisir parmi différents parcours pour " +
                                "développer des compétences supplémentaires, parmi la conception, l' innovation, ou encore la " +
                                "simulation numérique... Tu l'auras compris, c'est une formation passionnante si la technologie " +
                                "t'intéresse, car elle te permet de développer des connaissances sur toute la chaîne de conception. " +
                                "Par ailleurs, ce sont des profils très recherchés sur le marché du travail à l'heure actuelle !",
                        descriptifAttendus =
                            "COMPETENCES GENERALES<br/>- Être intéressé par l’industrie et ses métiers,<br/>- Avoir " +
                                "une maitrise du français permettant de communiquer à l’écrit et à l’oral de façon adaptée, de " +
                                "comprendre un énoncé, de l’analyser et de rédiger une solution,<br/>- Avoir une connaissance " +
                                "suffisante de l’anglais permettant de progresser pendant la formation : échanger à l’oral, lire " +
                                "et comprendre un texte, répondre aux questions écrites et orales,<br/>- Savoir mobiliser ses " +
                                "connaissances et développer un sens critique,<br/>- Être capable d’évoluer dans un environnement " +
                                "numérique et détenir des connaissances de base en bureautique.<br/><br/>COMPETENCES TECHNIQUES ET " +
                                "SCIENTIFIQUES<br/>- Avoir une bonne culture scientifique,<br/>- Savoir élaborer un raisonnement " +
                                "structuré et adapté à une situation scientifique,<br/>- Savoir mobiliser ses connaissances pour " +
                                "répondre à une résolution de problème,<br/>- Avoir une curiosité scientifique, technologique et " +
                                "expérimentale,<br/>- Être capable d’appliquer une technique de résolution de problème, qu'il soit " +
                                "scientifique ou technique,<br/>- Avoir un intérêt pour les manipulations pratiques, aimer " +
                                "expérimenter et avoir le goût de la réalisation.<br/><br/>QUALITES HUMAINES<br/>- Avoir une première " +
                                "réflexion sur son projet professionnel,<br/>- Avoir l'esprit d'équipe et savoir s'intégrer dans les " +
                                "travaux de groupe via les projets et les travaux pratiques,<br/>- Avoir le sens pratique, être " +
                                "attentif et rigoureux,<br/>- Montrer son intérêt et sa motivation pour les matières relevant des " +
                                "sciences et techniques,<br/>- Savoir s'impliquer et s'organiser dans ses études pour fournir le " +
                                "travail nécessaire à sa réussite.",
                        descriptifDiplome =
                            "Un BUT se prépare en 3 ans comme une licence, mais tu étudies en IUT. Ce type de formation " +
                                "te permet ainsi de suivre des cours magistraux en amphi, des travaux pratiques et des travaux dirigés " +
                                "en petits groupes. Tu devras aussi mener des projets tutorés sur plusieurs semaines, et effectuer " +
                                "plusieurs stages. Au cours de la première année, tu acquiers des compétences générales, puis tu " +
                                "commences à te spécialiser dès la deuxième année. Enfin, tu as souvent le choix entre des formations " +
                                "100 % en cours, ou bien en alternance (cours + expérience en entreprise) via l'apprentissage.",
                        descriptifConseils =
                            "Cette formation accueille des profils variés issus de la voie technologique, de la " +
                                "voie générale et, pour de très bons dossiers, de la voie professionnelle. <br/>La capacité " +
                                "d'accueil prévoit un nombre de places priorisé pour les bacheliers technologiques.",
                        formationsAssociees = listOf(),
                        liens =
                            listOf(
                                Lien(
                                    nom = "BUT - Génie mécanique et productique",
                                    url =
                                        "https://explorer-avenirs.onisep.fr/recherche?context=formation" +
                                            "&text=BUT%20G%C3%A9nie%20m%C3%A9canique%20et%20productique",
                                ),
                            ),
                        criteresAnalyseCandidature =
                            listOf(
                                CritereAnalyseCandidature(
                                    nom = "Compétences académiques",
                                    pourcentage = 16,
                                ),
                                CritereAnalyseCandidature(
                                    nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                    pourcentage = 4,
                                ),
                                CritereAnalyseCandidature(
                                    nom = "Résultats académiques",
                                    pourcentage = 47,
                                ),
                                CritereAnalyseCandidature(
                                    nom = "Savoir-être",
                                    pourcentage = 16,
                                ),
                                CritereAnalyseCandidature(
                                    nom = "Motivation, connaissance",
                                    pourcentage = 15,
                                ),
                            ),
                        statistiquesDesAdmis =
                            StatistiquesDesAdmis(
                                repartitionAdmis =
                                    RepartitionAdmis(
                                        total = 2724,
                                        parBaccalaureat =
                                            listOf(
                                                TotalAdmisPourUnBaccalaureat(
                                                    baccalaureat =
                                                        Baccalaureat(
                                                            id = "P",
                                                            idExterne = "P",
                                                            nom = "Bac Pro",
                                                        ),
                                                    nombreAdmis = 26,
                                                ),
                                                TotalAdmisPourUnBaccalaureat(
                                                    baccalaureat =
                                                        Baccalaureat(
                                                            id = "STI2D",
                                                            idExterne = "STI2D",
                                                            nom =
                                                                "Bac Techno STI2D - Sciences et Technologies de " +
                                                                    "l'Industrie et du Développement Durable",
                                                        ),
                                                    nombreAdmis = 1289,
                                                ),
                                                TotalAdmisPourUnBaccalaureat(
                                                    baccalaureat =
                                                        Baccalaureat(
                                                            id = "Générale",
                                                            idExterne = "Générale",
                                                            nom = "Série Générale",
                                                        ),
                                                    nombreAdmis = 1402,
                                                ),
                                                TotalAdmisPourUnBaccalaureat(
                                                    baccalaureat =
                                                        Baccalaureat(
                                                            id = "STD2A",
                                                            idExterne = "STD2A",
                                                            nom =
                                                                "Bac Techno STD2A - Sciences Technologiques " +
                                                                    "du Design et des Arts Appliquée",
                                                        ),
                                                    nombreAdmis = 2,
                                                ),
                                                TotalAdmisPourUnBaccalaureat(
                                                    baccalaureat =
                                                        Baccalaureat(
                                                            id = "STL",
                                                            idExterne = "STL",
                                                            nom = "Bac Techno STL - Sciences et technologie de laboratoire",
                                                        ),
                                                    nombreAdmis = 5,
                                                ),
                                            ),
                                    ),
                                moyenneGeneraleDesAdmis =
                                    MoyenneGeneraleDesAdmis(
                                        baccalaureat =
                                            Baccalaureat(
                                                id = "Générale",
                                                idExterne = "Générale",
                                                nom = "Série Générale",
                                            ),
                                        centiles =
                                            listOf(
                                                Centile(
                                                    centile = 5,
                                                    note = 11.0f,
                                                ),
                                                Centile(
                                                    centile = 25,
                                                    note = 12.5f,
                                                ),
                                                Centile(
                                                    centile = 75,
                                                    note = 14.5f,
                                                ),
                                                Centile(
                                                    centile = 95,
                                                    note = 16.0f,
                                                ),
                                            ),
                                    ),
                            ),
                        tauxAffinite = 66,
                        metiersTriesParAffinites =
                            listOf(
                                Metier(
                                    id = "MET_348",
                                    nom = "chargé / chargée d'affaires en génie mécanique",
                                    descriptif =
                                        "<p>Le chargé d'affaires en génie mécanique commercialise des produits de l'industrie, " +
                                            "qu'il s'agisse d'une machine-outil ou de produits beaucoup plus complexes. Il intervient " +
                                            "depuis la prospection de clients jusqu'au service après-vente.</p>",
                                    liens =
                                        listOf(
                                            Lien(
                                                nom = "Management et ingénierie d'affaires",
                                                url = "https://candidat.pole-emploi.fr/marche-du-travail/fichemetierrome?codeRome=H1102",
                                            ),
                                            Lien(
                                                nom = "chargé / chargée d'affaires en génie mécanique",
                                                url = "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.348",
                                            ),
                                        ),
                                ),
                                Metier(
                                    id = "MET_528",
                                    nom = "<span>Technicien / technicienne</span> <br> <span>en mécanique</span>",
                                    descriptif =
                                        "Dans de nombreux secteurs, le technicien mécanicien est compétent enmécanique et " +
                                            "électromécanique sur des machines et des installations. Cependant, comme la " +
                                            "mécatronique et l'automatisation enrichissent la mécanique traditionnelle, " +
                                            "l’électronique et l’informatique figurent aussi parmi les connaissances requises.<br/>" +
                                            "<br/><h3>Après le bac</h3>\n" +
                                            "        <p>2 ans pour préparer le BTS conception des processus de réalisation de produits " +
                                            "(option A : production unitaire ou option B : production sérielle) ; 3 ans pour le " +
                                            "BUT génie mécanique et productique, ou une licence professionnelle de la spécialité " +
                                            "(1 an après un bac + 2).</p>",
                                    liens =
                                        listOf(
                                            Lien(
                                                nom = "<span>Technicien / technicienne</span> <br> <span>en mécanique</span>",
                                                url = "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.528",
                                            ),
                                        ),
                                ),
                                Metier(
                                    id = "MET_462",
                                    nom = "technicien / technicienne en métrologie",
                                    descriptif =
                                        "<p>Selon l'entreprise pour laquelle il travaille, le technicien en métrologie a pour " +
                                            "mission de vérifier la bonne mesure des pièces ou le bon réglage des appareils servant à " +
                                            "mesurer ces mêmes pièces. Une tâche qui exige minutie et précision.</p>",
                                    liens =
                                        listOf(
                                            Lien(
                                                nom = "Intervention technique qualité en mécanique et travail des métaux",
                                                url = "https://candidat.pole-emploi.fr/marche-du-travail/fichemetierrome?codeRome=H1506",
                                            ),
                                            Lien(
                                                nom = "technicien / technicienne en métrologie",
                                                url = "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.462",
                                            ),
                                        ),
                                ),
                            ),
                        communesTrieesParAffinites = listOf(),
                        explications =
                            ExplicationsSuggestionDetaillees(
                                geographique =
                                    listOf(
                                        ExplicationGeographique(
                                            ville = "Paris",
                                            distanceKm = 4,
                                        ),
                                    ),
                                dureeEtudesPrevue = null,
                                alternance = null,
                                specialitesChoisies =
                                    listOf(
                                        AffiniteSpecialite(
                                            nomSpecialite = "Innovation Technologique (IT)",
                                            pourcentage = 47,
                                        ),
                                    ),
                                formationsSimilaires = listOf(),
                                interets = listOf(),
                                domaines =
                                    listOf(
                                        Domaine(
                                            id = "T_ITM_1534",
                                            nom = "mécanique",
                                            emoji = "\uD83D\uDD27",
                                        ),
                                    ),
                                explicationAutoEvaluationMoyenne =
                                    FicheFormation.FicheFormationPourProfil.ExplicationAutoEvaluationMoyenne(
                                        baccalaureatUtilise =
                                            Baccalaureat(
                                                id = "Générale",
                                                idExterne = "Générale",
                                                nom = "Série Générale",
                                            ),
                                        moyenneAutoEvalue = 14.0f,
                                        basIntervalleNotes = 12.0f,
                                        hautIntervalleNotes = 14.0f,
                                    ),
                                explicationTypeBaccalaureat =
                                    ExplicationTypeBaccalaureat(
                                        baccalaureat =
                                            Baccalaureat(
                                                id = "Générale",
                                                idExterne = "Générale",
                                                nom = "Série Générale",
                                            ),
                                        pourcentage = 51,
                                    ),
                            ),
                    ),
            )

        private val uneFormationMocke =
            FormationAvecExplicationsDTO(
                ficheFormation =
                    FicheFormation.FicheFormationPourProfil(
                        id = "fl680002",
                        nom = "Cycle pluridisciplinaire d'Études Supérieures - Sciences",
                        descriptifGeneral =
                            "Les cycles pluridisciplinaires d’ études supérieures sont de nouveaux cursus d’excellence " +
                                "en 3 ans, créés à la rentrée 2022. Ils visent à favoriser l'égalité des chances, notamment par " +
                                "l’accueil privilégié de candidats boursiers. Ils débouchent sur un niveau licence et ouvrent un " +
                                "large accès aux 2e cycles universitaires ou d’écoles.",
                        descriptifAttendus = null,
                        descriptifDiplome = null,
                        descriptifConseils = null,
                        formationsAssociees =
                            listOf(
                                "fl680013",
                                "fl680014",
                                "fl680012",
                                "fl680017",
                                "fl680018",
                                "fl680015",
                                "fl680010",
                                "fl680019",
                                "fl680008",
                                "fl680009",
                            ),
                        liens =
                            listOf(
                                Lien(
                                    nom = "Cycle pluridisciplinaire d'Études Supérieures - Sciences",
                                    url =
                                        "https://explorer-avenirs.onisep.fr/formation/apres-le-bac-les-etudes-superieures/" +
                                            "les-principales-filieres-d-etudes-superieures/" +
                                            "les-cycles-pluridisciplinaires-d-etudes-superieures-cpes",
                                ),
                            ),
                        criteresAnalyseCandidature =
                            listOf(
                                CritereAnalyseCandidature(
                                    nom = "Compétences académiques",
                                    pourcentage = 11,
                                ),
                                CritereAnalyseCandidature(
                                    nom = "Engagements, activités et centres d’intérêt, réalisations péri ou extra-scolaires",
                                    pourcentage = 3,
                                ),
                                CritereAnalyseCandidature(
                                    nom = "Résultats académiques",
                                    pourcentage = 59,
                                ),
                                CritereAnalyseCandidature(
                                    nom = "Savoir-être",
                                    pourcentage = 7,
                                ),
                                CritereAnalyseCandidature(
                                    nom = "Motivation, connaissance",
                                    pourcentage = 11,
                                ),
                            ),
                        statistiquesDesAdmis =
                            StatistiquesDesAdmis(
                                repartitionAdmis =
                                    RepartitionAdmis(
                                        total = 89,
                                        parBaccalaureat =
                                            listOf(
                                                TotalAdmisPourUnBaccalaureat(
                                                    baccalaureat =
                                                        Baccalaureat(
                                                            id = "Générale",
                                                            idExterne = "Générale",
                                                            nom = "Série Générale",
                                                        ),
                                                    nombreAdmis = 89,
                                                ),
                                            ),
                                    ),
                                moyenneGeneraleDesAdmis =
                                    MoyenneGeneraleDesAdmis(
                                        baccalaureat =
                                            Baccalaureat(
                                                id = "Générale",
                                                idExterne = "Générale",
                                                nom = "Série Générale",
                                            ),
                                        centiles =
                                            listOf(
                                                Centile(
                                                    centile = 5,
                                                    note = 14.5f,
                                                ),
                                                Centile(
                                                    centile = 25,
                                                    note = 16.5f,
                                                ),
                                                Centile(
                                                    centile = 75,
                                                    note = 18.5f,
                                                ),
                                                Centile(
                                                    centile = 95,
                                                    note = 19.0f,
                                                ),
                                            ),
                                    ),
                            ),
                        tauxAffinite = 90,
                        metiersTriesParAffinites =
                            listOf(
                                Metier(
                                    id = "MET_611",
                                    nom = "architecte produit industriel",
                                    descriptif =
                                        "< p>L'architecte produit industriel améliore des produits ou des technologies " +
                                            "existants, ou en conçoit de nouveaux. Ses objectifs : apporter une réponse innovante " +
                                            "à un besoin exprimé et connu, ou imaginer un produit qui créera un nouveau besoin et " +
                                            "un nouveau marché.</p>",
                                    liens =
                                        listOf(
                                            Lien(
                                                nom = "Management et ingénierie études, recherche et développement industriel",
                                                url = "https://candidat.pole-emploi.fr/marche-du-travail/fichemetierrome? codeRome=H1206",
                                            ),
                                            Lien(
                                                nom = "architecte produit industriel",
                                                url = "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.611",
                                            ),
                                        ),
                                ),
                                Metier(
                                    id = "MET_610",
                                    nom = "chargé / chargée d'études naturalistes",
                                    descriptif =
                                        "<p>Inventorier, protéger et valoriser les espèces animales et végétales, telles sont " +
                                            "les missions de la chargée ou du chargé d'études naturalistes. Ces professionnels " +
                                            "interviennent sur un espace naturel protégé ou en amont d'un projet d'urbanisme.</p>",
                                    liens =
                                        listOf(
                                            Lien(
                                                nom = "Ingénierie en agriculture et environnement naturel",
                                                url = "https://candidat.pole-emploi.fr/marche-du-travail/fichemetierrome?codeRome=A1303",
                                            ),
                                            Lien(
                                                nom = "chargé / chargée d'études naturalistes",
                                                url = "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.610",
                                            ),
                                        ),
                                ),
                                Metier(
                                    id = "MET_613",
                                    nom = "data manager",
                                    descriptif =
                                        "<p>Né de la multiplication et de la complexification des données, `" +
                                            "le data manager, ou gestionnaire de données, recueille et organise les " +
                                            "informations de l'entreprise, en vue de leur exploitation optimale. Il travaille " +
                                            "désormais dans tous les secteurs.</p>",
                                    liens =
                                        listOf(
                                            Lien(
                                                nom = "data manager",
                                                url = "https://explorer-avenirs.onisep.fr/http/redirection/metier/slug/MET.613)",
                                            ),
                                            Lien(
                                                nom = "Expertise et support en systèmes d'information",
                                                url = "https://candidat.pole-emploi.fr/marche-du-travail/fichemetierrome?codeRome=M1802",
                                            ),
                                        ),
                                ),
                            ),
                        communesTrieesParAffinites = listOf(),
                        explications =
                            ExplicationsSuggestionDetaillees(
                                geographique = listOf(),
                                dureeEtudesPrevue = null,
                                alternance = null,
                                specialitesChoisies = listOf(),
                                formationsSimilaires = listOf(),
                                interets =
                                    listOf(
                                        InteretSousCategorie(
                                            id = "chiffres_jongler",
                                            nom = "Jongler avec les chiffres",
                                            emoji = "\uD83D\uDCAF",
                                        ),
                                        InteretSousCategorie(
                                            id = "aider_autres",
                                            nom = "Aider les autres",
                                            emoji = "\uD83E\uDEC2",
                                        ),
                                    ),
                                domaines =
                                    listOf(
                                        Domaine(
                                            id = "T_ITM_1054",
                                            nom = "philosophie",
                                            emoji = "\uD83D\uDCD6",
                                        ),
                                        Domaine(
                                            id = "T_ITM_1351",
                                            nom = "agriculture",
                                            emoji = "\uD83E\uDD55",
                                        ),
                                        Domaine(
                                            id = "T_ITM_1534",
                                            nom = "mécanique",
                                            emoji = "\uD83D\uDD27",
                                        ),
                                        Domaine(
                                            id = "T_ITM_1248",
                                            nom = "bâtiment - construction",
                                            emoji = "\uD83D\uDEA7",
                                        ),
                                    ),
                                explicationAutoEvaluationMoyenne =
                                    FicheFormation.FicheFormationPourProfil.ExplicationAutoEvaluationMoyenne(
                                        baccalaureatUtilise =
                                            Baccalaureat(
                                                id = "Générale",
                                                idExterne = "Générale",
                                                nom = "Série Générale",
                                            ),
                                        moyenneAutoEvalue = 14.0f,
                                        basIntervalleNotes = 11.0f,
                                        hautIntervalleNotes = 14.0f,
                                    ),
                                explicationTypeBaccalaureat =
                                    ExplicationTypeBaccalaureat(
                                        baccalaureat =
                                            Baccalaureat(
                                                id = "Générale",
                                                idExterne = "Générale",
                                                nom = "Série Générale",
                                            ),
                                        pourcentage = 100,
                                    ),
                            ),
                    ),
            )
    }
}
