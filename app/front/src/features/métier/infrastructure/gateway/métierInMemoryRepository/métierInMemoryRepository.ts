import { type Métier, type MétierAperçu } from "@/features/métier/domain/métier.interface";
import { type MétierRepository } from "@/features/métier/infrastructure/métierRepository.interface";
import Fuse from "fuse.js";

export class métierInMemoryRepository implements MétierRepository {
  private MÉTIERS: Métier[] = [
    {
      id: "MET_1",
      nom: "contrôleur aérien / contrôleuse aérienne",
      descriptif:
        "<p>Garant de la sécurité et de la fluidité du trafic aérien, le contrôleur aérien guide les pilotes du décollage jusqu'à l'atterrissage de leur avion. Un métier scientifique à haute responsabilité, sans droit à l'erreur !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_10",
      nom: "peintre en bâtiment",
      descriptif:
        "<p>Dernier ouvrier à intervenir sur un chantier de construction, le peintre en bâtiment habille murs et plafonds. Passé maître dans l'art des finitions, il sait aussi conseiller les clients dans leurs choix de décoration.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_100",
      nom: "développeur rural / développeuse rurale humanitaire",
      descriptif:
        "<p>Le développeur rural humanitaire conseille les populations vulnérables dans les pays en développement. Son objectif : les conduire vers l'autosuffisance alimentaire dans une perspective de développement durable.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_101",
      nom: "conseiller / conseillère en fusions-acquisitions",
      descriptif:
        "<p>Rachat de sociétés, vente de filiales, fusions : le conseiller en fusions-acquisitions guide les entreprises dans la réalisation d'opérations financières complexes. Un métier où l'intuition et le sens des affaires sont essentiels.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1010",
      nom: "maître-chien d'avalanche",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1011",
      nom: "maître-chien de la Police nationale",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1012",
      nom: "maître de chien dans l'armée",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1013",
      nom: "maître de chien de la Gendarmerie nationale",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1014",
      nom: "pilote d'aviation d'affaires",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1015",
      nom: "pilote d'aviation de fret (cargo)",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1017",
      nom: "chirurgien/ne cardiaque",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1018",
      nom: "chirurgien/ne orthopédiste",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1019",
      nom: "chirurgien/ne plastique",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1020",
      nom: "chirurgien/ne thoracique",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1021",
      nom: "chirurgien/ne  viscéral/e",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1022",
      nom: "neurochirurgien/ne",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1023",
      nom: "stomatologue",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1024",
      nom: "juriste en propriété industrielle",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1025",
      nom: "juriste en propriété littéraire et artistique",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1027",
      nom: "néonatalogue",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_103",
      nom: "ingénieur / ingénieure en production et expérimentations végétales",
      descriptif:
        "<p>Céréales OGM (organismes génétiquement modifiés), croisements d'optimisation pour créer des fleurs exceptionnelles... l'ingénieur en production et expérimentations végétales innove, dans le respect des exigences éthiques et sociales et des normes en vigueur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1030",
      nom: "clown",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1031",
      nom: "contorsionniste",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1033",
      nom: "équilibriste",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1034",
      nom: "jongleur/euse",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1035",
      nom: "magicien/ne",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1038",
      nom: "acrobate voltigeur/euse",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1039",
      nom: "conservateur/trice de l'inventaire",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_104",
      nom: "ingénieur/e technico-commerciale en électronique",
      descriptif:
        "<p>Doué pour la négociation, l'ingénieur technico-commercial en électronique connaît parfaitement les produits qu'il vend (circuits intégrés analogiques et numériques, microprocesseurs et autres composants etc.) et les marchés sur lesquels ils sont attendus. Son objectif : \" faire du chiffre \" sans oublier de soigner le relationnel pour ne pas rater un marché.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1040",
      nom: "conservateur/trice de monuments historiques",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1041",
      nom: "conservateur/trice de musée",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1042",
      nom: "conservateur/trice des archives",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1043",
      nom: "conservateur/trice en archéologie",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1044",
      nom: "éditeur/trice jeunesse",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1045",
      nom: "éditeur/trice scolaire et parascolaire",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_105",
      nom: "géologue modélisateur/trice",
      descriptif:
        "<p>À l'aise sur le terrain comme devant son ordinateur à manipuler des logiciels 3D perfectionnés, le géologue modélisateur ou la géologue modélisatrice permet, par exemple, d'éviter une pollution du sol en suivant le cheminement d'un produit, ou de prévisualiser un forage.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_106",
      nom: "ingénieur / ingénieure hydrogéomorphologue",
      descriptif:
        "<p>L'ingénieur ou l'ingénieure hydrogéomorphologue analyse le fonctionnement naturel des cours d'eau et la structure de leurs vallées afin de cartographier les zones inondables. Il ou elle étudie également l'érosion côtière afin de prévenir d'éventuelles crues.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1065",
      nom: "salesman sur produits dérivés",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1066",
      nom: "salesman sur produits structurés",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1069",
      nom: "cambiste de marché",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_107",
      nom: "chargé/e d'affaires en génie climatique",
      descriptif:
        "<p>Ventilations, appareils de surpression d'eau, planchers chauffants, climatisations... pour chaque type de bâtiment, le chargé d'affaires en génie climatique vend la solution adaptée à des entreprises clientes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1070",
      nom: "proprietary trader ou prop trader",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1071",
      nom: "trader de produits structurés",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1072",
      nom: "trader de produits vanilles",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1073",
      nom: "trader market maker",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1074",
      nom: "anatomo-pathologiste",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_108",
      nom: "médiateur / médiatrice scientifique",
      descriptif:
        "<p>Rébarbatives, les sciences ? Le médiateur scientifique démontre chaque jour le contraire. Cet orateur est un bon pédagogue : il sait rendre accessible un savoir complexe à un public néophyte.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_109",
      nom: "responsable de laboratoire de recherche",
      descriptif:
        "<p> Un robot à vision autonome ou un traitement médical contre l'ostéoporose sont l'aboutissement final de longues études. Au responsable de laboratoire de recherche de définir, piloter et valider ces travaux d'une équipe de chercheurs et de techniciens. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_11",
      nom: "comptable",
      descriptif:
        "<p>Essentiel à toute organisation, le comptable enregistre les dépenses, les recettes et les investissements de l'entreprise au quotidien. En plongeant dans la comptabilité analytique, il étudie les coûts de revient ou le chiffre d'affaires par produit.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_110",
      nom: "maquilleur / maquilleuse artistique",
      descriptif:
        "<p>Faux nez, masques... le maquilleur artistique transforme les acteurs selon les indications du metteur en scène ou du réalisateur. Ce professionnel créatif donne vie à des personnages imaginaires pour le cinéma, le théâtre ou les spectacles vivants.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1102",
      nom: "argenteur/euse",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1103",
      nom: "ciseleur/euse",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1104",
      nom: "orfèvre monteur/euse",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1105",
      nom: "planeur/euse en orfèvrerie",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1106",
      nom: "polisseur/euse",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1107",
      nom: "tourneur/euse-repousseur/euse",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_111",
      nom: "médecin de secours en montagne",
      descriptif:
        "<p>Allô, le 112 ? Le médecin de secours en montagne embarque à bord de l'hélicoptère aux côtés des sauveteurs. Déposés en montagne par le pilote, les sauveteurs sécurisent le lieu, tandis que le médecin se charge des victimes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1110",
      nom: "courtier/ère en assurances",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1111",
      nom: "courtier/ère en immobilier",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1112",
      nom: "courtier/ère en marchandises",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1113",
      nom: "courtier/ère en prêt immobilier",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1114",
      nom: "courtier/ère en travaux",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1115",
      nom: "ethnolinguiste",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1116",
      nom: "linguiste informaticien/ne",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1117",
      nom: "psycholinguiste",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1118",
      nom: "sociolinguiste",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_112",
      nom: "critique d'art",
      descriptif:
        "<p>Rendre compte en quelques lignes d'une actualité artistique, rencontrer des artistes, visiter des expositions, participer à des conférences... telles sont quelques-unes des nombreuses activités du critique d'art. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1125",
      nom: "corsetier/ère",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_113",
      nom: "responsable qualité en agroalimentaire",
      descriptif:
        "<p>Personnage-clé de l'industrie agroalimentaire, le responsable qualité en agroalimentaire est chargé de contrôler toute la chaîne de fabrication d'un produit et de garantir la sécurité des aliments qui sortent de l'usine.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_114",
      nom: "localisateur/trice",
      descriptif:
        "<p>Trouver le mot juste, c'est la préoccupation constante du localisateur ou de la localisatrice en charge de l'adaptation d'un jeu vidéo ou d'un logiciel étranger pour les utilisateurs de son pays. Il ou elle apporte une couleur locale au produit multimédia.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_115",
      nom: "moniteur/trice d'escalade",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_116",
      nom: "conducteur / conductrice de travaux en entreprises de travaux agricoles",
      descriptif:
        "<p>Le conducteur de travaux en entreprise de travaux agricoles, forestiers ou ruraux fait le lien entre son entreprise et le client. Il va sur le terrain pour évaluer les besoins, puis établit le devis. Il veille au bon déroulement du chantier et au respect des délais.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1164",
      nom: "algoculteur/trice",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1165",
      nom: "conchyliculteur/trice",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1166",
      nom: "mytiliculteur/trice",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1167",
      nom: "ostréiculteur/trice",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1168",
      nom: "pisciculteur/trice",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1169",
      nom: "salmoniculteur/trice",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_117",
      nom: "technicien / technicienne prototypiste en agroéquipement",
      descriptif:
        "<p>Le technicien prototypiste en agroéquipement fait le lien entre le bureau d'études et la fabrication industrielle du produit. Il construit tout ou une partie d'un nouvel engin qu'il teste et modifie avant la production. Il allie bon niveau technique et créativité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1172",
      nom: "diamantaire",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_118",
      nom: "formateur/trice technique en agroéquipement",
      descriptif:
        "<p>Le formateur technique est chargé de former des professionnels à l'utilisation ou à la réparation d'un nouvel équipement ou outil. Très recherché, il travaille généralement chez un constructeur, un importateur ou un distributeur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1189",
      nom: "juriste bancaire",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1190",
      nom: "juriste droit des contrats",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1191",
      nom: "juriste droit des sociétés",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1192",
      nom: "juriste droit fiscal",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1194",
      nom: "juriste propriété industrielle",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1198",
      nom: "psychologue clinicien/ne",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1199",
      nom: "psychologue du travail",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_12",
      nom: "développeur / développeuse informatique",
      descriptif:
        "<p>Expert des langages informatiques, le développeur informatique traduit la demande d'un client en lignes de code informatique. La révolution numérique le place parmi les professionnels les plus recherchés, surtout s'il sait s'adapter et élargir ses compétences.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_120",
      nom: "ouvrier / ouvrière paysagiste",
      descriptif:
        "<p>L'ouvrière ou l'ouvrier paysagiste aménage et entretient les jardins des particuliers ou les espaces verts publics. Selon le cas, ces professionnels sont des agents du service public ou des employés d'une entreprise du paysage.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1206",
      nom: "visagiste",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1208",
      nom: "archetier/ère",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1209",
      nom: "facteur/trice d'accordéons",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_121",
      nom: "<span>Traiteur organisateur de réception/</span> <br> <span>traiteuse organisatrice de réception</span>",
      descriptif:
        "Un mariage, une soirée de remise de trophées, une réception commerciale, un festival : le traiteur organisateur ou la traiteuse organisatrice de réceptions est là pour superviser la préparation et le service du banquet. Ce ou cette spécialiste de l'événementiel sait concocter des menus alléchants en tenant compte du budget de sa clientèle.<br/><br/><h3>Après la 3<sup>e</sup></h3>\n<p>2 ans pour préparer le CAP cuisine ou l'un des CAP des métiers de bouche (boucher, boulanger, etc.), complété par la MC employé traiteur (1&nbsp;an) ou, après 2 ans d'expérience professionnelle au minimum, par le BP arts de la cuisine ou arts du service et commercialisation en restauration (2&nbsp;ans)&nbsp;; 3&nbsp;ans pour un bac professionnel du domaine de la restauration et de l'alimentation ou encore le bac STHR.</p>\n<h3>Après le bac</h3>\n<p>1 an pour préparer la MC organisateur de réceptions (après un bac professionnel du domaine ou le bac STHR).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1211",
      nom: "facteur/trice de pianos",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1212",
      nom: "facteur/trice d'instruments à vent",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1213",
      nom: "facteur/trice d'orgues",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1217",
      nom: "architecte des Monuments historiques",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1218",
      nom: "architecte programmiste",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1219",
      nom: "architecte et urbaniste de l'État",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_122",
      nom: "administrateur territorial / administratrice territoriale",
      descriptif:
        "<p>L'administrateur territorial correspond à un statut dans la fonction publique qui permet d'accéder à des responsabilités de direction dans une collectivité territoriale (ville de plus de 40 000 habitants, département, région, structure intercommunale...).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1221",
      nom: "sellier/ère bourrelier/ière",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1222",
      nom: "sellier/ère carrossier/ère",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1224",
      nom: "sellier/ère harnacheur/euse",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1233",
      nom: "livreur/euse",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1235",
      nom: "scénariste de cinéma",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1236",
      nom: "scénariste de télévision",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1237",
      nom: "scénariste multimédia",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1238",
      nom: "script doctor (consultant/e)",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_124",
      nom: "ingénieur/e études et développement en logiciels de simulation",
      descriptif:
        "<p>L'ingénieur études et développement en logiciels de simulation conçoit et développe les logiciels embarqués dans les cockpits des avions civils et militaires, à partir de spécifications établies avec les avionneurs.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1244",
      nom: "employé / employée d'élevage ovin",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1245",
      nom: "jardinier/ère spécialisé en maçonnerie paysagère",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1246",
      nom: "responsable d'élevage avicole",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1247",
      nom: "responsable d'élevage bovin",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1248",
      nom: "responsable d'élevage caprin",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1249",
      nom: "responsable d'élevage ovin",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_125",
      nom: "ingénieur/e analogicien/ne",
      descriptif:
        "<p>Lorsque l'électronique numérique ne donne pas un résultat satisfaisant, l'électronique analogique la complète dans certains domaines, notamment pour les puristes des images et des sons. L'ingénieur analogicien, fortement spécialisé, est recherché. Il travaille en équipe pour concevoir des circuits intégrés analogiques.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1250",
      nom: "responsable d'élevage porcin",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1252",
      nom: "employé/e d'élevage avicole",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1253",
      nom: "employé/e d'élevage bovin",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1254",
      nom: "employé/e d'élevage porcin",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1257",
      nom: "technicien/ne d'élevage avicole",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1258",
      nom: "technicien/ne d'élevage bovin",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1259",
      nom: "technicien/ne d'élevage caprin",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_126",
      nom: "biostatisticien/ne",
      descriptif:
        "<p> Aidant les chercheurs à planifier leurs expériences et à interpréter les résultats, le biostatisticien utilise ses compétences statistiques et informatiques dans les domaines de la santé, de la biologie, de l'agroalimentaire... Un métier de chiffres et d'équations.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_1260",
      nom: "technicien/ne d'élevage ovin",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1261",
      nom: "technicien/ne d'élevage porcin",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1262",
      nom: "avocat/e civiliste",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1264",
      nom: "avocat/e d'affaires",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_128",
      nom: "technicien/ne contrôle qualité aéronautique",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1288",
      nom: "couvreur/euse photovoltaique",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1289",
      nom: "couvreur/euse ardoisier/ière",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1290",
      nom: "couvreur/euse lauzier",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1291",
      nom: "couvreur/euse tuilier",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1292",
      nom: "couvreur/euse zingueur/euse",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_13",
      nom: "ingénieur / ingénieure système",
      descriptif:
        "<p>Le rôle de l'ingénieur système ? Analyser, fiabiliser et optimiser l'outil informatique de son entreprise afin que l'ensemble des utilisateurs dispose d'une installation adaptée et performante. Un expert du matériel et des logiciels.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_130",
      nom: "urbaniste concepteur/trice",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_1306",
      nom: "avocat/e pénaliste",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_132",
      nom: "<span>Médiateur social / médiatrice</span> <br> <span>sociale</span>",
      descriptif:
        "Les médiateurs sociaux exercent en zones sensibles pour prévenir ou tenter de remédier à des problèmes d'incivilité. Ils mènent des actions de prévention sur le terrain, et leurs missions dépendent de l'organisme qui les emploie.<br/><br/><h3>Après la 3<sup>e</sup></h3>\n<p>2 ans pour préparer le CAP agent de prévention et de médiation ; autres voies possibles le BPJEPS (brevet professionnel de la Jeunesse, de l’Éducation Populaire et des Sports) animation sociale ou le TP médiateur social accès aux droits et services, tous deux de niveau bac</p>\n<h3>Après le bac</h3>\n<p>3 ans pour obtenir le BUT carrières sociales parcours animation sociale et socio-culturelle </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_135",
      nom: "vendeur/euse d'articles de sport",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_136",
      nom: "conservateur territorial des bibliothèques / conservatrice territoriale des bibliothèques",
      descriptif:
        "<p>Le conservateur territorial de bibliothèques occupe des fonctions de direction au sein d'une bibliothèque, d'une médiathèque ou d'une direction culturelle. Il développe la diffusion des ressources de son établissement et manage son équipe dans cet objectif.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_138",
      nom: "ingénieur/e d'affaires en aéronautique",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_14",
      nom: "administrateur / administratrice de base de données",
      descriptif:
        "<p>Fichiers clients, catalogues produits, comptes financiers..., l'administrateur de base de données est le garant des milliers d'informations stockées dans les bases de données d'une entreprise. Il en assure la disponibilité, la qualité et la sécurité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_140",
      nom: "gestionnaire du sport",
      descriptif:
        "<p>Le ou la gestionnaire du sport est responsable d'une structure sportive, dont il ou elle dirige les équipes. A la tête d'une association, d'un club privé ou d'un équipement sportif municipal, il ou elle en assure le fonctionnement et le développement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_143",
      nom: "technicien / technicienne logistique",
      descriptif:
        "<p>Le technicien logistique contrôle, en qualité comme en quantité, toutes les entrées et toutes les sorties de marchandises dans l'entrepôt. Il veille à respecter un difficile équilibre : minimiser les stocks sans pour autant risquer la rupture.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_144",
      nom: "directeur / directrice de restaurant",
      descriptif:
        "<p>De la cuisine à l'accueil des clients, en passant par le service et la gestion, le directeur de restaurant est partout. Sa mission : tout mettre en oeuvre pour faire de son établissement un lieu apprécié de la clientèle.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_145",
      nom: "barman / barmaid",
      descriptif:
        "<p>Dynamique et adroit, le barman incarne l'âme et l'ambiance d'un bar. Il prépare et sert les boissons, des plus classiques aux plus sophistiquées. Il doit se montrer discret et diplomate, tout en restant à l'écoute de ses clients.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_146",
      nom: "comédien / comédienne",
      descriptif:
        "<p>Incarner un rôle sur scène ou à l'écran, apparaître dans un téléfilm ou un spot publicitaire, doubler un personnage... Quelques-unes des multiples facettes du métier de comédien.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_148",
      nom: "serrurier-métallier / serrurière-métallière",
      descriptif:
        "<p>Il dompte les métaux pour les transformer en rampe d'escalier, en balcon, en clés, en pièces de charpente, fenêtre... Lui, c'est le serrurier-métallier. A ne pas confondre avec le serrurier-dépanneur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_149",
      nom: "expert-comptable / experte-comptable",
      descriptif:
        "<p>L'activité principale de l'expert-comptable est le conseil aux entreprises. Au-delà de la comptabilité, il apporte son expertise en matière financière, fiscale ou juridique. Partenaire clé du dirigeant, il l'accompagne dans ses prises de décisions.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_15",
      nom: "gestionnaire de parc micro-informatique",
      descriptif:
        "<p>Le gestionnaire de parc micro-informatique est chargé d'organiser, d'installer, de remplacer et de transformer l'ensemble du parc informatique d'une entreprise. Pour cela, il a plusieurs casquettes et une disponibilité à toute épreuve...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_150",
      nom: "marchandiseur / marchandiseuse",
      descriptif:
        "<p>Les bonbons près des caisses de supermarchés, c'est lui. Les vins du mois en tête de rayon, c'est encore lui. Les présentoirs pour les promos, les vitrines des boutiques de mode, c'est toujours lui. Le marchandiseur met en scène les produits pour qu'ils soient remarqués... et achetés !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_151",
      nom: "caissier / caissière",
      descriptif:
        "<p>En grande surface, au guichet d'un stade, d'un théâtre ou d'un musée, le caissier reçoit les clients au moment d'encaisser le paiement de leurs achats. Il est aussi un contact primordial pour l'image de marque de l'enseigne. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_152",
      nom: "pâtissier / pâtissière",
      descriptif:
        "<p>Spécialiste des desserts, le pâtissier maîtrise les techniques classiques (pâtes, crèmes, nappages...) et apporte sa touche personnelle. Son savoir-faire est très recherché dans l'artisanat, la restauration ou la pâtisserie industrielle.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_153",
      nom: "charcutier-traiteur / charcutière-traiteuse",
      descriptif:
        "<p>Des terrines aux plats cuisinés en passant par les saucisses ou le jambon, le charcutier-traiteur prépare et vend une multitude de produits. À mi-chemin entre la boucherie et la cuisine, ce métier complet offre de très bons débouchés.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_155",
      nom: "lad-jockey, lad-driver",
      descriptif:
        "<p>Le lad-jockey s'occupe de chevaux de galop ; le lad-driver, de trotteurs. Ces cavaliers soignent et entraînent des chevaux de course pour en faire des champions. Au final, seulement 5 % d'entre eux deviendront jockeys.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_156",
      nom: "ingénieur forestier / ingénieure forestière",
      descriptif:
        "<p>Avec près de 15 millions d'hectares, la forêt française a besoin de professionnels. L'ingénieur forestier en fait partie. Il gère les projets d'aménagement et d'exploitation de cet immense territoire, tout en respectant l'environnement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_157",
      nom: "horticulteur / horticultrice",
      descriptif:
        "<p>L'horticulteur cultive les jardins potagers, floraux, d'ornement et d'agrément. Cette appellation générale regroupe d'autres spécialistes : le floriculteur, le pépiniériste, le maraîcher et l'arboriculteur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_158",
      nom: "paysagiste",
      descriptif:
        "<p>Voilà longtemps que le ou la paysagiste ne se contente plus de fleurir les carrefours. Véritable architecte des espaces verts, il ou elle fait appel à sa créativité pour modeler les villes et les campagnes. Son objectif : améliorer notre cadre de vie.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_159",
      nom: "oenologue",
      descriptif:
        "<p>Scientifique au palais développé, l'oenologue est un chef d'orchestre dans le secteur vinicole. Salarié ou indépendant, il analyse et supervise tous les stades de la production du vin, du cep de vigne à la table. Son credo : la qualité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_16",
      nom: "commerçant / commerçante en alimentation",
      descriptif:
        "<p>Épicier, crémier, primeur... dans sa boutique, le commerçant en alimentation vend les produits qu'il a achetés à des grossistes ou à des producteurs. Il veille à les mettre en valeur pour attirer le client qu'il fidélise par un service impeccable.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_160",
      nom: "opérateur / opératrice sur machine à commande numérique",
      descriptif:
        "<p>Partout où les machines à commande numérique sont présentes, l'opérateur est là pour les programmer, les alimenter, les décharger et veiller à ce que tout se passe au mieux, tout au long de la chaîne de fabrication.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_161",
      nom: "architecte",
      descriptif:
        "<p>Construction d'un immeuble ou d'un hôpital, réhabilitation de logements sociaux, reconversion d'une usine en bureaux... Autant de projets qui ne peuvent pas voir le jour sans l'intervention d'un architecte. Ce maître d'oeuvre exerce le plus souvent en libéral.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_162",
      nom: "coffreur-boiseur / coffreuse-boiseuse",
      descriptif:
        "<p>Constructeur de moules pour le béton armé, le coffreur-boiseur est un ouvrier du bâtiment très demandé. C'est un professionnel incontournable sur les grands chantiers de construction et de travaux publics.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_163",
      nom: "puériculteur/trice",
      descriptif:
        "<p>Le puériculteur ou la puéricultrice est un infirmier ou une infirmière spécialisée dans les soins médicaux apportés aux bébés et aux enfants. Il ou elle joue également un rôle de prévention, d'éducation et de conseil auprès des parents.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_164",
      nom: "météorologiste",
      descriptif:
        "<p>Spécialiste des phénomènes atmosphériques, le ou la météorologiste étudie et analyse les causes et les effets des changements climatiques. Il ou elle établit des prévisions et anticipe les risques de catastrophe naturelle (avalanche, séisme, inondation...).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_165",
      nom: "technicien / technicienne en optique de précision",
      descriptif:
        "<p>Espace, médecine, lunetterie, photographie, contrôle de la pollution... Autant de secteurs où il peut trouver sa place. Le technicien en optique de précision participe, avec le chercheur ou l'ingénieur, à la conception et à la réalisation d'instruments d'optique de haute technologie.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_166",
      nom: "archéologue",
      descriptif:
        "<p>L'archéologue est un spécialiste des \" choses \" anciennes. C'est un chercheur qui étudie les traces laissées par l'homme depuis la Préhistoire. Ses découvertes permettent de mieux connaître et comprendre les modes de vie des sociétés du passé.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_168",
      nom: "collaborateur de notaire / collaboratrice de notaire",
      descriptif:
        "<p>Sous la responsabilité du notaire, le collaborateur prépare et rédige les actes ponctuant la vie des familles et des entreprises : mariage, achat ou vente de propriété, succession, donation...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_169",
      nom: "technicien / technicienne d'exploitation du réseau gaz",
      descriptif:
        "<p>Assurer les branchements, la surveillance et l'entretien des canalisations qui acheminent le gaz dans toute la France, c'est le rôle du technicien d'exploitation du réseau gaz. Il prépare et contrôle des activités de maintenance sur le terrain.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_17",
      nom: "boulanger / boulangère",
      descriptif:
        "<p>Baguettes, pains spéciaux, brioches, croissants, sandwiches... Le boulanger fabrique et vend une grande variété de pains et viennoiseries. Son savoir-faire est très recherché dans l'artisanat mais aussi dans l'industrie et jusqu'à l'étranger !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_170",
      nom: "ingénieur / ingénieure chimiste",
      descriptif:
        "<p>Pétrochimie, agroalimentaire, pharmaceutique, colorants, transformation des plastiques... Dans de nombreuses industries, l'ingénieur chimiste participe à la recherche et développement, à la production... L'environnement fait aussi partie de ses missions.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_171",
      nom: "vétérinaire",
      descriptif:
        "<p>Les vétos ? En ville, leurs patients sont des chiens, des chats, des rongeurs... À la campagne, ce sont les animaux de la ferme. Dans les deux cas, les vétérinaires conseillent les propriétaires des animaux de compagnie ou les éleveurs, mais d'autres débouchés sont possibles.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_172",
      nom: "documentaliste",
      descriptif:
        "<p>Dans une entreprise, une bibliothèque, un musée, un journal..., ce spécialiste des technologies de l'information et de la communication collecte, classe et rend accessible l'information quel qu'en soit le support.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_174",
      nom: "viticulteur / viticultrice",
      descriptif:
        "<p>Pour cultiver la vigne, récolter le raisin, le vinifier puis vendre le vin, le viticulteur doit travailler dur et apprendre en permanence. Dans un pays réputé pour ses grands crus, les amateurs n'ont pas leur place.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_175",
      nom: "technicien / technicienne qualité",
      descriptif:
        "<p>Quel que soit le produit fabriqué (carton d'emballage, papier essuie-tout, etc.) le technicien qualité est là pour en contrôler la conformité par rapport au cahier des charges initial et garantir sa traçabilité en cas de défaut.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_176",
      nom: "sérigraphe",
      descriptif:
        "<p>De l'affiche à la carte à puce, la sérigraphie est partout ! Cette technique d'impression permet de reproduire un motif sur différents types de supports et matériaux. Une technique artisanale dont l'industrie s'est peu à peu emparée.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_177",
      nom: "tapissier / tapissière d'ameublement",
      descriptif:
        "<p>Le tapissier d'ameublement restaure ou conçoit fauteuils et literie. Il décore murs et fenêtres à l'aide de tissus, rideaux, coussins, têtes de lit… Garant de l'harmonie et du style (classique, baroque, contemporain, etc.), il travaille dans les règles de l'art.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_178",
      nom: "assistant réalisateur / assistante réalisatrice",
      descriptif:
        "<p>Organiser les repérages, mettre au point le plan de travail, fixer l'heure du maquillage... l'assistant réalisateur est le collaborateur direct du réalisateur. Il assure le lien entre ce dernier, les acteurs et l'équipe technique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_179",
      nom: "démographe",
      descriptif:
        "<p>Spécialiste des enquêtes et de la statistique, le démographe analyse les impacts économiques et sociaux des phénomènes démographiques. Fécondité, mortalité, migration, répartitions géographiques... sont au coeur de ses études.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_18",
      nom: "fleuriste",
      descriptif:
        "<p>Roses, orchidées, plantes dépolluantes, exotiques, carnivores... le fleuriste propose à la vente une grande diversité de végétaux, de parfums, de formes et de couleurs. Ses compositions et ses bouquets accompagnent chaque événement important de la vie.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_180",
      nom: "sociologue",
      descriptif:
        "<p>Qu'est-ce qu'une famille ? Quand cette notion est-elle apparue ? Voici le type de questions auxquelles tente de répondre un sociologue. Son objectif : comprendre comment fonctionne une société.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_181",
      nom: "magasinier / magasinière cariste",
      descriptif:
        "<p>Boulons, conserves, verres... quelle que soit la nature des marchandises, le magasinier cariste planifie leur livraison, les réceptionne, les transporte, les stocke et les expédie. Un métier nécessaire, qui a su évoluer avec les technologies.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_182",
      nom: "ingénieur / ingénieure du BTP",
      descriptif:
        "<p>Très qualifié en sciences et techniques, l'ingénieur du BTP (bâtiment et travaux publics) est un cadre qui travaille en équipe, prend des décisions et assume des responsabilités au sein d'une entreprise. Il développe les infrastructures d'une région ou d'un pays, en concevant ponts, barrages, bâtiments, routes...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_184",
      nom: "ingénieur pétrolier / ingénieure pétrolière",
      descriptif:
        "<p>Après repérage des gisements potentiels d'hydrocarbures par les géologues et les géophysiciens, l'ingénieur pétrolier procède à leur exploitation, calcule leur rendement, installe les puits, produit et raffine le pétrole.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_185",
      nom: "agent / agente de sûreté ferroviaire",
      descriptif:
        "<p>En uniforme et assermenté, l'agent de la sûreté ferroviaire protège les voyageurs, surveille les gares, les installations et les équipements de la SNCF, les trains de marchandises, etc. Il est formé pour prévenir ou intervenir si besoin.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_187",
      nom: "machiniste-constructeur ou plateau / machiniste-constructrice ou plateau",
      descriptif:
        "<p>Il n'a pas son pareil pour faire apparaître et disparaître un décor. Peu reconnu, le machiniste-constructeur est pourtant indispensable. Un métier tremplin pour accéder à d'autres fonctions.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_188",
      nom: "brodeur / brodeuse",
      descriptif:
        "<p>Le brodeur ajoute des éléments de décoration à la surface d'un tissu afin de l'enrichir et de le mettre en valeur. Perpétuant des savoir-faire traditionnels, il est artisan d'art ou travaille pour l'industrie textile sur des machines plus complexes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_19",
      nom: "palefrenier / palefrenière",
      descriptif:
        "<p>Nourriture, brossage, pansage... Le palefrenier prodigue aux chevaux les soins quotidiens. Mais, s'il les sort à la longe pour les maintenir en forme, il ne les monte que très rarement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_191",
      nom: "officier / officière de police",
      descriptif:
        "<p>Mener des enquêtes, c'est le rôle de l'officière ou de l'officier de police. Dans les différents services de la Police nationale, ce professionnel ou cette professionnelle de terrain assure également des missions de surveillance et de renseignement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_192",
      nom: "géomaticien/ne",
      descriptif:
        "<p>À la croisée de la géographie et de l'informatique, le géomaticien ou la géomaticienne exploite les données pour modéliser le territoire. Il ou elle intervient dans tous les secteurs qui ont besoin d'analyse spatiale : urbanisme, environnement, transport, énergie, assurance...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_193",
      nom: "chargé / chargée de production",
      descriptif:
        "<p>Le chargé de production collabore à la création d'un film, d'une émission de télévision, d'un spectacle. Responsable du budget, il supervise la production et assure la gestion matérielle et humaine du projet.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_194",
      nom: "régisseur général / régisseuse générale cinéma",
      descriptif:
        "<p>Lors du tournage d'un film, le régisseur général assure la logistique. Il accueille les comédiens, trouve des places de parking, achemine le matériel technique... À lui de gérer le quotidien des plateaux pour que tout se passe au mieux.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_195",
      nom: "chef / cheffe comptable",
      descriptif:
        "<p>Le chef comptable ne se contente pas de collecter les chiffres de sa société. Il les analyse et en tire des bilans et des synthèses. Il est ainsi l'interlocuteur du chef d'entreprise ou du directeur financier, qu'il aide à prendre des décisions.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_196",
      nom: "responsable de rémunération",
      descriptif:
        "<p>Si les ressources humaines se sont diversifiées, l'administration de la paie n'en reste pas moins une fonction clé dans l'entreprise. De bac + 3 à bac + 5, les jeunes diplômés peuvent s'y faire une place en tant que responsable de rémunération.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_197",
      nom: "technicien / technicienne paysagiste",
      descriptif:
        "<p>Le technicien ou la technicienne paysagiste travaille rarement en solo : c'est toute une équipe qu'il lui faut gérer pour réaliser jardins et espaces verts, en se conformant aux plans du paysagiste concepteur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_198",
      nom: "technicien électronicien / technicienne électronicienne",
      descriptif:
        "<p>Le technicien électronicien répare, installe et fabrique des produits intégrant des composantes électroniques : appareils audio-vidéo, instruments de mesure, systèmes de navigation, etc. Assistant de l'ingénieur, il maîtrise de multiples compétences.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_199",
      nom: "vendeur-conseil / vendeuse-conseil en matériel agricole",
      descriptif:
        "<p>Le vendeur-conseil en matériel agricole assure la vente des tracteurs et autres équipements agricoles. Un métier qui exige patience et compétences techniques. Mais, quand on a le bon profil, l'emploi est au rendez-vous.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_2",
      nom: "directeur / directrice d'hôtel",
      descriptif:
        "<p>Animateur d'équipe, gestionnaire et commercial, le directeur d'hôtel a tout du chef d'entreprise. Ses responsabilités varient fortement selon le type d'établissement qu'il dirige, mais exigent une présence de tous les instants.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_20",
      nom: "opérateur / opératrice de fabrication de produits alimentaires",
      descriptif:
        "<p>L'opérateur de fabrication de produits alimentaires transforme les matières premières en produits destinés à l'alimentation. Découpe, cuisson, mise en barquettes ou en bouteilles... il assure une ou plusieurs étapes de la réalisation d'un produit.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_200",
      nom: "maraîcher/ère",
      descriptif:
        "<p>Sans lui, pas de beaux légumes frais dans l'assiette. Le maraîcher cultive les légumes en plein champ ou sous serre. Un métier en contact avec la nature, moins physique grâce à la mécanisation, mais qui nécessite toujours rigueur et attention.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_201",
      nom: "bio-informaticien / bio-informaticienne",
      descriptif:
        "<p>Le bio-informaticien met l'univers normalisé de l'informatique au service du monde très mouvant des sciences du vivant. Ce qui exige une véritable double compétence.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_202",
      nom: "responsable assurance qualité",
      descriptif:
        "<p> Améliorer la qualité des produits et des services de l'entreprise est la mission du responsable assurance qualité. Un rôle essentiel pour l'image et la compétitivité de son employeur, mais qui demande l'adhésion de tous : de l'ouvrier jusqu'au PDG (président directeur général).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_203",
      nom: "chef / cheffe de projet informatique",
      descriptif:
        "<p>Véritable chef d'orchestre, le chef de projet informatique traduit les demandes de son client en solutions informatiques. De l'analyse des besoins à la livraison du produit, ses missions exigent des compétences aussi bien techniques que managériales.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_204",
      nom: "ingénieur/e technico-commercial/e en informatique",
      descriptif:
        "<p>L'ingénieur technico-commercial en informatique est le champion de la solution informatique sur mesure pour les entreprises et les administrations. Son rôle : accompagner un projet, de sa négociation commerciale à sa réalisation technique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_205",
      nom: "technicien / technicienne radioprotection",
      descriptif:
        "<p>Sur site ou chantier, le technicien en radioprotection joue un rôle-clé en analysant chaque environnement et situation afin de mettre en place des mesures de prévention et de limite des risques de rayonnement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_206",
      nom: "ingénieur / ingénieure calcul",
      descriptif:
        "<p>Frottements, température, pression, à l'aide de logiciels pointus de calcul, l'ingénieur calcul étudie la résistance des matériaux et structures. Il peut exercer dans l'aéronautique, la prospection pétrolière, la construction ferroviaire, urbaine...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_207",
      nom: "responsable de production alimentaire",
      descriptif:
        "<p>Le responsable de production alimentaire a en charge le suivi de la fabrication industrielle et du conditionnement des produits alimentaires de son usine : légumes en boîtes de conserve ou surgelés, plats préparés, pâtisseries, yaourts, boissons...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_208",
      nom: "ingénieur / ingénieure d'application",
      descriptif:
        "<p> Dans le secteur de la santé, l'ingénieur d'application travaille pour un fabricant industriel de matériels médicaux (scanners, échographes, etc.). Il assure la promotion d'une gamme de produits et forme les utilisateurs. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_209",
      nom: "chargé /chargée de recherche en recrutement",
      descriptif:
        "<p>Le chargé de recherche en recrutement identifie et présélectionne des candidats qui correspondent au profil établi par le responsable de recrutement. Il travaille en amont du recrutement proprement dit, le plus souvent en cabinet spécialisé.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_21",
      nom: "employé / employée de chai",
      descriptif:
        "<p>L'employé de chai intervient depuis l'arrivée du raisin en cave jusqu'à l'expédition des bouteilles. Dans une coopérative, chez un exploitant ou un négociant, ses responsabilités varient selon son niveau de formation et son degré d'autonomie.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_210",
      nom: "chauffeur-livreur / chauffeuse-livreuse",
      descriptif:
        "<p>Indispensable avec l'explosion du e-commerce, la chauffeuse-livreuse ou le chauffeur-livreur charge son véhicule avant sa tournée qui peut comprendre des dizaines d'arrêts pour livrer ses clients en temps et en heure. Organisation, ponctualité et amabilité sont requises !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_211",
      nom: "ingénieur/e production en aéronautique",
      descriptif:
        "<p>La gestion d'une ligne de production dans le milieu aéronautique demande à la fois de hautes compétences techniques et une grande capacité à manager. Des qualités que l'ingénieur production possède. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_212",
      nom: "consultant/e en systèmes d'information",
      descriptif:
        "<p>Expert technique, le consultant en systèmes d'information intervient à la demande d'une entreprise pour améliorer ses systèmes d'information : serveurs, bases de données, logiciels de gestion...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_213",
      nom: "gestionnaire de patrimoine",
      descriptif:
        "<p>Constamment à la recherche des solutions les mieux adaptées aux attentes de ses clients, le gestionnaire de patrimoine, est un spécialiste de l'investissement sur mesure et un expert en ingénierie patrimoniale qui possède de solides connaissances en économie, finance, fiscalité et droit</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_214",
      nom: "agent / agente artistique",
      descriptif:
        "<p>Comédien, chanteur, mannequin, écrivain... Tous ces artistes se reposent sur une seule et même personne : leur agent. Intermédiaire digne de confiance, il ou elle prend en charge leur carrière de A à Z. Avec un objectif : la faire décoller !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_215",
      nom: "chargé/e de pharmacovigilance",
      descriptif:
        "<p>Le chargé de pharmacovigilance contrôle la tolérance des médicaments fabriqués et commercialisés par son laboratoire pharmaceutique. Il analyse le moindre effet indésirable.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_216",
      nom: "infirmier/ère anesthésiste",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_217",
      nom: "développeur/euse multimédia",
      descriptif:
        "La développeuse ou le développeur multimédia écrit du code à l'aide d'un langage de programmation et conçoit les algorithmes qui permettront de construire un produit multimédia abouti au niveau technique. Il ou elle travaille au sein d'une équipe pluridisciplinaire et doit être à l'écoute des autres.<br/><br/><h3>Après le bac</h3>\n<p>2 ans pour obtenir un BTS&nbsp; services informatiques aux organisations option B solutions logicielles et applications métiers, 3 ans pour un BUT en informatique ; 5 ans pour un diplôme d'ingénieur ou un master.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_218",
      nom: "consultant/e en management de l'innovation",
      descriptif:
        "<p>Véritable guide pour les entreprises qui veulent innover, il les aide dans la mise en oeuvre de solutions technologiques, afin d'améliorer leur rentabilité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_219",
      nom: "chef / cheffe de mission humanitaire",
      descriptif:
        "<p>C'est le capitaine d'une mission humanitaire. Il suit de près l'évolution de la situation pour orienter au mieux les actions de terrain. En relation constante avec son ONG (organisation non gouvernementale), il détermine la stratégie de la mission, tout en dirigeant une équipe. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_22",
      nom: "mécanicien / mécanicienne et technicien / technicienne moto",
      descriptif:
        "<p>Dans l'atelier d'un garage ou d'une concession moto, le mécanicien assure les réparations, contrôles et réglages simples. Tandis que le technicien se charge des diagnostics et des opérations plus complexes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_221",
      nom: "ostéopathe",
      descriptif:
        "<p>Un membre bloqué ? Ce praticien peut vous soulager. L'ostéopathe est qualifié dans la manipulation des muscles et des articulations osseuses. Il pratique exclusivement avec les mains pour traiter certains problèmes mécaniques du corps humain.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_222",
      nom: "inspecteur / inspectrice de banque",
      descriptif:
        "<p>De la tenue des comptes au respect des réglementations, en passant par la stratégie commerciale, l'inspecteur de banque effectue des contrôles à tous les niveaux d'un établissement bancaire. Ses principes : observer, diagnostiquer et conseiller.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_223",
      nom: "climatologue",
      descriptif:
        "<p>La qualité de l'eau ou de l'air comme le réchauffement climatique sont en tête des préoccupations. Les climatologues sont des chercheurs qui mènent des études sur le long terme pour prévoir les évolutions de notre climat et les conséquences possibles.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_224",
      nom: "ingénieur/e forage",
      descriptif:
        "<p>Essentiel à l'industrie pétrolière, l'ingénieur forage prépare et suit les opérations de percement sous-marins ou souterrain. Cet ingénieur spécialisé, dont la carrière se déroule en grande partie à l'étranger, est très recherché.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_225",
      nom: "ingénieur / ingénieure analyste de l'air",
      descriptif:
        "<p>L'ingénieure ou l'ingénieur analyste de l'air a un rôle de sentinelle : il ou elle surveille les particules que nous respirons. Quand une pollution importante est détectée, il lui faut informer les pouvoirs publics et proposer des solutions pour améliorer la qualité de l'air.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_226",
      nom: "chef/fe d'exploitation d'usine d'incinération",
      descriptif:
        "<p>Incinérer les déchets consiste à les brûler à très haute température (plus de 1 000°C). La cheffe ou le chef d'exploitation d'usine d'incinération fait en sorte que tout se passe bien durant cette opération et garantit la conformité du procédé.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_227",
      nom: "animateur / animatrice de bassin versant",
      descriptif:
        "<p>L'animatrice ou l'animateur de bassin versant sensibilise à la protection du milieu aquatique local. Ils contribuent à la conservation des ressources en eau et à leur qualité en impliquant les agriculteurs dans une démarche d'amélioration continue.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_228",
      nom: "ingénieur / ingénieure cloud computing",
      descriptif:
        "<p>Pour les nouveaux besoins des entreprises en termes d'accessibilité de leur système d'information, l'ingénieur cloud computing travaille sur le stockage des données en dehors de l'entreprise et à leur accès sécurisé depuis mobiles, tablettes ou postes de travail.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_229",
      nom: "chef/fe de produit technique en informatique",
      descriptif:
        "<p>Le chef de produit technique en informatique gère la vie d'un produit, de sa conception à sa commercialisation, avec les équipes techniques et commerciales de l'entreprise. Il allie compétences techniques et marketing pour ce poste transversal.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_23",
      nom: "mécanicien / mécanicienne d'entretien d'avion",
      descriptif:
        "<p>L'entretien d'un avion, de la réparation du train d'atterrissage au contrôle technique, n'a pas de secret pour le mécanicien d'entretien d'avion. De sa compétence dépend la vie de milliers de passagers. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_230",
      nom: "statisticien/ne en analyse sensorielle",
      descriptif:
        "<p>Donner des éléments objectifs et quantifiables aux décideurs concernant leurs produits, c'est le rôle du statisticien en analyse sensorielle qui travaille sur les goûts des consommateurs, dans l'agroalimentaire, la cosmétique, l'automobile, etc.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_231",
      nom: "ingénieur / ingénieure intégration satellite",
      descriptif:
        "<p>Il coordonne et organise les activités d'intégration d'un satellite, notamment en supervisant l'assemblage des différentes parties, puis il teste la résistance de l'engin avant de le lancer sur orbite.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_232",
      nom: "hydrobiologiste",
      descriptif:
        "L'hydrobiologiste est spécialiste de la qualité de l'eau. Depuis les prélèvements en milieu aquatique jusqu'au laboratoire, en passant par l'ordinateur pour écrire des rapports, le métier demande de la rigueur et des connaissances scientifiques pointues.<br/><br/><h3>Après le bac</h3>\n<p>5 ans pour un diplôme d'ingénieur ou un master en hydro-biologie, sciences de l'environnement... La majorité des recrutements s'effectuent au niveau doctorat (8 ans après le bac).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_233",
      nom: "pépiniériste",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_234",
      nom: "conseiller/ère d'élevage",
      descriptif:
        "<p>Le conseiller d'élevage apporte son expertise pour optimiser la production. Il conseille l'éleveur sur la gestion et le suivi de ses animaux, pour améliorer la qualité de la production (lait, viande, oeufs, etc.), ainsi que la rentabilité de l'exploitation.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_239",
      nom: "concepteur/trice designer packaging",
      descriptif:
        "<p>Le concepteur designer packaging crée des emballages adaptés à la demande d'un client. Il respecte un cahier des charges détaillant l'ensemble des contraintes appliquées au packaging tout au long de son cycle de vie..</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_24",
      nom: "astrophysicien / astrophysicienne",
      descriptif:
        "<p>Scientifique de haut niveau, l'astrophysicien étudie les étoiles et les planètes afin de comprendre le fonctionnement de l'univers.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_242",
      nom: "ingénieur/e production en mécanique",
      descriptif:
        "<p>L'ingénieur de production mécanique organise et supervise les opérations de fabrication d'un produit industriel en respectant les contraintes de coûts, de qualité et de délais. Il est au coeur des métiers de la mécanique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_243",
      nom: "ingénieur/e en électronique numérique",
      descriptif:
        "<p>Pass transport sans contact, téléphones portables, drones, guidages de missiles... l'ingénieur en électronique numérique conçoit toute une gamme de produits pour le grand public ou les professionnels. Un métier où innovation et efficacité se cultivent au quotidien.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_244",
      nom: "ingénieur / ingénieure systèmes embarqués",
      descriptif:
        "<p>L'ingénieur électronique spécialisé en systèmes embarqués conçoit des systèmes complexes pour des objets mobiles et communicants via un réseau internet... à des fins de surveillance, de contrôle, de communication, de santé, de sécurité...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_246",
      nom: "responsable qualité aéronautique",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_247",
      nom: "<span>Thanatopracteur / thanatopractrice</span>",
      descriptif:
        "Salariés d'une entreprise de pompes funèbres, d'un service municipal ou à leur compte, les thanatopracteurs préparent les corps des défunts avant la mise en cercueil. Cette profession, très réglementée, demande rigueur, tact et endurance.<br/><br/><h3>Après le bac</h3>\n<p>Le métier n'est accessible qu'aux titulaires du diplôme national de thanatopraxie. Il se prépare dans 2 UFR (à Angers et Lyon) ou en écoles privées. Liste des centres de formation sur le site du Comité national d'évaluation de la formation pratique de thanatopracteur (<a href=\"https://www.cnt-pratique.fr/\">https://www.cnt-pratique.fr/</a>)</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_25",
      nom: "styliste",
      descriptif:
        "<p>Sensible à l'air du temps, le styliste anticipe les tendances de la mode et imprime sa griffe aux vêtements et accessoires qu'il crée. Imaginatif et doté d'un bon coup de crayon, il est aussi en phase avec les réalités (économiques, techniques).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_250",
      nom: "employé/e d'élevage",
      descriptif:
        "<p>Salarié sur une exploitation agricole spécialisée dans la production animale (bovins, ovins, caprins, porcs, volailles...), l'employé d'élevage concourt à soigner les animaux et veiller au bien-être du troupeau. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_253",
      nom: "journaliste radio et TV",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_257",
      nom: "décorateur / décoratrice d'intérieur",
      descriptif:
        "<p>Le décorateur d'intérieur, conçoit des espaces (appartements, restaurants, boutiques...) en respectant désirs de son client et contraintes du lieu qu'il tente d'optimiser. Il allie sens esthétique, connaissance des matériaux et des différents intervenants.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_258",
      nom: "architecte d'intérieur",
      descriptif:
        "<p> L'architecte d'intérieur conçoit et aménage des espaces intérieurs fonctionnels, esthétiques et confortables en jouant avec les volumes, la lumière, le mobilier et les matériaux, tout en tenant compte de contraintes techniques et budgétaires.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_26",
      nom: "conducteur / conductrice de travaux",
      descriptif:
        "<p>Le conducteur ou la conductrice de travaux organise et suit les différents moyens techniques, humains et financiers nécessaires à la réalisation d'un chantier de construction ou de travaux publics, depuis le début d'un projet de construction jusqu'à sa livraison, en respectant les délais.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_261",
      nom: "décorateur / décoratrice-scénographe",
      descriptif:
        "<p>Mettre en scène une exposition, réaliser le décor d'une émission ou reconstituer un salon dans un film d'époque... Ces missions ont un point commun : elles sont l'oeuvre du décorateur-scénographe. Ses meilleures armes : créativité et ingéniosité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_262",
      nom: "cadreur / cadreuse",
      descriptif:
        "<p>Un panoramique réussi, un mouvement de travelling impeccable... c'est l'oeuvre du cadreur, qui assure la prise de vues. Un métier très convoité au cinéma, mais qui est plus accessible en reportage ou sur un plateau de télévision.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_263",
      nom: "couvreur / couvreuse",
      descriptif:
        "<p>Grâce au couvreur, le ciel ne nous tombe pas sur la tête ! Spécialiste de la construction et de la réfection des toits, il nous met à l'abri des intempéries (pluie, neige, vent... ou soleil accablant). Un métier incontournable dans le bâtiment.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_264",
      nom: "monteur / monteuse en installations thermiques et climatiques",
      descriptif:
        "<p>Grâce à cet expert du chaud et du froid, on se sent bien, hiver comme été, à la maison, au bureau ou dans un centre commercial. Le monteur en installations thermiques et climatiques contribue à l'amélioration de notre confort, mais aussi aux économies d'énergie et au respect de l'environnement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_265",
      nom: "conseiller / conseillère agricole",
      descriptif:
        "<p>Le conseiller agricole accompagne les agriculteurs. À l'heure où l'agriculture se complexifie, il fait figure de partenaire privilégié de l'exploitant. Il l'aide à développer son activité, à choisir de nouveaux équipements et à améliorer la qualité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_266",
      nom: "plombier / plombière",
      descriptif:
        "<p>Un robinet qui fuit, un évier bouché, un chauffe-eau en panne... On a toujours besoin d'un plombier ! Le métier requiert une bonne condition physique, mais aussi une capacité à analyser la situation et à choisir la bonne méthode d'intervention.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_267",
      nom: "attaché commercial / attachée commerciale",
      descriptif:
        "<p>Véritable ambassadeur des marques qu'il représente, l'attaché commercial trouve les arguments pour convaincre ses clients d'acheter les produits dont il s'occupe. Son objectif : décrocher de nouveaux marchés pour son entreprise.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_268",
      nom: "boucher / bouchère",
      descriptif:
        "<p>Spécialiste de la viande, le boucher intervient de l'approvisionnement à la vente au détail. Qu'il travaille à son compte ou dans un supermarché, ce professionnel doit posséder des compétences techniques, le goût du contact et le sens de l'accueil.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_269",
      nom: "libraire",
      descriptif:
        "<p>Romans, guides pratiques, manuels scolaires, bandes dessinées… Quel que soit le domaine, le libraire guide et conseille sa clientèle. Plus qu'un simple commerçant, c'est un passionné de livres qui sait faire partager ses coups de coeur !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_27",
      nom: "conducteur / conductrice d'engins de travaux publics",
      descriptif:
        "<p>Parmi les principaux acteurs d'une chantier, le conducteur ou la conductrice d'engins de travaux publics pilote les engins les plus divers (niveleuse, pelle hydraulique, chargeuse, grue, pelleteuse...) utilisés pour la réalisation d'un chantier (terrassement, nivellement, manutention des charges, chargement des engins de transport des matériaux).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_270",
      nom: "agent immobilier / agente immobilière",
      descriptif:
        "<p>Commercial dans l'âme, l'agent immobilier met en relation vendeur et acheteur, ou bailleur et locataire, en vue de vendre ou louer des biens. À la tête d'une petite entreprise, franchisée ou non, il anime une équipe de négociateurs.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_271",
      nom: "guichetier / guichetière",
      descriptif:
        "<p>Le guichetier est la première personne à qui l'on a affaire quand on pousse la porte d'une agence bancaire. La qualité de son accueil compte beaucoup pour l'image de l'établissement. Ce poste permet à un débutant de faire ses preuves.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_273",
      nom: "chef / cheffe de fabrication des industries graphiques",
      descriptif:
        "<p>Livrer des imprimés dans les délais, tel est le défi relevé au quotidien par le chef de fabrication des industries graphiques. Ses missions : analyser les commandes de ses clients, organiser les plannings et gérer l'ensemble de la production.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_274",
      nom: "conducteur / conductrice de machine à imprimer",
      descriptif:
        "<p>Préparer la machine en effectuant les réglages, réaliser des essais, lancer l'impression d'étiquettes, de journaux, magazines, livres, affiches ou encore plaquettes... sont autant d'opérations orchestrées par le conducteur de machines à imprimer.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_275",
      nom: "économiste de la construction",
      descriptif:
        "<p>Présent en amont et durant tout le chantier, l'économiste de la construction chiffre l'ensemble des coûts, puis vérifie que ceux-ci ne dérapent pas. Il assure également une mission de conseil auprès du maître d'oeuvre, de l'architecte ou du client.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_276",
      nom: "orthoprothésiste",
      descriptif:
        "<p>L'orthoprothésiste conçoit et pose des prothèses (pour remplacer un membre) et des orthèses (pour corriger une déficience). Son but : compenser les handicaps et redonner aux patients leur autonomie.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_277",
      nom: "enseignant/e d'art",
      descriptif:
        "<p>À travers la découverte des oeuvres et par la pratique, l'enseignant d'art, créatif et pédagogue, éduque l'oeil et la main de ses élèves, développe leur sens artistique et esthétique, et leur communique le goût de la création.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_278",
      nom: "conseiller principal d'éducation / conseillère principale d'éducation",
      descriptif:
        "<p>Figure incontournable du collège et du lycée, le CPE (conseiller principal d'éducation) seconde le chef d'établissement dans l'organisation de la vie scolaire et assure le lien entre les familles et l'équipe pédagogique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_279",
      nom: "psychanalyste",
      descriptif:
        "<p>Le psychanalyste est un thérapeute qui aide une personne à mieux vivre grâce à une cure psychanalytique. Inventée par Freud, cette technique permet au patient d'explorer son inconscient pour essayer de résoudre les conflits qui remontent à son enfance et pèsent sur son existence.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_28",
      nom: "assistant commercial / assistante commerciale",
      descriptif:
        "<p>Traitement des commandes, facturation, tenue du fichier clients, surveillance des stocks... l'assistant commercial assure tout le suivi des ventes. Premier contact du client avec l'entreprise, il se voit confier de plus en plus de responsabilités.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_280",
      nom: "ergothérapeute",
      descriptif:
        "<p>Professionnel/le de santé, l'ergothérapeute intervient auprès des personnes de tous âges ou en situation de handicap, pour faciliter la réalisation de leurs activités, en tenant compte de leurs choix de vie et de leur environnement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_281",
      nom: "opticien-lunetier / opticienne-lunetière",
      descriptif:
        "<p>Commercial, technicien, professionnel de la santé... il cumule les fonctions ! Et peut regarder l'avenir avec confiance : le vieillissement de la population et un meilleur suivi médical lui garantissent une clientèle toujours plus nombreuse.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_282",
      nom: "podo-orthésiste",
      descriptif:
        "<p>Le podo-orthésiste est un spécialiste de l'appareillage du pied. Il facilite la marche au quotidien, en concevant et en fabriquant des chaussures, des semelles et des prothèses orthopédiques sur mesure.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_283",
      nom: "gérant / gérante de restauration collective",
      descriptif:
        "<p>Le gérant de restauration collective veille sur la préparation de plusieurs centaines de repas quotidiens, que ce soit pour une école, un hôpital ou une entreprise. Chef d'équipe et gestionnaire, son travail varie selon la taille de son restaurant.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_284",
      nom: "chargé / chargée de valorisation de la recherche",
      descriptif:
        "<p>Toujours au courant des dernières découvertes, le chargé de valorisation de la recherche accompagne les chercheurs pour protéger leurs travaux et favoriser leur application dans l'industrie.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_285",
      nom: "hôte / hôtesse d'accueil",
      descriptif:
        "<p>Premier interlocuteur des visiteurs et des collaborateurs, l'hôte d'accueil représente l'image de l'organisation qui l'emploie ; sa prestation doit donc être irréprochable. Ce professionnel est recherché, surtout s'il maîtrise l'anglais.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_286",
      nom: "chef / cheffe de chantier en installations électriques",
      descriptif:
        "<p>Intermédiaire entre le bureau d'études et les monteurs électriciens, le chef de chantier supervise le montage d'installations électriques dans les entreprises, les administrations, les logements. Un métier qualifié qui demande de l'expérience.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_287",
      nom: "conducteur/trice de machines agricoles",
      descriptif:
        "<p>Tracteur, ensileuse, moissonneuse-batteuse... Autant d'engins pilotés par le conducteur de machines agricoles, pour le compte d'un exploitant ou d'une entreprise de travaux agricoles. Il gère aussi l'entretien courant de ces machines sophistiquées. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_288",
      nom: "technicien / technicienne biologiste",
      descriptif:
        "<p>Recherche publique, hôpitaux, industrie pharmaceutique et agroalimentaire… les secteurs où les biologistes peuvent exercer sont nombreux. Leur rôle : analyser, mettre au point et contrôler les produits.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_289",
      nom: "ébéniste",
      descriptif:
        "<p>Fabrication de mobilier contemporain, restauration ou copie d'anciens... l'ébéniste réalise ou répare du mobilier ou des aménagements en bois. Salarié d'une entreprise ou à son compte, il exerce un métier d'art. Talent et motivation indispensables.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_29",
      nom: "masseur / masseuse-kinésithérapeute",
      descriptif:
        "<p>Une bronchiolite, une hospitalisation, une chute de vélo... Autant de situations où le masseur-kinésithérapeute intervient. Ce spécialiste de la rééducation utilise les massages et la gymnastique médicale pour aider ses patients à recouvrer leurs capacités.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_290",
      nom: "technicien forestier / technicienne forestière",
      descriptif:
        "<p>Très proche du terrain, le technicien forestier est un véritable gestionnaire de la forêt. Cette profession, aux débouchés restreints, s'adresse à des jeunes motivés par le contact avec la nature.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_291",
      nom: "tailleur-couturier / tailleuse-couturière",
      descriptif:
        "<p>Le tailleur-couturier modifie, ajuste, retouche ou crée des vêtements sur mesure et personnalisés. Il est doté d'un savoir-faire rigoureux, d'un grand sens de l'esthétique et d'une certaine fibre commerciale pour contenter une clientèle exigeante.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_292",
      nom: "formateur / formatrice d'adultes",
      descriptif:
        "<p>Dans des domaines très divers, le formateur d'adultes s'adresse à des publics variés qui veulent mettre à jour leurs connaissances ou acquérir de nouveaux apprentissages, dans le cadre d'une évolution de leur métier.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_293",
      nom: "professeur / professeure de musique ou de danse",
      descriptif:
        "<p>Collège, lycée, conservatoire, association... Quel que soit son cadre de travail, le professeur de musique ou de danse initie les élèves à son art en développant leur technique et leur créativité, depuis l'initiation jusqu'au perfectionnement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_294",
      nom: "technicien / technicienne en traitement des déchets",
      descriptif:
        "<p>Métal, plastique, verre, papier... Tous ces matériaux sont triés, analysés avant d'être recyclés par la technicienne ou le technicien en traitement des déchets. Chargés de leur redonner une nouvelle vie, ils contribuent au respect de l'environnement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_295",
      nom: "tailleur / tailleuse de pierre",
      descriptif:
        "<p>Héritier des bâtisseurs de cathédrales, cet ouvrier du bâtiment est un véritable artiste. Dépositaire d'un savoir-faire ancestral et respectueux des traditions, il n'en est pas moins ouvert aux dernières évolutions technologiques.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_296",
      nom: "charpentier / charpentière métallique",
      descriptif:
        "<p>Le charpentier ou la charpentière métallique fabrique ou assemble les structures en acier qui forment la charpente des ponts, des immeubles, des pylônes, des bâtiments, etc. Il ou elle travaille en atelier ou sur un chantier. Son savoir-faire est recherché.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_297",
      nom: "contrôleur / contrôleuse technique automobile",
      descriptif:
        "<p>Pneus, freins, suspensions, échappement... le contrôleur technique ausculte les véhicules automobiles sous tous les angles. Objectif : pointer les dysfonctionnements par rapport à la réglementation, et conseiller les conducteurs sur les réparations à effectuer.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_298",
      nom: "installateur / installatrice en télécoms",
      descriptif:
        "<p>L'installateur en télécoms installe les équipements et réseaux télécoms des particuliers (téléphone, télévision, Internet), des entreprises et des administrations. Puis il s'assure de leur bon fonctionnement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_299",
      nom: "chef / cheffe de rayon",
      descriptif:
        "<p>Maillon essentiel de la grande distribution, le chef de rayon gère et anime, avec l'aide de son équipe, un rayon spécialisé. Son objectif : générer du trafic et faire progresser le chiffre d'affaires.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_3",
      nom: "réceptionniste",
      descriptif:
        "<p>Ambassadeur de l'hôtel, le réceptionniste accueille les clients et gère les réservations. Il informe et répond aux questions, prépare les factures... Un poste tremplin à responsabilités où l'organisation et le sang-froid sont indispensables.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_30",
      nom: "responsable de formation",
      descriptif:
        "<p>Améliorer les compétences des salariés et répondre à leurs attentes, l'enjeu est de taille pour les entreprises, soucieuses de rester compétitives. Le responsable de formation occupe donc un poste stratégique au sein des ressources humaines. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_300",
      nom: "attaché / attachée de recherche clinique",
      descriptif:
        "<p>Avant sa mise sur le marché, un nouveau médicament est toujours testé sur des personnes volontaires. L'ARC (attaché de recherche clinique) assure le suivi scientifique et administratif de cet essai clinique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_301",
      nom: "secrétaire des Affaires étrangères",
      descriptif:
        "<p>Le secrétaire des Affaires étrangères remplit un rôle de représentant de la France à l'étranger, où il exerce des fonctions diplomatiques, consulaires et culturelles. Il peut également travailler à l'administration centrale, à Paris, comme rédacteur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_302",
      nom: "chef / cheffe de projet éolien",
      descriptif:
        "<p>Pas question d'installer un parc éolien sans un travail d'étude, de prospection et de concertation en amont. Choisir un site, identifier les obstacles et proposer des solutions : c'est le rôle du chef de projet éolien.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_303",
      nom: "ingénieur / ingénieure R&D en énergies renouvelables",
      descriptif:
        "<p>Accompagner la transition énergétique : tel est l'objectif de l'ingénieur R&amp;D (recherche et développement) en énergies renouvelables. Ce professionnel de haut niveau fait progresser les énergies renouvelables en rentabilisant les solutions existantes et en innovant...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_304",
      nom: "ingénieur / ingénieure gaz",
      descriptif:
        "<p>Il n'existe pas un, mais différents profils d'ingénieurs dans le domaine gazier. Leur mission : développer, entretenir et exploiter les réseaux de transport ou de distribution du gaz.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_305",
      nom: "opérateur / opératrice de raffinerie",
      descriptif:
        "<p>Au sein d'une unité pétrolière, l'opérateur de raffinerie fait fonctionner et surveille les installations de production qui transforment le pétrole brut en différents produits : fioul, kérosène, essence, paraffine... C'est ce que l'on appelle le raffinage.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_306",
      nom: "dessinateur / dessinatrice en construction mécanique",
      descriptif:
        "<p>Avant d'être fabriqué en usine, un produit (appareil de chauffage, élément de plomberie...) doit d'abord être décrit par une série de plans et de schémas. C'est le dessinateur en construction mécanique qui réalise le plan d'ensemble du produit et le plan détaillé des pièces qui le composent.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_307",
      nom: "éclairagiste",
      descriptif:
        "<p>Des ombres chinoises jusqu'aux effets de lumière programmés sur ordinateur, la palette de l'éclairagiste est large. À lui de créer un univers visuel au plus près des intentions du metteur en scène ou du chorégraphe. Ses éclairages viennent rythmer une scène ou créer une ambiance sur un tournage... Tout un art !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_308",
      nom: "étalagiste",
      descriptif:
        "<p>Séduire les passants, leur donner envie d'entrer, peut-être d'acheter… telle est l'ambition de l'étalagiste, véritable metteur en scène des produits dans les vitrines des magasins ou sur les stands des foires et des expositions.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_309",
      nom: "céramiste",
      descriptif:
        "<p>Poteries vernissées, grès, porcelaines, faïences… issues du mélange de l'argile et de l'eau après cuisson, ces créations surgissent des mains du céramiste qui procède ensuite à leur décoration.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_31",
      nom: "cartographe",
      descriptif:
        "<p>Les cartographes exploitent des données pour fournir une représentation visuelle d'un territoire, concernant des notions abstraites (carte politique, économique, démographique,...) ou par des éléments physiques (carte marine, routière, géologique...).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_310",
      nom: "orfèvre",
      descriptif:
        "<p>Théière, timbale, tabernacle, couvert, aiguière, ciboire... avec toujours le même souci du détail, l'orfèvre fabrique et restaure des objets en or, argent ou étain, pour la maison, la table, les cérémonies du culte ou les événements sportifs.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_311",
      nom: "éducateur spécialisé / éducatrice spécialisée",
      descriptif:
        "<p>L'éducateur spécialisé aide des personnes en situation de handicap ou en difficulté à devenir autonomes. Avec une double mission : contribuer à leur épanouissement personnel et à leur insertion en société.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_312",
      nom: "moniteur/trice de ski",
      descriptif:
        "<p>Amoureux de la montagne et sportif émérite, le moniteur de ski évolue dans un cadre de rêve et transmet sa passion aux vacanciers débutants comme experts. Mais il doit composer avec la concurrence et le caractère saisonnier de son activité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_314",
      nom: "chargé / chargée des relations publiques",
      descriptif:
        "<p>Le chargé des relations publiques (RP) cherche à promouvoir l'image d'une entreprise ou d'une marque auprès d'interlocuteurs variés. Affichage, publicité radio, télé ou Internet, séminaires, salons, dossiers de presse... sont ses outils.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_315",
      nom: "mixeur / mixeuse son",
      descriptif:
        "<p>Le mixeur son mélange les voix, les musiques, les ambiances... pour donner à un film ou à un disque une ambiance sonore. Une technique complexe qui exige une certaine sensibilité musicale.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_316",
      nom: "concepteur-rédacteur / conceptrice-rédactrice",
      descriptif:
        "<p>Le concepteur-rédacteur travaille à trouver des idées et des mots qui transformeront un slogan publicitaire en une formule-choc qui, peut-être, fera bientôt partie de notre culture...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_317",
      nom: "vendeur-magasinier / vendeuse-magasinière en fournitures automobiles",
      descriptif:
        "<p>Phares, plaquettes de frein, pneus, autoradio, pompe à gasoil, pare-brise... le vendeur-magasinier en fournitures automobiles se partage entre le comptoir et le magasin. À la fois technicien, gestionnaire et vendeur, il conseille les particuliers comme les professionnels (de l'auto, de la moto ou du véhicule industriel).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_318",
      nom: "façonnier / façonnière des industries graphiques",
      descriptif:
        "<p>Massicotage, pliage, encartage, reliure... autant d'opérations réalisées par le façonnier des industries graphiques. Dernier maillon de la chaîne graphique, il donne aux documents imprimés (livres, journaux, brochures, etc.) leur forme définitive.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_319",
      nom: "<span>Assistant / assistante de direction</span>",
      descriptif:
        "Pour seconder la direction d'une entreprise, l'assistante ou l'assistant de direction exerce des missions variées, de la prise de rendez-vous à l'organisation de déplacements ou de réunions en passant par le filtrage des appels ou la réception des clients. Un poste à responsabilités.<br/><br/><h3>Après le bac</h3>\n<p>2 ans pour préparer le BTS support à l'action managériale.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_32",
      nom: "ingénieur opticien / ingénieure opticienne",
      descriptif:
        "<p>Fibres optiques, miroirs de télescope, laser médical, DVD... Féru de technologies de pointe, l'ingénieur opticien recherche et développe la production des instruments de l'optique instrumentale et de la photonique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_321",
      nom: "technicien / technicienne d'exploitation de l'eau",
      descriptif:
        "<p>Responsable de la qualité et de la quantité d'eau utilisée par son usine, ainsi que de son recyclage, la technicienne ou le technicien d'exploitation de l'eau a un rôle-clé dans les industries utilisant cette ressource lors du processus de fabrication.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_322",
      nom: "miroitier / miroitier",
      descriptif:
        "<p>L'architecture moderne est synonyme de façades en verre. Dans beaucoup de constructions, le miroitier devient donc incontournable. Le métier fait appel à la fois aux techniques de la miroiterie et à celles du bâtiment.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_324",
      nom: "mécanicien/ne de maintenance des matériels agricoles ou d'espaces verts",
      descriptif:
        "<p>Le mécanicien de maintenance des matériels agricoles ou d'espaces verts effectue l'entretien périodique des matériels qui lui sont confiés. Il assure les réglages et repère les anomalies qui pourront faire l'objet d'une intervention plus poussée.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_325",
      nom: "ingénieur / ingénieure nucléaire",
      descriptif:
        "<p>Concevoir les centrales nucléaires de dernière génération, réaliser des études et des calculs pour la sûreté nucléaire, participer à des opérations de démantèlement... le métier d'ingénieur nucléaire intéresse différents profils, du neutronicien au chimiste.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_326",
      nom: "ingénieur / ingénieure en automatismes",
      descriptif:
        "<p>L'ingénieur en automatismes est le maître d'oeuvre de l'automatisation des usines, des entrepôts, des centres de tri, etc. Il conçoit et met en place des systèmes automatisés complexes : robots, véhicules à guidage automatique, machine à commande numérique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_327",
      nom: "audiodescripteur / audiodescriptrice",
      descriptif:
        "<p>L'audiodescriptrice ou l'audiodescripteur permet aux personnes non-voyantes ou malvoyantes de suivre un film, une pièce de théâtre, une expo, une rencontre sportive, etc. en leur racontant ce qui se passe. Un métier essentiel et complexe.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_328",
      nom: "consultant/e SaaS",
      descriptif:
        "<p>Le consultant SaaS (Software as a Service) conseille et accompagne ses clients dans le transfert de leurs données informatiques vers des serveurs externes à l'entreprise (cloud). Ses conseils portent sur l'aspect technique et organisationnel.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_329",
      nom: "expert / experte en assurances",
      descriptif:
        "<p>En cas de vol, d'incendie, de dégât des eaux, d'accident de la route... l'expert en assurances intervient pour évaluer le coût du préjudice subi et permettre l'indemnisation des personnes ou des entreprises assurées.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_33",
      nom: "avocat / avocate",
      descriptif:
        "<p>Défendre les personnes et les entreprises engagées dans un procès, telle est la principale mission de l'avocat. Il joue aussi un rôle de conseiller pour régler les conflits avant qu'ils ne soient portés sur la scène judiciaire.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_331",
      nom: "correcteur / correctrice",
      descriptif:
        "<p>L'esprit en alerte, l'oeil acéré, le flair aiguisé, le correcteur traque les fautes de toute nature : de la bête « coquille » (erreur de frappe) au subtil contresens en passant par la classique faute d'accord. Pour que la lecture reste un plaisir.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_332",
      nom: "chirurgien/ne",
      descriptif:
        "<p>Des tumeurs aux fractures ouvertes en passant par les hémorragies, le chirurgien répare le corps humain et sauve des vies. Il allie compétences intellectuelles et grande dextérité manuelle, avec une bonne dose d'énergie.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_333",
      nom: "juriste en propriété intellectuelle",
      descriptif:
        "<p>Contrats et partenariats industriels, brevets, droits d'auteur... Les entreprises font appel au juriste en propriété intellectuelle pour garantir la protection des innovations industrielles et des créations artistiques.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_334",
      nom: "rédacteur/trice on line",
      descriptif:
        "<p>Le rédacteur on line produit des contenus pour Internet. Son écriture concise et directe lui permet d'« accrocher » le lecteur dès les premiers mots.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_335",
      nom: "pédiatre",
      descriptif:
        "<p>Concilier médecine et travail auprès des enfants, c'est possible en devenant pédiatre. Ce médecin des bébés et des enfants accompagne leur croissance et les soigne, en lien avec les parents.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_336",
      nom: "gardien / gardienne de la paix",
      descriptif:
        "<p>Agents de la police nationale, les gardiens de la paix effectuent des missions de contrôle et de surveillance. En fonction de leur affectation, ils interviennent dans les commissariats, les rues, sur les routes ou lors de manifestations.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_337",
      nom: "géologue minier / minière",
      descriptif:
        "<p>Le géologue minier ou la géologue minière assure la bonne conduite de la politique de production d'une mine et du renouvellement des ressources. Il ou elle travaille sur le terrain en amont et pendant la production, mais aussi devant son ordinateur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_338",
      nom: "consultant/e web analytique",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_34",
      nom: "mouleur-noyauteur / mouleuse-noyauteuse",
      descriptif:
        "<p>Le mouleur-noyauteur assure la fabrication de moules qui seront remplis de métal en fusion pour donner forme à des pignons de boîte de vitesses, à du matériel agricole ou ferroviaire... et à bien des objets de notre environnement quotidien.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_340",
      nom: "chargé/e de veille législative et réglementaire",
      descriptif:
        "<p>À la fois juriste et scientifique, le chargé de veille législative et réglementaire suit l'évolution de la loi et des réglementations concernant les médicaments et les activités de santé, en France comme à l'international.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_341",
      nom: "conseiller / conseillère en génétique",
      descriptif:
        "<p>En collaboration avec le médecin généticien, le conseiller en génétique intervient auprès du patient pour lui expliquer la maladie génétique dont il souffre et lui présenter les risques éventuels de transmission aux membres de sa famille.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_342",
      nom: "conseiller / conseillère en insertion sociale et professionnelle",
      descriptif:
        "<p>La conseillère ou le conseiller en insertion sociale et professionnelle est le contact privilégié des 16-25 ans confrontés à des problèmes d'insertion, de formation, d'emploi ou de vie quotidienne. Elle ou il leur apporte une réponse personnalisée.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_343",
      nom: "infirmier/ère humanitaire",
      descriptif:
        "<p>Organiser les soins, mener une campagne de vaccinations, gérer un centre de nutrition ou former des soignants locaux... l'infirmier humanitaire assume différentes fonctions selon le programme auquel il participe.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_344",
      nom: "médecin humanitaire",
      descriptif:
        "<p>Généraliste ou spécialiste, le médecin humanitaire a une triple mission : soigner des populations dans un contexte de crise, mettre en place des programmes de prévention et de développement des soins, former des équipes médicales sur place.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_345",
      nom: "moniteur/trice d'activités équestres",
      descriptif:
        "<p>Le moniteur d'activités équestres enseigne l'équitation aux débutants comme aux cavaliers plus aguerris. Aussi à l'aise avec les chevaux qu'avec les humains, le moniteur mêle pédagogie et passion équine avec beaucoup de rigueur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_346",
      nom: "salesman",
      descriptif:
        "<p>Le salesman est le technico-commercial des salles de marché. Il conseille les clients sur des investissements boursiers en tenant compte des analyses de sa banque et des fluctuations du marché.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_347",
      nom: "agent / agente de sécurité",
      descriptif:
        "<p>L'agente ou l'agent de sécurité fait appliquer le règlement du site dont elle ou il assure la surveillance. À l'entrée de bâtiments, lors de rondes de nuit ou de vérifications, ces professionnels assurent la protection des biens et des personnes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_348",
      nom: "chargé / chargée d'affaires en génie mécanique",
      descriptif:
        "<p>Le chargé d'affaires en génie mécanique commercialise des produits de l'industrie, qu'il s'agisse d'une machine-outil ou de produits beaucoup plus complexes. Il intervient depuis la prospection de clients jusqu'au service après-vente.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_349",
      nom: "ingénieur / ingénieure hydroécologue",
      descriptif:
        "<p>L'ingénieur ou l'ingénieure hydroécologue utilise des plantes aquatiques (comme des roseaux, des nénuphars...) pour nettoyer naturellement les eaux usées ou polluées. Il ou elle crée des systèmes de traitement des eaux 100 % écologiques !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_35",
      nom: "technicien / technicienne des industries du verre",
      descriptif:
        "<p>Bouteille, pare-brise de voiture, miroir... grâce au technicien des industries du verre, une multitude d'objets voient le jour. De l'atelier de fabrication au service clientèle, il intervient à tous les stades de la production du verre.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_350",
      nom: "chef / cheffe de station de traitement des eaux",
      descriptif:
        "<p>À la tête d'une station d'épuration, la cheffe ou le chef de station de traitement des eaux organise le travail des équipes techniques pour assurer la dépollution des eaux usées, avant leur rejet final dans la nature.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_351",
      nom: "ingénieur / ingénieure métallurgiste",
      descriptif:
        "<p>L'ingénieur métallurgiste apporte son expertise dans le choix des matériaux ou des alliages en liaison avec la production. Au sein du service recherche et développement, il programme des études pour répondre à des besoins techniques précis.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_352",
      nom: "yield manager",
      descriptif:
        "<p>Responsable en partie de la politique tarifaire des transports ou de l'hôtellerie, le yield manager ajuste en permanence les prix des chambres, billets d'avion ou de train. Son but : optimiser les revenus de son employeur en proposant le juste prix.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_353",
      nom: "responsable e-CRM",
      descriptif:
        "<p>Pour augmenter les ventes, le responsable e-CRM traque les habitudes et les comportements des internautes pour leur faire des offres sur mesure. Ce rôle stratégique, entre l'informatique web et le marketing, gagne tous les secteurs d'activité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_354",
      nom: "traducteur/trice technique",
      descriptif:
        "<p> Les notices de nos appareils quotidiens (appareil photo, frigo, ordinateur, lave-vaisselle...) sont généralement disponibles en plusieurs langues. C'est le traducteur technique qui rédige le texte en français depuis la langue étrangère d'origine. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_355",
      nom: "responsable de laboratoire de contrôle en chimie",
      descriptif:
        "<p> Dernière étape avant la mise sur le marché d'un produit issu de l'industrie de la chimie : son contrôle. Sous la direction du responsable de laboratoire de contrôle, des tests sont effectués pour évaluer sa qualité et sa conformité aux normes. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_356",
      nom: "ingénieur / ingénieure matériaux",
      descriptif:
        "<p>L'ingénieur matériaux intervient de la conception à l'utilisation des matériaux. À la pointe de l'innovation, cet expert met ses compétences au service d'un bureau d'études, d'une entreprise industrielle ou d'un organisme de recherche.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_357",
      nom: "électromécanicien/ne en remontées mécaniques",
      descriptif:
        "<p>Télésièges, télécabines, téléphériques... l'électromécanicien en remontées mécaniques intervient sur les éléments électriques et mécaniques des remontées mécaniques. Plutôt prévenir que guérir, c'est sa devise.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_358",
      nom: "pilote d'hélicoptère",
      descriptif:
        "<p>Le pilote d'hélicoptère, militaire ou civil, participe à des missions de combat ou de secours. Il est capable de se poser n'importe où et d'évoluer en montagne. Sang-froid et ténacité sont nécessaires pour exercer ce métier.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_359",
      nom: "régisseur / régisseuse lumière",
      descriptif:
        "<p>Sans lumière, pas de spectacle ! De la bougie au laser, tous les effets sont mis en place et programmés sur console par le régisseur lumière. Ce professionnel dirige la préparation technique du matériel et assure l'éclairage lors du spectacle.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_36",
      nom: "technicien / technicienne chimiste",
      descriptif:
        "<p>Métallurgie, pharmacie, cosmétique, automobile, agroalimentaire... nombreux sont les secteurs professionnels où le technicien chimiste réalise expériences et analyses, participant ainsi à l'élaboration de nouvelles molécules, composants ou produits.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_361",
      nom: "psychologue de l'Éducation nationale spécialité éducation, développement et apprentissages",
      descriptif:
        "<p>Le psychologue de l'Éducation nationale (psy-EN) spécialité éducation, développement et apprentissages (Eda) intervient dans les écoles maternelles et primaires. Il veille à la socialisation et aux apprentissages des élèves ou intervient en situation de crise.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_362",
      nom: "responsable d'élevage agricole",
      descriptif:
        "<p>Vaches, chèvres, moutons, porcs, volailles, escargots, autruches... la personne responsable d'élevage s'occupe de ces animaux à des fins commerciales. Elle les nourrit, les soigne et contrôle leur reproduction, avant de les vendre.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_366",
      nom: "physicien médical / physicienne médicale",
      descriptif:
        "<p>Le physicien médical est un scientifique de haut niveau qui garantit la sécurité des patients et du personnel soignant lors d'examens utilisant les techniques de rayonnement. Il contrôle et programme les machines, prépare les dosages, etc. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_367",
      nom: "chef / cheffe de projet sites et sols pollués",
      descriptif:
        "<p>La cheffe ou le chef de projet sites et sols pollués réhabilite les terrains souillés par une activité minière ou industrielle. Il ou elle traite les déchets enfouis, les liquides déversés... afin qu'ils ne présentent plus de risques pour l'environnement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_368",
      nom: "ingénieur/e environnement et risques industriels",
      descriptif:
        "<p>Qui dit industrie chimique dit risques écologiques. L'ingénieure ou l'ingénieur environnement et risques industriels cherche à neutraliser les possibilités d'accident et veille à ce que l'activité d'une usine respecte les normes écologiques en vigueur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_369",
      nom: "pédologue",
      descriptif:
        "<p>Spécialiste des sols, le ou la pédologue intervient avant tout aménagement urbain ou agricole, afin d'établir un diagnostic et d'organiser fouilles et prélèvements, pour le compte d'un organisme de recherche, une collectivité ou un bureau d'études.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_37",
      nom: "conducteur / conductrice de machines à papier",
      descriptif:
        "<p>Le conducteur de machines à papier veille au bon déroulement de la fabrication industrielle du papier, sous forme de bobines, qui deviendront des feuilles pour écrire, des mouchoirs, des lingettes, du papier essuie-tout, des masques chirurgicaux...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_370",
      nom: "ingénieur / ingénieure essais",
      descriptif:
        "<p>Entre les fonctions recherche-études et fabrication, l'ingénieur essais a la responsabilité d'un programme de tests, depuis sa conception jusqu'à la mise au point du produit. Son rôle est déterminant avant la mise en production en grande série.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_371",
      nom: "aérodynamicien / aérodynamicienne en aéronautique",
      descriptif:
        "<p>L'aérodynamicien conçoit, développe et améliore les profils des engins propulsés dans l'espace aérien. De l'avion, civil ou militaire, en passant par les hélicoptères, les navettes spatiales ou les satellites, son objectif est d'accroître leurs performances en dépensant moins d'énergie. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_372",
      nom: "écotoxicologue",
      descriptif:
        "<p>L'écotoxicologue étudie l'impact des polluants sur les écosystèmes, à partir d'échantillons récoltés sur le terrain ou avant la mise sur le marché d'un nouveau produit. Scientifique, il ou elle travaille pour un organisme public ou pour l'industrie.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_373",
      nom: "infirmier/ère de bloc opératoire",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_374",
      nom: "architecte du patrimoine",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_375",
      nom: "masseur-kinésithérapeute (sport)",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_38",
      nom: "infirmier / infirmière",
      descriptif:
        "<p>L'infirmier dispense des soins de nature préventive, curative ou palliative pour promouvoir, maintenir et restaurer la santé des patients. Ce métier à haute responsabilité exige rigueur, vigilance et technicité. À l'hôpital, en entreprise ou en libéral, toutes les formules offrent d'excellents débouchés professionnels.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_381",
      nom: "directeur/trice de collection",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_384",
      nom: "technicien / technicienne packaging",
      descriptif:
        "<p>Le technicien packaging participe à la création ou à la modification des emballages. Référent technique, il effectue des tests et suit l'évolution du projet, en relation avec différents services (production, qualité, recherche et développement, méthodes, marketing, logistique) et différents fournisseurs (cartonniers, verriers, plasturgistes...).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_385",
      nom: "journaliste sportive",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_388",
      nom: "technicien/ne essais sol sur aéronef",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_389",
      nom: "designer d'espaces publics",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_39",
      nom: "coiffeur / coiffeuse",
      descriptif:
        "<p>Balayage, permanente, coupe au carré, brushing... Aussi habile à manier les ciseaux, le pinceau, la tondeuse, la brosse... le coiffeur se plie au moindre désir de ses clients. Son savoir-faire se double d'un certain sens de l'esthétique et de qualités relationnelles.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_390",
      nom: "technicien / technicienne frigoriste",
      descriptif:
        "<p>Spécialiste de l'installation d'équipements frigorifiques, de la mise en service et de la maintenance ou du dépannage d'équipements de production de froid, le technicien ou la technicienne frigoriste intervient dans de nombreux secteurs où le froid est indispensable.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_392",
      nom: "ergonome du sport",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_394",
      nom: "sculpteur / sculptrice sur bois",
      descriptif:
        "<p>Le sculpteur sur bois donne vie à un morceau de bois. Armé de ses outils, suivant un dessin, cet artisan d'art crée ou décore des meubles, des statuettes, des objets utilitaires ou artistiques. Il est le dépositaire d'un savoir-faire ancestral.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_395",
      nom: "antiquaire",
      descriptif:
        "<p>Plus qu'un vendeur d'objets anciens, l'antiquaire est un véritable connaisseur en histoire de l'art. Pour trouver les plus belles pièces, il prospecte dans les salles des ventes, les expositions, les brocantes... et chine même chez les particuliers.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_396",
      nom: "administrateur / administratrice de biens",
      descriptif:
        "<p>De la location aux travaux de rénovation, l'administrateur de biens, spécialiste du droit immobilier, gère des logements, des bureaux ou encore des locaux commerciaux, pour le compte de particuliers ou de sociétés. Il peut aussi s'occuper de copropriétés en tant que syndic.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_397",
      nom: "analyste financier / financière",
      descriptif:
        "<p>Spécialiste du placement en Bourse, l'analyste financier a la cote. Son rôle : aider les investisseurs à choisir les valeurs les moins risquées et les plus rentables grâce à des études approfondies.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_398",
      nom: "agent / agente de transit",
      descriptif:
        "<p>Comment expédier à travers le monde des marchandises et s'assurer qu'elles arrivent à bon port ? En les plaçant sous le contrôle d'un agent de transit. Il négocie les meilleures conditions de transport et respecte les lois en vigueur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_399",
      nom: "consignataire de navire",
      descriptif:
        "<p>Organiser l'escale des navires au port et le transport des marchandises, c'est la mission commerciale, administrative et technique confiée au consignataire de navire, poste clé du transport maritime.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_4",
      nom: "sommelier / sommelière",
      descriptif:
        "<p>Expert dans l'art de servir et de déguster le vin, le sommelier a le palais fin, l'odorat subtil et le don de faire partager ses coups de coeur aux clients du restaurant dans lequel il travaille. Il navigue entre la cave qu'il gère et le restaurant.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_40",
      nom: "gouvernante",
      descriptif:
        "<p>De la poussière sur un meuble à l'ampoule grillée, aucun détail n'échappe au regard expérimenté du gouvernant. Chargé de superviser et de gérer une équipe de femmes et valets de chambre, il est un personnage clé dans les étages d'un hôtel. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_400",
      nom: "pilote de ligne",
      descriptif:
        "<p>Aujourd'hui Tokyo, demain New York, Dakar la semaine prochaine... Le métier de pilote de ligne fascine. Mais ce professionnel du ciel doit garder les pieds sur terre : il est responsable de la vie de centaines de passagers.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_401",
      nom: "ingénieur / ingénieure en mécanique",
      descriptif:
        "<p>Sans ingénieur en mécanique, adieu satellites, robots, turbines, moteurs, boîtes de vitesses, trains d'atterrissage... Exploitant les technologies de pointe, il crée de nouveaux produits, organise leur fabrication et améliore les moyens de production.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_402",
      nom: "microtechnicien / microtechnicienne",
      descriptif:
        "<p>Horlogerie, appareils photo, instruments de mesure... L'univers des microtechniques inclut tous les appareils miniaturisés utilisant plusieurs technologies : micromécanique, microélectronique, optique, électrotechnique... Selon son niveau de qualification, le microtechnicien occupe un poste de concepteur ou d'opérateur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_403",
      nom: "technicien électrotechnicien / technicienne électrotechnicienne",
      descriptif:
        "<p>Le technicien électrotechnicien est le spécialiste des applications de l'électricité : il conçoit, analyse, installe et s'occupe de la maintenance des équipements électriques domestiques ou industriels (automates programmables des usines) ou de bureau.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_404",
      nom: "électronicien / électronicienne automobile",
      descriptif:
        "<p>Contrôle de trajectoire ou de vitesse, dispositifs d'aide à la conduite, système de navigation... les applications de l'électronique embarquée à bord des véhicules ne cessent de se perfectionner. Dans ce contexte liées aux nouvelles technologies, le rôle de l'électronicien automobile, technicien high-tech, monte en puissance.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_405",
      nom: "agenceur/euse de cuisines et salles de bains",
      descriptif:
        "<p>A la fois artiste et technicien, l'agenceur de cuisines et salles de bains conçoit et réalise des espaces personnalisés, esthétiques et fonctionnels, tout en répondant aux contraintes techniques et budgétaires.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_406",
      nom: "opérateur / opératrice prépresse",
      descriptif:
        "<p>L'oeil rivé à son écran, l'opérateur prépresse, as des logiciels de PAO (publication assistée par ordinateur), met en pages texte et images, avant l'impression des livres, journaux, affiches, plaquettes, dépliants...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_407",
      nom: "urbaniste",
      descriptif:
        "<p>Spécialiste de la ville, l'urbaniste aménage de nouveaux quartiers et réhabilite ceux qui n'offrent pas de bonnes conditions de vie à leurs habitants. Au service des élus, son action s'inscrit toujours dans les politiques publiques de la ville.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_408",
      nom: "géomètre-topographe",
      descriptif:
        "<p>Arpenteur de terrains, le géomètre-topographe donne la mesure du chantier. Missions en extérieur et travail sur plans et sur ordinateur : le métier convient à ceux qui aiment l'action autant que la réflexion.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_409",
      nom: "agent général / agente générale d'assurances",
      descriptif:
        "<p>Indépendant ou en agence, l'agent général d'assurances distribue à une clientèle de particuliers ou de professionnels les produits d'une compagnie d'assurances. Un métier réservé à ceux qui aiment entreprendre et n'ont pas peur de relever des défis.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_41",
      nom: "accompagnateur / accompagnatrice de voyages",
      descriptif:
        "<p>L'accompagnateur de voyages est l'un des rares professionnels du tourisme qui voit du pays. Il accompagne les touristes lors d'un circuit ou d'une excursion, en France ou à l'étranger, en leur garantissant un séjour confortable, plaisant et sans risques.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_410",
      nom: "contrôleur / contrôleuse de gestion",
      descriptif:
        "<p>Qu'il travaille dans une usine, un service commercial ou une entreprise, le contrôleur de gestion n'a qu'un objectif : rechercher la performance. De la mise en place de tableaux de bord au suivi du budget, il dispose de nombreux outils de pilotage.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_411",
      nom: "prothésiste dentaire",
      descriptif:
        "<p>Le prothésiste dentaire fabrique des prothèses (couronnes, bagues, appareils dentaires). À partir des empreintes prises par le dentiste, il façonne des moules et utilise la céramique, des matériaux composites et des métaux précieux.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_412",
      nom: "pharmacien industriel / pharmacienne industrielle",
      descriptif:
        "<p>Garants de la qualité et de la sécurité du médicament, les pharmaciens industriels sont présents à toutes les étapes du processus industriel, de la découverte de la molécule jusqu'à la commercialisation du produit. Un processus qui s'étale sur 10 ans en moyenne...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_413",
      nom: "océanologue",
      descriptif:
        "<p>Scientifique de haut niveau, à la croisée de plusieurs disciplines, l'océanologue effectue des recherches afin de mieux connaître les fonctionnements particuliers des océans et d'évaluer leurs ressources.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_414",
      nom: "ethnologue",
      descriptif:
        "<p>Professionnel de terrain, l'ethnologue s'intéresse à des sujets extrêmement divers : populations lointaines, institutions, banlieues, pratiques sportives... Il met au point une méthode de travail précise avant de réaliser sa propre enquête et de livrer ses conclusions.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_416",
      nom: "manipulateur / manipulatrice en électroradiologie médicale",
      descriptif:
        "<p>Le manipulateur en électroradiologie effectue des examens d'imagerie médicale. Spécialiste des scanners, des radios et des échographies, il participe aux diagnostics et traite, grâce à la radiothérapie, des maladies comme le cancer.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_417",
      nom: "ambulancier / ambulancière",
      descriptif:
        "<p>Auxiliaire de soins, l'ambulancier assure le transport des blessés et des malades au moyen d'un véhicule adapté. Il est également chargé de tâches annexes : tenue de documents de bord, entretien du véhicule, etc.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_418",
      nom: "visiteur médical / visiteuse médicale",
      descriptif:
        "<p>Représentant de l'industrie pharmaceutique, le visiteur médical fait le lien avec les professionnels de santé amenés à prescrire des médicaments. Sa mission : informer ces derniers sur les produits dont il assure la promotion.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_419",
      nom: "médecin spécialiste",
      descriptif:
        "<p>Le médecin spécialiste examine les malades, établit un diagnostic et met en place un traitement adapté. De la dermatologie à la pédiatrie, la profession compte une trentaine de disciplines. Actuellement, la France manque d'anesthésistes-réanimateurs, de pédiatres, de gynécologues-obstétriciens et de psychiatres.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_42",
      nom: "danseur / danseuse",
      descriptif:
        "<p>Arabesque, tour de valse, spin... Les pas de danse varient autant que les genres. En danse classique comme en hip-hop, le danseur allie expression artistique et performance physique. Son quotidien : échauffements, auditions et représentations.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_420",
      nom: "cuisinier / cuisinière",
      descriptif:
        "<p>Maître dans sa cuisine, le cuisinier choisit les produits, élabore les recettes et réalise les différents plats prévus au menu. Selon son lieu d'exercice, il travaille seul ou à la tête d'une équipe très hiérarchisée. Tous sont très recherchés. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_421",
      nom: "scripte",
      descriptif:
        "<p>Chronomètre et stylo en main, le scripte note les particularités de chaque séquence filmée. Garant de la cohésion d'un enregistrement, il se souvient de chaque détail et s'assure que les raccords entre les tournages passent inaperçus.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_422",
      nom: "webmestre",
      descriptif:
        "<p>À la fois technicien, graphiste et rédacteur, le webmestre est responsable de la vie d'un site, du développement à l'animation, en passant par la mise en ligne et la veille technologique. Un métier à géométrie variable nécessitant d'avoir plusieurs cordes à son arc.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_423",
      nom: "professeur / professeure dans l'enseignement agricole",
      descriptif:
        "<p>Le professeur dans l'enseignement agricole délivre des savoirs généraux, technologiques et professionnels à des élèves qui se destinent aux métiers de l'agriculture et de la pêche. Théorie et pratique sont toujours complémentaires.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_424",
      nom: "botaniste",
      descriptif:
        "<p>Description et classement font partie des missions du botaniste. Ce professionnel de terrain et de laboratoire, spécialiste de la biologie végétale, étudie la cartographie botanique d'un lieu, la croissance et la reproduction des plantes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_425",
      nom: "cordonnier / cordonnière",
      descriptif:
        "<p>Chaussures, bottes, sacs, vêtements, ceintures, gants, chapeaux en cuir... n'ont aucun secret pour le cordonnier qui les répare pour leur redonner une seconde vie, ou les renforcent, en prévention d'un usage intensif. À la fois artisan et commerçant, le cordonnier sait répondre aux demandes des clients, grâce à son savoir-faire pointu et son sens relationnel.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_426",
      nom: "conducteur opérateur / conductrice opératrice de scierie",
      descriptif:
        "<p>Le conducteur opérateur de scierie est spécialisé dans le débit de bois. Depuis un poste informatisé, il transforme l'arbre en planches ou en poutres de construction. C'est de lui que dépendent la qualité de la production et le rendement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_427",
      nom: "officier marinier / officière marinière",
      descriptif:
        "<p>L'officier marinier et l'officière marinière sont des sous-officiers de la Marine nationale. Au quotidien, ils sont marins, combattants, techniciens ou gestionnaires. Ils ont fait l'École de maistrance de Brest ou sont passés par la voie de l'engagement sous contrat des matelots de la Flotte.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_428",
      nom: "agronome",
      descriptif:
        "<p>Sélection des plantes, des animaux ou des agroéquipements pour les adapter aux besoins de l'agriculture d'aujourd'hui (productivité, qualité, respect de l'environnement)... Les compétences de l'agronome sont avant tout scientifiques.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_429",
      nom: "responsable de scierie",
      descriptif:
        "<p>Le responsable de scierie encadre les équipes d'ouvriers et donne la cadence de la production au quotidien, en fonction des commandes. Gestionnaire avisé, il négocie les achats de bois et conseille les clients de l'entreprise.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_43",
      nom: "professeur/e d'éducation physique et sportive",
      descriptif:
        "<p>Sur un terrain, dans un gymnase ou sur une piste d'athlétisme, le professeur d'EPS (éducation physique et sportive) a pour mission d'initier les collégiens ou les lycéens à la théorie et à la pratique de plusieurs disciplines sportives.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_430",
      nom: "mécanicien / mécanicienne en engins de travaux publics",
      descriptif:
        "<p>Le mécanicien ou la mécanicienne en engins de travaux publics entretient, prépare et dépanne les engins et machines utilisés sur les chantiers : pelles hydrauliques, bouteurs, niveleuses, chargeuses... Spécialiste ou polyvalent, sédentaire ou itinérant, ce métier offre de bons débouchés.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_431",
      nom: "professeur / professeure de lycée professionnel",
      descriptif:
        "<p>Le professeur de lycée professionnel forme avant tout des jeunes à un métier ou une famille de métiers. Son quotidien est à la fois ancré dans une pratique professionnelle et tourné vers l'avenir.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_432",
      nom: "acheteur / acheteuse d'espaces publicitaires",
      descriptif:
        "<p>Panneau d'affichage, passage à la radio ou à la télé, encart dans la presse... l'acheteur d'espaces publicitaires doit trouver le meilleur ­emplacement et au meilleur prix pour offrir à l'annonceur la plus grande visibilité pour sa publicité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_433",
      nom: "ingénieur / ingénieure environnement",
      descriptif:
        "<p>Faire respecter les normes environnementales au sein des entreprises ou des administrations, tout en préservant leur production et leur rentabilité, telle est la mission de l'ingénieure ou de l'ingénieur environnement. Grâce à eux, l'écologie entre dans l'entreprise.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_434",
      nom: "directeur / directrice d'office de tourisme",
      descriptif:
        "<p>Le directeur d'office de tourisme participe à la promotion d'une ville, d'une région, etc. Il centralise l'offre touristique de son territoire et développe des produits pour les différents publics, en concertation avec les professionnels et les élus.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_435",
      nom: "carreleur / carreleuse-mosaïste",
      descriptif:
        "<p>C'est l'artiste des sols et des murs. Le carreleur-mosaïste intervient une fois le gros oeuvre terminé, sur les façades, dans les salles de bains, les cuisines et les piscines. Créatif, il sait s'adapter au style de vie et au goût de ses clients.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_436",
      nom: "électromécanicien / électromécanicienne",
      descriptif:
        "<p>Montage, câblage, ajustage... l'électromécanicien intervient sur toutes les machines qui comportent des éléments électriques et mécaniques : compresseurs, robots industriels, moteurs électriques... Un métier de terrain.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_438",
      nom: "chef / cheffe de chantier",
      descriptif:
        "<p>Le chef ou la cheffe de chantier joue un rôle de premier plan dans les entreprises du BTP (bâtiment et travaux publics). Il ou elle dirige les équipes, contrôle, coordonne et planifie les travaux. En résumé, il ou elle assure, au quotidien, l'organisation générale d'un ou plusieurs chantier(s) selon l'importance du projet.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_439",
      nom: "menuisier / menuisière",
      descriptif:
        "<p>Associant savoir-faire traditionnel et connaissance des outils numériques, le menuisier fabrique et pose des fenêtres, volets, portes, placards, parquets... Le plus souvent en bois, mais parfois aussi en aluminium ou en matériaux composites.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_44",
      nom: "bibliothécaire",
      descriptif:
        "<p>Dans une bibliothèque municipale ou à la Bibliothèque nationale de France, le bibliothécaire est le trait d'union entre les ouvrages et les usagers. Accueil du public, conservation du fonds, classement, gestion du prêt... ses tâches sont nombreuses.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_440",
      nom: "parfumeur / parfumeuse",
      descriptif:
        '<p>Chanel, Guerlain, Lancôme... des marques de luxe dans le domaine des parfums et des produits de beauté, dont les fragrances ont été imaginées et créées par la sensibilité olfactive d\'un parfumeur créateur, appelé aussi " nez ".</p>',
      urls: [],
      formations: [],
    },
    {
      id: "MET_441",
      nom: "attaché territorial / attachée territoriale",
      descriptif:
        "<p>L'attaché territorial est un fonctionnaire. Une fois le concours réussi, il peut prétendre à un grand nombre de postes et de métiers au sein des communes, des organismes publics, des départements ou des régions. D'importants recrutements sont prévus.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_442",
      nom: "conseiller / conseillère espace info-énergie",
      descriptif:
        "<p>Cap sur le développement des énergies renouvelables dans la maison avec le conseiller espace info-énergie. À la fois informateur, éducateur et technicien, il apprend au public l'art et la manière de maîtriser l'énergie dans l'habitat.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_443",
      nom: "géothermicien / géothermicienne",
      descriptif:
        "<p>Capter la chaleur terrestre pour chauffer des bâtiments ou produire de l'électricité, tel est l'objectif du géothermicien ou de la géothermicienne. Une appellation regroupant différents spécialistes : géologues, géochimistes, hydrogéologues, ingénieurs forage ou réservoir.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_445",
      nom: "animateur / animatrice du patrimoine",
      descriptif:
        "<p>L'animateur du patrimoine met en valeur les aspects culturels, architecturaux et naturels d'une ville ou d'une région. Expositions, colloques, visites guidées, classes patrimoines... sont ses outils pour sensibiliser les habitants comme les touristes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_446",
      nom: "costumier / costumière",
      descriptif:
        "<p>L'habit fait le moine, du moins sur scène ! Le costumier habille les artistes en fonction de la psychologie des personnages qu'ils incarnent. Ses créations doivent se fondre harmonieusement dans les décors et servir la mise en scène.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_447",
      nom: "bottier / bottière",
      descriptif:
        "<p>Grâce à son esprit créatif, à ses compétences techniques et à son habileté, le bottier confectionne à la main des souliers sur mesure. Objectif : obtenir des chaussures confortables répondant aux envies esthétiques ou aux besoins des clients.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_448",
      nom: "esthéticien-cosméticien / esthéticienne-cosméticienne",
      descriptif:
        "<p>Épilations, gommages, massage du visage, modelage du corps… L'esthéticienne est la spécialiste du soin et de la mise en beauté. Un métier en contact direct avec la clientèle, qui exige amabilité, doigté et aptitudes commerciales.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_449",
      nom: "linguiste",
      descriptif:
        "<p>Observer, décortiquer, comprendre le fonctionnement du langage et des langues selon une approche scientifique, c'est l'objectif de la linguistique. À la croisée d'une multitude de disciplines, le linguiste intervient partout où se trouve le langage.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_45",
      nom: "journaliste",
      descriptif:
        "<p>JRI (journaliste-reporter d'images), web rédacteur, présentateur radio ou TV... Le métier de journaliste recouvre des réalités très diverses. Mais, quel que soit le média (papier ou web), cette profession exigeante reste difficile d'accès.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_450",
      nom: "webdesigner",
      descriptif:
        "<p>À la fois artiste et informaticien, le webdesigner est capable de réaliser une interface web ergonomique et un design adapté au contenu d'un site Internet donné.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_451",
      nom: "conseiller / conseillère en séjour",
      descriptif:
        "<p>Chargé par les offices de tourisme d'informer mais aussi d'animer et de promouvoir leur ville et leur région, le conseiller en séjour aime le relationnel et parle au moins 2 langues. Patient et efficace, il sait concilier conseil et vente.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_452",
      nom: "régisseur / régisseuse de spectacles",
      descriptif:
        "<p>Dans un théâtre, une salle de spectacles, un festival en plein air... Le régisseur de spectacles a l'oeil sur tout. Il prépare l'accueil des artistes, achemine le matériel technique, encadre le personnel... Objectif : que la représentation se déroule sans accroc.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_453",
      nom: "commercial / commerciale export",
      descriptif:
        "<p>Le commercial export parcourt le monde, à la recherche de nouveaux contrats. Sa mission : développer les parts de marché de son entreprise sur une zone géographique précise.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_454",
      nom: "garde à cheval",
      descriptif:
        "<p>Trompeur, le terme de garde à cheval recouvre pas loin d'une dizaine de métiers : garde républicain, garde nature... En ville comme en milieu rural, leur objectif est commun : le maintien de l'ordre.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_455",
      nom: "dessinateur-projeteur / dessinatrice-projeteuse",
      descriptif:
        "<p>Le dessinateur-projeteur ou la dessinatrice-projeteuse réalise les plans d'un ouvrage, en transposant l'avant-projet en dessins, grâce aux outils assistés par ordinateur. Selon sa spécialité, il ou elle intervient sur une partie spécifique (circuits électriques, réseaux de canalisations...).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_456",
      nom: "staffeur/euse-ornemaniste",
      descriptif:
        "<p>Le staffeur-ornemaniste réalise des éléments de décoration en plâtre (staff) ou en matériau imitant la pierre (stuc) pour habiller un intérieur ou restaurer une construction ancienne. Des débouchés intéressants pour des professionnels qualifiés.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_457",
      nom: "consultant / consultante",
      descriptif:
        "<p>Expert et fin stratège, le consultant recherche des solutions pour améliorer le fonctionnement des entreprises, dans des domaines comme l'organisation, la relation client, les ressources humaines, les systèmes d'information, l'environnement, etc.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_458",
      nom: "credit manager",
      descriptif:
        "<p>Un maximum de ventes pour un minimum de risques : telle est la devise du credit manager, chargé de sécuriser les ventes de son entreprise. Il définit les conditions optimales de règlement. Et, si un client ne paie pas, il multiplie les relances.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_459",
      nom: "expert / experte en sécurité informatique",
      descriptif:
        "<p>Étudier la fiabilité du système d'information d'une entreprise et en assurer la sûreté, telle est la mission de l'expert en sécurité informatique. Un défi pour ce spécialiste, à l'heure où les échanges de données se multiplient.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_46",
      nom: "secrétaire de rédaction",
      descriptif:
        "<p>Le secrétaire de rédaction (SR) est un journaliste d'un genre un peu particulier. Si le rédacteur produit la matière première, le SR affine, vérifie, corrige et met en scène l'information. Un poste incontournable de la chaîne de production éditoriale.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_460",
      nom: "ingénieur / ingénieure en métrologie",
      descriptif:
        "<p>Spécialisé dans les techniques de mesure, proche des services qualité et recherche-développement, l'ingénieur en métrologie crée, avec ses équipes de techniciens, de nouveaux logiciels pour améliorer et optimiser la rentabilité des instruments. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_461",
      nom: "statisticien / statisticienne",
      descriptif:
        "<p>Opinions politiques, potentiel commercial d'un produit, pratiques sportives... Sur des sujets variés, le statisticien recueille les avis ou les données chiffrées et en propose une synthèse accessible aux non-initiés. Dans des domaines multiples : industrie, administration, médias...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_462",
      nom: "technicien / technicienne en métrologie",
      descriptif:
        "<p>Selon l'entreprise pour laquelle il travaille, le technicien en métrologie a pour mission de vérifier la bonne mesure des pièces ou le bon réglage des appareils servant à mesurer ces mêmes pièces. Une tâche qui exige minutie et précision.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_463",
      nom: "écrivain / écrivaine",
      descriptif:
        "<p>De sa plume coulent les mots : l'écrivain écrit des fictions ou relate des histoires vraies. Romans, contes, poésies, pièces de théâtre, essais... plusieurs genres littéraires possibles avec une même ambition : être publié !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_464",
      nom: "directeur/trice de golf",
      descriptif:
        "<p>Un golf est une véritable entreprise, installée dans un cadre naturel. Le directeur de golf doit avoir des compétences dans la gestion des affaires, des ressources humaines et des finances, ainsi que dans la communication.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_465",
      nom: "généticien/ne",
      descriptif:
        "<p>Des OGM (organismes génétiquement modifiés) au clonage, en passant par la FIV (fécondation in vitro), toutes ces innovations des dernières décennies sont le fruit du travail du généticien.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_466",
      nom: "nivoculteur / nivocultrice",
      descriptif:
        "<p>De la bonne neige avant tout (surtout pour les adeptes de la poudreuse), et en quantité suffisante pendant toute la saison de ski, c'est ce à quoi doit veiller le nivoculteur...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_468",
      nom: "économètre statisticien / statisticienne",
      descriptif:
        "<p>Scientifique de haut niveau, l'économètre statisticien effectue un traitement mathématique et statistique de données économiques. Son objectif : fournir à ses employeurs des éléments fiables et quantifiés afin qu'ils puissent prendre des décisions. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_469",
      nom: "administrateur / administratrice de spectacle",
      descriptif:
        "<p>Gestion économique, financière, du personnel... rien n'échappe à l'administrateur de spectacle. Il intègre les données administratives et budgétaires aux éléments artistiques afin qu'un spectacle voie le jour.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_470",
      nom: "technicien / technicienne télécoms et réseaux",
      descriptif:
        "<p>Le domaine du technicien télécoms et réseaux : les liaisons (avec ou sans fil) des équipements téléphoniques et informatiques. Que la transmission soit opérée par câble, fibre optique, satellite ou voie hertzienne. 3 grands axes d'activité pour ce professionnel : l'installation, la maintenance et le conseil.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_471",
      nom: "entraîneur / entraîneuse de chevaux",
      descriptif:
        "<p>À l'image d'un Guy Forget pour le tennis ou d'un Laurent Blanc pour le football, l'entraîneur est un véritable coach pour les chevaux que les propriétaires lui confient. Une activité qui comprend aussi une part de management et de travail administratif.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_472",
      nom: "juge des enfants",
      descriptif:
        "<p>Retirer un enfant à sa famille, envoyer un adolescent en prison... Le juge des enfants assume de lourdes responsabilités. Dans cette fonction, écoute et force de caractère sont indispensables.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_473",
      nom: "chargé / chargée de projet événementiel",
      descriptif:
        "<p>Congrès, soirées de gala, festivals, salons professionnels, rencontres sportives, petits déjeuners d'entreprise, séminaires ou lancement du nouveau smartphone… c'est le chargé de projet événementiel qui conçoit et réalise ces opérations de communication pour son entreprise ou pour un client.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_474",
      nom: "ingénieur/e électronicien/ne des systèmes de la sécurité aérienne (IESSA)",
      descriptif:
        "<p>L'ingénieur électronicien des systèmes de la sécurité aérienne (Iessa) installe, contrôle, assure la maintenance et développe les équipements aéronautiques liés à la sécurité aérienne. Ses responsabilités : la sécurité des passagers et celle des équipages.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_475",
      nom: "géochimiste",
      descriptif:
        "<p>Grâce à l'étude des composantes de l'écorce terrestre ou de l'eau, le ou la géochimiste tente d'apporter des éléments de réponse aux changements climatiques ou à la gestion des ressources et des risques naturels.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_476",
      nom: "<span>Réalisateur / réalisatrice de films</span>",
      descriptif:
        "À la tête d'une équipe de tournage, le réalisateur ou la réalisatrice de films transmet ses directives aux techniciens et aux acteurs pour façonner son œuvre cinématographique. Responsable de la création du film, il ou elle en dirige les étapes, de la pré-production à la post-production.&nbsp;<br/><br/><h3>Après le bac</h3>\n<p>5 à 6 ans pour préparer un diplôme d'école de cinéma (la Fémis, Louis-Lumière) ou d'école de réalisation audiovisuelle (Esra ou autre).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_477",
      nom: "<span>écrivain / écrivaine public</span>",
      descriptif:
        "Courriers administratifs, CV, mémoires, biographies ou encore lettres d'amour... autant d'exemples de travaux pris en charge par les écrivains publics. Professionnels de l'écrit au service des autres, ils s'adaptent à toutes les demandes pour proposer des textes personnalisés.<br/><br/><h3>Après le bac</h3>\n<p>1 an (après un bac + 2) pour obtenir une licence professionnelle mention intervention sociale : accompagnement social parcours conseil en écriture professionnelle et privée, écrivain public de l'université Sorbonne Nouvelle. Le Cned (Centre national d'enseignement à distance) propose également une formation d'écrivain public.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_478",
      nom: "architecte des systèmes d'information",
      descriptif:
        "<p>Indispensable au bon fonctionnement de l'entreprise, l'architecte des systèmes d'information conçoit et organise l'ensemble des matériels et logiciels nécessaires à la bonne circulation des données. Il les fait évoluer au gré des avancées techniques.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_479",
      nom: "assistant maternel / assistante maternelle",
      descriptif:
        "<p>L'assistant maternel garde des enfants (dont les parents travaillent) à son domicile. Disponibilité et responsabilité sont de mise pour veiller à chaque instant au bien-être et à la sécurité des bambins qui lui sont confiés.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_48",
      nom: "zoologiste",
      descriptif:
        "<p>Sur le terrain ou dans son laboratoire, le zoologiste étudie les animaux et leur mode de vie. Face à l'immense diversité des espèces, il s'intéresse à une catégorie d'animaux en particulier, ou se spécialise dans une discipline.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_480",
      nom: "ingénieur/e textile",
      descriptif:
        "<p>L'ingénieur textile peut travailler dans presque tous les secteurs, tant le textile et les matériaux souples sont partout. Tissu intelligent, matériaux composites high-tech pour l'aéronautique ou les travaux publics font partie de ses champs d'étude.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_481",
      nom: "entraîneur/euse sportif/ve",
      descriptif:
        "<p>Lucide et exigeant, l'entraîneur sportif n'a qu'un objectif : amener un athlète ou une équipe à son meilleur niveau. Qu'il travaille dans un club local ou à un niveau plus élevé, ce technicien du sport est aussi un fin pédagogue, proche des joueurs.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_482",
      nom: "expert immobilier / experte immobilier",
      descriptif:
        "<p>L'expert immobilier définit le plus justement possible la valeur d'un bien appartenant à un particulier, dans la perspective d'une vente ou d'une location. Il peut aussi expertiser un terrain, des locaux industriels ou commerciaux...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_483",
      nom: "souscripteur / souscriptrice",
      descriptif:
        "<p>Le souscripteur décide si sa compagnie d'assurance assurera ou non les risques liés à une activité professionnelle. Récolte détruite, entrepôt en feu, avion qui s'écrase... A lui de voir si le dommage peut être couvert, et à quelles conditions.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_484",
      nom: "convoyeur/euse de fonds",
      descriptif:
        "<p>La convoyeuse ou le convoyeur de fonds assure le transport et la sécurité de valeurs (billets de banque ou monnaies, bijoux, titres de paiement, métaux précieux...) qui lui sont confiées par des banques, des commerces ou des administrations.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_485",
      nom: "chargé / chargée de recherche et développement déchets",
      descriptif:
        "<p>Transformer nos déchets en ressources, tel est l'objectif principal de la chargée ou du chargé de recherche et développement déchets. Ces spécialistes de haut niveau cherchent des techniques nouvelles ou des améliorations pour aller plus loin.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_486",
      nom: "responsable de plate-forme biotechnologique",
      descriptif:
        "<p> La recherche dans les biotechnologies nécessite le développement d'outils de plus en plus puissants, chers et complexes. Ces matériels, souvent rassemblés sur un même lieu, sont gérés par le responsable de plate-forme biotechnologique. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_487",
      nom: "responsable biométrie",
      descriptif:
        "<p>Dans l'industrie pharmaceutique, le responsable biométrie organise et supervise le traitement des données des études cliniques. Il dirige un service composé de data managers et de biostatisticiens, et valide les résultats obtenus.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_488",
      nom: "ingénieur / ingénieure combustion et brûleurs",
      descriptif:
        "<p>Spécialiste des chaudières industrielles, l'ingénieur combustion et brûleurs conçoit des équipements à la pointe de l'innovation technique, permettant de produire plus d'énergie tout en polluant moins.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_489",
      nom: "traffic manager",
      descriptif:
        "<p>Spécialiste de la publicité et de l'analyse statistique du comportement des internautes, le traffic manager définit la stratégie, supervise la réalisation technique et assure le suivi des campagnes promotionnelles en ligne.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_49",
      nom: "constructeur / constructrice de réseaux de canalisations",
      descriptif:
        "<p>Les constructeurs ou les constructrices de réseaux en canalisations installent, entretiennent et renouvellent toutes les canalisations permettant le transport et la distribution d'eau potable, des fluides de toute nature (gaz, chauffage urbain...) ou de la fibre, l'évacuation des eaux de pluie ou des eaux usées. Un métier qui recrute et qui évolue avec les techniques de pointe.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_490",
      nom: "chef / cheffe de projet web-mobile",
      descriptif:
        "<p>Responsable du développement d'un site Internet ou d'une application mobile, le chef de projet web/mobile est l'interface entre le client et les différents professionnels impliqués dans la réalisation. L'expérience est appréciée pour ce poste.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_491",
      nom: "ingénieur / ingénieure de maintenance industrielle",
      descriptif:
        "<p>Grâce à cet expert en maintenance industrielle, la machine est sous contrôle ! Garant du bon fonctionnement des équipements, l'ingénieur de maintenance industrielle contribue à une surveillance méthodique du matériel avec l'aide du big data et de l'intelligence artificielle pour l'exploitation des données.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_492",
      nom: "ingénieur/e qualité moteur",
      descriptif:
        "<p>L'ingénieur qualité moteur met au point des moteurs pour chaque appareil roulant ou volant. À lui de garantir la qualité de ces moteurs. De la conception à l'industrialisation, il intervient à tous les stades de leur fabrication.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_493",
      nom: "motard/e de la police ou de la gendarmerie",
      descriptif:
        "Chevauchant leur BMW F 750 GS, R 1250 GS, R 1250 RT ou leur Yamaha Tracer 7, les motards de la police ou de la gendarmerie patrouillent sur les routes et les autoroutes pour assurer des missions de sécurité routière. Veillant au respect du code de la route et de la réglementation liée à la sécurité routière, il ou elle verbalise les conducteurs en cas d'infractions.<br/><br/><h3>Après la 3<sup>e</sup></h3>\n<p>Concours de gardien de la paix, accessible avec un bac général, technologique ou professionnel. Concours de sous-officier ou d'officier de gendarmerie.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_497",
      nom: "réflexologue",
      descriptif:
        "<p>En touchant des zones réflexes spécifiques des pieds, des mains ou du crâne, le réflexologue apporte un mieux-être aux personnes qui le consultent. Généralement à son compte, il peut exercer cette activité en complément d'une autre, du domaine paramédical notamment.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_498",
      nom: "chargé / chargée d'études de prix",
      descriptif:
        "<p>Combien coûtera une route, un pont, un stade ? Les chargés d'études de prix aident à répondre à cette question. Ils ou elles évaluent le coût d'un projet pour que les équipes commerciales puissent faire la meilleure proposition au client.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_499",
      nom: "ingénieur/e méthodes mécaniques",
      descriptif:
        "<p>Responsable de l'industrialisation d'un produit, l'ingénieur méthodes mécaniques étudie les caractéristiques techniques de la pièce à fabriquer et détermine les étapes et les machines à utiliser pour optimiser la production.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_5",
      nom: "designer graphique",
      descriptif:
        "<p>Affiches, logos, emballages, sites web... l'empreinte du designer graphique est partout : dans la presse, l'édition en passant par la publicité, la communication et le design... Sa mission : concevoir un visuel, alliant sens artistique aux enjeux commerciaux et marketing, qui saura séduire le public.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_50",
      nom: "ouvrier forestier / ouvrière forestière",
      descriptif:
        "<p>Les récentes tempêtes ont mieux fait connaître le travail des ouvriers forestiers. Sylviculteurs ou bûcherons, ils réalisent au quotidien les travaux d'entretien et d'aménagement des massifs forestiers. Un métier à exercer en plein air... qu'il pleuve, neige ou vente.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_500",
      nom: "ingénieur/e efficacité énergétique du bâtiment",
      descriptif:
        "<p>L'ingénieur efficacité énergétique du bâtiment réalise des études permettant de diminuer la consommation d'énergie ou d'intégrer les énergies renouvelables dans un édifice. Il travaille avec les maîtres d'ouvrage avant ou après la construction.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_501",
      nom: "chargé / chargée de référencement web",
      descriptif:
        "<p>Qu'il travaille en agence, à son compte ou en entreprise, le chargé de référencement web met tout en oeuvre pour que le site de son client se retrouve en tête des recherches sur Internet. Une profession en plein boom.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_502",
      nom: "ingénieur/e procédés en chimie",
      descriptif:
        "<p>La mission de l'ingénieur procédés en chimie ? Concevoir et suivre la mise en oeuvre d'équipements nouveaux, ou adapter l'outil existant, au sein des usines et unités de production, en répondant aux questions de sûreté, d'efficacité et de performance.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_503",
      nom: "développeur/euse d'applications mobiles",
      descriptif:
        "<p>Le développeur d'applications mobiles est chargé de la réalisation technique d'une application, basée sur un cahier des charges précis. Il calcule et conçoit des programmes informatiques pour le traitement des données.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_504",
      nom: "<span>Mannequin</span>",
      descriptif:
        "Faire la couverture de célèbres magazines de mode, défiler pour les grands noms de la haute-couture, participer à des séances photos pour des publicités... le rêve de tous les apprentis mannequins. Hélas, il n'est réservé qu'à une poignée d'entre eux car la concurrence est féroce et les critères de sélection drastiques, même si le milieu s'ouvre un peu plus aux personnes atypiques.<br/><br/><h3>Après la 3<sup>e</sup></h3>\n<p>Aucun diplôme ne forme au métier de mannequin et les petites annonces racoleuses sont légions, avec le risque de se faire abuser. Les agences sérieuses doivent justifier d'une licence accordée par l'État et prennent en charge les tests et la réalisation du book. Elles forment ensuite, gratuitement, les personnes sélectionnées. En France, les plus prestigieuses agences adhèrent au Synam (Syndicat national des agences de mannequins)..</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_505",
      nom: "auditeur/trice vert/e HSE",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_506",
      nom: "technicien réalisateur / technicienne réalisatrice radio",
      descriptif:
        "<p>Le technicien réalisateur radio est le porte-voix de l'animateur de l'émission. Avec lui, il orchestre la diffusion des événements sonores sur l'antenne et veille à la qualité du son.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_507",
      nom: "coloriste (papier-carton)",
      descriptif:
        "<p>Tel un cuisinier, le coloriste mélange plusieurs encres et composants pour obtenir la couleur exacte souhaitée par le client pour ses papiers ou emballages. En fonction du support à teinter, il ajuste sa recette et suit son évolution à l'impression. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_509",
      nom: "agent / agente d'exploitation de l'eau",
      descriptif:
        "<p>L'agente ou l'agent d'exploitation de l'eau veille à la bonne qualité et la bonne distribution de l'eau. Ils vérifient les installations, participent à l'entretien du réseau d'eau, effectuent des contrôles... pour éviter fuites ou contaminations.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_51",
      nom: "professeur / professeure de collège et de lycée",
      descriptif:
        "<p>Le professeur de collège et de lycée est spécialiste d'une discipline (français, maths, histoire...) qu'il enseigne à des classes de 20 à 30 élèves. Pédagogue, il transmet ses connaissances dans le respect des programmes de l'Éducation nationale.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_510",
      nom: "fromager / fromagère",
      descriptif:
        "<p>Passionné par l'univers des fromages, le fromager fait le lien entre les producteurs et les clients sur un marché, au rayon fromage d'un supermarché, dans une coopérative ou en boutique spécialisée. La demande en personnel qualifié est importante.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_511",
      nom: "logisticien/ne de l'humanitaire",
      descriptif:
        "Dans les zones de conflits ou de catastrophes naturelles, les logisticiens de l'humanitaire interviennent pour organiser l'acheminement de personnes, de vivres ou de matériels, pour faciliter la construction d'abris de secours, etc. Leurs tâches varient selon leur mission.<br/><br/><h3>Après le bac</h3>\n<p>Pour travailler dans l'humanitaire, il faut avoir une technicité dans un domaine (mécanique, transport et logistique, génie civil, gestion des stocks, etc.), assortie d'une solide expérience. Bioforce et l'Ifaid sont deux organismes proposant des formations à l'humanitaire.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_512",
      nom: "data scientist",
      descriptif:
        "<p>Grâce à une vision globale des données de son entreprise, le data scientist est capable d'aider sa direction ou les différents services (marketing, qualité, process...) à prendre des décisions grâce au traitement et à l'analyse de données fiables.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_513",
      nom: "<span>Personnel de hall d'hôtel de luxe</span>",
      descriptif:
        "Au sein d'un établissement cinq étoiles, le personnel de hall d'hôtel de luxe délivre un service de très haute qualité. Voituriers, grooms ou concierges... mettent tout en œuvre pour satisfaire une clientèle fortunée et exigeante. Sens de l'accueil et souci de la perfection sont attendus.&nbsp;<br/><br/><h3>Après la 3<sup>e</sup></h3>\n<p>2 ans pour préparer le CAP commercialisation et services en hôtel-café-restaurant&nbsp;; 3&nbsp;ans pour le bac STHR permettant d'accéder aux différents métiers cités&nbsp;; 3 ans également pour obtenir le diplôme de l'Institut de conciergerie international afin d'être concierge d'hôtel.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_514",
      nom: "<span>Mosaïste d'art</span>",
      descriptif:
        "Souvent à son compte, la mosaïste ou le mosaïste d'art excelle dans l'agencement de petites pièces constituées de différentes matières. L'artiste en mosaïque peut effectuer des pièces de mobilier, des sols, des éléments muraux, des tableaux, etc.<br/><br/><h3>Après le bac</h3>\n<p>3 ans pour préparer un DN MADE mention ornement ou mention matériaux (spécialité mosaïque, conception, création et innovation) ou un DNA art. 5 ans pour préparer un DNSEP en école des beaux-arts.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_515",
      nom: "<span>Contrôleur / contrôleuse</span> <br> <span>des Finances publiques</span>",
      descriptif:
        "Après avoir réussi un concours, la contrôleuse ou le contrôleur des Finances publiques peut exercer l'un des nombreux métiers du domaine de la fiscalité, de la gestion publique ou du contrôle, à l'échelon local ou national.<br/><br/><h3>Après la 3<sup>e</sup></h3>\n<p>3 ans pour préparer un bac général, technologique ou professionnel avant de se présenter au concours de la Fonction publique d'État de catégorie B. Dans les faits, beaucoup de candidats ont un niveau plus élevé (BTS, BUT, licence…) dans le domaine de la comptabilité, de la gestion, de l'économie, du droit, etc.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_518",
      nom: "ingénieur en chef territorial / ingénieure en chef territoriale",
      descriptif:
        "<p>L'ingénieur en chef territorial correspond à un statut de la Fonction publique donnant accès à des postes de direction dans une collectivité territoriale (métropole, région, communes...), autour de la maîtrise d'ouvrage publique et de l'ingénierie (aménagement, urbanisme, développement durable, transport, systèmes d'information, services techniques...).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_519",
      nom: "knowledge manager",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_52",
      nom: "restaurateur/trice d'oeuvres d'art",
      descriptif:
        "<p>Des tableaux aux couleurs assombries par le temps, une sculpture ternie par la pollution, une jarre brisée, un meuble endommagé... Le restaurateur d'oeuvres d'art est là pour donner une seconde jeunesse à tous ces objets.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_522",
      nom: "<span>Professeur / professeure de sport</span>",
      descriptif:
        "Fonctionnaire, la professeure ou le professeur de sport est spécialiste d'une discipline sportive. Ces pros peuvent exercer des missions de formation ou de conseil, notamment dans les fédérations sportives, où ils repèrent les champions de demain.<br/><br/><h3>Après le bac</h3>\n<p>3 ans après le bac et réussir le concours externe de professeur de sport, accessible aux titulaires d'une licence ou d'un diplôme classé au niveau 6 en STAPS, du brevet d'état d'éducatif sportif, du DESJEPS performance sportive, ou du diplôme de guide de haute montagne. Des dérogations existent pour les sportifs de haut niveau, pour celles et ceux qui peuvent justifier d'un titre de formation ou d'une expérience professionnelle dans le secteur sportif.&nbsp;</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_523",
      nom: "secrétaire de mairie",
      descriptif:
        "La ou le secrétaire de mairie exerce des tâches très variées au sein d'une mairie, parfois en lien direct avec le maire, le plus souvent avec ses adjoints. En fonction de la taille de la mairie, il ou elle aura plus ou moins de polyvalence et de responsabilités.<br/><br/><h3>Après le bac</h3>\n<p>Concours de rédacteur territorial accessible avec un bac ou d'attaché territorial ouvert aux titulaires d'un diplôme de niveau bac + 3.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_526",
      nom: "designer transports",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_527",
      nom: "serrurier dépanneur / serrurière dépanneuse",
      descriptif:
        "<p>La porte d'un appartement a claqué et la clef est malencontreusement restée à l'intérieur ? Pas de panique ! Le serrurier dépanneur intervient pour permettre à son client d'entrer à nouveau chez lui sans endommager sa porte.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_528",
      nom: "<span>Technicien / technicienne</span> <br> <span>en mécanique</span>",
      descriptif:
        "Dans de nombreux secteurs, le technicien mécanicien est compétent enmécanique et électromécanique sur des machines et des installations. Cependant, comme la mécatronique et l'automatisation enrichissent la mécanique traditionnelle, l’électronique et l’informatique figurent aussi parmi les connaissances requises.<br/><br/><h3>Après le bac</h3>\n<p>2 ans pour préparer le BTS conception des processus de réalisation de produits (option A : production unitaire ou option B : production sérielle) ; 3 ans pour le BUT génie mécanique et productique, ou une licence professionnelle de la spécialité (1 an après un bac + 2).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_530",
      nom: "animateur/trice handisport",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_533",
      nom: "graveur/euse sur métal",
      descriptif:
        "Avec minutie, la graveuse ou le graveur sur métal travaille délicatement une pièce d'orfèvrerie ou d'horlogerie pour y faire apparaître un motif ou un dessin, en creux ou en relief. Sens artistique et maîtrise du dessin sont indispensables.<br/><br/><h3>Après la 3<sup>e</sup></h3>\n<p>2 ans pour préparer le CAP métiers de la gravure, éventuellement complété par le BMA orfèvrerie option gravure-ciselure (2 ans).</p>\n<h3>Après le bac</h3>\n<p>3 ans pour obtenir le DN MADE mention matériaux (parcours création métal).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_534",
      nom: "femme / valet de chambre",
      descriptif:
        "<p>Chaque matin, la femme ou le valet de chambre remet en état les chambres d'un hôtel en suivant un planning précis. Rapide, efficace, discret/ète et poli/e, il/elle contribue largement à l'image de l'établissement qui l'emploie.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_535",
      nom: "garçon / serveuse de café",
      descriptif:
        "<p>Souvent vêtu/e d'une tenue noire et blanche, souriant/e et rapide, le garçon ou la serveuse de café évolue entre les tables, un plateau à la main, pour venir déposer devant le client les boissons ou le plat commandés un instant plus tôt.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_536",
      nom: "maquettiste",
      descriptif:
        "<p>Quel délice de se plonger dans son magazine préféré ! Ce plaisir, on le doit au maquettiste, véritable metteur en scène des textes et des titres, des images et des couleurs. Un créatif qui allie qualités artistiques et maîtrise des logiciels de PAO (publication assistée par ordinateur).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_537",
      nom: "directeur / directrice artistique",
      descriptif:
        "<p>Le directeur artistique (DA) traduit les messages d'un client par l'image. Avec l'aide de son équipe de création, il invente des logos, élabore des maquettes de journaux, conçoit des emballages... Son objectif majeur : le style et la personnalité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_538",
      nom: "plâtrier / plâtrière",
      descriptif:
        "<p>Aménagement, isolation, décoration des espaces intérieurs... le plâtrier intervient du sol au plafond, en passant par les murs et les cloisons, lorsque toutes les réalisations du gros oeuvre ont été effectuées et avant l'intervention des peintres et des menuisiers.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_539",
      nom: "chef / cheffe de produit marketing",
      descriptif:
        "<p>Séduire le consommateur en s'adaptant en permanence à ses goûts et besoins, tout en dopant le chiffre d'affaires de l'entreprise : telle est l'ambition du chef de produit marketing qui prend en charge un article ou une gamme de produits, depuis sa création jusqu'à sa mise en vente.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_54",
      nom: "matelot de la marine marchande",
      descriptif:
        "<p>Charger, décharger, entretenir les machines, surveiller le pont, tenir la barre... autant de tâches dévolues au matelot de la marine marchande. Polyvalent, il exécute les ordres sur un navire transportant des passagers ou des marchandises. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_540",
      nom: "chargé / chargée d'études en marketing",
      descriptif:
        "<p>Avant de lancer un produit ou un nouveau service sur le marché, une entreprise demande au chargé d'études en marketing d'analyser les attentes des clients et l'offre de la concurrence. C'est un préalable nécessaire à tout lancement commercial.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_541",
      nom: "chef / cheffe des ventes",
      descriptif:
        "<p>Le chef des ventes pilote une équipe de commerciaux sur une zone géographique donnée ou sur une ligne de produits. Son objectif : optimiser les ventes. Un métier accessible après quelques années d'expérience.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_542",
      nom: "directeur / directrice de magasin à grande surface",
      descriptif:
        "<p>Responsable d'un point de vente (hypermarché, supermarché...) aux couleurs d'une enseigne, le directeur de grande surface gère et développe l'activité en appliquant la politique commerciale de la chaîne.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_543",
      nom: "responsable du service après-vente",
      descriptif:
        "<p>Quand un client appelle pour une chaîne hi-fi ou un aspirateur en panne, le responsable SAV (service après-vente) s'assure que les réparations sont effectuées dans un délai raisonnable. En un mot, il veille au bon fonctionnement du SAV.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_544",
      nom: "directeur / directrice d'agence bancaire",
      descriptif:
        "<p>Tel le patron d'une petite entreprise, le directeur d'agence bancaire assume de lourdes responsabilités. Motivant ses employés et soignant ses clients, il cherche à limiter les risques financiers et à accroître le chiffre d'affaires. Un métier tout en diplomatie.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_545",
      nom: "déclarant / déclarante en douane",
      descriptif:
        "<p>Sacs de céréales, ballots de vêtements, oeuvres d'art ou animaux ne voyagent pas sans papiers. Certificat de vente, attestation sanitaire, contrat d'assurances... le déclarant en douane veille à ce que tout soit en règle.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_546",
      nom: "déménageur / déménageuse",
      descriptif:
        "<p>Emballer, charger, transporter et réinstaller sans casse : tel est le rôle du déménageur. Dans ce métier accessible à tous et à toutes, la force physique n'est plus un critère sélectif. Le professionnel d'aujourd'hui gère les formalités administratives et a de réelles perspectives d'évolution.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_547",
      nom: "agriculteur / agricultrice",
      descriptif:
        "<p>Passionné de nature, doté d'une solide formation technique et assisté d'outils informatisés, l'agriculteur est un véritable chef d'entreprise, qu'il produise des céréales, des légumes, des fruits, du lait ou de la viande...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_548",
      nom: "technico-commercial / technico-commerciale en agroalimentaire",
      descriptif:
        "<p>Grâce à sa double compétence, le technico-commercial en agroalimentaire connaît aussi bien les techniques de vente que les spécificités des produits qu'il commercialise. Ce professionnel de terrain est très recherché dans les PME (petites et moyennes entreprises) du secteur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_549",
      nom: "conducteur / conductrice de ligne de production alimentaire",
      descriptif:
        "<p>Découpage, cuisson, congélation, conditionnement... Le conducteur de ligne de production alimentaire programme et gère les machines automatisées ainsi que les opérateurs chargés de l'une ou plusieurs de ces actions de transformation d'un produit brut.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_55",
      nom: "étanchéiste",
      descriptif:
        "<p>Des infiltrations d'eau dans les murs, le sol ou le toit, et c'est tout un édifice qui peut s'écrouler. Pour éviter cela, l'étanchéiste pose des revêtements d'imperméabilisation. Un métier exigeant physiquement et très recherché.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_550",
      nom: "technicien / technicienne de forge",
      descriptif:
        "<p>Sans marteau ni enclume, dans des usines automatisées et informatisées de l'aéronautique, de l'automobile, de la robinetterie, du nucléaire... le technicien de forge définit les moyens de fabrication et organise la production en atelier de pièces métalliques.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_551",
      nom: "architecte réseaux",
      descriptif:
        "<p>L'architecte réseaux répond aux besoins en communication des entreprises. Il conçoit, planifie, développe l'organisation générale des réseaux de télécommunications et supervise leur réalisation... à moindre coût.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_552",
      nom: "modéliste",
      descriptif:
        "<p>Le modéliste transcrit techniquement et en 3D le croquis du styliste. De l'ordinateur aux ciseaux, il découpe et ajuste une toile sur un mannequin puis établit le patron pour la mise au point du prototype qui sert de base pour la fabrication en série.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_553",
      nom: "technicien / technicienne de fabrication de mobilier et de menuiserie",
      descriptif:
        "<p>Au coeur de la chaîne de fabrication, le technicien de fabrication de mobilier et de menuiserie participe à la conception et à la production de meubles, de fenêtres, d'escaliers... en conjuguant sens de l'esthétique et compétences techniques.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_554",
      nom: "technicien / technicienne de maintenance en informatique",
      descriptif:
        "<p>Un virus, des fichiers volatilisés, un écran noir, une imprimante bloquée... Tel est le lot quotidien du technicien de maintenance en informatique. Véritable urgentiste, il veille au bon fonctionnement des matériels comme des logiciels.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_555",
      nom: "manager de risques",
      descriptif:
        "<p>Produit défectueux, explosion, accident du travail, ­pollution, virus informatique, perte financière... autant de catastrophes que le manager de risques doit prévoir, faire assurer et, dans la mesure du possible, empêcher.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_556",
      nom: "secrétaire médical/e",
      descriptif:
        "<p>Sa voix rassurante fait le lien entre les patients et les médecins. Prise de rendez-vous, accueil en salle d'attente et comptes rendus des consultations constituent le quotidien du secrétaire médical.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_557",
      nom: "professeur des écoles / professeure des écoles",
      descriptif:
        "<p>Instruire les enfants et commencer à leur donner des méthodes d'acquisition des connaissances : c'est l'affaire du professeur des écoles qui transmet les savoirs de la maternelle au CM2.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_558",
      nom: "notaire",
      descriptif:
        "<p>Contrat de mariage, promesse de vente d'un appartement, succession... Le notaire authentifie des actes d'ordre immobilier et familial. C'est-à-dire qu'il certifie la volonté exprimée par les signataires.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_559",
      nom: "toiletteur / toiletteuse d'animaux",
      descriptif:
        "<p>Entre ses mains, les chiens et les chats retrouvent fière allure. Coiffeur et esthéticien, le toiletteur se fait aussi vendeur pour proposer des produits de soin, des accessoires ou de l'alimentation pour animaux.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_56",
      nom: "électricien installateur / électricienne installatrice",
      descriptif:
        "<p>Soucieux de sécurité, l'électricien installateur réalise les installations électriques des maisons, des immeubles, des usines ou des centres commerciaux. Désormais, il s'occupe aussi de la pose de câbles informatiques, d'équipements de vidéosurveillance et de téléphonie.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_560",
      nom: "verrier / verrière au chalumeau",
      descriptif:
        "<p>Le verrier au chalumeau transforme le verre pour produire différents objets, comme du matériel pour les laboratoires de physique et de chimie, ou des enseignes lumineuses. On le rencontre surtout dans la verrerie technique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_562",
      nom: "agent / agente de propreté et d'hygiène",
      descriptif:
        "<p>L'agente ou l'agent de propreté et d'hygiène effectue l'entretien courant dans les bureaux, les immeubles, les commerces, les usines, les écoles, les trains, les aéroports, etc. Un métier parfois boudé mais qui offre pourtant des débouchés importants.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_563",
      nom: "archiviste",
      descriptif:
        "<p>Manuscrit du Moyen Âge, vidéo d'une émission de télé, acte notarié attestant de l'achat d'une maison... l'archiviste est le gardien de la mémoire. De nouveaux supports informatiques lui permettent désormais de gagner du temps et de la place.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_564",
      nom: "concepteur / conceptrice multimédia",
      descriptif:
        "<p>Ce spécialiste de l'interactivité crée des produits multimédias en mêlant sons, textes et images, en collaboration avec des graphistes, des auteurs, des développeurs et des webdesigners.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_565",
      nom: "officier / officière de la marine nationale",
      descriptif:
        "<p>De la conduite d'un navire à la gestion administrative, l'officière ou l'officier de la Marine nationale occupe des fonctions de direction et de commandement. Il existe des officiers de carrière et des officiers sous contrat.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_566",
      nom: "maroquinier / maroquinière",
      descriptif:
        "<p>Le maroquinier travaille le cuir et les matériaux souples pour la fabrication de sacs, gants, ceintures, portefeuilles... . Entre tradition et modernité, il perpétue un savoir-faire ancestral tout en adaptant ses modèles aux évolutions de la mode.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_567",
      nom: "officier / officière de l'armée de terre",
      descriptif:
        "<p>L'officière ou l'officier de l'armée de terre assume un rôle de commandement. Officiers des armes ou d'état-major, ingénieurs ou gestionnaires, les officiers de l'armée de terre ont, selon leur affectation, entre 15 et 1 000 hommes et femmes sous leurs ordres.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_568",
      nom: "designer industriel / designeuse industrielle",
      descriptif:
        "<p>Concevoir un canapé, relooker une gamme de bagages, imaginer les stands de la Foire de Paris... autant de missions relevant des compétences du designer industriel. Au service de l'entreprise, il crée pour séduire... et pour vendre !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_569",
      nom: "éducateur/trice sportif/ve des activités aquatiques et de la natation",
      descriptif:
        "<p>Surveillance des bassins, cours de natation, bébés nageurs ou aquagym, l'éducateur sportif des activités aquatiques et de la natation, plus connu sous le nom de maître-nageur sauveteur (MNS), fait toujours preuve de vigilance et de sens pédagogique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_57",
      nom: "informaticien industriel / informaticienne industrielle",
      descriptif:
        "<p>Dans l'industrie, l'informatique est omniprésente. Elle permet de piloter robots et automates. De la conception des produits à leur livraison, en passant par leur fabrication, les informaticiens industriels (techniciens et ingénieurs) se révèlent indispensables.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_570",
      nom: "géologue",
      descriptif:
        "<p>Contribuant à l'exploration scientifique de la Terre, le ou la géologue observe, prélève et analyse l'écorce terrestre. Ce ou cette spécialiste des géosciences étudie la composition, la structure, la physique, l'histoire et l'évolution de notre planète et de son sol.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_571",
      nom: "acheteur / acheteuse",
      descriptif:
        "<p>Son rôle : acheter les produits et services dont son entreprise a besoin, en négociant les meilleures conditions de prix, de délais et de service après-vente. Une fonction de plus en plus stratégique dans le contexte économique actuel.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_573",
      nom: "monteur / monteuse en réseaux de distribution électrique",
      descriptif:
        "<p>Au volant d'un véhicule de service, le monteur en réseaux de distribution électrique se déplace en ville ou à la campagne pour assurer à tous les foyers de bénéficier d'électricité. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_574",
      nom: "technicien / technicienne d'essais",
      descriptif:
        "<p>Performance, sécurité, résistance à l'usure, bruit d'un moteur... rien n'échappe à la vigilance du technicien d'essais. Sur un banc d'essais, une piste ou en laboratoire, il occupe un poste clé entre le bureau d'études et la fabrication en série.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_575",
      nom: "ajusteur-monteur / ajusteuse-monteuse",
      descriptif:
        "<p>C'est un peu comme s'il jouait au Lego. Mais l'ajusteur-monteur exerce un vrai métier pour lequel un certain nombre de compétences sont nécessaires. Qu'il assemble les pièces d'un système mécanique ou procède à des finitions, il reste un as du sur-mesure.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_576",
      nom: "chaudronnier / chaudronnière",
      descriptif:
        "<p>Le chaudronnier donne forme aux feuilles de métal, puis il les assemble afin de réaliser les produits les plus variés : cuve d'une usine de chimie, wagon, chaudière d'une centrale nucléaire...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_577",
      nom: "batelier / batelière",
      descriptif:
        "<p>Les bateliers (aussi appelés mariniers) sont des amoureux des fleuves. Tour à tour techniciens, commerçants et gestionnaires, ils transportent des passagers ou des marchandises. Un métier entre tradition et modernité. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_578",
      nom: "adjoint administratif / adjointe administrative",
      descriptif:
        "<p>Accueil et information du public, activités de secrétariat et de comptabilité dans le domaine social, scolaire ou des finances : les missions de l'adjoint administratif sont aussi variées que les endroits où exercer ce métier.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_58",
      nom: "contremaître",
      descriptif:
        "<p>Le contremaître est un chef d'équipe. Au quotidien, il dirige des conducteurs de machines et travaille en collaboration avec les autres services, pour réaliser le programme de production prévu. Il s'assure que son équipe réponde aux commandes dans les délais.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_580",
      nom: "responsable de projets culturels",
      descriptif:
        "<p>À la tête d'un festival, dans un service municipal ou au sein d'une Drac (direction régionale des affaires culturelles), le responsable de projets culturels programme des manifestations (concert, exposition, pièce de théâtre...) dont il suit la réalisation de A à Z.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_581",
      nom: "relieur-doreur / relieuse-doreuse",
      descriptif:
        "<p>Professionnel du livre, le relieur protège les ouvrages, augmentant ainsi leur durée de vie. Il relie les feuilles d'un livre et les embellit en les dorant avec de fines feuilles d'or. Il répare aussi les ouvrages abîmés et restaure les livres anciens.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_582",
      nom: "animateur / animatrice de radio et de télévision",
      descriptif:
        "<p>Il est sympathique et toujours de bonne humeur, il parle vite et bien, répond du tac au tac ! Une spontanéité qui demande à l'animateur radio ou télé une préparation approfondie de chaque émission. Objectif : capter un maximum d'auditeurs ou de téléspectateurs.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_583",
      nom: "iconographe",
      descriptif:
        "<p>Spécialiste de l'image, l'iconographe gère un fonds ou répond aux demandes émises par les journalistes ou les éditeurs, par exemple pour illustrer un contenu rédactionnel. Il doit dénicher les illustrations qui collent parfaitement au texte.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_584",
      nom: "auteur-compositeur interprète / auteure-compositrice interprète",
      descriptif:
        "<p>Parolier, musicien et chanteur : trois fonctions pour un métier, celui d'auteur-compositeur-interprète. Artiste complet, il conjugue créativité et technicité pour pouvoir interpréter sur scène ses oeuvres musicales.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_585",
      nom: "ingénieur / ingénieure du son",
      descriptif:
        "<p>À la fois artiste et technicien, l'ingénieur du son assure la qualité du son produit pour une réalisation audiovisuelle, un album de musique, un concert ou un spectacle. Il allie pratique musicale et maîtrise de technologies complexes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_586",
      nom: "reporter-photographe / reportrice-photographe",
      descriptif:
        "<p>Témoin de son temps, le reporter-photographe parcourt le monde, appareil photo en bandoulière. Son but ? Réussir des images d'actualité pour informer et sensibiliser le public. Un métier passionnant qui reste difficile d'accès.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_588",
      nom: "agent / agente de propreté urbaine",
      descriptif:
        "<p>L'agente ou l'agent de propreté urbaine maintient la ville et ses équipements propres. C'est un travail indispensable à la qualité de vie au sein des espaces publics. L'activité est essentiellement à l'extérieur, à des horaires souvent atypiques.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_589",
      nom: "biologiste en environnement",
      descriptif:
        "<p>Les biologistes en environnement exercent dans de nombreux secteurs : santé, recherche vétérinaire, industrie agroalimentaire... Ces scientifiques luttent contre les dérives du monde moderne et leurs effets néfastes sur l'environnement comme sur notre santé.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_59",
      nom: "inspecteur / inspectrice des douanes, des finances publiques ou du travail",
      descriptif:
        "<p>L'inspecteur employé par les Douanes, la DGFiP (Direction générale des finances publiques) ou l'Inspection du travail est un cadre de la fonction publique de catégorie A. Expert en fiscalité et en droit, il possède aussi des qualités humaines.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_590",
      nom: "chercheur / chercheuse en chimie",
      descriptif:
        "<p>Trouver de nouvelles voies à explorer, observer, formuler des hypothèses… La vie du chercheur en chimie est une quête permanente, jalonnée d'avancées et de doutes. Un métier passion pour des scientifiques accomplis.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_591",
      nom: "auditeur / auditrice externe",
      descriptif:
        "<p>Employé d'un cabinet spécialisé, l'auditeur externe doit diagnostiquer la situation d'une entreprise cliente et émettre des recommandations objectives. Le plus souvent, il réalise des audits comptables et financiers, et doit certifier les comptes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_592",
      nom: "cogniticien / cogniticienne",
      descriptif:
        "<p>Ingénieur spécialisé dans l'intelligence artificielle, le cogniticien modélise et conçoit des machines intelligentes pouvant aider les utilisateurs dans leur travail ou leur vie quotidienne. Un métier à la croisée de l'informatique, de l'automatique et des sciences humaines.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_593",
      nom: "formateur/trice en informatique",
      descriptif:
        "<p>À l'heure où les technologies ne cessent d'évoluer, le formateur en informatique est partout. Connaissances affûtées et pédagogie en poche, il répond aux besoins en formation d'utilisateurs en tout genre.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_594",
      nom: "vendeur / vendeuse en micro-informatique et multimédia",
      descriptif:
        "<p>Comme tout commercial, son objectif est de vendre. Son rayon : les produits informatiques, tels que les ordinateurs, imprimantes, logiciels... Les connaissant bien, il se considère avant tout comme un conseiller qui assiste le client dans son achat.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_595",
      nom: "décolleteur / décolleteuse",
      descriptif:
        "<p>Le décolleteur fabrique en série des pièces de précision (vis, écrous, goupilles, composants...) utilisées partout : aéronautique, automobile, bâtiment, électronique, optique, médical... Un secteur de pointe où la France est en bonne place.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_596",
      nom: "mécanicien / mécanicienne marine navigant",
      descriptif:
        "<p>Au sein du service machines, le mécanicien marine navigant assure la maintenance et l'entretien des moteurs de propulsion et de tous les appareils du bord des navires de commerce.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_597",
      nom: "agent / agente d'escale",
      descriptif:
        "<p>L'agent d'escale est souvent la première personne que l'on rencontre dans un aéroport. Il accueille, informe et prend en charge les passagers à leur arrivée et à leur départ. Sa mission est de simplifier leur voyage et de le rendre agréable.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_598",
      nom: "ingénieur / ingénieure de la police technique et scientifique",
      descriptif:
        "<p>Éprouvettes, microscopes, portraits-robots numériques... tout un arsenal technologique permet à l'ingénieure ou à l'ingénieur de la police technique et scientifique (PTS) de faire analyser des indices par son équipe afin de démasquer les criminels.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_599",
      nom: "consultant/e en solutions intégrées",
      descriptif:
        "<p>Le consultant en solutions intégrées apporte son expertise à des clients désireux de faire évoluer leur système informatique. Après analyse, il propose des outils système innovants dont il suit la mise en place en conseillant les développeurs.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_6",
      nom: "musicien / musicienne",
      descriptif:
        "<p>Créer des mélodies, les interpréter, transmettre son savoir... Le métier de musicien recouvre une multitude de spécificités : à chacun de choisir sa propre musique, de jouer sa propre histoire !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_60",
      nom: "économe de flux",
      descriptif:
        "<p>Mission de l'économe de flux : faire baisser la facture d'énergie des entreprises et des collectivités (villes, administrations). À lui de repérer les surconsommations et de proposer des solutions pour économiser l'eau, le chauffage, l'électricité...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_600",
      nom: "poissonnier / poissonnière",
      descriptif:
        "<p>Le poissonnier vend des poissons ou des coquillages fraîchement pêchés, en respectant scrupuleusement les règles d'hygiène de la chaîne du froid. À la demande du client, il prépare les poissons et ouvre les coquillages.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_602",
      nom: "ingénieur / ingénieure en imagerie médicale",
      descriptif:
        "<p> L'ingénieur en imagerie médicale conçoit les logiciels des appareils médicaux (scanners, échographes, Doppler, IRM, etc.), permettant d'apporter une aide essentielle au diagnostic médical. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_603",
      nom: "ingénieur / ingénieure frigoriste",
      descriptif:
        "<p>L'ingénieur frigoriste conçoit et/ou développe des équipements frigorifiques ou de climatisation. Cet expert technique est un relais indispensable à la fois pour les équipes de techniciens et les commerciaux spécialisés dans la chaîne du froid.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_604",
      nom: "conseiller / conseillère en voyages",
      descriptif:
        "<p>Dans une agence de voyages, le conseiller en voyages enregistre, propose et organise des séjours. Fortement concurrencé par Internet, c'est sa capacité à donner des conseils appropriés qui constitue sa valeur ajoutée.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_605",
      nom: "horloger / horlogère",
      descriptif:
        "<p>Montres, réveils, pendules... l'horloger fabrique, répare ou vend tout type d'appareils mesurant le temps. Une activité de haute précision, relancée par les nouveaux matériaux et les technologies de pointe. Habileté, logique et patience sont de rigueur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_606",
      nom: "fiscaliste",
      descriptif:
        "<p>Stratégiques, les choix en matière fiscale concernent toutes les entreprises. Au carrefour du droit et de la finance, le fiscaliste choisit les meilleures options pour réduire le montant des impôts, dans le respect de la législation en vigueur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_607",
      nom: "hydrogéologue",
      descriptif:
        "<p>Parce que l'eau est précieuse, l'hydrogéologue recherche mais aussi surveille les nappes phréatiques et les poches souterraines afin de les préserver. Ce ou cette scientifique lutte contre les prélèvements excessifs ou la pollution.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_61",
      nom: "ingénieur/e en génie climatique",
      descriptif:
        "<p>Obtenir une température et une qualité de l'air optimales dans des locaux professionnels ou des habitations, tout en réalisant des économies d'énergie et en respectant un cadre réglementaire strict et rigoureux : tel est le défi de cet ingénieur du BTP confronté aux enjeux du développement durable.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_610",
      nom: "chargé / chargée d'études naturalistes",
      descriptif:
        "<p>Inventorier, protéger et valoriser les espèces animales et végétales, telles sont les missions de la chargée ou du chargé d'études naturalistes. Ces professionnels interviennent sur un espace naturel protégé ou en amont d'un projet d'urbanisme.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_611",
      nom: "architecte produit industriel",
      descriptif:
        "<p>L'architecte produit industriel améliore des produits ou des technologies existants, ou en conçoit de nouveaux. Ses objectifs : apporter une réponse innovante à un besoin exprimé et connu, ou imaginer un produit qui créera un nouveau besoin et un nouveau marché.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_612",
      nom: "ingénieur/e traitement de l'image",
      descriptif:
        "<p>Expert en mathématiques et en informatique, l'ingénieur traitement de l'image contribue à l'amélioration d'un système en développant des logiciels d'analyse et de modélisation d'images utilisés dans la recherche médicale, la navigation embarquée, le cinéma, la surveillance, l'aménagement du territoire...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_613",
      nom: "data manager",
      descriptif:
        "<p>Né de la multiplication et de la complexification des données, le data manager, ou gestionnaire de données, recueille et organise les informations de l'entreprise, en vue de leur exploitation optimale. Il travaille désormais dans tous les secteurs.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_614",
      nom: "collecteur / collectrice de fonds",
      descriptif:
        "<p>En organisant les campagnes de dons, le collecteur de fonds fait rentrer l'argent nécessaire à une opération de solidarité. Place aux spécialistes de la gestion des bases de données et du marketing direct.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_615",
      nom: "substitut/e du procureur",
      descriptif:
        "<p>De la direction d'enquête judiciaire à l'exécution de la peine, en passant par la décision d'engager des poursuites, le substitut du procureur de la République intervient à toutes les étapes de la chaîne pénale. Au tribunal, il défend l'intérêt de la société.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_616",
      nom: "chargé / chargée d'études en valorisation agricole des déchets",
      descriptif:
        "<p>Les déchets sont une mine d'or ! La chargée ou le chargé d'études en valorisation agricole des déchets s'intéresse aux détritus organiques urbains, industriels ou agricoles, et les recycle en les transformant en fertilisant pour les sols.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_617",
      nom: "ingénieur/e process aval",
      descriptif:
        "<p>Rattaché à une raffinerie ou à un centre de recherche, l'ingénieur process aval a pour mission d'optimiser le fonctionnement des raffineries. Ses objectifs sont les économies d'énergie, le respect de l'environnement, la qualité et le rendement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_618",
      nom: "chargé / chargée des méthodes outils et qualité en informatique",
      descriptif:
        "<p>Le chargé des méthodes outils et qualité en informatique est garant de la qualité de la production informatique de son entreprise (ou de son client) en définissant et en accompagnant la mise en place de nouvelles procédures et méthodes de travail.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_619",
      nom: "gestionnaire de contrats informatiques",
      descriptif:
        "<p>Le gestionnaire de contrats informatiques est devenu indispensable pour garantir la qualité de prestations, souvent complexes, effectuées avec de plus en plus de fournisseurs et de prestataires de services informatiques dans l'entreprise.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_62",
      nom: "bronzier / bronzière",
      descriptif:
        "<p>Il y a 4000 ans, le bronze naissait du mélange du cuivre et de l'étain... Depuis, le bronzier travaille cet alliage pour créer ou restaurer des objets d'art ou décoratifs (sculptures, serrures, pièces d'ameublement...), grâce à des gestes traditionnels.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_620",
      nom: "halieute",
      descriptif:
        "<p> L'halieute protège les espèces de poissons menacées de surpêche. Son expertise permet d'évaluer les stocks et de réglementer les méthodes de capture. Il s'intéresse aussi à l'élevage aquacole et contribue à préserver l'environnement marin. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_621",
      nom: "designer sonore",
      descriptif:
        "<p>Jingle, claquement de porte, sonnerie de micro-ondes, signaux sonores pour les non-voyants ou musique d'un jeu vidéo... autant de missions qui peuvent être dévolues au designer sonore en fonction de son secteur d'activité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_622",
      nom: "consultant/e informatique",
      descriptif:
        "<p>Expert extérieur à l'entreprise, le consultant informatique apporte ses connaissances à des clients désireux de faire évoluer leur système. Il propose des solutions techniques et organisationnelles dont il suit ensuite la mise en place.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_623",
      nom: "spécialiste de l'accessibilité numérique",
      descriptif:
        "<p>Le spécialiste de l'accessibilité numérique veille à ce que tous, notamment seniors ou personnes en situation de handicap, puissent utiliser les matériels et logiciels informatiques quel que soit le support utilisé (ordinateur, téléphone, tablette...). </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_624",
      nom: "consultant/e en conduite de changement",
      descriptif:
        "<p> L'organisation de l'entreprise est un facteur essentiel pour sa compétitivité. Le consultant en conduite de changement intervient pour diagnostiquer la situation et l'aide à apporter aux employés, pour que la transition voulue se fasse au mieux. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_625",
      nom: "responsable achats en chimie",
      descriptif:
        "<p>Le responsable achats en chimie commande des produits et des services pour une entreprise de chimie. Il coordonne l'activité d'une équipe d'acheteurs et contrôle les performances des fournisseurs sur la qualité des produits et les délais de livraison.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_626",
      nom: "ingénieur/e d'études en sûreté nucléaire",
      descriptif:
        "<p>L'ingénieur d'études en sûreté nucléaire analyse l'ensemble des risques présents sur un site donné et les moyens de prévention, mis ou à mettre en place, afin de garantir la sécurité des personnes, des installations nucléaires et de l'environnement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_627",
      nom: "statisticien/ne industriel/le",
      descriptif:
        "<p>Le but du statisticien industriel est d'améliorer la production de l'entreprise. Il effectue donc des études sur la prévision... des imprévus dans la production, l'optimisation des processus de fabrication ou la durée de vie des produits, par exemple.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_628",
      nom: "responsable de fabrication en chimie",
      descriptif:
        "<p>Le responsable de fabrication en chimie gère et organise les activités de production, établit le planning des personnels qu'il encadre et suit toutes les étapes de la fabrication. Son objectif : la productivité de son entreprise. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_63",
      nom: "encadreur / encadreuse",
      descriptif:
        "<p>L'encadreur fabrique ou restaure un cadre pour une toile, un dessin ou une photographie. Son but est à la fois de les mettre en valeur et de les protéger de l'usure.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_631",
      nom: "vendeur/euse conseil caviste",
      descriptif:
        "<p>Le vendeur conseil caviste vend vins, spiritueux et parfois aussi bières à une clientèle avide de conseils. Il connaît chacune de ses bouteilles et est à l'écoute de ses clients pour leur proposer ce qui leur correspondra le mieux. Un métier passion.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_632",
      nom: "expert / experte bilan carbone",
      descriptif:
        "<p>L'expert bilan carbone réalise des diagnostics afin d'aider les entreprises à mesurer leurs émissions de GES (gaz à effet de serre). Il leur propose ensuite des solutions pour réduire l'impact de leurs activités sur le réchauffement climatique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_633",
      nom: "ingénieur écoconcepteur / ingénieure écoconceptrice",
      descriptif:
        "<p>Objectif de l'écoconceptrice ou de l'écoconcepteur : diminuer au maximum l'impact environnemental de nos produits de consommation courante et services, en agissant dès leur conception et en proposant des solutions à chaque étape de leur vie.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_634",
      nom: "psychologue de l'éducation nationale spécialité éducation, développement et conseil en orientation scolaire et professionnelle",
      descriptif:
        "<p>Le psychologue de l'Éducation nationale (ou psy-EN) contribue à la réussite des élèves, accompagne ceux qui sont les plus en difficulté, les reçoit avec leur famille pour les conseiller et les informer à différentes étapes de leur scolarité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_636",
      nom: "digital learning manager",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_638",
      nom: "ingénieur / ingénieure de recherche (papiers-cartons)",
      descriptif:
        "<p>Inventer le papier ou l'emballage du futur, tel est le défi de l'ingénieur de recherche. Il fait le lien entre les innovations technologiques et le monde de la fabrication. Ses travaux peuvent déboucher sur des brevets.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_639",
      nom: "ludothécaire",
      descriptif:
        "<p>Le ludothécaire fait découvrir jeux et jouets à un public varié au sein d'une médiathèque, d'une bibliothèque ou d'un espace dédié qu'il gère. Il se tient au courant des nouveautés et conseille chacun en fonction de ses envies et de son niveau.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_64",
      nom: "assistant / assistante de service social",
      descriptif:
        "<p>L'ASS (assistant de service social) épaule des personnes en difficulté économique, sociale ou psychologique. Il joue aussi un rôle important pour la protection de l'enfance.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_640",
      nom: "retoucheur/euse",
      descriptif:
        "Changer une fermeture éclair, réaliser un ourlet de pantalon, réduire la taille d'une robe en ajoutant des pinces... Le retoucheur ou la retoucheuse est avant tout un couturier ou une couturière qui adapte les vêtements à la morphologie de ses clients. Ces professionnels exercent à leur compte ou comme salariés au sein d'un grand magasin ou d'un atelier de mode.<br/><br/><h3>Après la 3<sup>e</sup></h3>\n<p>2 ans pour préparer le CAP métiers de la mode-vêtement flou ou métiers de la mode-vêtement tailleur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_641",
      nom: "<span>Fondeur / fondeuse</span>",
      descriptif:
        "Dans l'artisanat ou l'industrie, la fondeuse ou le fondeur travaille le métal en fusion ou surveille la fabrication automatique de certaines pièces. Les spécialistes qualifiés sont recherchés, surtout dans les ateliers ou les fonderies industrielles.<br/><br/><h3>Après la 3<sup>e</sup></h3>\n<p>2 ans pour préparer un CAP (métier de la fonderie ; mouleur- noyauteur : cuivre et bronze ; outillages en moules métalliques) ; 3 ans pour le bac pro fonderie.</p>\n<h3>Après le bac</h3>\n<p>2 ans pour obtenir le BTS fonderie, complété éventuellement par le parcours fonderie de la licence professionnelle mention métiers de l'industrie : métallurgie, mise en forme des matériaux et soudage.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_643",
      nom: "chargé/e d'études (urbanisme)",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_644",
      nom: "consultant/e en communication",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_646",
      nom: "designer mobilier",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_647",
      nom: "moniteur/trice de voile",
      descriptif:
        "La monitrice ou le moniteur de voile encadre des élèves et leur apprend les techniques de navigation d'une famille de pratique (dériveur, catamaran, planche à voile, habitable) sur des plans maritimes ou sur une base nautique, afin de leur faire acquérir autonomie et règles de sécurité.<br/><br/><h3>Après la 3<sup>e</sup></h3>\n<p>2 ans pour préparer le BPJEPS spécialité éducateur sportif mention « voile multi-supports jusqu'à 6 milles nautiques d'un abri » ou le BPJEPS spécialité éducateur sportif mention voile croisière jusqu'à 200 milles nautiques d'un abri »</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_648",
      nom: "assistant / assistante en architecture",
      descriptif:
        "<p>Son rôle : traduire graphiquement les projets de l'architecte. À l'aide de logiciels, l'assistant en architecture crée des maquettes virtuelles en 3D (3 dimensions). Il assiste l'architecte aux cours de la conception, des études techniques et du suivi des travaux.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_649",
      nom: "technicien/ne de contrôle",
      descriptif:
        "<p>Le technicien de contrôle est le garant de la conformité des produits fabriqués dans son usine. Matières premières, emballages ou produits manufacturés : il leur fait passer tests et analyses pour assurer la qualité constante de la production.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_65",
      nom: "animateur socioculturel / animatrice socioculturelle",
      descriptif:
        "<p>Atelier hip-hop, sortie culturelle, débats... l'animateur socioculturel organise des activités variées pour le public dont il s'occupe : enfants, adolescents, personnes âgées... Son objectif ? Favoriser le lien, la socialisation et la créativité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_653",
      nom: "officier / officière de la marine marchande",
      descriptif:
        "<p>L'officier de la marine marchande assure la conduite du navire et encadre l'équipage. Il veille à la sécurité des passagers et des marchandises, et à la bonne conduite du bateau.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_654",
      nom: "maître / maîtresse d'hôtel",
      descriptif:
        "<p>Hôte attentionné et prévenant, le maître d'hôte est l'homme-orchestre de la salle de restaurant. À la tête des chefs de rang, il veille à la qualité du service et à la satisfaction des clients qu'il accueille et cherche à fidéliser.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_655",
      nom: "employé / employée de restaurant",
      descriptif:
        "<p>Cheville ouvrière de l'établissement qui l'emploie, l'employé de restaurant endosse tout ou une partie du rôle de serveur, de chef de rang, de maître d'hôtel... Il est le relais entre les cuisines et la salle.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_656",
      nom: "chef monteur / cheffe monteuse",
      descriptif:
        "<p>Après le tournage, le chef monteur donne du sens et une esthétique à un projet audiovisuel en choisissant et assemblant les plans, et en calant le son. Il exerce son art dans l'ombre du réalisateur, avec un sens de l'écoute non dénué d'esprit critique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_657",
      nom: "chargé / chargée d'études média",
      descriptif:
        "<p>Fin stratège, le chargé d'études média s'appuie sur des données chiffrées, sur des études de marché et sur son intuition pour concevoir des campagnes publicitaires et assurer leur visibilité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_658",
      nom: "solier-moquettiste / solière-moquettiste",
      descriptif:
        "<p>Le solier-moquettiste pose au sol et sur les murs divers revêtements tels que linoléum, moquette, mosaïque collée, matière plastique, tissu... Son objectif : assurer le confort des clients et l'harmonie de leur intérieur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_659",
      nom: "responsable de la promotion des ventes",
      descriptif:
        "<p>Le responsable de la promotion des ventes met au point des \" opérations séduction \" pour stimuler les ventes d'une marque ou d'un produit. Il travaille avec les professionnels du marketing et de la publicité, et avec les commerciaux sur le terrain.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_66",
      nom: "chargé / chargée de communication interne",
      descriptif:
        "<p>Le chargé de communication interne est la courroie de transmission entre la direction et les salariés. Son rôle : informer pour rassembler le personnel autour de l'image et des valeurs de l'entreprise via un journal, l'intranet, un séminaire...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_660",
      nom: "gérant / gérante de portefeuille",
      descriptif:
        "<p>Le gérant de portefeuille s'occupe des fonds des investisseurs et des institutions. Il en optimise la valorisation, en faisant des placements opportuns dans le cadre d'une stratégie déterminée en amont.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_661",
      nom: "analyste de crédit",
      descriptif:
        "<p>Prêter de l'argent, oui... Prendre des risques inconsidérés, non. Au sein d'une banque ou d'un organisme de crédit, l'analyste de crédit étudie les garanties offertes par les particuliers et les entreprises qui souhaitent obtenir un crédit (un prêt).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_663",
      nom: "affûteur / affûteuse",
      descriptif:
        "<p>L'affûteur est responsable des outils de coupe d'une scierie. Au quotidien, il entretient, prépare, répare et contrôle l'état des lames de scies et rabots dont dépend la qualité des sciages. Il occupe un poste indispensable dans la scierie.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_664",
      nom: "technicien / technicienne automobile",
      descriptif:
        "<p>Injection, freinage, suspension, transmission... le technicien automobile entretient et répare des véhicules de plus en plus sophistiqués dans lesquels l'électronique a tendance à s'imposer. Trouver l'origine d'un dysfonctionnement et poser un diagnostic : c'est le coeur de son activité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_665",
      nom: "administrateur / administratrice réseaux",
      descriptif:
        "<p>La circulation d'informations et de données est primordiale dans les entreprises et l'administration. L'administrateur réseaux veille à ce que les équipements fonctionnent de façon optimale et soient adaptés aux besoins en constante évolution des salariés.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_666",
      nom: "technicien / technicienne plasturgiste",
      descriptif:
        "<p>Le technicien plasturgiste fabrique et contrôle les objets en plastique comme les jouets, les pare-chocs, les emballages... La plasturgie concerne plus d'un millier de matières synthétiques différentes, omniprésentes dans notre environnement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_667",
      nom: "chargé / chargée d'études économiques",
      descriptif:
        "<p>Le chargé d'études économiques étudie et analyse la conjoncture économique. À l'aide de savants calculs, il établit des prévisions pour un organisme public ou une entreprise tout en suivant de près les marchés boursiers et la concurrence.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_668",
      nom: "éducateur / éducatrice de jeunes enfants",
      descriptif:
        "<p>Par le jeu et les activités d'éveil, l'EJE (éducateur de jeunes enfants) permet aux enfants de moins de 7 ans de s'épanouir et de s'initier à la vie en société. Un emploi qui demande patience et créativité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_669",
      nom: "conseiller / conseillère en économie sociale et familiale",
      descriptif:
        "<p>Spécialiste de l'action socio-éducative, le conseiller en économie sociale et familiale aide les personnes en situation précaire à résoudre leurs difficultés quotidiennes. Son rôle est essentiel dans le champ de l'insertion sociale et professionnelle.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_67",
      nom: "secrétaire d'édition",
      descriptif:
        "<p>La mission du secrétaire d'édition ? Transformer un manuscrit en livre, en suivant toutes les étapes de la réalisation d'un ouvrage, de la réception du texte jusqu'à son impression. Le tout sous l'autorité d'un responsable éditorial.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_670",
      nom: "secrétaire",
      descriptif:
        "<p>Avec le sourire, le secrétaire jongle avec le téléphone, la souris de l'ordinateur et de nombreux dossiers. Qu'il exerce dans une PME (petite ou moyenne entreprise), dans un service technique ou chez un avocat, sa discrétion et ses initiatives sont appréciées.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_671",
      nom: "chargé / chargée de clientèle banque",
      descriptif:
        "<p>Connaissant parfaitement les produits proposés par sa banque et le profil de ses clients, le chargé de clientèle banque cherche la solution la plus adaptée et la plus rentable pour les deux parties. C'est un métier à la fois technique et commercial.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_672",
      nom: "diététicien / diététicienne",
      descriptif:
        "<p>Le diététicien est un professionnel du secteur paramédical. Champion de l'alimentation équilibrée, il sait concocter des régimes adaptés à l'âge, au mode de vie, aux goûts et à l'état de santé des personnes qui viennent le consulter.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_673",
      nom: "commissaire-priseur / commissaire-priseuse",
      descriptif:
        "<p>Une fois, deux fois, trois fois... adjugé, vendu ! Avant de prononcer cette célèbre formule, le commissaire-priseur inventorie les objets qui lui sont soumis, organise les ventes et dirige les enchères.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_674",
      nom: "matelot à la pêche",
      descriptif:
        "<p>De la pêche au traitement des captures, en passant par la conduite du navire (bateau destiné à la navigation maritime) et son entretien, le matelot à la pêche est un polyvalent de la mer, surtout quand l'équipage est réduit.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_675",
      nom: "ingénieur papetier / ingénieure papetière",
      descriptif:
        "<p>L'ingénieur papetier est responsable d'une unité de production papetière. Au sein de l'usine de fabrication, il assure différentes fonctions : la maintenance, la gestion, le contrôle qualité, la surveillance des normes environnementales...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_676",
      nom: "audioprothésiste",
      descriptif:
        "<p>Technicien médical, l'audioprothésiste est le spécialiste des corrections de l'audition. Sur prescription d'un médecin ORL (oto-rhino-laryngologiste), il procède à l'appareillage des déficiences de l'ouïe.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_677",
      nom: "journaliste reporter d'images",
      descriptif:
        "<p>Journaliste de terrain par excellence, le journaliste reporter d'images (JRI) est spécialisé dans la réalisation de reportages pour la télévision, les agences de presse audiovisuelle, les sociétés de production... Caméra à l'épaule, il enregistre les images et le son. Il rédige ensuite un commentaire et le monte sur images pour finaliser son sujet.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_678",
      nom: "éditeur / éditrice",
      descriptif:
        "<p>Pour prendre forme, un roman, un album jeunesse ou un livre de voyage doit d'abord convaincre l'éditeur. Ce dernier suit toutes les étapes d'un projet éditorial, de la coordination d'une équipe à la création d'une nouvelle collection, de la découverte d'un auteur à la présentation du livre aux équipes commerciales.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_679",
      nom: "conducteur routier / conductrice routière",
      descriptif:
        "<p>Poids lourd, camionnette, véhicule léger... au volant de son véhicule du matin au soir, le conducteur routier a pour mission de livrer la marchandise en bon état et en respectant les délais impartis.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_68",
      nom: "carrossier / carrossière",
      descriptif:
        "<p>Changer un pare-chocs ou redresser de la tôle froissée, c'est l'affaire du carrossier réparateur. Au carrossier peintre de prendre ensuite le relais pour donner au véhicule son aspect définitif. Parfois, carrossier réparateur et carrossier peintre ne font qu'une seule et même personne.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_680",
      nom: "enseignant / enseignante de la conduite automobile et de la sécurité routière",
      descriptif:
        "<p>L'enseignant de la conduite automobile et de la sécurité routière, communément appelé moniteur d'auto-école, prépare les candidats à l'examen du permis. Il a aussi un rôle de sensibilisation aux règles de prudence. Patience et réflexes lui sont indispensables...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_681",
      nom: "bijoutier-joaillier / bijoutière-joaillière",
      descriptif:
        "<p>Bagues, bracelets, colliers... autant de parures qui prennent vie dans les mains du bijoutier-joaillier. Une activité tout en finesse combinant le travail des métaux précieux et l'art de monter les pierres et les perles.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_682",
      nom: "maréchal-ferrant / maréchale-ferrante",
      descriptif:
        "<p>« Pas de pied, pas de cheval » : cette expression anglaise traduit le rôle central du maréchal-ferrant. Sans lui, le cheval marcherait à vif. Un vieux métier qui reprend vigueur avec la floraison des centres équestres.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_683",
      nom: "officier / officière de l'armée de l'air",
      descriptif:
        "<p>L'officière ou l'officier de l'armée de l'air peut être militaire de carrière ou sous contrat. Officiers de l'air, officiers mécaniciens, officiers de base ou commissaires des armées... tous travaillent de concert et oeuvrent à la défense du territoire.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_684",
      nom: "régleur / régleuse",
      descriptif:
        "<p>Bouchons, jouets, sacs, téléphones... la plupart des objets en plastique de notre quotidien sont produits en usine. Le régleur participe à leur fabrication en veillant au bon fonctionnement des machines qui leur donnent forme.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_685",
      nom: "mécanicien / mécanicienne bateaux",
      descriptif:
        "<p>Cet ouvrier mécanicien est chargé de l'entretien courant, du diagnostic des pannes et de la réparation sur les bateaux. Il s'occupe aussi bien de voiliers de plaisance que de gros navires à moteur, voire d'embarcations comme les scooters des mers.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_686",
      nom: "mécanicien-outilleur / mécanicienne-outilleuse",
      descriptif:
        "<p>Le mécanicien-outilleur fabrique et entretient les outils (matrices, poinçons, moules...) qui permettront de produire des pièces en grande série pour l'industrie : tableaux de bord de voiture, portières, claviers d'ordinateur...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_687",
      nom: "attaché / attachée d'administration",
      descriptif:
        "<p>Cadre fonctionnaire, l'attaché d'administration exerce dans des contextes professionnels variés. Les missions qu'on lui confie concernent la préparation ou l'élaboration de politiques publiques et la gestion des moyens humains, matériels et financiers.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_688",
      nom: "directeur / directrice d'hôpital",
      descriptif:
        "<p>Un hôpital est une véritable entreprise dirigée par le directeur d'hôpital. Celui-ci gère tous les aspects de son établissement (finances, personnel, gestion, etc.) dans une recherche permanente de qualité et de performance.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_69",
      nom: "accessoiriste",
      descriptif:
        "<p>Dénicher, réparer, fabriquer des objets, telles sont les missions de l'accessoiriste. Objectif : donner un côté réaliste à un projet artistique tout en restant fidèle aux intentions du metteur en scène.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_691",
      nom: "officier / officière de gendarmerie",
      descriptif:
        "<p>Militaire et spécialiste des questions de sécurité, l'officier de gendarmerie ou l'officière de gendarmerie exerce ses compétences en police judiciaire, en sécurité routière, en sécurité publique, en maintien de l'ordre, en renseignement...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_692",
      nom: "conservateur / conservatrice du patrimoine",
      descriptif:
        "<p>Le conservateur du patrimoine étudie, classe, conserve, entretient et met en valeur œuvres d'art, archives, monuments... avec un objectif : les transmettre aux générations futures.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_693",
      nom: "dessinateur / dessinatrice de BD",
      descriptif:
        "<p>Beaucoup d'appelés, peu d'élus : le 9e art n'est pas des plus accueillant. Pour percer, le dessinateur de BD (bande dessinée) doit avoir un bon coup de crayon, mais aussi une forte personnalité, un réel talent de scénariste et une infinie patience.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_694",
      nom: "teinturier/ère et blanchisseur/euse",
      descriptif:
        "<p>Dans un pressing, le teinturier prend soin des vêtements et du linge (de table, de toilette, de maison...) que lui confient les particuliers. Le blanchisseur quant à lui, traite de grandes quantités de linge pour le compte d'établissements tels que les hôpitaux, les restaurants ou encore les hôtels.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_696",
      nom: "chef / cheffe de projet multimédia",
      descriptif:
        "<p>Le chef de projet multimédia coordonne l'ensemble de la production d'un projet multimédia. Son objectif : proposer au client un produit de qualité, dans les délais impartis et respectant le budget défini au préalable.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_697",
      nom: "responsable des ressources humaines",
      descriptif:
        "<p>Le responsable des ressources humaines est chargé de diriger la stratégie de gestion du personnel et du développement des effectifs au sein d'une entreprise. Ce poste de management est confié à des professionnels très qualifiés.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_698",
      nom: "grutier/ère",
      descriptif:
        "<p>Du haut de son engin, le grutier ou la grutière déplace des charges lourdes, répartit les matériaux sur le chantier ou distribue les éléments préfabriqués. Personne incontournable du chantier, il ou elle s'assure que la sécurité est garantie avant d'agir.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_699",
      nom: "chercheur / chercheuse en biologie",
      descriptif:
        "<p>Observer des phénomènes, formuler des hypothèses, trouver de nouvelles voies à explorer... La vie du chercheur en biologie est une quête permanente, jalonnée d'avancées et de doutes. Un métier passion pour des scientifiques de haut vol.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7",
      nom: "chanteur / chanteuse",
      descriptif:
        "<p>Opéra, jazz, rock, variétés... autant de voies pour une seule voix ! Mais ne devient pas chanteur qui veut : les passionnés devront fournir des efforts et s'entraîner sans relâche pour espérer gagner les faveurs du public.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_70",
      nom: "apiculteur / apicultrice",
      descriptif:
        "<p>« Éleveur » d'abeilles, l'apiculteur veille à leur bonne santé, en surveillant les ruches pour les protéger des prédateurs et des maladies. Il récolte la gelée royale, la cire et le miel fabriqués par les abeilles, et les commercialise en l'état ou après transformation.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_700",
      nom: "gestionnaire de données cliniques",
      descriptif:
        "<p>Les essais cliniques sont la phase ultime avant l'autorisation de mise sur le marché d'un médicament. Le gestionnaire de données cliniques élabore les bases de données destinées à recevoir les informations obtenues, et contrôle leur validité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_701",
      nom: "microbiologiste",
      descriptif:
        "<p> Le microbiologiste est un chercheur qui étudie les micro-organismes (bactéries, moisissures, virus, microbes...). Ses observations et ses travaux de recherche sont ensuite mis à profit pour préserver l'environnement, combattre une maladie, etc. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_702",
      nom: "histologiste",
      descriptif:
        "<p> Comprendre et analyser le fonctionnement et les relations qu'entretiennent entre elles les cellules du vivant, tel est le défi quotidien de l'histologiste ! Il travaille sur les tissus humains, animaux ou végétaux. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_703",
      nom: "volcanologue",
      descriptif:
        "<p>Géologue spécialiste de l'étude des volcans en activité, éteints ou sous-marins, le ou la volcanologue passe généralement plus de temps en laboratoire que sur le terrain. Son travail permet de comprendre l'origine et l'évolution de la Terre.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_704",
      nom: "arbitre",
      descriptif:
        "<p>Indispensable à toute rencontre sportive, du plus petit au plus haut niveau, l'arbitre est au coeur du jeu. Il ou elle fait respecter l'ordre sur le terrain ainsi que les règles du jeu, en toute impartialité. Activité passion souvent bénévole au départ.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_705",
      nom: "bûcheron/ne",
      descriptif:
        "Au grand air, dans les forêts d'exploitation, le bûcheron ou la bûcheronne exerce un métier à la fois sportif et technique. Il ou elle abat les arbres, découpe des troncs, les trie et les empile sur place pour faciliter le travail du débardeur. Bonne condition physique, capacité à anticiper la direction et la surface couverte par la chute de l'arbre, respect des conditions de sécurité pour manipuler les tronçonneuses... caractérisent ces ouvriers et ouvrières d'exploitation forestière.<br/><br/><h3>Après la 3<sup>e</sup></h3>\n<p>2 ans pour préparer le CAP agricole travaux forestiers, éventuellement complété par le BP responsable de chantiers forestiers (2 ans) ; 3 ans pour le bac professionnel forêt.</p>\n<p>2 ans également pour obtenir un BPA travaux forestiers spécialité travaux de bûcheronnage.</p>\n<h3>Après un bac pro, un BP ou un BTSA du secteur de l'aménagement ou de l'agroéquipement</h3>\n<p>1 an pour préparer le CS pilote de machines de bûcheronnage.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_706",
      nom: "ingénieur / ingénieure en fonderie",
      descriptif:
        "<p>L'ingénieur en fonderie est le spécialiste des procédés de fabrication des pièces métalliques composant la plupart des objets du quotidien (pièces automobiles, vélos, canalisations...).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_707",
      nom: "ingénieur / ingénieure logiciel",
      descriptif:
        "<p>Concevoir puis mettre en place des logiciels de comptabilité, de gestion des stocks, de traitement des images, d'applications web... en suivant les demandes des entreprises ou des administrations, c'est le rôle de l'ingénieur logiciel.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_708",
      nom: "employé / employée de pressing",
      descriptif:
        "<p>Un chewing-gum collé sur un vêtement, un blouson à nettoyer à sec, une nappe tachée avec du vin... L'employée ou l'employé de pressing doit nettoyer les articles textiles ordinaires ou délicats qui lui sont confiés et les rendre aux clients \" comme neufs \".</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_709",
      nom: "géotechnicien / géotechnicienne",
      descriptif:
        "<p>Pour travailler la terre, il faut bien la connaître. Le géotechnicien ou la géotechnicienne teste la résistance des sols avant la construction d'un bâtiment ou d'un port. Une reconnaissance du terrain qui permet d'éviter les risques d'écroulement dus aux glissements de sol.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_71",
      nom: "garde (chasse, pêche, littoral, rivière, parcs nationaux)",
      descriptif:
        "<p>Les gardes-chasse, gardes-pêche, gardes du littoral, gardes-rivière et gardes des parcs nationaux ont pour mission de protéger l'environnement, la faune et la flore par des actions de prévention, de répression et d'éducation.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_710",
      nom: "anesthésiste-réanimateur/trice",
      descriptif:
        "<p>L'anesthésiste-réanimateur endort les patients lors d'une intervention chirurgicale pour leur éviter de souffrir. Puis il les surveille jusqu'au réveil et les soulage des douleurs post-opératoires.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_711",
      nom: "éducateur technique spécialisé / éducatrice technique spécialisée",
      descriptif:
        "<p>L'éducateur technique spécialisé travaille auprès de personnes handicapées ou en difficulté. Sa mission : leur transmettre un savoir-faire professionnel pour favoriser leur épanouissement personnel et leur insertion sociale.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_712",
      nom: "conseiller / conseillère pénitentiaire d'insertion et de probation",
      descriptif:
        "<p>Le conseiller pénitentiaire d'insertion et de probation (CPIP) suit les personnes condamnées par la justice (peine de prison, contrôle judiciaire, etc.). Il propose des aménagements de peine, veille au respect des obligations et facilite la réinsertion.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_713",
      nom: "directeur / directrice d'accueil collectif de mineurs (ACM)",
      descriptif:
        "<p>Le directeur d'accueil collectif de mineurs (ACM) encadre des jeunes (enfants et/ou des adolescents) dans le cadre d'un centre aéré, d'un centre de vacances ou de loisirs, ou encore d'une colonie de vacances, auxquels il propose des activités récréatives ou éducatives.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_714",
      nom: "coupeur/euse",
      descriptif:
        "Cuir, coton, soie... La coupeuse ou le coupeur utilise un tranchet ou un emporte-pièce pour les peaux, des ciseaux dans les ateliers textiles artisanaux ou une machine à commandes numériques dans l'industrie où ces professionnels sont très recherchés. Précision et attention sont des qualités indispensables.<br/><br/><h3>Après la 3e</h3>\n<p>3 ans pour préparer le bac pro métiers de la mode-vêtements ou métiers du cuir.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_715",
      nom: "chargé / chargée de recherche en acoustique musicale",
      descriptif:
        "<p>Le chargé de recherche en acoustique musicale étudie les phénomènes sonores en relation avec la musique. Scientifique de haut niveau, il transforme les sons et les vibrations en équations mathématiques pour comprendre le fonctionnement des instruments de musique, quelles que soient leur origine ou leur époque.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_716",
      nom: "bactériologiste",
      descriptif:
        "<p>Le bactériologiste est un scientifique spécialisé en microbiologie. Il observe et étudie les bactéries, des micro-organismes unicellulaires qu'il scrute au microscope pour évaluer leur rôle dans l'environnement humain, végétal ou animal. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_717",
      nom: "administrateur/trice de logiciels de laboratoire",
      descriptif:
        "<p>Les laboratoires d'analyses médicales sont équipés de matériels et de logiciels informatiques spécifiques qui gèrent chaque étape du travail. L'administrateur de logiciels de laboratoire a un rôle clé dans ces organisations où l'erreur n'est pas permise.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_718",
      nom: "architecte web",
      descriptif:
        "<p>Expert technique, l'architecte web a pour mission principale de créer et de faire évoluer le schéma technique d'une application mobile ou d'un site Internet. Il intervient sur des projets de grande envergure et conseille l'ensemble de l'équipe projet.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_719",
      nom: "prototypiste en matériaux souples",
      descriptif:
        "<p>Entre création et production, le prototypiste en matériaux souples fabrique les prototypes et maquettes de produits pour les industries de l'habillement, du textile, de la maroquinerie... ou de l'automobile et de l'aéronautique pour les tissus techniques.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_72",
      nom: "greffier / greffière",
      descriptif:
        "<p>Garder la trace des débats lors des audiences, authentifier les décisions de justice, être garant du respect des règles de procédure, telle est la mission du greffier. Un métier de rigueur et de précision.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_720",
      nom: "cryptologue",
      descriptif:
        "<p>Expert en sécurité des systèmes de communication, le cryptologue chiffre et déchiffre des informations numériques sensibles. Mathématicien de formation, il travaille à partir d'algorithmes complexes qu'il doit sans cesse améliorer.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_721",
      nom: "ingénieur / ingénieure en caractérisation des matériaux",
      descriptif:
        "<p>Expert en matériaux, l'ingénieur en caractérisation des matériaux améliore et conçoit de nouveaux produits de plus en plus performants, à la fois résistants, économiques et écologiques. Il intervient à partir de leur conception jusqu'à leur utilisation.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_722",
      nom: "technicien / technicienne céramiste",
      descriptif:
        "<p>Porcelaine, prothèses, composants électroniques... La céramique prend toutes les formes et permet au technicien d'exercer dans des secteurs très divers, des arts de la table à l'aéronautique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_723",
      nom: "conseiller sportif / conseillère sportive en salle de remise en forme",
      descriptif:
        "<p>Tantôt il encadre un cours de step, de cycling ou de cardio-training, tantôt il fait du conseil personnalisé sur le plateau de musculation. Le conseiller sportif en salle de remise en forme sait communiquer son enthousiasme à la clientèle de son club.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_724",
      nom: "responsable de réseau eau potable",
      descriptif:
        "<p>Le ou la responsable de réseau eau potable veille sur un labyrinthe de canalisations et de réservoirs. Garantissant la qualité de l'eau depuis la station de pompage, il ou elle s'assure que chacun accède à de l'eau saine en ouvrant le robinet.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_725",
      nom: "ingénieur / ingénieure fluides, énergies, réseaux, environnement",
      descriptif:
        "<p>Produire de l'énergie propre est devenu un enjeu de taille. L'ingénieur fluides, énergies, réseaux, environnement veille à la qualité de l'air, au traitement des eaux et à la valorisation des déchets, dans un contexte industriel.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_726",
      nom: "rudologue",
      descriptif:
        "<p>Le ou la rudologue étudie le contenu de nos ordures, dresse des bilans concernant nos modes de production et de consommation, et propose des solutions pour prévenir l'augmentation des déchets ménagers ou industriels.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_727",
      nom: "ingénieur / ingénieure support",
      descriptif:
        "<p>À la demande du client utilisateur, l'ingénieur support intervient en cas de problème informatique, qu'il soit logiciel ou matériel. Réactif, il pose un diagnostic et trouve une solution le plus rapidement possible, puis accompagne sa mise en oeuvre.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_728",
      nom: "ingénieur / ingénieure en acoustique",
      descriptif:
        "<p>L'ingénieur en acoustique s'attache à lutter contre la pollution sonore, un facteur de stress qui peut être très important dans les villes ou dans certaines usines. Conception de matériaux, mesure, contrôle... son champ d'action est vaste.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_729",
      nom: "auditeur / auditrice qualité",
      descriptif:
        "<p>Pour chaque site demandant une certification, l'auditeur qualité réalise des audits d'évaluation et d'habilitation dans les domaines de l'hygiène, de la qualité, de la sécurité et de l'environnement. Il rédige un rapport qui sera soumis au comité certificateur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_73",
      nom: "géographe",
      descriptif:
        "<p>Comprendre l'organisation de l'espace, tel est l'objectif du ou de la géographe. Ses recherches trouvent des applications dans de nombreux domaines : risques, aménagement, environnement, urbanisme, développement durable...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_730",
      nom: "responsable approvisionnement",
      descriptif:
        "<p>Le responsable approvisionnement gère les flux et les stocks de produits en fonction des prévisions de vente, afin d'optimiser les quantités et les coûts de l'entreprise. En lien direct avec les fournisseurs, il participe à chaque négociation impliquant les équipes d'acheteurs.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_731",
      nom: "psychiatre",
      descriptif:
        "<p>Médecin spécialiste, le ou la psychiatre reçoit les patients atteints de troubles mentaux, de névroses ou d'addictions. Il ou elle met en place un traitement, utilisant notamment la parole comme outil et prescrit des médicaments pour parvenir à la guérison.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_732",
      nom: "technicien démonstrateur / technicienne démonstratrice en matériel agricole",
      descriptif:
        "<p>Qu'il représente une ou plusieurs marques, le technicien démonstrateur en matériel agricole fait la promotion de machines auprès des vendeurs de concessions ou directement des utilisateurs. Il participe à des salons et met en route les engins neufs.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_733",
      nom: "ingénieur / ingénieure logistique",
      descriptif:
        "<p>Professionnel de terrain et manager d'équipe, l'ingénieur logistique organise la circulation des produits dans l'entreprise, depuis la réception des matières premières jusqu'à la livraison au client. Il n'a de cesse d'optimiser stocks, coûts et délais.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_736",
      nom: "spécialiste des affaires réglementaires en chimie",
      descriptif:
        "<p>Le spécialiste des affaires réglementaires en chimie a pour mission d'obtenir l'AMM (autorisation de mise sur le marché) pour les nouveaux produits fabriqués par son entreprise, qui doivent être conformes à la réglementation en vigueur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_737",
      nom: "ingénieur/e radioprotection",
      descriptif:
        "<p>Spécialiste de la prévention des risques dans le domaine de la radioactivité, l'ingénieur radioprotection a un rôle-clé dans l'industrie nucléaire qui a encore de beaux jours devant elle.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_738",
      nom: "ingénieur/e chimiste en développement analytique",
      descriptif:
        "<p>L'ingénieur chimiste en développement analytique est chargé d'assurer le suivi et la planification des analyses réalisées en laboratoire, d'optimiser la prise en charge des échantillons et de maintenir la qualité technique des analyses.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_739",
      nom: "responsable des ouvrages hydroélectriques",
      descriptif:
        "<p>En charge d'un territoire donné, le responsable des ouvrages hydroélectriques s'assure que ceux-ci sont fiables et respectent à la fois les objectifs de production et les règles de sécurité. Il gère des équipes et vise à optimiser les installations.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_74",
      nom: "ingénieur technico-commercial / ingénieure technico-commerciale",
      descriptif:
        "<p>L'ingénieur technico-commercial se distingue par sa double compétence : la négociation commerciale et la connaissance parfaite des produits qu'il vend. Spécialiste du sur-mesure, il sait adapter son offre et ses services aux besoins de ses clients. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_740",
      nom: "conducteur/trice de machine onduleuse",
      descriptif:
        "<p>La production du carton ondulé est sa spécialité. Le conducteur de machine onduleuse veille au bon déroulement de la fabrication industrielle de ce carton d'emballage, particulièrement solide grâce à ses cannelures internes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_741",
      nom: "conducteur / conductrice d'engins forestiers de récolte en entreprises de travaux forestiers",
      descriptif:
        "<p>Depuis sa cabine, le conducteur d'engins forestiers en entreprises de travaux forestiers coupe, façonne puis collecte, à l'aide de sa grue, les troncs d'arbres. Il les achemine jusqu'à une place de dépôt où ils seront enlevés par des camions.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_742",
      nom: "généalogiste",
      descriptif:
        "<p>Qu'il soit généalogiste familial ou successoral, ce professionnel spécialiste des archives est missionné pour retrouver un héritier, un document ancien, un lien familial, etc. Ses recherches peuvent l'amener à voyager, y compris à l'étranger.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_745",
      nom: "designer automobile",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_746",
      nom: "<span>Perchman / perchwoman</span>",
      descriptif:
        "Sur le plateau d'un tournage de cinéma, le perchman ou la perchwoman tient à bout de bras une perche équipée d'un micro qui enregistre les dialogues des comédiens. À l'écart des projecteurs, il ou elle réalise un travail technique indispensable à la qualité sonore du film.<br/><br/><h3>Après le bac</h3>\n<p>2 ans pour préparer le BTS métiers de l'audiovisuel, option métiers du son ; 3 à 5 ans pour obtenir un diplôme d'école de cinéma (la Fémis, Louis-Lumière, école privée).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_749",
      nom: "juriste en droit immobilier",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_75",
      nom: "ingénieur électricien / ingénieure électricienne",
      descriptif:
        "<p>L'ingénieur électricien développe les réseaux d'électricité, conçoit des équipements électriques, améliore l'installation d'un hôpital, par exemple... Il travaille principalement chez les producteurs d'énergie, dans des entreprises industrielles ou du BTP (bâtiment et travaux publics).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_750",
      nom: "greffier/ère des tribunaux de commerce",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_751",
      nom: "acheteur/euse (distribution)",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_752",
      nom: "énergéticien / énergéticienne",
      descriptif:
        "<p>Au coeur de l'usine, l'énergéticien veille à la qualité, à la quantité et à la régularité de la vapeur émise, en l'ajustant aux besoins de la production. Autonome et réactif, il a le sens des responsabilités car sans vapeur, c'est toute l'usine qui s'arrête!</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_753",
      nom: "chef / cheffe de produit touristique",
      descriptif:
        "<p>Treck au Népal, safari au Kenya, circuit culturel en Égypte... armé de son téléphone et de son ordinateur, le chef de produit touristique concocte derrière son bureau des voyages de rêve, pour le plaisir des autres.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_754",
      nom: "scénariste",
      descriptif:
        "<p>Adapter un roman noir pour le grand écran, prévoir tous les rebondissements d'une saga familiale pour la télévision, concevoir un nouveau jeu vidéo ou d'animation en 3D... autant de défis relevés par un professionnel de l'écriture : le scénariste.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_755",
      nom: "directeur / directrice de la photographie",
      descriptif:
        "<p>Quel point commun entre une séquence de cinéma, une émission de télévision et un spot publicitaire ? Une image travaillée par le directeur de la photographie. Avec une attention particulière portée aux couleurs, à la lumière et au cadrage.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_756",
      nom: "responsable du recrutement",
      descriptif:
        "<p>De la prévision des besoins à l'embauche et à l'intégration des candidats, l'objectif du responsable du recrutement reste le même : dénicher la perle rare, c'est-à-dire la personne parfaite pour le poste proposé.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_757",
      nom: "trader",
      descriptif:
        "<p>Parier sur la hausse ou la baisse d'une monnaie, d'un indice ou d'une action représente le travail quotidien du trader. Les yeux rivés sur les cours de la Bourse, ce professionnel des salles de marché brasse des millions d'euros par jour.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_758",
      nom: "responsable du back office",
      descriptif:
        "<p>Le responsable du back office est chargé des fonctions administratives liées aux opérations bancaires. Il supervise le traitement des transactions et travaille dans une banque de détail ou dans une banque de financement et d'investissement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_759",
      nom: "technicien / technicienne en automatisme",
      descriptif:
        "<p>Expert en machines-outils et en commandes numériques, le technicien en automatismes connaît tout des robots : leur conception, leur mise en service et leur maintenance. C'est un professionnel polyvalent et très recherché.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_76",
      nom: "chef / cheffe de cultures légumières",
      descriptif:
        "<p>Les missions du chef de cultures légumières ? Répondre aux demandes des consommateurs (produire des légumes beaux, frais, savoureux et abordables), respecter l'environnement et s'adapter aux nouvelles technologies (informatique, électronique...).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_760",
      nom: "ingénieur / ingénieure en aéronautique",
      descriptif:
        "<p>L'ingénieur en aéronautique conçoit, teste, fabrique, entretient et commercialise des avions et des hélicoptères (civils ou militaires), mais aussi des lanceurs spatiaux, des satellites et des missiles. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_761",
      nom: "ingénieur / ingénieure en construction navale",
      descriptif:
        "<p>L'ingénieur en construction navale conçoit et assure la fabrication de bateaux de toutes sortes. Employé par des chantiers navals de tailles diverses, il peut être généraliste ou spécialisé dans un aspect de la conception ou de la fabrication.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_762",
      nom: "télévendeur / télévendeuse",
      descriptif:
        "<p>\" Ne quittez pas, un conseiller va traiter votre demande... \" Le télévendeur vous informe sur les garanties de votre contrat d'assurance, sur l'état de votre compte bancaire...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_763",
      nom: "facteur / factrice",
      descriptif:
        "<p>Les facteurs, agents de tri et opérateurs de colis sont chargés de trier et de distribuer chaque jour les plis et les paquets. Des activités qui requièrent une certaine autonomie, beaucoup de rigueur et une bonne condition physique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_764",
      nom: "syndic de copropriété",
      descriptif:
        "<p>Grâce à ses compétences techniques et juridiques, le syndic de copropriété gère les parties communes et les équipements collectifs d'immeubles d'habitation, de bureaux, de locaux commerciaux pour le compte des copropriétaires.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_765",
      nom: "orthophoniste",
      descriptif:
        "<p>L'orthophoniste prévient, repère et traite les troubles de la voix, de la parole et du langage chez les enfants et les adultes. L'une de ses compétences principales : concevoir et mettre en oeuvre des programmes de rééducation.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_766",
      nom: "enseignant spécialisé / enseignante spécialisée",
      descriptif:
        "<p>Instruire et accompagner des élèves souffrant de troubles du comportement ou de handicaps, tel est le rôle de l'enseignant spécialisé. Un métier où le travail en équipe (éducateurs, psychologues, médecins, assistants sociaux...) est primordial.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_767",
      nom: "pharmacien / pharmacienne d'officine",
      descriptif:
        "<p>Dans son officine (plus communément appelée pharmacie), le pharmacien ou la pharmacienne délivre, stocke et prépare les médicaments. En contact quotidien avec les patients, il ou elle les accompagne dans leur parcours de soins en collaboration d’autres professions de santé. Il ou elle participe aussi au dépistage, à la vaccination et à la prévention de certaines maladies.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_768",
      nom: "traducteur-interprète / traductrice-interprète",
      descriptif:
        "<p>Le traducteur transpose un texte d'une langue dans une autre, et l'interprète adapte un discours oral. Grâce aux nouvelles technologies, ce métier a évolué et a élargi son champ d'intervention à l'audiovisuel, à Internet, etc.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_769",
      nom: "magistrat / magistrate",
      descriptif:
        "<p>Rendre la justice et veiller au respect des textes législatifs et réglementaires, telles sont les missions du magistrat. Si cette profession attire de nombreux candidats, elle reste extrêmement difficile d'accès.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_77",
      nom: "animateur nature / animatrice nature",
      descriptif:
        "<p>Sorties pour découvrir la flore et la faune locales, création de sentiers, organisation d'une exposition... l'animateur ou l'animatrice nature ne manque pas d'idées pour sensibiliser petits et grands à la préservation de l'environnement. De quoi former des écocitoyens.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_770",
      nom: "ingénieur / ingénieure plasturgiste",
      descriptif:
        "<p>Au sein de l'industrie de la plasturgie, secteur innovant et créateur d'emplois, l'ingénieur plasturgiste exerce des fonctions variées, de la conception à la commercialisation d'un produit en plastique (appareils électroménagers, éléments d'automobiles...).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_771",
      nom: "technicien / technicienne d'analyses biomédicales",
      descriptif:
        "<p>À l'hôpital ou en laboratoire privé, ce professionnel de santé effectue les analyses biomédicales permettant de prévenir ou d'identifier une maladie. Un travail sur prescription médicale uniquement, et sous la responsabilité du biologiste.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_773",
      nom: "géophysicien/ne",
      descriptif:
        "<p>Le géophysicien ou la géophysicienne, scientifique spécialiste en géologie, étudie les caractéristiques physiques internes et externes de la Terre, ou d'autres planètes, en utilisant les méthodes des sciences physiques, et en procédant à des observations et à des mesures.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_774",
      nom: "conseiller/ère en environnement",
      descriptif:
        "<p>Les missions de la conseillère et du conseiller en environnement sont entièrement tournées vers l'amélioration du cadre de vie et le développement durable : gestion de nouveaux sites industriels, assainissement des rivières, protection des forêts...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7744",
      nom: "interprète français / LSF (langue des signes française)",
      descriptif:
        "<p>L'interprète en langue des signes fait le lien entre une personne ou un groupe de personnes sourdes et un ou plusieurs entendants. Il traduit les propos des uns et des autres, tour à tour en français et en langue des signes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7748",
      nom: "menuisier/ière aluminium-verre",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_775",
      nom: "ascensoriste",
      descriptif:
        "<p>L'ascensoriste installe, entretient, dépanne et modernise les ascenseurs, monte-charge, escaliers mécaniques, trottoirs roulants et portails automatiques sur différents sites. Une profession qui recrute pour faire face à l'accroissement des besoins et assurer la relève.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7764",
      nom: "technicien/ne de maintenance des matériels agricoles ou d'espaces verts",
      descriptif:
        "<p>Le technicien de maintenance des matériels agricoles ou d'espaces verts intervient en SAV (service après-vente) ou en cas de panne d'un engin. Il effectue différents tests sur la machine avant de poser un diagnostic et proposer une intervention.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7766",
      nom: "chercheur/euse en robotique médicale",
      descriptif:
        "<p>Le chercheur en robotique médicale fait évoluer les techniques pour assister les malades, les médecins ou le personnel hospitalier. Il travaille sur des projets de longue haleine mais à fort enjeu.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7767",
      nom: "spécialiste en shiatsu",
      descriptif:
        "<p>Grâce à des pressions rythmées sur le corps, le spécialiste en shiatsu aide les personnes à retrouver un équilibre physique, psychique ou émotionnel. Il travaille le plus souvent en libéral et exerce souvent en complément d'une autre activité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7768",
      nom: "couturier industriel / couturière industrielle",
      descriptif:
        "<p>Le couturier industriel effectue une ou plusieurs opérations de couture pour réaliser des vêtements, des uniformes, du linge de maison, des tissus d'ameublement, etc. Il maîtrise différentes techniques de couture sur plusieurs machines spécifiques.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_777",
      nom: "technicien pétrolier / technicienne pétrolière",
      descriptif:
        "<p>Le technicien pétrolier exerce son activité en production ou en maintenance. Dans les 2 cas, il contribue à produire du pétrole à partir d'une plateforme offshore (basée en mer ) ou onshore (à terre) . Il travaille le plus souvent à l'étranger.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7772",
      nom: "collaborateur / collaboratrice de cabinet d'expertise comptable",
      descriptif:
        "<p>Le collaborateur ou la collaboratrice de cabinet d’expertise comptable peut exercer dans quatre principaux services ou pôles : expertise, audit, social et juridique. Il ou elle intervient auprès de tous types de clients pour accompagner le développement des entreprises, analyser les chiffres, proposer des indicateurs-clés, etc.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7773",
      nom: "sylviculteur/trice",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_7776",
      nom: "poseur / poseuse de voies ferrées",
      descriptif:
        "<p>Sans son intervention, pas de circulation de train, de métro ou de tramway... Le poseur ou la poseuse de voies ferrées participe aux travaux de construction et de maintenance des voies ferrées, en toute sécurité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7777",
      nom: "électromécanicien/ne de chantier TP (travaux publics)",
      descriptif:
        "<p>Les équipements utilisés par les entreprises de travaux publics ont besoin des compétences de l'électromécanicien ou de l'électromécanicienne de chantier TP (travaux publics). Il ou elle intervient sur toutes les machines comportant des éléments électriques et mécaniques.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7778",
      nom: "préposé / préposée au tir",
      descriptif:
        "<p>Le préposé ou la préposée au tir est en charge des opérations d'amorçage, de raccordement et de tir de mines pour creuser des galeries techniques, démolir des bâtiments, effectuer des opérations de déroctage (travaux sous-marins, voies navigables).</p>\n            <p>Sur les chantiers de travaux public, il ou elle démolit des roches, creuse des tunnels, des galeries pour les réseaux, etc. Une activité explosive !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_778",
      nom: "animateur/trice 2D et 3D",
      descriptif:
        "<p>Maître du mouvement, l'animateur 2D et 3D est d'abord un artiste, spécialiste des images de synthèse. Cinéma, jeux vidéo, publicité ou site Internet, les projets d'animation ne manquent pas, et attirent de plus en plus de jeunes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_779",
      nom: "artiste de cirque",
      descriptif:
        "<p>Trapéziste, équilibriste, jongleur, clown... l'artiste de cirque imagine, conçoit et présente un numéro sur la piste d'un cirque, dans une salle de spectacle, un cabaret, une rue, un studio de télévision... Son objectif : faire rêver !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_78",
      nom: "façadier / façadière",
      descriptif:
        "<p>Le métier de façadier est né avec l'arrivée des machines à projeter les enduits. À mi-chemin entre le gros oeuvre et la finition, ce professionnel a pour mission de protéger, imperméabiliser et isoler l'extérieur de nos habitations. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_780",
      nom: "illustrateur / illustratrice",
      descriptif:
        "<p>Un bon coup de crayon et un style affirmé : tels sont les atouts de l'illustrateur qui met en images un article de presse, un conte pour enfants, un message publicitaire, un site web ou encore un jeu vidéo.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7803",
      nom: "agent/e de sécurité incendie",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_781",
      nom: "ferronnier / ferronnière d'art",
      descriptif:
        "<p>Spécialiste du fer forgé, le ferronnier d'art aménage intérieur (luminaires, tables...) et extérieur (rampes d'escaliers, grilles, portails...). Créateur, il dessine et innove constamment pour s'adapter à tous les styles d'architecture.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7812",
      nom: "maître-chien",
      descriptif:
        "<p>Le maître-chien ou la maîtresse-chien et son berger allemand ou son golden retriever... forment un tandem très efficace pour détecter des explosifs ou de la drogue, intercepter des individus dangereux, retrouver des personnes disparues ou ensevelies sous une avalanche...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7813",
      nom: "documentariste",
      descriptif:
        "<p>La ou le documentariste écrit et réalise des documentaires pour la télévision, le cinéma, la communication, parfois même pour la radio ou sous forme de podcast. Son travail commence par l'écriture et de nombreuses recherches sur son sujet.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7814",
      nom: "animateur / animatrice de site web",
      descriptif:
        "<p>L'animatrice ou l'animateur de site web lance des discussions et supervise les échanges sur le site web de son entreprise. Il ou elle répond aux questions des internautes et rédige des informations susceptibles de les intéresser.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7815",
      nom: "musicien intervenant / musicienne intervenante",
      descriptif:
        "<p>Instrumentiste et pédagogue, la musicienne intervenante ou le musicien intervenant se consacre à l'enseignement et à la transmission de la musique ou du chant dans des contextes et avec des publics variés et de tous les âges.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7817",
      nom: "éducateur/trice de chiens guides d'aveugles",
      descriptif:
        "<p>\" À droite, à gauche, stop ! \" : des mots inlassablement répétés en séance de travail. L'éducatrice ou l'éducateur de chiens guides d'aveugles doit habituer le chien (souvent un labrador) à faire ce qu'on attend de lui pour aider une personne en situation de déficience visuelle.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7818",
      nom: "agent / agente valoriste",
      descriptif:
        "<p>L'agente ou l'agent valoriste réceptionne ou collecte, évalue, nettoie, répare les objets, matériaux ou encombrants, dans le but d'une revente ou d'une réutilisation. Son rôle est polyvalent et varie selon le lieu de travail.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7819",
      nom: "assistant médical / assistante médicale",
      descriptif:
        "<p>Indispensable pour décharger les médecins de certaines tâches, l'assistante ou l'assistant médical exerce en cabinet médical ou en maison de santé. Ce professionnel de santé effectue des pré-consultations et/ou des tâches administratives.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_782",
      nom: "accompagnant éducatif et social / accompagnante éducative et sociale",
      descriptif:
        "<p>Ce professionnel aide au quotidien des enfants et des adultes handicapés ou des personnes âgées vulnérables, à domicile ou dans une structure collective. Il les accompagne dans les actes de la vie courante, et les aide à avoir une vie sociale.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7820",
      nom: "médecin urgentiste",
      descriptif:
        "<p>En première ligne aux urgences de l'hôpital ou lors d'accidents, la ou le médecin urgentiste agit rapidement pour poser un diagnostic et soulager le patient. Travaillant en équipe, ces professionnels sont très recherchés pour leur polyvalence, notamment.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7821",
      nom: "agent / agente d'opérations aériennes",
      descriptif:
        "<p>Maillon essentiel d'un vol domestique ou international, de voyageurs ou de fret, l'agente ou l'agent d'opérations aériennes prépare et suit les vols à sa charge afin d'en garantir la sécurité et la rentabilité économique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7829",
      nom: "accompagnant/e d'élèves en situation de handicap",
      descriptif:
        "Au quotidien, l'accompagnante ou l'accompagnant d'élèves en situation de handicap facilite leur accès aux activités dans les écoles, les collèges et les lycées. Il ou elle apporte son aide aux enfants ou adolescents concernés pour les apprentissages scolaires mais aussi dans la vie sociale.<br/><br/><h3>&nbsp;Après la 3e</h3>\n<p>un an pour préparer le DE AES (diplôme d'État) accompagnant éducatif et social, accessible aux personnes âgées de 18 ans au moment des épreuves finales, ou avec tout&nbsp; diplôme du secteur de l'aide à la personne. La formation comporte 60 heures à l'entrée en fonction et en formation continue par la suite.&nbsp;&nbsp;</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_783",
      nom: "éducateur sportif / éducatrice sportive",
      descriptif:
        "<p>Tennis, gymnastique, football, judo, boxe, roller, escalade ou multisports... L'éducateur sportif initie ou entraîne des publics variés (jeunes, adultes, seniors) dans une ou plusieurs disciplines en adaptant ses cours pour une bonne progression. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7830",
      nom: "<span>Adjoint / adjointe technique</span> <br> <span>des établissements d'enseignement</span>",
      descriptif:
        "Grâce à l'adjointe ou l'adjoint technique des établissements d'enseignement, les élèves sont accueillis dans de bonnes conditions pour poursuivre leur scolarité. Selon son domaine d'affectation, il ou elle assure le service de la cantine, entretient les salles de classes ou encore réalise de petites réparations avec son équipe d'agents.&nbsp;<br/><br/><h3>&nbsp;Après la 3e</h3>\n<p>&nbsp;2 ans pour un CAP, 3 ans pour un bac professionnel dont la spécialité dépend du métier visé. Les adjoints techniques sont recrutés pour leurs compétences dans différentes spécialités : agencement et revêtements, conduite de véhicules, cuisine, équipements bureautiques et audiovisuels, espaces verts et installations sportives, installations électriques, sanitaires et thermiques.&nbsp;</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7831",
      nom: "<span>Assistant / assistante d'éducation</span>",
      descriptif:
        "L'assistante ou l'assistant d'éducation (AED) encadre et surveille les élèves au sein des collèges et des lycées. Il ou elle assure la surveillance des heures de permanence, mais aussi des temps de récréation et seconde le ou la CPE (conseiller principal ou conseillère principale d'éducation).<br/><br/><h3>&nbsp;Après la 3e</h3>\n<p>3 ans pour obtenir le bac. En complément, un diplôme de l'animation, comme le BAFA (brevet d'aptitude aux fonctions d'animateur) ou le BAFD (brevet d'aptitude aux fonctions de directeur) est un atout. Les étudiants de licence, titulaires d'au moins 60 crédits ECTS, qui se destinent à l'enseignement, peuvent bénéficier d'un contrat de préprofessionnalisation d'une durée de trois ans. Il faut avoir 20 ans pour exercer en internat.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7832",
      nom: "<span>Chef / cheffe d'établissement</span> <br> <span>scolaire</span>",
      descriptif:
        "C'est le premier jour de la rentrée, la cheffe ou le chef d'établissement scolaire accueille les élèves et les réunit dans la cour pour leur présenter les grandes orientations de l'année à venir. Aux côtés des enseignants, il ou elle expose les règles à respecter au sein du collège ou du lycée et encourage les élèves au travail.<br/><br/><h3>&nbsp;Après le bac</h3>\n<p>&nbsp;5 ans pour obtenir un master MEEF (métiers de l'enseignement, de l'éducation et de la formation) suivi de 4 ans de services avant un recrutement sur concours interne ou concours de type 3<sup>è</sup> voie</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7833",
      nom: "<span>Kiosquier / kiosquière</span>",
      descriptif:
        "Les kiosquiers gèrent un point de vente de produits de presse (magazines et journaux) mais aussi de produits hors presse (cartes postales, souvenirs, boissons ou consommations à emporter, etc.) pour lesquels ils ont obtenu une autorisation de la ville dans laquelle se situe leur kiosque. Soumis à une forte amplitude horaire, 6 jours sur 7, ils font preuve d'une aisance relationnelle et commerciale et savent parfaitement organiser leur petit espace.<br/><br/><h3>&nbsp;Après la 3e</h3>\n<p>&nbsp;Il n'existe pas de diplôme spécifique pour devenir kiosquier. Le métier nécessite cependant d'avoir suivi une formation professionnelle démontrant son aptitude à gérer un kiosque à journaux. Par exemple, un bac professionnel métiers du commerce et de la vente option A animation et gestion de l'espace commercial. MédiaKiosk accompagne le réseau des kiosquiers à Paris, en Ile-de-France et en régions.&nbsp;</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7834",
      nom: "<span>Artiste graffeur professionnel /</span> <br> <span>artiste graffeuse professionnelle</span>",
      descriptif:
        "Graffitis, street art... les artistes graffeurs professionnels utilisent différents matériels (bombes aérosols, pochoirs, stickers, marqueurs...) pour donner vie à leurs créations, sur les murs des villes ou à l'intérieur d'immeubles d'habitations ou de bureaux. Ils taguent ou graffent dans un but artistique ou porteur de messages.<br/><br/><h3>&nbsp;Après le bac</h3>\n<p>&nbsp;Beaucoup d'artistes graffeurs professionnels sont des autodidactes, possédant un talent réel pour le dessin, la perspective, la 3DS... Il n'existe pas de diplôme dédié spécifiquement à l'art urbain, un diplôme d'une école d'art est envisageable. 3 ans pour préparer le DN MADE mention graphisme, ou le DNA, option communication ou design dans une école supérieure d'art ; 5 ans pour obtenir le DNSEP, option communication ou design..</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7835",
      nom: "<span>Astronaute</span>",
      descriptif:
        "Objectif : les vols dans l'espace extra-atmosphérique ! Les astronautes défient la gravité en accomplissant des missions au-delà de l'atmosphère terrestre. Membres d'équipage au sein d'un vaisseau spatial, ils réalisent des études et des expériences scientifiques, des travaux de maintenance à l'intérieur de la station spatiale, et exécutent de nombreux exercices physiques pour entretenir leur corps en apesanteur. Beaucoup d'appelés et peu d'élus pour ce métier qui nécessite de multiples compétences.<br/><br/><h3>&nbsp;Après le bac</h3>\n<p>&nbsp;5 ans pour un master scientifique ou un diplôme d'ingénieur, éventuellement poursuivi d'un doctorat en 3 ans. Une expérience professionnelle est exigée.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7836",
      nom: "<span>Influenceur / influenceuse</span>",
      descriptif:
        "Vêtements, cosmétiques, voyages... l'influenceur ou l'influenceuse donne son avis sur de nombreux produits. Il ou elle se met en scène et poste des vidéos sur les réseaux sociaux, afin d'augmenter sa notoriété et de susciter des achats.&nbsp;<br/><br/><h3>&nbsp;Après le bac</h3>\n<p>&nbsp;La plupart des influenceurs sont autodidactes. Toutefois, un cursus préparant aux métiers du marketing, de la communication ou de l'internet peut être envisagé après le bac. Par exemple, 2 ans pour un BTS communication ; 3 ans pour un BUT métiers du multimédia et de l'internet, une licence professionnelle métiers du numérique ou un bachelor d'école de commerce...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7837",
      nom: "technicien / technicienne valoriste",
      descriptif:
        "<p>Donner une seconde vie aux objets du quotidien plutôt que de les jeter, c'est la mission de la technicienne ou du technicien valoriste. Son objectif : réduire nos montagnes de déchets en réutilisant ceux qui peuvent l'être.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_784",
      nom: "accompagnateur / accompagnatrice en moyenne montagne",
      descriptif:
        "<p>Sportif accompli, l'accompagnateur en moyenne montagne organise des randonnées à pied ou en raquettes, en été comme en hiver. Il fait également découvrir à sa clientèle la faune et la flore du milieu naturel mais aussi son patrimoine culturel. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_785",
      nom: "accompagnateur / accompagnatrice de tourisme équestre",
      descriptif:
        "<p>Cavalier émérite et animateur, l'accompagnateur de tourisme (ATE) équestre transmet sa passion de la nature à des cavaliers amateurs, le temps d'une sortie, qu'elle soit de quelques heures ou de quelques jours. Il adapte le parcours au groupe et prépare les étapes avec soin.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7852",
      nom: "responsable d'un centre de compostage",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_7855",
      nom: "addictologue",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_7856",
      nom: "gérontopsychiatre",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_7857",
      nom: "pédopsychiatre",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_7858",
      nom: "pharmacien chercheur / pharmacienne chercheuse",
      descriptif:
        "<p>Le pharmacien chercheur ou la pharmacienne chercheuse, spécialiste du médicament, mène des travaux sur l’innovation et le développement de produits de santé, en particulier des médicaments ou des dispositifs médicaux.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7859",
      nom: "pharmacien hospitalier / pharmacienne hospitalière",
      descriptif:
        "<p>Au cœur du système de soins, le pharmacien hospitalier ou la pharmacienne hospitalière mène des activités variées en collaboration avec d’autres professionnels de santé, notamment des médecins, au sein des établissements de santé et médicosociaux publics et privés. Il ou elle contribue à la sécurisation de la prise en charge médicamenteuse des patients et à l’utilisation des dispositifs médicaux stériles.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_786",
      nom: "commissaire de police",
      descriptif:
        "<p>Cadre de la Police nationale, le ou la commissaire de police dirige un service ou un commissariat. Ses missions : gérer ses équipes, piloter les opérations, suivre l'action des enquêteurs... en coordination avec la politique ministérielle.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_7860",
      nom: "pharmacien/ne distributeur/trice en gros",
      descriptif:
        "<p>Le pharmacien distributeur ou la pharmacienne distributrice en gros assure un approvisionnement continu pour répondre aux besoins des patients et garantir un accès aux soins pour tous. Il ou elle travaille dans les entreprises distributrices de médicaments, de dispositifs médicaux et d’autres produits de santé auprès des pharmacies (officines) et des établissements de santé (hôpitaux, cliniques…) en France et à l’étranger.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_787",
      nom: "sage-femme",
      descriptif:
        "<p>Le ou la sage-femme assure le suivi médical des femmes enceintes tout au long de leur grossesse et durant le mois après la naissance, ainsi que celui de leur bébé. Il ou elle fait appel, si nécessaire, à des médecins spécialistes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_788",
      nom: "veilleur / veilleuse stratégique",
      descriptif:
        "<p>À la frontière de la documentation et du renseignement, le veilleur stratégique informe les décideurs d'une entreprise sur l'évolution de leur environnement économique. Objectif : les éclairer dans le choix de leurs décisions.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_789",
      nom: "intégrateur / intégratrice web",
      descriptif:
        "<p>Travaillant dans une agence web, en indépendant ou dans une ESN (entreprise de services du numérique), l'intégrateur web assemble différents éléments (textes, images, sons, vidéos, animations) en vue de la construction d'un site Internet.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_79",
      nom: "ingénieur aromaticien / ingénieure aromaticienne",
      descriptif:
        "<p>L'ingénieur aromaticien crée, reproduit ou adapte les saveurs de nos produits alimentaires, qu'ils soient salés ou sucrés. Formé aux techniques modernes de la chimie, c'est un familier des molécules d'origine chimique ou naturelle... et de leurs dosages. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_790",
      nom: "agent hydrothermal / agente hydrothermale",
      descriptif:
        "<p>Se situant à mi-chemin entre l'esthéticien et l'aide-soignant, l'agent hydrothermal utilise les techniques et les vertus de l'eau (douches au jet, bains aux algues, hydromassages...) pour rééduquer, embellir ou détendre les clients.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_791",
      nom: "chef / cheffe de projet démantèlement nucléaire",
      descriptif:
        "<p>De l'arrêt d'une centrale au processus d'assainissement (nettoyage) et d'évacuation des déchets nucléaires, en passant par le démontage du bâtiment réacteur et la destruction de toutes les charges explosives du site, la cheffe ou le chef de projet démantèlement nucléaire organise la déconstruction d'une installation.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_792",
      nom: "administrateur / administratrice judiciaire",
      descriptif:
        "<p>Une entreprise est dans l'impossibilité de payer ses dettes ? L'administrateur judiciaire, nommé par le tribunal, la prend sous sa responsabilité et s'efforce de la sauver. Cet auxiliaire de justice est un spécialiste du droit et de la gestion.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_793",
      nom: "agent/e arboricole",
      descriptif:
        "<p>Spécialiste des arbres fruitiers, l'agent arboricole plante, taille, met en forme, protège, entretient et procède aux différentes récoltes, Un métier pour les passionnés de nature et de plein air.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_794",
      nom: "vendeur / vendeuse en animalerie",
      descriptif:
        "<p>Le vendeur en animalerie est là pour conseiller au client l'animal de compagnie qui s'adaptera le mieux à son mode de vie et à sa personnalité. Il doit veiller également à la bonne tenue de son « rayon » et surtout s'occuper des animaux !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_795",
      nom: "domoticien / domoticienne",
      descriptif:
        '<p>Éteindre une chaudière via son smartphone, allumer une lampe à la voix, déclencher à distance l\'arrosage automatique du jardin... le domoticien installe des équipements de domotique ("domus" : maison en latin + robotique) rendant nos habitats plus intelligents, plus écologiques et plus confortables.</p>',
      urls: [],
      formations: [],
    },
    {
      id: "MET_796",
      nom: "pilote de ligne automatisée (chimie - agroalimentaire - industrie pharmaceutique)",
      descriptif:
        "<p>Sur la ligne de fabrication, les matières premières se séparent ou se mélangent, les biscuits ou les comprimés sont rangés puis conditionnés. Le pilote de ligne automatisée contrôle les différentes étapes du processus.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_797",
      nom: "assistant / assistante de gestion en PME",
      descriptif:
        "<p>Tour à tour comptable, commercial ou secrétaire, l'assistant de gestion est le pivot central des petites entreprises. À la fois alerte et soutien du chef d'entreprise, ce professionnel polyvalent ne connaît pas la routine. Un métier très formateur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_798",
      nom: "assistant / assistante en ressources humaines",
      descriptif:
        "<p>Porte d'entrée idéale du secteur, le métier d'assistant en RH (ressources humaines) permet d'en aborder tous les aspects. Fort de son expérience, l'assistant peut prendre des responsabilités, ou évoluer vers le recrutement ou la formation, par exemple.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_799",
      nom: "hot liner",
      descriptif:
        "<p>« Allô ! Aucune feuille ne sort de mon imprimante. Que dois-je faire ? » Au bout du fil, le hot liner écoute, attentif. Cet expert en pannes informatiques guide à distance les manipulations des utilisateurs en détresse pour résoudre leurs problèmes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_8",
      nom: "chef / cheffe de publicité",
      descriptif:
        "<p>Commercial en régie publicitaire, conseiller en communication ou en agence de publicité, le métier n'est pas le même. En régie, le chef de publicité vend des espaces publicitaires pour les supports qu'il représente. En agence, il gère un budget de communication.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_80",
      nom: "formulateur / formulatrice",
      descriptif:
        "<p>Un shampooing qui donne du volume aux cheveux, un fond de teint \" 2 en 1 \" avec effet hydratant renforcé... toutes ces créations sont l'oeuvre du formulateur, scientifique doté de créativité et de qualités sensorielles, qui maîtrise l'art des mélanges.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_800",
      nom: "chercheur / chercheuse en physique",
      descriptif:
        "<p>Trouver de nouvelles voies à explorer, observer, formuler des hypothèses... La vie du chercheur est une quête permanente, jalonnée d'avancées et de doutes. Un « métier passion » pour des scientifiques de haut vol.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_802",
      nom: "chef / cheffe de projet communication digitale",
      descriptif:
        "<p>Spécialiste de la communication en ligne, le chef de projet communication digitale intègre les usages d'Internet, des réseaux sociaux, applications mobiles et du multimédia dans la stratégie des relations publiques d'une marque ou d'une entreprise. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_803",
      nom: "animateur / animatrice de tourisme",
      descriptif:
        "<p>L'animatrice ou l'animateur de tourisme garantit l'ambiance et la participation du plus grand nombre aux activités qu'il ou elle organise pour les vacanciers, notamment dans les lieux de villégiatures, en France ou à l'étranger.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_804",
      nom: "tonnelier / tonnelière",
      descriptif:
        "<p>Le tonnelier fabrique ou répare les fûts en bois de chêne, qui servent à l'élevage des vins et eaux-de-vie. Partenaire du vigneron, le tonnelier joue un rôle important dans l'élaboration des arômes des alcools. Il est garant d'un savoir-faire ancestral.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_805",
      nom: "juge d'instruction",
      descriptif:
        "<p>Après enquête, le juge d'instruction analyse les éléments qui peuvent accuser ou innocenter un suspect. À lui d'estimer si les preuves sont suffisantes pour le juger.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_806",
      nom: "juriste droit de l'environnement",
      descriptif:
        "<p>Nouvelles normes de qualité, dépollution des sols, mise en conformité des constructions... Les entreprises font de plus en plus souvent appel aux compétences du juriste en droit de l'environnement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_807",
      nom: "directeur / directrice des services pénitentiaires",
      descriptif:
        "<p>Garant de la sécurité et de l'application des peines prononcées à l'encontre des détenus, le directeur des services pénitentiaires est à la fois un gestionnaire en charge d'un établissement et un manager sachant encadrer le personnel pénitentiaire.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_808",
      nom: "animateur / animatrice d'activités physiques et sportives pour tous",
      descriptif:
        "<p>L'animateur d'activités physiques et sportives pour tous encadre des activités de découverte, de développement ou de maintien des capacités physiques pour des publics allant de la petite enfance aux seniors. Son action est porteuse de valeurs et de lien.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_809",
      nom: "ingénieur/e en photonique",
      descriptif:
        "<p>Science et technologie de la lumière, la photonique associe la physique, l'optique et l'électronique. De la conception au service commercial, en passant par la production, l'ingénieur en photonique est présent à toutes les étapes de l'élaboration d'un produit à haute valeur ajoutée technologique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_81",
      nom: "auditeur / auditrice interne",
      descriptif:
        "<p>Comme un médecin généraliste, l'auditeur interne examine son entreprise, ses filiales et ses services sous tous les angles : commercial, comptable, fiscal, informatique... Son but : améliorer le fonctionnement et la productivité de sa société.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_810",
      nom: "démineur/euse",
      descriptif:
        "Son expertise évite des drames et sauve des vies. Le démineur ou la démineuse est capable de désamorcer une bombe susceptible d'exploser. Sang-froid, extrême concentration et précision du geste sont requis pour ces militaires de carrière ou ces policiers et policières de la sécurité civile.<br/><br/><h3>Après la 3<sup>e</sup></h3>\n<p>La spécialisation est accessible aux militaires.</p>\n<h3>Après le bac</h3>\n<p>Être gardien de la paix depuis 2 ans avant de postuler. Accès sur tests (physiques et psychotechniques) et sur entretien, puis formation interne.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_812",
      nom: "biologiste médical/e",
      descriptif:
        "<p>Au sein d’un laboratoire de biologie médicale privé ou hospitalier, le biologiste médical réalise, analyse et interprète des prélèvements qui serviront ensuite au prescripteur (médecin, dentiste, sage-femme) à poser un diagnostic, à surveiller un traitement ou à prévenir des maladies.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_813",
      nom: "roboticien/ne",
      descriptif:
        "<p>Spécialiste des systèmes automatisés, le roboticien, toujours à la pointe du progrès, crée des robots, plus ou moins autonomes, qui effectueront différentes tâches, en fonction des besoins des utilisateurs.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_814",
      nom: "web-ergonome",
      descriptif:
        "<p>La tâche de cet ergonome \" dernier clic \" consiste à faire du site web un espace agréable et clair pour tout navigateur, expert ou novice. Il doit savoir attirer l'oeil de l'internaute et faciliter sa recherche Internet. Un vrai architecte d'interface !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_815",
      nom: "gestionnaire actif / passif",
      descriptif:
        "<p>Relativement nouveau dans le domaine de l'assurance, le gestionnaire actif/passif met à disposition de sa direction toutes les informations permettant l'évaluation des risques et des opportunités financières permettant d'améliorer les performances.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_816",
      nom: "administrateur / administratrice de mission humanitaire",
      descriptif:
        "<p>C'est sur lui que repose l'équilibre financier d'une mission. Dans l'humanitaire comme ailleurs, l'administrateur tient les cordons de la bourse. À Port-au-Prince ou Islamabad, il s'adapte sans cesse au contexte, toujours difficile !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_817",
      nom: "enquêteur privé / enquêtrice privée",
      descriptif:
        "<p>Contrefaçons, fraude aux assurances... Loin de se limiter aux affaires familiales, les investigations de l'enquêteur privé ou de l'enquêtrice privée concernent principalement le monde économique et industriel. Toujours dans le strict respect du droit.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_818",
      nom: "mandataire judiciaire",
      descriptif:
        "<p>Le mandataire judiciaire représente les créanciers (salariés, Trésor public, fournisseurs, banques...) d'une entreprise placée en redressement judiciaire. En cas de liquidation de celle-ci, il est chargé de vendre les biens, de rembourser les dettes et de prononcer l'arrêt total de l'activité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_819",
      nom: "secrétaire juridique",
      descriptif:
        "<p>À l'aise aussi bien avec la bureautique qu'avec le vocabulaire juridique, le secrétaire juridique à la double compétence assiste les avocats, les huissiers, les notaires ou les juristes d'entreprise.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_82",
      nom: "juriste d'entreprise",
      descriptif:
        "<p>Protéger les intérêts de sa société sur les plans commercial, fiscal, social..., telle est la mission du juriste d'entreprise. Cela, qu'il soit généraliste ou spécialisé dans un domaine comme les contrats ou les contentieux.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_820",
      nom: "chercheur/euse en biologie du sport",
      descriptif:
        "<p>Le chercheur en biologie du sport est un scientifique de haut niveau qui étudie le corps humain. Spécialisé dans un domaine (physiologie, traumatologie, biomécanique), il s'intéresse à la thématique du sport et contribue aux progrès de la science. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_821",
      nom: "ingénieur/e réservoir",
      descriptif:
        "<p>Dans un souci constant de rentabilité, l'ingénieur réservoir oriente tout son savoir vers l'optimisation de l'exploitation du pétrole. C'est un personnage-clé dans l'industrie pétrolière, depuis la prévision jusqu'au suivi de la production.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_822",
      nom: "responsable de réseau d'assainissement",
      descriptif:
        "<p>De la maison jusqu'à la station d'épuration, tout un réseau d'égouts évacue nos eaux usées. Le ou la responsable de réseau d'assainissement supervise et contrôle la gestion et l'exploitation de ces canalisations.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_823",
      nom: "concepteur / conceptrice de niveaux de jeu web",
      descriptif:
        "<p>Au sein d'un univers donné, construire un parcours et ajuster la difficulté pour passer au niveau supérieur tout en se creusant les méninges et en s'amusant : telles sont les missions du concepteur de niveaux de jeu web.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_824",
      nom: "neurobiologiste",
      descriptif:
        "<p>Comprendre le fonctionnement de notre cerveau, c'est le défi du neurobiologiste ! Ce scientifique cherche à rendre intelligibles les interactions des neurones, les cellules qui composent notre cerveau, et leurs milliards de connexions électriques.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_825",
      nom: "consultant/e en validation",
      descriptif:
        "<p> Le consultant en validation est un expert en qualité. Il s'assure que l'entreprise dans laquelle il intervient respecte les normes en vigueur et l'aide à mettre en place des méthodes fiables pour obtenir la commercialisation de ses produits. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_826",
      nom: "ingénieur/e technico-commercial en chimie",
      descriptif:
        "<p> Polymères, produits de traitement des surfaces, matières premières colorantes et ingrédients cosmétiques... tous ces produits sont vendus par l'ingénieur technico-commercial en chimie. Doué pour la négociation, son objectif est de faire du chiffre. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_827",
      nom: "coordonnateur/trice d'études cliniques",
      descriptif:
        "<p> Avant sa mise sur le marché, un nouveau médicament est toujours testé sur des personnes volontaires. Le coordonnateur d'études cliniques supervise le travail des attachés de recherche clinique (ARC) responsables de ces tests. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_828",
      nom: "rédacteur médical / rédactrice médicale",
      descriptif:
        "<p> Rédiger un rapport d'étude clinique, une demande d'autorisation, une plaquette d'information... c'est le quotidien du rédacteur médical. Dans l'industrie pharmaceutique, il exerce un métier de communication exigeant un solide bagage scientifique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_829",
      nom: "glaciologue",
      descriptif:
        "<p>Parcourir le monde pour prélever des échantillons de glace pour en étudier la composition ou la structure en laboratoire : telle est la mission du ou de la glaciologue. Avec la montagne pour passion, il ou elle partage son temps entre le terrain et le laboratoire.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_83",
      nom: "technicien / technicienne de maintenance en génie climatique",
      descriptif:
        "<p>Le technicien de maintenance en génie climatique surveille le fonctionnement des équipements techniques et assure leur entretien et leur dépannage. Toujours disponible, il apporte une solution rapide et efficace aux clients victimes d'une panne.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_830",
      nom: "responsable des produits structurés actions",
      descriptif:
        "<p>Expert des marchés financiers, de l'analyse financière et de l'évaluation des risques, le responsable des produits structurés actions conçoit des produits d'investissement sur mesure, dont il définit aussi la stratégie de marketing et de vente.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_831",
      nom: "inséminateur / inséminatrice",
      descriptif:
        "<p>Sélectionner les meilleurs reproducteurs, effectuer les inséminations, s'assurer du succès de l'opération... telle est la tâche de l'inséminateur, technicien spécialiste de la génétique et précieux conseiller de l'éleveur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_832",
      nom: "pisteur / pisteuse secouriste",
      descriptif:
        "<p>Dans une station de sports d'hiver, la pisteuse ou le pisteur secouriste veille à la sécurité des skieurs. Dès l'ouverture des pistes, il ou elle prévient les risques, signale les dangers et porte secours aux personnes en difficulté.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_837",
      nom: "<span>Gardien / gardienne d'immeubles</span>",
      descriptif:
        "Au service des résidents d'un immeuble, chargé de maintenir les lieux propres et sécurisés, la gardienne ou le gardien d'immeuble est la personne référente sur laquelle on peut toujours compter, y compris pour récupérer un colis ou appeler un serrurier.<br/><br/><h3>Après la 3<sup>e</sup></h3>\n<p>2 ans pour préparer le CAP gardien d'immeubles ou agent de sécurité ;&nbsp; Des diplômes appréciés, mais non exigés. À noter : le certificat d'agent polyvalent pour la gérance de logements sociaux, préparé en formation continue.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_839",
      nom: "moniteur/trice de tennis",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_84",
      nom: "testeur / testeuse en informatique",
      descriptif:
        "<p>Le testeur est le spécialiste de la chasse aux bugs, ces erreurs qui empêchent le bon fonctionnement d'un logiciel. À lui de les signaler au service développement. Plus qu'une passion, c'est un métier à part entière.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_840",
      nom: "directeur/trice des ventes",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_842",
      nom: "responsable du soutien logistique intégré",
      descriptif:
        "<p>Le responsable du soutien logistique intégré intervient dès la phase de conception d'un produit ou d'un système électronique. Son rôle est de prévoir son entretien, les moyens de le réparer, ainsi que les formations pour les futurs utilisateurs.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_843",
      nom: "ingénieur / ingénieure production dans les biotechnologies",
      descriptif:
        "<p>Objectifs : qualité et rendement ! L'ingénieur production dans les biotechnologies coordonne un travail d'équipe pour livrer des produits (vaccins, extraits d'algues, biocarburants...) conformes au plan de production qu'il a lui-même établi.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_844",
      nom: "ingénieur / ingénieure de recherche clinique et épidémiologique",
      descriptif:
        "<p>L'ingénieur de recherche clinique et épidémiologique étudie les maladies. Il élabore, coordonne et analyse des enquêtes épidémiologiques et des essais cliniques. Ses recherches sont utiles pour mettre au point de nouveaux traitements.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_845",
      nom: "gynécologue-obstétricien/ne",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_85",
      nom: "professeur/e de maths ou de physique-chimie",
      descriptif:
        "<p>En collège ou en lycée, le professeur de mathématiques ou de physique-chimie s'ingénie à transmettre son plaisir à étudier sa discipline. À raison de 30 élèves par classe, la tâche demande une bonne résistance physique et nerveuse.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_850",
      nom: "peintre aéronautique",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_854",
      nom: "responsable e-commerce",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_857",
      nom: "vendeur/euse spécialisé/e en bricolage",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_858",
      nom: "chef / cheffe de projet packaging",
      descriptif:
        "<p>Le chef de projet packaging prend en charge un projet d'emballage, depuis la demande du client jusqu'à la livraison. Il suit chaque étape et sert de référent à l'ensemble des interlocuteurs. Il veille au respect du cahier des charges, du budget, du planning.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_859",
      nom: "facteur / factrice d'instruments",
      descriptif:
        "<p>Le facteur d'instruments fabrique, restaure ou accorde des pianos, des instruments à cordes ou à vent, des orgues, etc. Dans la tradition de l'artisanat d'art, ce passionné de musique allie savoirs ancestraux et techniques de pointe.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_86",
      nom: "metteur / metteuse en scène",
      descriptif:
        "<p>S'approprier une oeuvre et la traduire sur scène, telle est l'ambition du metteur en scène. Grâce à ses directives avisées, textes, décors, costumes, éclairages, musique et jeu des acteurs s'assemblent en un tout artistique cohérent : un spectacle !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_860",
      nom: "vendeur / vendeuse en magasin",
      descriptif:
        "<p>En magasin spécialisé, en grande surface, dans une petite boutique... face à une clientèle informée et exigeante, les vendeurs doivent argumenter et adapter leur discours en fonction des besoins de chacun pour vendre les produits.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_861",
      nom: "actuaire",
      descriptif:
        "<p>Chargé de concevoir et/ou de modifier les contrats d'assurance, ou de mesurer les risques encourus par sa société, l'actuaire se livre à de savants calculs avec un objectif triple : maîtriser l'aléatoire, minimiser les pertes financières et dégager des bénéfices.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_862",
      nom: "technicien / technicienne en lignes haute tension",
      descriptif:
        "<p>Les autoroutes de l'énergie transportent l'électricité à 400 000 volts depuis son lieu de production jusqu'aux grandes agglomérations. Le technicien en lignes haute tension contrôle ces réseaux afin d'éviter une baisse de régime ou une coupure de courant.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_863",
      nom: "ingénieur électronicien / ingénieure électronicienne",
      descriptif:
        "<p>Informatique, télécommunications, aéronautique, automobile... sans l'ingénieur électronicien, certaines innovations technologiques n'auraient pas vu le jour. L'électronique est partout et l'innovation, un de ses leviers de croissance, est indispensable.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_864",
      nom: "architecte naval / navale",
      descriptif:
        "<p>L'architecte naval imagine la coque, le pont ou la voilure d'un navire. Il effectue les calculs nécessaires à sa bonne marche, comme la flottabilité ou la résistance des matériaux. Un travail passionnant, sachant que les places sont extrêmement rares.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_865",
      nom: "contrôleur / contrôleuse de performance",
      descriptif:
        "<p>Une appellation, deux professions : le contrôleur laitier et le contrôleur de croissance. Tandis que le premier analyse la qualité du lait, le second surveille la prise de poids du bétail destiné à la boucherie. Un appui technique indispensable pour les éleveurs.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_866",
      nom: "technicien / technicienne de maintenance industrielle",
      descriptif:
        "<p>Machines à papier, à onduler, à imprimer... le technicien de maintenance industrielle travaille sur des machines de production sous contrôle permanent. Sa devise : plutôt prévenir que guérir. Il met tout en oeuvre pour éviter la panne en suivant les évolutions de données sensibles.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_867",
      nom: "gestionnaire de contrats d'assurance",
      descriptif:
        "<p>Vol, incendie, accidents... Le gestionnaire de contrats d'assurances est l'interlocuteur privilégié des assurés, qu'il accompagne de l'établissement du contrat jusqu'à la réparation du dommage. Il intervient aussi pour indemniser en cas de sinistre.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_868",
      nom: "préparateur / préparatrice en pharmacie",
      descriptif:
        "<p>Il n'est ni pharmacien ni médecin. Et pourtant, il connaît les médicaments et leur usage sur le bout des doigts ! C'est le préparateur en pharmacie, chargé de la gestion des stocks et de la vente.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_869",
      nom: "médecin généraliste",
      descriptif:
        "<p>Qu'il traite une grippe ou prescrive un examen, le généraliste prend en charge ses patients dans leur globalité (habitudes, hygiène de vie, antécédents) et les suit le plus souvent sur une longue période. Médecin traitant, il assure la coordination avec ses confrères spécialistes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_870",
      nom: "chirurgien-dentiste / chirurgienne-dentiste",
      descriptif:
        "<p>Du détartrage à la pose d'implants, en passant par l'extraction d'une dent de sagesse, le chirurgien-dentiste fait jouer sa dextérité, doublée de la maîtrise de techniques de soins complexes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_871",
      nom: "psychologue",
      descriptif:
        "<p>Qu'il s'agisse d'accompagner des malades, de dépister des troubles du comportement ou de recruter du personnel..., le psychologue écoute, observe, évalue et conseille.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_872",
      nom: "huissier / huissière de justice",
      descriptif:
        "<p>Délivrer une convocation à un particulier, faire exécuter une décision de justice, établir des constats... autant d'activités exercées par l'huissier de justice. Tous les actes de ce professionnel ont une valeur officielle.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_873",
      nom: "technicien thermicien / technicienne thermicienne",
      descriptif:
        "<p>Le technicien thermicien est un spécialiste des installations de chauffage, de production d'énergie et de climatisation. Il contrôle, régule, installe et assure la maintenance d'équipements chez les clients en respectant les normes environnementales.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_874",
      nom: "souffleur / souffleuse de verre",
      descriptif:
        "<p>À partir de pâte de verre en fusion, le souffleur de verre donne naissance aux objets les plus somptueux (verres, vases...). Son secret : une main en or et une canne en acier, le tout dans un environnement extrêmement chaud.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_875",
      nom: "pédicure-podologue",
      descriptif:
        "<p>Le pédicure-podologue est spécialisé dans l'étude et le traitement des affections du pied et des ongles, à l'exclusion de toute intervention chirurgicale (sans effusion de sang).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_876",
      nom: "psychomotricien / psychomotricienne",
      descriptif:
        "<p>Avoir le corps et l'esprit en harmonie pour dépasser les difficultés à la suite d'un accident ou d'une maladie, tel est l'objectif du psychomotricien. Il utilise le jeu et d'autres médiations pour rééduquer, notamment les troubles du geste, les problèmes d'orientation dans le temps ou dans l'espace, ou parvenir à mieux réguler ses émotions et s'adapter à son environnement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_877",
      nom: "orthoptiste",
      descriptif:
        "<p>Spécialiste du dépistage, de la rééducation et de la réadaptation oculaires, l'orthoptiste intervient notamment à la demande d'un ophtalmologiste pour mesurer le champ visuel ou déceler des strabismes.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_878",
      nom: "chargé / chargée HSE (hygiène sécurité environnement)",
      descriptif:
        "<p>Le chargé ou la chargé HSE (hygiène sécurité environnement) participe à la définition de la politique de qualité et de sécurité des entreprises (personnels, matériels, conditions de travail, respect de l'environnement) et la met en oeuvre. Il ou elle assure la prévention des risques et accident qui pourraient survenir au sein d'une entreprise et remettre en cause la sécurité des salariés.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_879",
      nom: "attaché / attachée de presse",
      descriptif:
        "<p>Un article dans la presse écrite, un sujet de 1 min 30 au journal télévisé... voilà de quoi satisfaire tout attaché de presse ! Faire en sorte que les médias parlent de l'entreprise qu'il représente, tel est son pari au quotidien.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_88",
      nom: "chocolatier/ère-confiseur/euse",
      descriptif:
        "<p>Spécialiste de la fabrication de douceurs chocolatées ou à base de sucre, le chocolatier-confiseur, souvent artisan, est un artiste qui soigne la présentation de ses créations. À son compte ou dans l'industrie, il est reconnu pour son savoir-faire.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_880",
      nom: "enseignant-chercheur / enseignante-chercheuse",
      descriptif:
        "<p>Double mission pour l'enseignant-chercheur au sein d'une université ou d'une grande école : faire progresser la recherche dans sa discipline et transmettre les connaissances qui en sont issues à ses étudiants.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_881",
      nom: "dépanneur / dépanneuse en électroménager",
      descriptif:
        '<p>Le dépanneur en électroménager répare les produits dits " blancs " : lave-linge, lave-vaisselle, four, réfrigérateur... en intervenant aussi bien dans les lieux d\'hébergement et de restauration, les hôpitaux... que dans les entreprises pour la maintenance de leurs équipements (distributeurs, matériels de cuisson...) ou les particuliers.</p>',
      urls: [],
      formations: [],
    },
    {
      id: "MET_882",
      nom: "ingénieur / ingénieure recherche et développement en agroalimentaire",
      descriptif:
        "<p>Nouveaux produits ou emballages, nouvelles procédures de fabrication... l'ingénieur recherche et développement (R&amp;D) en agroalimentaire n'a qu'un mot d'ordre : innover ! Une condition indispensable pour les entreprises d'un secteur très concurrentiel.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_883",
      nom: "photographe",
      descriptif:
        "<p>Faire des prises de vue, tel est l'objectif de tout photographe. Pourtant, les emplois se situent surtout dans le tirage et la vente, sous des statuts variés, et les lieux d'exercice sont très divers : laboratoire, studio, extérieur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_884",
      nom: "ergonome",
      descriptif:
        "<p>L'ergonome conçoit et améliore des lieux de vie, des objets ou des postes de travail afin de les adapter au maximum aux besoins des utilisateurs, en termes de confort, sécurité et efficacité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_885",
      nom: "concepteur/trice de jeux vidéo",
      descriptif:
        "<p>Qui seront les ennemis du héros ? Par quel artifice pourra-t-il s'échapper de sa prison ? Comment le soumettre à des épreuves de plus en plus difficiles ? Autant de questions que se pose le concepteur de jeux vidéo.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_886",
      nom: "expert / experte automobile",
      descriptif:
        "<p>Auto, moto, camion, bus, tracteur, moissonneuse, voire bateau... l'expert automobile est un généraliste du véhicule dont il connaît parfaitement la structure. Ses compétences techniques lui permettent d'en évaluer précisément l'état, d'en déceler les défauts, d'en apprécier la valeur et d'évaluer le coût de remise en état.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_887",
      nom: "ingénieur / ingénieure télécoms et réseaux",
      descriptif:
        "<p>L'ingénieur télécoms et réseaux participe à l'optimisation des systèmes de communication, de la recherche à la conception d'équipements et de services en passant par la gestion d'infrastructures réseaux.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_889",
      nom: "matelot / matelote de la marine nationale",
      descriptif:
        "<p>Fusiliers marins, cuisiniers, mécaniciens... Les matelots occupent diverses fonctions au sein des équipages de la marine nationale, à bord de bateaux ou sur les bases navales. Le matelot ou la matelote est avant tout un soldat ou une soldate, qui a signé un contrat avec la marine.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_89",
      nom: "médiateur / médiatrice familiale",
      descriptif:
        "<p>Le médiateur familial est le spécialiste du conflit familial. Il intervient principalement dans des situations de séparation. Son rôle : trouver des solutions répondant aux besoins de chacun des membres de la famille.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_890",
      nom: "professeur-documentaliste / professeure-documentaliste",
      descriptif:
        "<p>Au sein du centre de documentation et d'information (CDI), le professeur-documentaliste apprend aux collégiens et aux lycéens à construire un rapport critique face à l'information (pertinence, qualité, fiabilité), tout en les initiant aux techniques de recherche documentaire. Il constitue et fait également évoluer le fonds documentaire de l'établissement, organise les ressources papier et numériques qu'il met à disposition des élèves et de la communauté éducative. Il gère aussi certains événements : rencontre avec des artistes, visonnage de films, mise en place d'une exposition, visite de musée...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_891",
      nom: "secrétaire administratif/ve",
      descriptif:
        "Rédaction, communication, comptabilité, encadrement et gestion du personnel : la palette des attributions d'un/e secrétaire administratif/ve est très large. Elle dépend beaucoup de son lieu d'affectation : commune, université, ministère...<br/><br/><h3>Après la 3<sup>e</sup></h3>\n<p>Bac ou certificat de fin d'études secondaires (CFES) ; certificat de fins d'études technologiques ou professionnnelles secondaires (CFETS, CFEPS) ; capacité en droit, pour passer le concours de secrétaire administratif ou de secrétaire d'administration scolaire et universitaire (catégorie B). Aucune condition d'âge.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_892",
      nom: "technicien/ne de police technique et scientifique",
      descriptif:
        "Empreintes génétiques, études de faux documents, examens de peintures, d'explosifs, d'armes... La technicienne ou le technicien de police technique et scientifique cherche à identifier les auteurs d'infractions. Il ou elle contribue à la lutte contre le terrorisme, le crime organisé ou la délinquance.<br/><br/><h3>Après le bac</h3>\n<p>Être titulaire d'un bac, réussir le concours de technicien de police technique et scientifique puis suivre une formation rémunérée dans une structure de police nationale. Le concours de technicien principal est accessible après un diplôme de niveau bac&nbsp;+&nbsp;2.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_893",
      nom: "agent / agente de développement des énergies renouvelables",
      descriptif:
        "<p>Intégrer la dimension énergétique dans les politiques locales : c'est la mission de l'agent de développement des énergies renouvelables, à la fois animateur, technicien et gestionnaire. Gros plan sur ce métier, au service de la ville et au coeur de l'enjeu environnemental.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_894",
      nom: "ingénieur / ingénieure en énergie solaire",
      descriptif:
        "<p>De l'abri solaire qui recharge un véhicule électrique à la centrale qui produit de l'électricité, l'ingénieur en énergie solaire conçoit et pilote des projets au coeur de l'énergie dite verte.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_895",
      nom: "technicien / technicienne d'intervention clientèle gaz",
      descriptif:
        "<p>Le technicien d'intervention clientèle gaz entretient et répare les installations d'alimentation en gaz chez des clients. Son activité repose sur 3 missions essentielles : les interventions techniques, la maintenance et le conseil.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_896",
      nom: "technicien / technicienne de la circulation ferroviaire SNCF",
      descriptif:
        "<p>Le technicien de la circulation ferroviaire SNCF, prépare, organise et gère l'incessante circulation des trains dans une zone géographique donnée, depuis le poste d'aiguillage ou le centre de régulation du trafic.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_897",
      nom: "modiste",
      descriptif:
        "<p>Le modiste imagine et réalise sur mesure, à l'unité ou en petites séries, des chapeaux et accessoires de tête, qu'il vend ensuite dans sa boutique à une clientèle de particuliers ou aux professionnels du monde du spectacle ou de la mode.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_898",
      nom: "chargé / chargée d'études ressources humaines",
      descriptif:
        "<p>Le chargé d'études ressources humaines (RH) harmonise les pratiques RH au sein d'un groupe : il pointe les dysfonctionnements et propose des solutions. Ce professionnel est souvent un jeune diplômé qui fait ses premiers pas dans les RH.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_899",
      nom: "soudeur / soudeuse",
      descriptif:
        "<p>Le soudeur assemble, par fusion ou par apport de métal, les différents éléments qui composent un chauffe-eau, un avion, un pont de plate-forme...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_9",
      nom: "charpentier / charpentière bois",
      descriptif:
        "<p>Le charpentier conçoit, fabrique et pose des charpentes et ossatures en bois qui servent de structures à des maisons et autres constructions. C'est un métier à forte tradition qui se modernise et recrute des jeunes qualifiés.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_90",
      nom: "aquaculteur / aquacultrice",
      descriptif:
        "<p>L'aquaculteur est un agriculteur d'un genre un peu particulier puisque sa production est immergée (dans la mer, dans un étang ou dans un bassin). Élevant des poissons, des crustacés ou des fruits de mer, il veille à leur reproduction et s'occupe de leur commercialisation.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_900",
      nom: "élagueur / élagueuse",
      descriptif:
        "<p>L'élagueur travaille en hauteur, dans les arbres, à bord d'une nacelle ou arnaché avec un harnais. Il taille, souvent à la tronçonneuse électrique, des branches mortes ou malades, en binôme avec un collègue au sol, dans le respect des règles de sécurité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_901",
      nom: "rédacteur / rédactrice en chef",
      descriptif:
        "<p>Presse, radio, télévision, Web... tous les médias ont besoin d'un rédacteur en chef qui donne le ton et définit les contenus. Ce professionnel de l'information anime l'équipe de journalistes de sa rédaction et développe l'image du support pour lequel il travaille.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_902",
      nom: "BIM manager",
      descriptif:
        "<p>La modélisation des données a bouleversé le secteur du BTP (bâtiment et travaux publics). Le ou la BIM manager est la tête de pont de ce mouvement visant à optimiser les échanges entre les professionnels, limiter les erreurs et donc gagner en temps et en qualité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_903",
      nom: "ouvrier / ouvrière agricole",
      descriptif:
        "<p>Bras droit de l'agriculteur, l'ouvrier agricole participe à tous les travaux d'une exploitation. C'est une occupation pointue et un bon marchepied pour s'installer ensuite à son compte.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_904",
      nom: "constructeur / constructrice de routes et d'aménagements urbains",
      descriptif:
        "<p>Le constructeur ou la constructrice de routes et d'aménagements urbains construit, entretient et modernise les infrastructures de circulation (routes, rues, voies piétonnes, pistes cyclables, équipements publics et mobiliers urbains, végétalisation des espaces urbains, aires de stationnement, parkings, plateformes logistiques, installations de loisirs et d'équipements sportifs, ralentisseurs, aérodromes, pistes d'aéroport, etc.).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_905",
      nom: "soigneur / soigneuse d'animaux",
      descriptif:
        "<p>S'occuper des lions, des éléphants, des dauphins : le rêve de nombreux enfants, mais le métier d'une petite poignée d'adultes. Au quotidien, le soigneur est au service d'animaux exotiques ou familiers, sauvages ou apprivoisés.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_906",
      nom: "responsable de la collecte des déchets ménagers",
      descriptif:
        "<p>À la tête d'une équipe d'agents de propreté urbaine, le ou la responsable de la collecte des déchets ménagers coordonne les opérations de ramassage et de tri dans sa municipalité. Son rôle : orchestrer la ronde matinale des bennes d'enlèvement des ordures.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_907",
      nom: "conducteur / conductrice de bus ou d'autocar",
      descriptif:
        "<p>Sécurité, ponctualité et qualité de service sont les préoccupations premières du conducteur de bus. Cet amoureux de la conduite n'assure pas seulement le transport : il accueille, informe et conseille les usagers.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_908",
      nom: "hydraulicien/ne",
      descriptif:
        "<p>Spécialiste de la mécanique des fluides, l'hydraulicien ou l'hydraulicienne intervient dans la conception ou la gestion de centrales hydroélectriques, de systèmes d'assainissement ou de réseaux d'irrigation et d'alimentation en eau potable.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_909",
      nom: "conseiller / conseillère en assurances",
      descriptif:
        "<p>Il gère un portefeuille de clients, auxquels il propose et vend des contrats d'assurances (habitation, automobile, épargne...). Prospecter une nouvelle clientèle sur une zone géographique précise, et la fidéliser font aussi partie de ses missions.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_91",
      nom: "juge des contentieux de la protection",
      descriptif:
        "<p>Travaux mal exécutés, loyers impayés, surendettement... Le juge des contentieux de la protection, anciennement appelé juge d'instance, juge les litiges les quotidien. Si cette profession attire de nombreux candidats, elle reste difficile d'accès.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_910",
      nom: "diagnostiqueur / diagnostiqueuse immobilier",
      descriptif:
        "<p>Le diagnostiqueur immobilier intervient avant tout achat ou location d'un logement pour en déterminer l'état. Plomb, amiante, électricité, gaz, termites, performance énergétique... la loi prévoit une série de contrôles qui ne s'improvisent pas.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_911",
      nom: "ingénieur biomédical / ingénieure biomédicale",
      descriptif:
        "<p> L'ingénieur biomédical est un spécialiste des matériels et des équipements de haute technologie destinés au secteur hospitalier, aux établissements de soins et aux professionnels de la santé. Il supervise une équipe de techniciens biomédicaux. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_912",
      nom: "hydrologue",
      descriptif:
        "<p>Spécialiste de l'étude du cycle de l'eau, l'hydrologue contrôle la qualité et les quantités d'eau de surface, depuis la source jusqu'à la distribution. L'eau étant désormais un enjeu stratégique, les hydrologues sont de plus en plus sollicités.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_915",
      nom: "charpentier/ère de marine",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_916",
      nom: "consultant/e en informatique décisionnelle",
      descriptif:
        "<p>Spécialiste des bases de données, le consultant en informatique décisionnelle propose et met en place des solutions informatiques pour les dirigeants des entreprises qui le sollicitent. Sa ligne de mire : améliorer les performances de leur entreprise.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_917",
      nom: "directeur / directrice de création",
      descriptif:
        "<p>Valoriser l'image d'une entreprise, manager une équipe de créatifs, développer la créativité... Le quotidien du directeur de création est ponctué d'idées lumineuses, de visuels attractifs et de textes accrocheurs. Sa passion : la communication !</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_918",
      nom: "guide de haute montagne",
      descriptif:
        "<p>Cordes, mousquetons, crampons, piolets... tels sont les outils du guide de haute montagne pour faire découvrir à des touristes les sommets, en toute sécurité, en hiver mais aussi en été, puisqu'il est habilité à encadrer de nombreuses activités de haute montagne.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_919",
      nom: "agent / agente de développement touristique",
      descriptif:
        "<p>L'agent de développement touristique met sur pied une stratégie globale pour faire connaître ou valoriser un lieu, une commune, une région, etc. En lien avec de nombreux partenaires, il suit la mise en place du projet et supervise la communication.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_92",
      nom: "juriste en droit social",
      descriptif:
        "<p>Plans sociaux, contrats de travail, réforme des retraites... Le droit social évolue constamment, au gré de nouveaux textes de loi. D'où la nécessité pour les entreprises et les administrations de faire appel à un juriste en droit social.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_920",
      nom: "ingénieur/e maintenance aéronautique",
      descriptif:
        "<p>Dans le secteur aéronautique, l'ingénieur maintenance est responsable de l'entretien des avions et des hélicoptères (civils ou militaires), mais aussi des lanceurs spatiaux, des satellites et des missiles. Il dirige des équipes de techniciens.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_921",
      nom: "anatomiste",
      descriptif:
        "<p>Chercheur en médecine, l'anatomiste est un spécialiste de la structure, de la composition, de la croissance et du fonctionnement des organes et tissus du corps humain ou animal.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_922",
      nom: "agent / agente de développement local",
      descriptif:
        "<p>L'agente ou l'agent de développement local fait en sorte que les habitants d'un quartier, d'une ville ou d'une zone rurale vivent mieux ensemble. Mi-animateurs mi-médiateurs, ces professionnels dynamisent le territoire sur le plan socioculturel.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_923",
      nom: "développeur / développeuse économique",
      descriptif:
        "<p>Pour favoriser l'essor d'une ville, d'une agglomération ou d'une zone rurale, le développeur économique stimule la création d'entreprise ou accompagne les sociétés déjà implantées dans leurs projets.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_924",
      nom: "éducateur / éducatrice de la protection judiciaire de la jeunesse",
      descriptif:
        "<p>L'éducateur de la PJJ (protection judiciaire de la jeunesse) intervient auprès de mineurs en conflit avec la loi, délinquants ou en danger. Qu'il partage leur quotidien ou les rencontre régulièrement, il leur apporte une aide éducative et favorise leur réinsertion sociale.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_925",
      nom: "courtier / courtière",
      descriptif:
        "<p>Particuliers et entreprises comptent sur le courtier pour dénicher la meilleure offre au meilleur prix. Une prouesse qu'il ne peut accomplir sans une bonne connaissance du marché et des talents de négociateur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_927",
      nom: "responsable de site de traitement des déchets",
      descriptif:
        "<p>Avec 340 millions de tonnes de déchets produits par an par les ménages et les entreprises, le ou la responsable de site de traitement des déchets a fort à faire ! Sa mission : gérer les opérations de recyclage, d'incinération, de valorisation et d'enfouissement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_928",
      nom: "designer d'interaction",
      descriptif:
        "<p>Le designer d'interaction choisit la façon dont un objet ou un service va se comporter dans le dialogue avec l'utilisateur. Les applications de son métier sont très nombreuses, du secteur des transports à celui de la santé en passant par la culture.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_929",
      nom: "paléontologue",
      descriptif:
        '<p> Fouiller le passé, rechercher et étudier des créatures préhistoriques, des os ou des végétaux fossilisés vieux de plusieurs millions d\'années... tel est le travail du paléontologue, un scientifique popularisé par le film " Jurassic Park ". </p>',
      urls: [],
      formations: [],
    },
    {
      id: "MET_93",
      nom: "community manager",
      descriptif:
        "<p>Garant de la présence et de la (bonne !) réputation d'une marque ou d'une entreprise sur les réseaux sociaux, le community manager anime une communauté d'internautes, publie des tweets, répond aux questions sur le Net, alimente la page Facebook...</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_930",
      nom: "programmiste",
      descriptif:
        "<p>Grâce à son étude minutieuse en amont de la construction, le programmiste assiste le maître d'ouvrage, lui permettant d'éviter toute dérive ou contentieux plus tard. Coûts, recherche de contraintes, utilisation optimale... rien ne lui échappe.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_931",
      nom: "ingénieur mathématicien / ingénieure mathématicienne",
      descriptif:
        "<p>L'ingénieur mathématicien utilise ses connaissances théoriques pour apporter des solutions concrètes à des problématiques complexes, dans tous les secteurs d'activité. Ses travaux apportent des éléments clés pour le développement industriel notamment.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_932",
      nom: "statisticien/ne en géomarketing",
      descriptif:
        "<p>À la croisée des statistiques, du marketing et de l'information géographique, le statisticien en géomarketing utilise ces données pour aider son client à choisir une localisation, optimiser l'implantation de ses points de vente ou améliorer ses services.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_933",
      nom: "ingénieur/e d'affaires en génie électrique",
      descriptif:
        "<p>L'ingénieur d'affaires en génie électrique élabore le dossier technique d'un réseau électrique, que ce dernier soit à installer ou à moderniser. Débouchés dans toutes les industries de pointe et dans les grandes entreprises comme EDF, la SNCF ou la RATP.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_934",
      nom: "chef / cheffe d'exploitation des remontées mécaniques",
      descriptif:
        "<p>Le chef d'exploitation des remontées mécaniques a la responsabilité d'acheminer les clients sur les domaines skiables. Il coordonne les opérations de mise en fonction, de conduite et de surveillance des téléskis ou des téléphériques.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_935",
      nom: "ingénieur / ingénieure recherche et développement (R&D) en agroéquipement",
      descriptif:
        "<p>L'ingénieur recherche et développement (R&amp;D) en agroéquipement imagine le tracteur et les machines de demain, les drones ou les robots qui assisteront les exploitants agricoles ou forestiers, sans perdre de vue le rendement et le respect de l'environnement.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_936",
      nom: "<span>UX designer</span>",
      descriptif:
        "Prendre en compte les utilisateurs dès la conception d'un projet numérique, c'est le rôle de l'UX designer. Il ou elle a pour objectif de rendre la navigation sur les produits multimédias la plus simple et la plus agréable possible.&nbsp;<br/><br/><h3>&nbsp;Après le bac</h3>\n<p>&nbsp;DN MADE mention graphisme ou numérique (bac + 3); licence pro métiers du design ou du numérique (bac + 3) ; diplôme d'école d'art, d'école du numérique ou de design (bac + 3 à bac + 5) ; master création numérique (bac + 5).</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_937",
      nom: "enseignant / enseignante en activité physique adaptée",
      descriptif:
        "<p>Spécialiste de l'activité physique, de la santé et du handicap, l'enseignant en activité physique adaptée (Apa) intervient auprès de personnes dont les aptitudes physiques, psychologiques ou les conditions sociales réduisent l'activité physique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_939",
      nom: "art-thérapeute",
      descriptif:
        "<p>Intégré à l'équipe paramédicale, l'art-thérapeute est un spécialiste de l'activité artistique qui exploite le pouvoir et les effets de l'art dans une visée thérapeutique et humanitaire pour soulager les personnes souffrant de déficits physiques, psychiques ou socio-relationnels.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_94",
      nom: "maçon / maçonne",
      descriptif:
        "<p>Premier à intervenir sur une construction, le maçon met en place les fondations des futurs immeubles, des maisons individuelles ou des bâtiments industriels. Puis il monte les éléments porteurs : murs, poutrelles et planchers.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_940",
      nom: "ingénieur/e essais sol sur aéronef",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_941",
      nom: "guide-conférencier / guide-conférencière",
      descriptif:
        "<p>Dans un musée ou lors d'un circuit dans un pays étranger, le guide-conférencier partage ses vastes connaissances avec un groupe de touristes. Il est capable de s'exprimer en plusieurs langues et travaille généralement en indépendant.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_942",
      nom: "technicien / technicienne en traitement des matériaux",
      descriptif:
        "<p>Dans l'industrie, certains matériaux (métal, plastique, verre, céramique...) doivent subir des traitements pour être plus résistants, plus souples etc. Le technicien en traitement des matériaux contrôle ces opérations de traitement chimique, mécanique ou thermique.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_943",
      nom: "ingénieur / ingénieure en construction automobile",
      descriptif:
        "<p>Passionné par l'automobile et la mécanique, l'ingénieur en construction automobile participe à l'amélioration des modèles existants ou à la conception des véhicules du futur. Révolution écologique et évolutions technologiques l'obligent à innover.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_944",
      nom: "chef / cheffe de projet biodiversité",
      descriptif:
        "<p>La cheffe ou le chef de projet biodiversité cherche à minimiser l'impact négatif de l'activité humaine sur le milieu naturel lors d'un projet d'aménagement. Ses objectifs? Préserver au maximum la faune et la flore, et compenser ce qui est détruit.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_945",
      nom: "consultant / consultante green IT",
      descriptif:
        "<p>Alléger la consommation d'énergie des ordinateurs ou d'un centre téléphonique, optimiser la durée de vie du matériel, réduire les déchets... telles sont les missions du consultant green IT, qu'il soit intégré à l'entreprise ou consultant extérieur.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_946",
      nom: "responsable de laboratoire de contrôle en biologie",
      descriptif:
        "<p>Sous la direction du responsable du laboratoire de contrôle, des tests sont effectués pour évaluer la qualité et la conformité d'un médicament ou d'un produit cosmétique avant sa mise en place sur le marché... ou l'arrêt de sa production.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_947",
      nom: "ingénieur/e structures",
      descriptif:
        "<p>À partir des plans de l'architecte, l'ingénieur structures calcule les dimensions des murs, poutres, etc. pour assurer la stabilité d'un ouvrage. Pour cela, il réalise des plans en 3D et des simulations pour tester ses calculs.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_948",
      nom: "mécatronicien/ne",
      descriptif:
        "<p>Au carrefour de la mécanique, de l'électronique et de l'informatique, le mécatronicien crée des ensembles automatisés miniaturisés. Les applications sont très nombreuses et les secteurs qui recrutent variés, de l'automobile à la défense.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_949",
      nom: "ingénieur/e concepteur/trice en mécanique",
      descriptif:
        "<p>Acteur central du service R &amp; D (recherche et développement), l'ingénieur concepteur en mécanique imagine la forme des produits du futur (voitures électriques, éoliennes, robots...) en fonction des matériaux choisis. Ses missions sont stratégiques et, bien souvent, confidentielles.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_95",
      nom: "ingénieur / ingénieure brevets",
      descriptif:
        "<p>Une innovation peut valoir de l'or... si elle n'est pas copiée ! L'ingénieur brevets, spécialiste en propriété industrielle, veille sur les dernières trouvailles des entreprises et officialise leurs découvertes pour en garantir l'exclusivité. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_952",
      nom: "aide-chimiste",
      descriptif:
        "<p>L'aide-chimiste concourt aux expériences et analyses réalisées dans les laboratoires industriels, participant ainsi à l'élaboration de nouvelles molécules, composants ou produits. Il intervient aussi lors des opérations de contrôle et de qualité.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_954",
      nom: "urbaniste conseil",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_958",
      nom: "gestionnaire de flux reverse logistic",
      descriptif:
        "<p>Le ou la gestionnaire de flux reverse logistic (logistique inversée) optimise et gère le transport, le stockage et le traitement des produits retournés par les clients. Un canapé, un vêtement, un appareil ménager... peut être réparé, reconditionné, recyclé, revendu.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_96",
      nom: "conseiller / conseillère microcrédit",
      descriptif:
        "<p>À l'écoute, la conseillère ou le conseiller microcrédit reçoit des personnes souhaitant obtenir une petite somme d'argent pour réaliser un projet. Évaluation, montage et défense du dossier, suivi des remboursements font partie de ses tâches.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_960",
      nom: "journaliste territorial/e",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_961",
      nom: "responsable éditorial/e web",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_962",
      nom: "orthodontiste",
      descriptif:
        "<p>Docteur en chirurgie dentaire, l'orthodontiste reçoit enfants, adolescents, mais aussi adultes pour corriger l'alignement de leurs dents ou la position de leur mâchoire. Il pose bagues et appareils qu'il ajuste régulièrement avec minutie. </p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_964",
      nom: "éducateur canin / éducatrice canine",
      descriptif:
        "<p>Ne pas tirer sur la laisse, rester assis, venir à l'appel, ne pas mordre... voici quelques-uns des comportements que l'éducateur canin inculque aux chiens non disciplinés.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_965",
      nom: "ouvrier / ouvrière plasturgiste",
      descriptif:
        "<p>Le plastique est une matière dérivée principalement du pétrole. L'ouvrier plasturgiste travaille sur des machines transformant des billes ou des granulés de plastique en toutes sortes d'objets du quotidien : chaises, coques de téléphones, etc.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_966",
      nom: "luthier/ère",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_967",
      nom: "e-merchandiser",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_97",
      nom: "créateur / créatrice textile",
      descriptif:
        "<p>Le métier de créateur textile est à la fois créatif et technique. C'est lui qui imagine et crée les tissus qui, demain, serviront à fabriquer vêtements, nappes, rideaux, etc.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_970",
      nom: "agent/e littéraire",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_976",
      nom: "employé / employée d'élevage caprin",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_98",
      nom: "sellier/ère",
      descriptif:
        "<p>À l'origine spécialiste du cuir pour fabriquer selles et harnachements pour les chevaux, le sellier travaille désormais tous les matériaux souples. Son savoir-faire trouve des débouchés dans l'industrie automobile ou nautique, la maroquinerie, etc.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_982",
      nom: "peintre décorateur/trice",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_984",
      nom: "sellier/ère-gainier/ère",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_987",
      nom: "cancérologue",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_988",
      nom: "cardiologue",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_989",
      nom: "dermatologue",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_99",
      nom: "vitrailliste",
      descriptif:
        "<p>Chartres, La Sainte-Chapelle de Paris... Du roman au gothique, l'art du vitrail a connu son âge d'or. Aujourd'hui, le vitrailliste partage son temps entre restauration et création. Il est à la fois artiste et technicien du verre.</p>",
      urls: [],
      formations: [],
    },
    {
      id: "MET_990",
      nom: "endocrinologue",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_992",
      nom: "médecin-conseil de la sécurité sociale",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_993",
      nom: "médecin du travail",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_994",
      nom: "médecin scolaire",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_995",
      nom: "nutritionniste",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_996",
      nom: "ophtalmologue",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_997",
      nom: "ORL (oto-rhino-laryngologiste)",
      descriptif: null,
      urls: [],
      formations: [],
    },
    {
      id: "MET_998",
      nom: "radiologue",
      descriptif: null,
      urls: [],
      formations: [],
    },
  ];

  public async récupérerTous(): Promise<Métier[] | undefined> {
    return [this.MÉTIERS[0], this.MÉTIERS[1]];
  }

  public async rechercher(recherche: string): Promise<MétierAperçu[] | undefined> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const fuse = new Fuse<Métier>(this.MÉTIERS, {
          distance: 200,
          threshold: 0.4,
          keys: ["nom"],
        });

        const correspondances = fuse.search(recherche);
        resolve(correspondances.map((correspondance) => this._mapperVersAperçu(correspondance.item)));
      }, 300);
    });
  }

  public async récupérerAperçus(métierIds: Array<MétierAperçu["id"]>): Promise<MétierAperçu[] | undefined> {
    return this.MÉTIERS.filter((métier) => métierIds.includes(métier.id)).map(this._mapperVersAperçu);
  }

  private _mapperVersAperçu = (métier: Métier) => {
    return {
      id: métier.id,
      nom: métier.nom,
    };
  };
}
