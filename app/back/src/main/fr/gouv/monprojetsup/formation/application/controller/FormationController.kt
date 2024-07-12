package fr.gouv.monprojetsup.formation.application.controller

import fr.gouv.monprojetsup.commun.lien.domain.entity.Lien
import fr.gouv.monprojetsup.formation.application.dto.FormationAvecExplicationsDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationCourteDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationsAvecExplicationsDTO
import fr.gouv.monprojetsup.formation.application.dto.FormationsCourtesDTO
import fr.gouv.monprojetsup.formation.application.dto.ProfilObligatoireRequeteDTO
import fr.gouv.monprojetsup.formation.application.dto.ProfilOptionnelRequeteDTO
import fr.gouv.monprojetsup.formation.domain.entity.AffiniteSpecialite
import fr.gouv.monprojetsup.formation.domain.entity.CritereAnalyseCandidature
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationGeographique
import fr.gouv.monprojetsup.formation.domain.entity.ExplicationsSuggestionDetaillees
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation
import fr.gouv.monprojetsup.formation.domain.entity.FicheFormation.FicheFormationPourProfil.ExplicationTypeBaccalaureat
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.MoyenneGeneraleDesAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.MoyenneGeneraleDesAdmis.Centile
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.RepartitionAdmis
import fr.gouv.monprojetsup.formation.domain.entity.StatistiquesDesAdmis.RepartitionAdmis.TotalAdmisPourUnBaccalaureat
import fr.gouv.monprojetsup.formation.usecase.RecupererFormationService
import fr.gouv.monprojetsup.formation.usecase.RecupererFormationsService
import fr.gouv.monprojetsup.formation.usecase.SuggestionsFormationsService
import fr.gouv.monprojetsup.metier.domain.entity.Metier
import fr.gouv.monprojetsup.referentiel.domain.entity.Baccalaureat
import fr.gouv.monprojetsup.referentiel.domain.entity.Domaine
import fr.gouv.monprojetsup.referentiel.domain.entity.InteretSousCategorie
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("api/v1/formations")
@RestController
class FormationController(
    val suggestionsFormationsService: SuggestionsFormationsService,
    val recupererFormationService: RecupererFormationService,
    val recupererFormationsService: RecupererFormationsService,
) {
    @PostMapping("/suggestions")
    fun postSuggestionsFormations(
        @RequestBody profilObligatoireRequeteDTO: ProfilObligatoireRequeteDTO,
    ): FormationsAvecExplicationsDTO {
        val formationsPourProfil: List<FicheFormation.FicheFormationPourProfil> =
            suggestionsFormationsService.suggererFormations(
                profilEleve = profilObligatoireRequeteDTO.profil.toProfil(),
                deLIndex = 0,
                aLIndex = NOMBRE_FORMATIONS_SUGGEREES,
            )
        return FormationsAvecExplicationsDTO(
            formations = formationsPourProfil.map { FormationAvecExplicationsDTO(it) },
        )
    }

    @PostMapping("/{idformation}")
    fun postFormation(
        @PathVariable("idformation") idFormation: String,
        @RequestBody profilOptionnelRequeteDTO: ProfilOptionnelRequeteDTO,
    ): FormationAvecExplicationsDTO {
        val ficheFormation =
            recupererFormationService.recupererFormation(
                profilEleve = profilOptionnelRequeteDTO.profil?.toProfil(),
                idFormation = idFormation,
            )
        return FormationAvecExplicationsDTO(ficheFormation)
    }

    @GetMapping("/recherche/succincte")
    fun getRechercheFormationSuccincte(
        @RequestParam recherche: String,
    ): FormationsCourtesDTO {
        val formations =
            listOf(
                FormationCourteDTO(id = "fl840009", "DN MADE - Numérique"),
                FormationCourteDTO(id = "fl840008", "DN MADE - Mode"),
                FormationCourteDTO(id = "fl840007", "DN MADE - Matériaux"),
                FormationCourteDTO(id = "fl840006", "DN MADE - Livre"),
                FormationCourteDTO(id = "fl840001", "DN MADE - Espace"),
                FormationCourteDTO(id = "fl840000", "DN MADE - Animation"),
                FormationCourteDTO(id = "fl840005", "DN MADE - Instrument"),
                FormationCourteDTO(id = "fl840004", "DN MADE - Innovation sociale"),
                FormationCourteDTO(id = "fl840003", "DN MADE - Graphisme"),
                FormationCourteDTO(id = "fl840002", "DN MADE - Événement"),
                FormationCourteDTO(id = "fl840012", "DN MADE - Patrimoine"),
                FormationCourteDTO(id = "fl840011", "DN MADE - Ornement"),
                FormationCourteDTO(id = "fl840010", "DN MADE - Objet"),
                FormationCourteDTO(id = "fl840013", "DN MADE - Spectacle"),
                FormationCourteDTO(id = "fr89000", "LP - Droit-économie-gestion"),
                FormationCourteDTO(id = "fl551002", "D.E Ergothérapeute - en apprentissage"),
                FormationCourteDTO(id = "fl2117", "L1 - Sciences de la vigne et du vin"),
                FormationCourteDTO(id = "fl691052", "FP - Responsable Commercial et Marketing (Bac +3) - en apprentissage"),
                FormationCourteDTO(id = "fr640", "BPJEPS"),
                FormationCourteDTO(id = "fr641", "DEJEPS"),
                FormationCourteDTO(id = "fl2100", "L1 - Sciences et société"),
                FormationCourteDTO(id = "fl2113", "L1 - Économie, Science politique"),
                FormationCourteDTO(id = "fl2112", "L1 - Sciences des systèmes communicants"),
                FormationCourteDTO(id = "fl2111", "L1 - Sciences de la transition écologique et sociétale"),
                FormationCourteDTO(id = "fl2110", "L1 - Sciences cognitives"),
                FormationCourteDTO(id = "fl2118", "L1 - Génie urbain"),
                FormationCourteDTO(id = "fl2116", "L1 - Langues, enseignement, médiation en milieu amazonien"),
                FormationCourteDTO(id = "fl2115", "L1 - Histoire de l'art"),
                FormationCourteDTO(id = "fl660008", "BTSA - Métiers du Végétal : Alimentation, Ornement, Environnement"),
                FormationCourteDTO(id = "fl660009", "BTSA - Analyses biologiques, biotechnologiques, agricoles et environnementales"),
                FormationCourteDTO(id = "fl660006", "BTSA - Technico-commercial - Spécialité produit de la filière bois"),
                FormationCourteDTO(id = "fl660007", "BTSA - Agronomie et cultures durables"),
                FormationCourteDTO(
                    id = "fl691166",
                    nom = "FP - Formation professionnelle - Manager en hôtellerie internationale (Bac +3) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl660004", "BTSA - Technico-commercial - Spécialité alimentation et boisson"),
                FormationCourteDTO(id = "fl660005", "BTSA - Technico-commercial - Spécialité univers jardins et animaux de compagnie"),
                FormationCourteDTO(id = "fl660002", "BTSA - Technico-commercial - Spécialité biens et services pour l'agriculture"),
                FormationCourteDTO(id = "fl660003", "BTSA - Technico-commercial - Spécialité vins, bières et spiritueux"),
                FormationCourteDTO(
                    id = "fl491025",
                    "CS - Techn. en énergies renouvelables opt. a énergie électrique - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl691167",
                    "FP - Formation professionnelle - Chef de projet e.business (Bac +3) - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl660011",
                    "BTSA - QUalité, ALimentation, Innovation et Maîtrise Sanitaire (BioQUALIM) aliments et processus technologiques",
                ),
                FormationCourteDTO(
                    id = "fl660010",
                    "BTSA - QUalité, ALimentation, Innovation et Maîtrise Sanitaire (BioQUALIM) produits laitiers",
                ),
                FormationCourteDTO(id = "fr89100", "LP - Sciences - technologies - santé"),
                FormationCourteDTO(
                    id = "fl1176",
                    "FP - Technicien supérieur en pharmacie et cosmétique industrielles (Bac +2) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl1183", "D.E manipulateur/trice en électroradiologie médicale - en apprentissage"),
                FormationCourteDTO(id = "fl491011", "CS - Cybersécurité - en apprentissage"),
                FormationCourteDTO(id = "fl491017", "CS - Maquettes et prototypes - en apprentissage"),
                FormationCourteDTO(
                    id = "fl491027",
                    "CS - Technicien(ne) en chaudronnerie " +
                        "aéronautique et spatiale - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl491026",
                    "CS - Techn. en énergies renouvelables opt. b énergie thermique - en apprentissage",
                ),
                FormationCourteDTO(id = "fl491024", "CS - Sommellerie - en apprentissage"),
                FormationCourteDTO(id = "fl491022", "CS - Production et réparation de produits électroniques - en apprentissage"),
                FormationCourteDTO(id = "fl491020", "CS - Organisateur de réception - en apprentissage"),
                FormationCourteDTO(id = "fl1", "Classe préparatoire aux études supérieures - Cinéma audiovisuel"),
                FormationCourteDTO(id = "fl3", "Classe préparatoire aux études supérieures - Economie-Gestion"),
                FormationCourteDTO(id = "fl2", "Classe préparatoire aux études supérieures - Sciences politiques et sociales"),
                FormationCourteDTO(id = "fl5", "Classe préparatoire aux études supérieures - Sciences économiques"),
                FormationCourteDTO(id = "fl4", "Classe préparatoire aux études supérieures - Arts"),
                FormationCourteDTO(id = "fl691158", "FP - Administrateur systèmes et réseaux (Bac +3) - en apprentissage"),
                FormationCourteDTO(id = "fl7", "Classe préparatoire aux études supérieures - Littéraire"),
                FormationCourteDTO(id = "fl6", "Classe préparatoire aux études supérieures - Scientifique"),
                FormationCourteDTO(id = "fl8", "Classe préparatoire aux études supérieures - Générale"),
                FormationCourteDTO(id = "fl491029", "CS - Technicien(ne) en réseaux électriques - en apprentissage"),
                FormationCourteDTO(id = "fl491004", "CS - Aéronautique option avions à moteurs à turbines - en apprentissage"),
                FormationCourteDTO(id = "fl491003", "CS - Aéronautique option avions à moteurs à pistons - en apprentissage"),
                FormationCourteDTO(id = "fl491001", "CS - Accueil-réception - en apprentissage"),
                FormationCourteDTO(id = "fl491008", "CS - Animation-gestion de projets dans le secteur sportif - en apprentissage"),
                FormationCourteDTO(id = "fl491006", "CS - Aéronautique option hélicoptères à moteurs à turbines - en apprentissage"),
                FormationCourteDTO(id = "fl720007", "DEUST - Guide nature multilingue"),
                FormationCourteDTO(id = "fl491031", "CS - Technicien(ne) en soudage - en apprentissage"),
                FormationCourteDTO(id = "fl610002", "MAN - Mise à niveau pour l'accès au BTS MASEN"),
                FormationCourteDTO(
                    id = "fl610001",
                    "MAN - Classe de mise à niveau au BTS Maritime Pêche et Gestion de l'Environnement Marin",
                ),
                FormationCourteDTO(id = "fr22", "C.M.I - Cursus Master en Ingénierie"),
                FormationCourteDTO(id = "fr44", "BTS - Maritime"),
                FormationCourteDTO(id = "fr70", "Diplôme d'établissement"),
                FormationCourteDTO(id = "fr63", "Année préparatoire"),
                FormationCourteDTO(id = "fr64", "FCIL"),
                FormationCourteDTO(id = "fr90", "Sciences Po / Instituts d'études politiques"),
                FormationCourteDTO(id = "fr81", "DMA"),
                FormationCourteDTO(id = "fr83", "CUPGE - Sciences, technologie, santé"),
                FormationCourteDTO(id = "fr85", "CUPGE - Droit-économie-gestion"),
                FormationCourteDTO(id = "fr86", "CUPGE - Arts Lettres Langues"),
                FormationCourteDTO(
                    id = "fl661009",
                    "BTSA - Analyses biologiques, biotechnologiques, agricoles et environnementales - en apprentissage",
                ),
                FormationCourteDTO(id = "fl661007", "BTSA - Agronomie et cultures durables - en apprentissage"),
                FormationCourteDTO(id = "fl481008", "CSA - Conduite d'un élevage ovin viande - en apprentissage"),
                FormationCourteDTO(
                    id = "fl661008",
                    "BTSA - Métiers du Végétal : Alimentation, Ornement, Environnement - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl661005",
                    "BTSA - Technico-commercial - Spécialité univers jardins et animaux de compagnie - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl661006",
                    "BTSA - Technico-commercial - Spécialité produit de la filière bois - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl661003",
                    "BTSA - Technico-commercial - Spécialité vins, bières et spiritueux - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl661004",
                    "BTSA - Technico-commercial - Spécialité alimentation et boisson - en apprentissage",
                ),
                FormationCourteDTO(id = "fl3004", "L1 - Gestion - en apprentissage"),
                FormationCourteDTO(
                    id = "fl661002",
                    "BTSA - Technico-commercial - Spécialité biens et services pour l'agriculture - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl661010",
                    "BTSA - QUalité, ALimentation, Innovation et Maîtrise Sanitaire " +
                        "(BioQUALIM) produits laitiers - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl661011",
                    "BTSA - QUalité, ALimentation, Innovation et Maîtrise Sanitaire (BioQUALIM) " +
                        "aliments et processus technologiques - en apprentissage",
                ),
                FormationCourteDTO(id = "fl250001", "EA-BAC3 - Archéologie, histoire de l'art, anthropologie"),
                FormationCourteDTO(id = "fl481031", "CSA - Conduite d’un élevage bovin viande  - en apprentissage"),
                FormationCourteDTO(id = "fl481033", "CSA - Sols sportifs engazonnés - en apprentissage"),
                FormationCourteDTO(id = "fl481032", "CSA - Arrosage automatique : espaces verts et sols sportifs - en apprentissage"),
                FormationCourteDTO(
                    id = "fl481035",
                    "CSA - Pilotage de machines agricoles et travaux mécanisés à haute technicité - en apprentissage",
                ),
                FormationCourteDTO(id = "fl481011", "CSA - Constructions paysagères - en apprentissage"),
                FormationCourteDTO(id = "fl481010", "CSA - Conduite de l'élevage porcin - en apprentissage"),
                FormationCourteDTO(id = "fl3036", "CUPGE - Cycle Universitaire Préparatoire aux Grandes Ecoles"),
                FormationCourteDTO(id = "fl721006", "DEUST - Préparateur/Technicien en pharmacie - en apprentissage"),
                FormationCourteDTO(id = "fl481012", "CSA - Education et travail des jeunes équidés - en apprentissage"),
                FormationCourteDTO(
                    id = "fl481015",
                    "CSA - Production, transformation et commercialisation des produits fermiers - en apprentissage",
                ),
                FormationCourteDTO(id = "fl481017", "CSA - Commercialisation du bétail : acheteur-estimateur - en apprentissage"),
                FormationCourteDTO(id = "fl481018", "CSA - Technicien spécialisé en transformation laitière - en apprentissage"),
                FormationCourteDTO(id = "fl481020", "CSA - Apiculture - en apprentissage"),
                FormationCourteDTO(id = "fl720009", "DEUST - Technicien en environnement et déchets"),
                FormationCourteDTO(id = "fl481021", "CSA - Technicien cynégétique - en apprentissage"),
                FormationCourteDTO(id = "fl481023", "CSA - Conduite d'un élevage bovin lait - en apprentissage"),
                FormationCourteDTO(id = "fl481002", "CSA - Arboriste-élagueur - en apprentissage"),
                FormationCourteDTO(id = "fl481006", "CSA - Conduite d'un élevage caprin - en apprentissage"),
                FormationCourteDTO(
                    id = "fl481005",
                    "CSA - Conduite d'un élevage avicole et commercialisation des produits - en apprentissage",
                ),
                FormationCourteDTO(id = "fl720008", "DEUST - Technicien en qualité et distribution des produits alimentaires"),
                FormationCourteDTO(id = "fl720000", "DEUST - Accueil d'excellence en tourisme"),
                FormationCourteDTO(id = "fl720005", "DEUST - Théâtre"),
                FormationCourteDTO(id = "fl720003", "DEUST - Bureautique et Communication Multimédia"),
                FormationCourteDTO(id = "fl720010", "DEUST - Infrastructures numériques"),
                FormationCourteDTO(id = "fl2003", "L1 - Economie"),
                FormationCourteDTO(id = "fl2002", "L1 - Droit"),
                FormationCourteDTO(id = "fl2001", "L1 - Administration publique"),
                FormationCourteDTO(id = "fl2009", "L1 - Humanités"),
                FormationCourteDTO(id = "fl2008", "L1 - Sciences sanitaires et sociales"),
                FormationCourteDTO(id = "fl2007", "L1 - Science politique"),
                FormationCourteDTO(id = "fl2006", "L1 - Administration économique et sociale"),
                FormationCourteDTO(id = "fl2004", "L1 - Gestion"),
                FormationCourteDTO(id = "fl2014", "L1 - Psychologie"),
                FormationCourteDTO(id = "fl850003", "DN MADE - Graphisme - en apprentissage"),
                FormationCourteDTO(id = "fl2013", "L1 - Sociologie"),
                FormationCourteDTO(id = "fl2012", "L1 - Géographie et aménagement"),
                FormationCourteDTO(id = "fl2010", "L1 - Histoire"),
                FormationCourteDTO(id = "fl2019", "L1 - Sciences de l'Homme, Anthropologie, Ethnologie"),
                FormationCourteDTO(id = "fl2018", "L1 - Sciences sociales"),
                FormationCourteDTO(id = "fl2017", "L1 - Théologie"),
                FormationCourteDTO(
                    id = "fl691150",
                    "FP - Titre professionnel - Technicien d'exploitation de " +
                        "tranches de production nucléaire (Bac +2) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl2016", "L1 - Philosophie"),
                FormationCourteDTO(id = "fl2015", "L1 - Sciences de l'éducation"),
                FormationCourteDTO(id = "fl2024", "L1 - Arts"),
                FormationCourteDTO(id = "fl2023", "L1 - Information et communication"),
                FormationCourteDTO(
                    id = "fl2022",
                    "L1 - Mathématiques et informatique " +
                        "appliquées aux sciences humaines et sociales",
                ),
                FormationCourteDTO(id = "fl2021", "L1 - Sciences du langage"),
                FormationCourteDTO(
                    id = "fl11436",
                    "BTS - Cybersécurité, Informatique et réseaux, ELectronique - " +
                        "Option A : Informatique et réseaux - en apprentissage",
                ),
                FormationCourteDTO(id = "fl2020", "L1 - Sciences et Techniques des Activités Physiques et Sportives"),
                FormationCourteDTO(id = "fl2029", "L1 - Langues, littératures et civilisations étrangères et régionales"),
                FormationCourteDTO(id = "fl2028", "L1 - Lettres"),
                FormationCourteDTO(id = "fl2027", "L1 - Musicologie"),
                FormationCourteDTO(id = "fl2035", "L1 - Chimie"),
                FormationCourteDTO(
                    id = "fl811511",
                    "BUT - Gestion administrative et " +
                        "commerciale des organisations - en apprentissage",
                ),
                FormationCourteDTO(id = "fl2034", "L1 - Physique"),
                FormationCourteDTO(id = "fl2033", "L1 - Mathématiques"),
                FormationCourteDTO(id = "fl2032", "L1 - Informatique"),
                FormationCourteDTO(id = "fl2030", "L1 - Langues étrangères appliquées"),
                FormationCourteDTO(id = "fl2038", "L1 - Sciences de la terre"),
                FormationCourteDTO(id = "fl2037", "L1 - Sciences de la vie"),
                FormationCourteDTO(
                    id = "fl691151",
                    "FP - Titre professionnel - Technicien Spécialisé des Laboratoires, " +
                        "Parfum Cosmétique et Arômes (Bac+2) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl2050", "L1 - Etudes culturelles"),
                FormationCourteDTO(id = "fl2047", "L1 - Parcours d'Accès Spécifique Santé (PASS)"),
                FormationCourteDTO(id = "fl2046", "L1 - Sciences et technologies"),
                FormationCourteDTO(id = "fl2044", "L1 - Sciences pour l'ingénieur"),
                FormationCourteDTO(id = "fl2043", "L1 - Sciences pour la santé"),
                FormationCourteDTO(id = "fl2053", "L1 - Administration et Echanges internationaux"),
                FormationCourteDTO(
                    id = "fl691153",
                    "FP - Formation professionnelle - Manager dans l'univers de la beauté (Bac +3) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl2042", "L1 - Génie civil"),
                FormationCourteDTO(id = "fl2041", "L1 - Mécanique"),
                FormationCourteDTO(id = "fl2040", "L1 - Electronique, énergie électrique, automatique"),
                FormationCourteDTO(id = "fl2061", "L1 - Sciences de la Terre et environnement"),
                FormationCourteDTO(id = "fl2060", "L1 - Cinéma"),
                FormationCourteDTO(id = "fl890002", "LP-DEG - Licence professionnelle - En apprentissage"),
                FormationCourteDTO(id = "fl2052", "L1 - Etudes européennes et internationales"),
                FormationCourteDTO(id = "fl2051", "L1 - Sciences et Humanités"),
                FormationCourteDTO(id = "fl2072", "L1 - Etudes théâtrales"),
                FormationCourteDTO(id = "fl2070", "L1 - Histoire des Arts"),
                FormationCourteDTO(id = "fl2067", "L1 - Histoire / Anglais"),
                FormationCourteDTO(id = "fl2063", "L1 - Lettres / Anglais"),
                FormationCourteDTO(id = "fl2073", "L1 - Acoustique et Vibrations"),
                FormationCourteDTO(id = "fl2094", "L1 - Sciences, Enseignement, Médiation"),
                FormationCourteDTO(
                    id = "fl656002",
                    "FP - Technicien(ne) supérieur(e) en maintenance industrielle (Bac +2) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl691188", "FP - Chargé de marketing et communication (Bac +3) - en apprentissage"),
                FormationCourteDTO(id = "fl2093", "L1 - Tourisme"),
                FormationCourteDTO(id = "fl2092", "L1 - Sciences de l'éducation et de la formation"),
                FormationCourteDTO(id = "fl2091", "L1 - Sciences biomédicales"),
                FormationCourteDTO(id = "fl2090", "L1 - Santé et société"),
                FormationCourteDTO(id = "fl2089", "L1 - Pluridisciplinaire"),
                FormationCourteDTO(id = "fl2096", "L1 - Sciences de la Terre et Sciences Physiques"),
                FormationCourteDTO(id = "fl2095", "L1 - STAPS, Sciences pour l'ingénieur"),
                FormationCourteDTO(
                    id = "fl1002029",
                    "L1 - Langues, littératures et civilisations étrangères et régionales -  Accès Santé (LAS)",
                ),
                FormationCourteDTO(id = "fl1002021", "L1 - Sciences du langage -  Accès Santé (LAS)"),
                FormationCourteDTO(
                    id = "fl1002022",
                    "L1 - Mathématiques et informatique appliquées aux sciences humaines et sociales -  Accès Santé (LAS)",
                ),
                FormationCourteDTO(id = "fl1002023", "L1 - Information et communication -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002027", "L1 - Musicologie -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl620125", "MC - Cybersécurité"),
                FormationCourteDTO(id = "fl1002028", "L1 - Lettres -  Accès Santé (LAS)"),
                FormationCourteDTO(
                    id = "fl1002020",
                    "L1 - Sciences et Techniques des Activités Physiques et Sportives -  Accès Santé (LAS)",
                ),
                FormationCourteDTO(id = "fl1002018", "L1 - Sciences sociales -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002019", "L1 - Sciences de l'Homme, Anthropologie, Ethnologie -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002010", "L1 - Histoire -  Accès Santé (LAS)"),
                FormationCourteDTO(
                    id = "fl11437",
                    "BTS - Cybersécurité, Informatique et réseaux, ELectronique - " +
                        "Option B : Electronique et réseaux - en apprentissage",
                ),
                FormationCourteDTO(id = "fl1002011", "L1 - Histoire de l'art et archéologie -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002012", "L1 - Géographie et aménagement -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002013", "L1 - Sociologie -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002014", "L1 - Psychologie -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002016", "L1 - Philosophie -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002007", "L1 - Science politique -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002008", "L1 - Sciences sanitaires et sociales -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002009", "L1 - Humanités -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002002", "L1 - Droit -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002003", "L1 - Economie -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002004", "L1 - Gestion -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl620103", "MC - Vendeur-conseil en produits techniques pour l'habitat"),
                FormationCourteDTO(id = "fl1002005", "L1 - Economie et gestion -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002006", "L1 - Administration économique et sociale -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002090", "L1 - Santé et société -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002091", "L1 - Sciences biomédicales -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002092", "L1 - Sciences de l'éducation et de la formation -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl11438", "BTS - Banque  - en apprentissage"),
                FormationCourteDTO(id = "fl1002093", "L1 - Tourisme -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002089", "L1 - Pluridisciplinaire -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002079", "L1 - Droit, Sciences et Innovation -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002066", "L1 - Histoire / Géographie -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002051", "L1 - Sciences et Humanités -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002053", "L1 - Administration et Echanges internationaux -  Accès Santé (LAS)"),
                FormationCourteDTO(
                    id = "fl691180",
                    "FP - Responsable en gestion d'activité opérationnelle (Bac +3) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl1002043", "L1 - Sciences pour la santé -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002044", "L1 - Sciences pour l'ingénieur -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002046", "L1 - Sciences et technologies -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002040", "L1 - Electronique, énergie électrique, automatique -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002041", "L1 - Mécanique -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl620110", "MC - Services numériques aux organisations"),
                FormationCourteDTO(id = "fl1002032", "L1 - Informatique -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002033", "L1 - Mathématiques -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002034", "L1 - Physique -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002035", "L1 - Chimie -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002036", "L1 - Physique, chimie -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl85001", "Formation valant grade de licence"),
                FormationCourteDTO(id = "fl691182", "FP - Technicien diagnostics immobilier (Bac +2) - en apprentissage"),
                FormationCourteDTO(id = "fl1002037", "L1 - Sciences de la vie -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002038", "L1 - Sciences de la terre -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002039", "L1 - Sciences de la vie et de la terre -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002030", "L1 - Langues étrangères appliquées -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl1002112", "L1 - Sciences des systèmes communicants -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl620082", "MC - Technicien en peinture aéronautique"),
                FormationCourteDTO(id = "fr75001", "Diplôme de spécialisation professionnelle"),
                FormationCourteDTO(id = "fr75000", "Diplôme d'Etablissement"),
                FormationCourteDTO(id = "fl560002", "D.E Educateur de Jeunes Enfants"),
                FormationCourteDTO(id = "fl560001", "D.E Assistant de Service Social"),
                FormationCourteDTO(id = "fl560004", "D.E Educateur Technique Spécialisé"),
                FormationCourteDTO(id = "fl560003", "D.E Educateur Spécialisé"),
                FormationCourteDTO(id = "fr26000", "Formations des écoles vétérinaires"),
                FormationCourteDTO(id = "fl620124", "MC - Sommellerie"),
                FormationCourteDTO(id = "fl620123", "MC - Art de la dorure à chaud"),
                FormationCourteDTO(id = "fl620126", "MC - Production et réparation de produits électroniques"),
                FormationCourteDTO(id = "fl550005", "D.E Audioprothésiste"),
                FormationCourteDTO(id = "fl550004", "Certificat de capacité d'Orthoptiste"),
                FormationCourteDTO(id = "fl550003", "Certificat de capacité d'Orthophoniste"),
                FormationCourteDTO(id = "fl550002", "D.E Ergothérapeute"),
                FormationCourteDTO(id = "fl550007", "D.E Psychomotricien"),
                FormationCourteDTO(
                    id = "fl691183",
                    "FP - Développeur de solutions numériques sécurisées (Bac +3) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl550006", "D.E Pédicure-Podologue"),
                FormationCourteDTO(id = "fl550001", "D.E Infirmier"),
                FormationCourteDTO(id = "fl177", "D.E Technicien de Laboratoire Médical"),
                FormationCourteDTO(id = "fl183", "D.E manipulateur/trice en électroradiologie médicale"),
                FormationCourteDTO(id = "fl253", "EA-BAC5 - Conservation-restauration des biens culturels"),
                FormationCourteDTO(id = "fl252", "EA-BAC5 - Paysage"),
                FormationCourteDTO(id = "fl251", "EA-BAC5 - Bicursus Architecture Ingénieur"),
                FormationCourteDTO(id = "fl250", "EA-BAC5 - Architecture"),
                FormationCourteDTO(id = "fl231", "Formation Bac + 4"),
                FormationCourteDTO(id = "fl230", "Bachelors des écoles d'ingénieurs Ingénierie (Bac+3)"),
                FormationCourteDTO(id = "fl242", "Formation des écoles de commerce et de management Bac + 4"),
                FormationCourteDTO(id = "fl850010", "DN MADE - Objet - en apprentissage"),
                FormationCourteDTO(id = "fl241", "Formation des écoles de commerce et de management Bac + 3"),
                FormationCourteDTO(id = "fl240", "Formation des écoles de commerce et de management Bac + 5"),
                FormationCourteDTO(id = "fl210", "Formation d'ingénieur Bac + 5"),
                FormationCourteDTO(id = "fl490025", "CS - Techn. en énergies renouvelables opt. a énergie électrique"),
                FormationCourteDTO(id = "fl561003", "D.E Educateur Spécialisé - en apprentissage"),
                FormationCourteDTO(id = "fl490026", "CS - Techn. en énergies renouvelables opt. b énergie thermique"),
                FormationCourteDTO(id = "fl490023", "CS - Services numériques aux organisations"),
                FormationCourteDTO(id = "fl490024", "CS - Sommellerie"),
                FormationCourteDTO(id = "fl490021", "CS - Peinture décoration"),
                FormationCourteDTO(id = "fl490022", "CS - Production et réparation de produits électroniques"),
                FormationCourteDTO(id = "fl490020", "CS - Organisateur de réception"),
                FormationCourteDTO(
                    id = "fl691185",
                    "FP - Responsable en développement commercial et marketing (Bac +3) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl490029", "CS - Technicien(ne) en réseaux électriques"),
                FormationCourteDTO(id = "fl490027", "CS - Technicien(ne) en chaudronnerie aéronautique et spatiale"),
                FormationCourteDTO(id = "fl490034", "CS - Vendeur-conseil en produits techniques pour l'habitat"),
                FormationCourteDTO(id = "fl490032", "CS - Technicien(ne) en tuyauterie"),
                FormationCourteDTO(id = "fl490033", "CS - Technicien(ne) en peinture aéronautique"),
                FormationCourteDTO(id = "fl691186", "FP - Concepteur développeur d'applications (Bac +3) - en apprentissage"),
                FormationCourteDTO(id = "fl490030", "CS - Technicien(ne) ascensoriste (service et modernisation)"),
                FormationCourteDTO(id = "fl490031", "CS - Technicien(ne) en soudage"),
                FormationCourteDTO(id = "fl490003", "CS - Aéronautique option avions à moteurs à pistons"),
                FormationCourteDTO(id = "fl490004", "CS - Aéronautique option avions à moteurs à turbines"),
                FormationCourteDTO(id = "fl490001", "CS - Accueil-réception"),
                FormationCourteDTO(id = "fl635", "MC - Organisateur de réception"),
                FormationCourteDTO(id = "fl490002", "CS - Aéronautique option avionique"),
                FormationCourteDTO(id = "fl490000", "CS - Accueil dans les transports"),
                FormationCourteDTO(id = "fl490009", "CS - Art de la dorure à chaud"),
                FormationCourteDTO(id = "fl490008", "CS - Animation-gestion de projets dans le secteur sportif"),
                FormationCourteDTO(id = "fl490006", "CS - Aéronautique option hélicoptères à moteurs à turbines"),
                FormationCourteDTO(
                    id = "fl691172",
                    "FP - Responsable marketing et commercial spécialisé en acquisition numérique (Bac + 3) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl490015", "CS - Encadrement secteur sportif opt. activités physiques pour tous"),
                FormationCourteDTO(id = "fl490012", "CS - Encadrement secteur sportif opt. activités aquatiques& de la natation"),
                FormationCourteDTO(id = "fl490010", "CS - Assistance, conseil, vente à distance"),
                FormationCourteDTO(id = "fl490011", "CS - Cybersécurité"),
                FormationCourteDTO(id = "fr89200", "LP -  Sciences Humaines et Sociales"),
                FormationCourteDTO(id = "fl490018", "CS - Mécatronique navale"),
                FormationCourteDTO(id = "fl490016", "CS - Maintenance des installations oléohydrauliques et pneumatiques"),
                FormationCourteDTO(id = "fl490017", "CS - Maquettes et prototypes"),
                FormationCourteDTO(id = "fl621082", "MC - Technicien en peinture aéronautique - en apprentissage"),
                FormationCourteDTO(id = "fl398", "BTS - Fluide, énergie, domotique - option B froid et conditionnement d'air"),
                FormationCourteDTO(id = "fl395", "BTS - Fluide, énergie, domotique - option A génie climatique et fluidique"),
                FormationCourteDTO(id = "fl811007", "BUT - Génie mécanique et productique - en apprentissage"),
                FormationCourteDTO(id = "fl393", "BTS - Aéronautique"),
                FormationCourteDTO(id = "fl392", "BTS - Métiers de la mode-chaussure et maroquinerie"),
                FormationCourteDTO(id = "fl391", "BTS - Innovations textiles - Option B : Traitements"),
                FormationCourteDTO(id = "fl390", "BTS - Innovations textiles - Option A : Structures"),
                FormationCourteDTO(id = "fl498", "DTS Imagerie médicale et radiologie thérapeutique"),
                FormationCourteDTO(id = "fl470", "BTS - Biotechnologies"),
                FormationCourteDTO(id = "fl486", "BTS - Comptabilité et gestion"),
                FormationCourteDTO(id = "fl485", "BTS - Métiers des Services à l'environnement"),
                FormationCourteDTO(id = "fl455", "BTS - Métiers de l'audio-visuel opt : gestion de la production"),
                FormationCourteDTO(id = "fl850007", "DN MADE - Matériaux - en apprentissage"),
                FormationCourteDTO(id = "fl454", "BTS - Métiers de l'audio-visuel opt : techn. d'ingeniérie et exploit. équipements"),
                FormationCourteDTO(id = "fl453", "BTS - Métiers de l'audio-visuel opt : montage et post-production"),
                FormationCourteDTO(id = "fl452", "BTS - Métiers de l'audio-visuel opt : métiers du son"),
                FormationCourteDTO(id = "fl451", "BTS - Métiers de l'audio-visuel opt : métiers de l'image"),
                FormationCourteDTO(
                    id = "fl691034",
                    "FP - Responsable de chantier - bâtiment et travaux publics (Bac +2) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl458", "BTS - Services informatiques aux organisations"),
                FormationCourteDTO(id = "fl467", "BTS - Métiers de l'eau"),
                FormationCourteDTO(id = "fl464", "BTS - Tourisme"),
                FormationCourteDTO(id = "fl462", "BTS - Economie sociale familiale"),
                FormationCourteDTO(id = "fl460", "BTS - Diététique"),
                FormationCourteDTO(id = "fl434", "BTS - Conception et réalisation de carrosseries"),
                FormationCourteDTO(id = "fl432", "BTS - Conception et réalisation en chaudronnerie industrielle"),
                FormationCourteDTO(id = "fl436", "BTS - Electrotechnique"),
                FormationCourteDTO(id = "fl443", "BTS - Assurance"),
                FormationCourteDTO(id = "fl850009", "DN MADE - Numérique - en apprentissage"),
                FormationCourteDTO(id = "fl691031", "FP - Développeur Informatique (Bac +2) - en apprentissage"),
                FormationCourteDTO(id = "fl442", "BTS - Professions immobilières"),
                FormationCourteDTO(id = "fl447", "BTS - Communication"),
                FormationCourteDTO(id = "fl418", "BTS - Travaux publics"),
                FormationCourteDTO(id = "fl416", "BTS - Bâtiment"),
                FormationCourteDTO(id = "fl561002", "D.E Educateur de Jeunes Enfants - en apprentissage"),
                FormationCourteDTO(id = "fl422", "BTS - Fluide, énergie, domotique - option C domotique et bâtiment communicants"),
                FormationCourteDTO(id = "fl421", "BTS - Développement et Réalisation Bois"),
                FormationCourteDTO(id = "fl420", "BTS - Systèmes constructifs bois et habitat"),
                FormationCourteDTO(id = "fl429", "BTS - Conception et industrialisation en microtechniques"),
                FormationCourteDTO(id = "fl426", "BTS - Assistance technique d'ingénieur"),
                FormationCourteDTO(
                    id = "fl691032",
                    "FP - Gestionnaire en maintenance et support informatique (Bac +2) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl409", "BTS - Bioanalyses et contrôles"),
                FormationCourteDTO(id = "fl405", "BTS - Contrôle industriel et régulation automatique"),
                FormationCourteDTO(id = "fl404", "BTS - Conception et Réalisation de Systèmes Automatiques"),
                FormationCourteDTO(id = "fl402", "BTS - Conception de produits industriels"),
                FormationCourteDTO(id = "fl11427", "BTS - Commerce International - en apprentissage"),
                FormationCourteDTO(id = "fl561004", "D.E Educateur Technique Spécialisé - en apprentissage"),
                FormationCourteDTO(id = "fl561001", "D.E Assistant de Service Social - en apprentissage"),
                FormationCourteDTO(id = "fl271", "Formation des écoles supérieure d'art Bac + 3"),
                FormationCourteDTO(id = "fl270", "Formation des écoles supérieure d'art Bac + 5"),
                FormationCourteDTO(id = "fl389", "BTS - Maintenance des systèmes - option B Systèmes énergétiques et fluidiques"),
                FormationCourteDTO(id = "fl388", "BTS - Métiers de la mode-vêtements"),
                FormationCourteDTO(id = "fl348", "BTS - Prothésiste orthésiste"),
                FormationCourteDTO(id = "fl385", "BTS - Maintenance des systèmes - option A Systèmes de production"),
                FormationCourteDTO(id = "fl384", "BTS - Traitement des matériaux"),
                FormationCourteDTO(id = "fl383", "BTS - Fonderie"),
                FormationCourteDTO(id = "fl382", "BTS - Photographie"),
                FormationCourteDTO(id = "fl691068", "FP - Gestionnaire comptable et fiscal (Bac +2) - en apprentissage"),
                FormationCourteDTO(id = "fl381", "BTS - Banque conseiller de clientèle"),
                FormationCourteDTO(id = "fl352", "BTS - Géologie appliquée"),
                FormationCourteDTO(id = "fl351", "BTS - Industries céramiques"),
                FormationCourteDTO(id = "fl358", "BTS - Métiers de l'esthétique-cosmétique-parfumerie"),
                FormationCourteDTO(id = "fl364", "BTS - Techniques et services en matériels agricoles"),
                FormationCourteDTO(id = "fl368", "BTS - Opticien-Lunetier"),
                FormationCourteDTO(id = "fl367", "BTS - Service et prestation des secteurs sanitaire et social"),
                FormationCourteDTO(id = "fl366", "BTS - Métiers de la coiffure"),
                FormationCourteDTO(id = "fl365", "BTS - Prothésiste dentaire"),
                FormationCourteDTO(
                    id = "fl621126",
                    "MC - Mention complémentaire - Production et réparation de produits électroniques - en apprentissage",
                ),
                FormationCourteDTO(id = "fl621124", "MC - Sommellerie - en apprentissage"),
                FormationCourteDTO(id = "fl691069", "FP - Modeleur BIM du bâtiment (Bac + 2) - en apprentissage"),
                FormationCourteDTO(id = "fl363", "BTS - Maintenance des systèmes - option C Systèmes éoliens"),
                FormationCourteDTO(id = "fl369", "BTS - Analyses de biologie médicale"),
                FormationCourteDTO(id = "fl621110", "MC - Services numériques aux organisations - en apprentissage"),
                FormationCourteDTO(id = "fl337", "BTS - Edition"),
                FormationCourteDTO(id = "fl341", "BTS - Environnement nucléaire"),
                FormationCourteDTO(id = "fl347", "BTS - Podo-orthésiste"),
                FormationCourteDTO(id = "fl324", "BTS - Systèmes photoniques"),
                FormationCourteDTO(id = "fl621103", "MC - Vendeur-conseil en produits techniques pour l'habitat - en apprentissage"),
                FormationCourteDTO(id = "fl11424", "BTS - Gestion des transports et logistique associée - en apprentissage"),
                FormationCourteDTO(id = "fl11425", "BTS - Management opérationnel de la sécurité - en apprentissage"),
                FormationCourteDTO(id = "fl11426", "BTS - Bioqualité - en apprentissage"),
                FormationCourteDTO(
                    id = "fl691062",
                    "FP - Conducteur de travaux en entreprises de travaux agricoles (Bac +2) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl11428", "BTS - Métiers de la mesure - en apprentissage"),
                FormationCourteDTO(id = "fl11429", "BTS - Conseil et commercialisation de solutions techniques - en apprentissage"),
                FormationCourteDTO(id = "fl11420", "BTS - Support à l'action managériale - en apprentissage"),
                FormationCourteDTO(id = "fl11421", "BTS - Gestion de la PME - en apprentissage"),
                FormationCourteDTO(id = "fl11422", "BTS - Management en hôtellerie restauration - en apprentissage"),
                FormationCourteDTO(id = "fl11423", "BTS - Management Commercial Opérationnel - en apprentissage"),
                FormationCourteDTO(
                    id = "fl11435",
                    "BTS - Maintenance des systèmes - option D maintenance des ascenseurs et élévateurs - en apprentissage",
                ),
                FormationCourteDTO(id = "fl11430", "BTS - Management économique de la construction - en apprentissage"),
                FormationCourteDTO(
                    id = "fl11431",
                    "BTS - Finitions, aménagement des bâtiments: conception et réalisation - en apprentissage",
                ),
                FormationCourteDTO(id = "fl11433", "BTS - Collaborateur juriste notarial - en apprentissage"),
                FormationCourteDTO(id = "fl11402", "BTS - Enveloppe des bâtiments : conception et réalisation - en apprentissage"),
                FormationCourteDTO(
                    id = "fl11403",
                    "BTS - Métiers du géomètre-topographe et de la modélisation numérique - en apprentissage",
                ),
                FormationCourteDTO(id = "fl11405", "BTS - Pilotage des procédés - en apprentissage"),
                FormationCourteDTO(
                    id = "fl11414",
                    "BTS - Europlastics et composites à référentiel commun européen - " +
                        "option Conception d'Outillage - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl11406",
                    "BTS - Europlastics et composites à référentiel commun européen - " +
                        "option Pilotage et Optimisation de la production - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl11407",
                    "BTS - Maintenance des véhicules option voitures " +
                        "particulières - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl11408",
                    "BTS - Maintenance des véhicules option véhicules de transport routier - en apprentissage",
                ),
                FormationCourteDTO(id = "fl11409", "BTS - Maintenance des véhicules option motocycles - en apprentissage"),
                FormationCourteDTO(id = "fl11401", "BTS - Métiers de la chimie - en apprentissage"),
                FormationCourteDTO(
                    id = "fl11413",
                    "BTS - Etudes de réalisation d'un projet de communication - 1ère année commune - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl691064",
                    "FP - Designer en communication graphique éco-responsable (Bac +3) - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl11415",
                    "BTS - Maintenance des matériels de construction et de manutention - en apprentissage",
                ),
                FormationCourteDTO(id = "fl11416", "BTS - Conception de processus de découpe et d'emboutissage - en apprentissage"),
                FormationCourteDTO(id = "fl11417", "BTS - Conception et industrialisation en construction navale - en apprentissage"),
                FormationCourteDTO(id = "fl11418", "BTS - Négociation et digitalisation de la Relation Client - en apprentissage"),
                FormationCourteDTO(id = "fl11419", "BTS - Architectures en Métal : conception et Réalisation - en apprentissage"),
                FormationCourteDTO(
                    id = "fl11410",
                    "BTS - Conception des processus de réalisation de produits (1ère année commune) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl11411", "BTS - Etude et réalisation d'agencement - en apprentissage"),
                FormationCourteDTO(id = "fl811518", "BUT - Management de la Logistique et des Transports - en apprentissage"),
                FormationCourteDTO(id = "fl750001", "DU - Diplôme d'Université"),
                FormationCourteDTO(id = "fl750022", "DU - Diplôme de spécialisation professionnelle"),
                FormationCourteDTO(
                    id = "fl690",
                    "FP - Chargé de mission qualité, sécurité, santé au travail et environnement (Bac + 3)",
                ),
                FormationCourteDTO(id = "fl699", "FP - Responsable technique en bâtiment et travaux publics (bac +2)"),
                FormationCourteDTO(
                    id = "fl673",
                    "FP - Gestionnaire d'unité commerciale (option généraliste / option spécialisée) (bac +2)",
                ),
                FormationCourteDTO(id = "fl672", "BTSA - Aquaculture"),
                FormationCourteDTO(id = "fl671", "MC - Animation-gestion de projets dans le secteur sportif"),
                FormationCourteDTO(id = "fl811502", "BUT - Gestion des entreprises et des administrations - en apprentissage"),
                FormationCourteDTO(id = "fl691056", "FP - Responsable d'affaires en immobilier (Bac +3) - en apprentissage"),
                FormationCourteDTO(id = "fl811504", "BUT - Techniques de commercialisation - en apprentissage"),
                FormationCourteDTO(
                    id = "fl811506",
                    "BUT - Carrières sociales Parcours animation sociale et socioculturelle - en apprentissage",
                ),
                FormationCourteDTO(id = "fl811505", "BUT - Carrières juridiques - en apprentissage"),
                FormationCourteDTO(
                    id = "fl811509",
                    "BUT - Carrières sociales parcours coordination et gestion des " +
                        "établissements et services sanitaires et sociaux - en apprentissage",
                ),
                FormationCourteDTO(id = "fl688", "FP - Assistant ingénieur en biologie-biochimie - biotechnologies (bac +3)"),
                FormationCourteDTO(id = "fl654", "BTSA - Développement, animation des territoires ruraux"),
                FormationCourteDTO(id = "fl624", "MC - Assistance Conseil Vente à distance"),
                FormationCourteDTO(id = "fl653", "BTSA - Viticulture-Oenologie"),
                FormationCourteDTO(id = "fl659", "BTSA - Gestion et maitrise de l'eau"),
                FormationCourteDTO(id = "fl658", "MC - Technicien(ne) en soudage"),
                FormationCourteDTO(id = "fl665", "BTSA - Gestion forestière"),
                FormationCourteDTO(id = "fl664", "MC - Mécatronique navale"),
                FormationCourteDTO(id = "fl663", "BTSA - Gestion et protection de la nature"),
                FormationCourteDTO(id = "fl661", "BTSA - Développement de l'agriculture des régions chaudes"),
                FormationCourteDTO(id = "fl669", "MC - Technicien en chaudronnerie aéronautique et spatiale"),
                FormationCourteDTO(id = "fl668", "MC - Technicien(ne) en tuyauterie"),
                FormationCourteDTO(id = "fl666", "BTSA - Aménagements paysagers"),
                FormationCourteDTO(id = "fl639", "MC - Aéronautique - option avionique"),
                FormationCourteDTO(id = "fl638", "MC - Maintenance des installations oléohydrauliques et pneumatiques"),
                FormationCourteDTO(id = "fl636", "MC - Peinture décoration"),
                FormationCourteDTO(
                    id = "fl691136",
                    "FP - Diplôme supérieur en marketing, commerce et gestion (Bac +3) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl640", "BTSA - génie des équipements agricoles"),
                FormationCourteDTO(id = "fl1000210", "Formation d'ingénieur Bac + 5 -  Accès Santé (LAS)"),
                FormationCourteDTO(id = "fl649", "BTSA - productions animales"),
                FormationCourteDTO(id = "fl648", "BTSA - Analyse, conduite et stratégie de l'entreprise agricole"),
                FormationCourteDTO(id = "fl646", "MC - Agent de contrôle non destructif"),
                FormationCourteDTO(id = "fl619", "MC - Aéronautique option hélicoptère moteur à turbines"),
                FormationCourteDTO(id = "fl618", "MC - Aéronautique option avions à moteur à pistons"),
                FormationCourteDTO(id = "fl617", "MC - Maquettes et prototypes"),
                FormationCourteDTO(id = "fl616", "MC - Technicien des services à l'énergie"),
                FormationCourteDTO(id = "fl612", "MAN - Hôtellerie restauration"),
                FormationCourteDTO(id = "fl620", "MC - Accueil réception"),
                FormationCourteDTO(id = "fl629", "MC - Technicien en énergies renouvelables (option énergie électrique)"),
                FormationCourteDTO(id = "fl628", "MC - Technicien en réseau électrique"),
                FormationCourteDTO(id = "fl627", "MC - Technicien en énergies renouvelables (option thermique)"),
                FormationCourteDTO(id = "fl626", "MC - Accueil dans transports"),
                FormationCourteDTO(id = "fl623", "MC - Aéronautique option avions à moteur à turbines"),
                FormationCourteDTO(id = "fl622", "MC - Technicien ascensoriste, service et modernisation"),
                FormationCourteDTO(
                    id = "fl691109",
                    "FP - Titre professionnel - Concepteur développeur d'applications (Bac +3) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl691102", "FP - Titre professionnel - Assistant commercial (Bac +2) - en apprentissage"),
                FormationCourteDTO(id = "fl691103", "FP - Responsable Retail dans le Luxe (Bac +3) - en apprentissage"),
                FormationCourteDTO(id = "fl691129", "FP - Manager opérationnel en hôtellerie de luxe (Bac + 3) - En apprentissage"),
                FormationCourteDTO(id = "fl691118", "FP - Styliste de mode (Bac+2) - en apprentissage"),
                FormationCourteDTO(id = "fl691119", "FP - Chargé des Ressources Humaines (Bac+3) - en apprentissage"),
                FormationCourteDTO(
                    id = "fl691117",
                    "FP - Responsable en gestion administrative et ressources humaines (Bac+3) - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl691147",
                    "FP - Formation professionnelle - Chef de projet logiciel et réseau (Bac +3) - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl691149",
                    "FP - Formation professionnelle - Technicien systèmes et réseaux (Bac +2) - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl691140",
                    "FP - Titre professionnel - Conducteur de travaux aménagement finitions (Bac +2) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl691142", "FP - Bachelor Réalisateur monteur (Bac +3) - en apprentissage"),
                FormationCourteDTO(id = "fl691132", "FP - FP - Développeur web (Bac +2) - en apprentissage"),
                FormationCourteDTO(id = "fl691133", "FP - FP - Technicien performance industrielle (Bac +2) - en apprentissage"),
                FormationCourteDTO(
                    id = "fl691134",
                    "FP - Diplôme supérieur en marketing " +
                        "et communication (Bac +3) - en apprentissage",
                ),
                FormationCourteDTO(
                    id = "fl691135",
                    "FP - Bachelor Manager dans l’hôtellerie restauration (Bac + 3) - en apprentissage",
                ),
                FormationCourteDTO(id = "fl828", "DEUST - Animation et gestion des activités physiques, sportives ou culturelles"),
                FormationCourteDTO(id = "fl826", "DEUST - Métiers de la forme"),
                FormationCourteDTO(id = "fl825", "DEUST - Animation, commercialisation des services sportifs"),
                FormationCourteDTO(id = "fl824", "DEUST - Analyse des milieux biologiques"),
                FormationCourteDTO(id = "fl894", "DEUST - Santé environnement : Techniques de laboratoire"),
                FormationCourteDTO(id = "fl893", "DEUST - Production, contrôles et qualité des produits de santé"),
                FormationCourteDTO(id = "fl874", "DEUST - Médiations citoyennes"),
                FormationCourteDTO(id = "fl872", "DEUST - Bâtiment et travaux publics"),
                FormationCourteDTO(
                    id = "fl879",
                    "DEUST - Activités physiques et sportives adaptées : déficiences intellectuelles, troubles psychiques",
                ),
                FormationCourteDTO(id = "fl877", "DEUST - Webmaster et métiers de l'internet"),
                FormationCourteDTO(id = "fl865", "DEUST - Formation de base aux métiers du théâtre"),
                FormationCourteDTO(id = "fl830", "DEUST - Technicien de la mer et du littoral"),
                FormationCourteDTO(id = "fl839", "DEUST - Technologies de l'organisation dans les professions de santé"),
                FormationCourteDTO(id = "fl836", "DEUST - Bâtiment et Construction"),
                FormationCourteDTO(id = "fl834", "DEUST - Métiers des bibliothèques et de la documentation"),
                FormationCourteDTO(id = "fl833", "DEUST - Intervention Sociale"),
                FormationCourteDTO(
                    id = "fl844",
                    "DEUST - Pratique et gestion des activités physiques, sportives et de loisirs pour les publics séniors",
                ),
                FormationCourteDTO(id = "fl691057", "FP - Attaché(e) de Direction (Bac +2) - en apprentissage"),
                FormationCourteDTO(id = "fl842", "DEUST - Activités physiques et sportives et inadaptations sociales"),
                FormationCourteDTO(id = "fl690025", "FP - Aumônerie protestante, médiation et société"),
            )
        return FormationsCourtesDTO(
            formations = formations.filter { it.nom.contains(recherche, true) },
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

    @PostMapping
    fun getFormations(
        @RequestParam ids: List<String>,
        @RequestBody profilObligatoireRequeteDTO: ProfilObligatoireRequeteDTO,
    ): FormationsAvecExplicationsDTO {
        val profilEleve = profilObligatoireRequeteDTO.profil.toProfil()
        val toutesLesSuggestions = suggestionsFormationsService.recupererToutesLesSuggestionsPourUnProfil(profilEleve)
        val formations = recupererFormationsService.recupererFichesFormationPourProfil(profilEleve, toutesLesSuggestions, ids)
        return FormationsAvecExplicationsDTO(formations = formations.map { FormationAvecExplicationsDTO(it) })
    }

    companion object {
        private const val NOMBRE_FORMATIONS_SUGGEREES = 30

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
