import { type Bac, type Spécialité } from "@/features/bac/domain/bac.interface";
import { type BacRepository } from "@/features/bac/infrastructure/bacRepository.interface";

export class bacInMemoryRepository implements BacRepository {
  private BACS = [
    {
      id: "NC",
      nom: "Je ne sais pas",
    },
    {
      id: "Générale",
      nom: "Série Générale",
    },
    {
      id: "P",
      nom: "Bac Pro",
    },
    {
      id: "PA",
      nom: "Bac Pro Agricole",
    },
    {
      id: "S2TMD",
      nom: "Bac Techno S2TMD - Sciences et Techniques du Théâtre de la Musique et de la Danse",
    },
    {
      id: "ST2S",
      nom: "Bac Techno ST2S - Sciences et technologies de la santé et du social",
    },
    {
      id: "STAV",
      nom: "Bac Techno STAV - Sciences et Technologies de l'agronomie et du vivant",
    },
    {
      id: "STD2A",
      nom: "Bac Techno STD2A - Sciences Technologiques du Design et des Arts Appliquées",
    },
    {
      id: "STHR",
      nom: "Bac Techno STHR - Science et Techniques de l'Hôtellerie et de la Restauration",
    },
    {
      id: "STI2D",
      nom: "Bac Techno STI2D - Sciences et Technologies de l'Industrie et du Développement Durable",
    },
    {
      id: "STL",
      nom: "Bac Techno STL - Sciences et technologie de laboratoire",
    },
    {
      id: "STMG",
      nom: "Bac Techno STMG - Sciences et Technologies du Management et de la Gestion",
    },
  ];

  private SPÉCIALITÉS = [
    {
      id: "1006",
      nom: "Economie et gestion hôtelière",
    },
    {
      id: "1008",
      nom: "Enseignement scientifique alimentation - environnement",
    },
    {
      id: "1009",
      nom: "Ressources humaines et communication",
    },
    {
      id: "1038",
      nom: "Droit et Economie",
    },
    {
      id: "1039",
      nom: "Chimie, biologie et physiopathologie humaines",
    },
    {
      id: "1040",
      nom: "Physique-Chimie et Mathématiques",
    },
    {
      id: "1041",
      nom: "Biochimie-Biologie-Biotechnologie",
    },
    {
      id: "1042",
      nom: "Conception et création en design et métiers d'art",
    },
    {
      id: "1043",
      nom: "Analyse et méthodes en design",
    },
    {
      id: "1044",
      nom: "Pratique théâtrale",
    },
    {
      id: "1045",
      nom: "Culture et sciences théâtrale",
    },
    {
      id: "1046",
      nom: "Culture et sciences musicale",
    },
    {
      id: "1047",
      nom: "Pratique musicale",
    },
    {
      id: "1048",
      nom: "Culture et sciences chorégraphiques",
    },
    {
      id: "1049",
      nom: "Pratique chorégraphiques",
    },
    {
      id: "1050",
      nom: "Sciences et technologies culinaires et des services",
    },
    {
      id: "1051",
      nom: "Sciences et technologies culinaires et services-ESAE",
    },
    {
      id: "1052",
      nom: "Physique-Chimie pour la santé",
    },
    {
      id: "1053",
      nom: "Sciences de la gestion et numérique",
    },
    {
      id: "1054",
      nom: "Management",
    },
    {
      id: "1055",
      nom: "Ingénierie et développement durable",
    },
    {
      id: "1056",
      nom: "Innovation Technologique",
    },
    {
      id: "1057",
      nom: "Biochimie-Biologie",
    },
    {
      id: "1058",
      nom: "Design et métiers d'art",
    },
    {
      id: "1059",
      nom: "Outils et langages numériques",
    },
    {
      id: "1060",
      nom: "Economie, droit et environnement du spectacle vivant",
    },
    {
      id: "1061",
      nom: "Sciences de l'ingénieur et sciences physiques",
    },
    {
      id: "1062",
      nom: "Histoire-Géographie, Géopolitique et Sciences politiques",
    },
    {
      id: "1063",
      nom: "Littérature et langues et cultures de l’Antiquité: Latin",
    },
    {
      id: "1065",
      nom: "Numérique et Sciences Informatiques",
    },
    {
      id: "1066",
      nom: "Littérature et langues et cultures de l'Antiquité: Grec",
    },
    {
      id: "1067",
      nom: "Humanités, Littérature et Philosophie",
    },
    {
      id: "1076",
      nom: "Langues, littératures et cultures étrangères et régionales",
    },
    {
      id: "1077",
      nom: "Gestion des ressources et alimentation",
    },
    {
      id: "1078",
      nom: "Territoires et technologie",
    },
    {
      id: "1079",
      nom: "Territoires et société",
    },
    {
      id: "1095",
      nom: "Éducation Physique, Pratiques Et Culture Sportives",
    },
    {
      id: "1096",
      nom: "Ingénierie, innovation et développement durable",
    },
    {
      id: "282",
      nom: "Mercatique",
    },
    {
      id: "320",
      nom: "Sciences et techniques sanitaires et sociales",
    },
    {
      id: "321",
      nom: "Biologie et physiopathologie humaines",
    },
    {
      id: "339",
      nom: "Technologie",
    },
    {
      id: "4",
      nom: "Sciences de l'ingénieur",
    },
    {
      id: "5",
      nom: "Biologie/Ecologie",
    },
    {
      id: "700",
      nom: "Mathématiques",
    },
    {
      id: "701",
      nom: "Physique-Chimie",
    },
    {
      id: "702",
      nom: "Sciences de la vie et de la Terre",
    },
    {
      id: "703",
      nom: "Sciences Economiques et Sociales",
    },
    {
      id: "704",
      nom: "Musique",
    },
    {
      id: "705",
      nom: "Danse",
    },
    {
      id: "706",
      nom: "Arts Plastiques",
    },
    {
      id: "707",
      nom: "Histoire des Arts",
    },
    {
      id: "708",
      nom: "Arts du Cirque",
    },
    {
      id: "709",
      nom: "Théâtre-Expression dramatique",
    },
    {
      id: "710",
      nom: "Cinéma-Audiovisuel",
    },
    {
      id: "719",
      nom: "Sciences physiques et chimiques en laboratoire",
    },
    {
      id: "740",
      nom: "Biotechnologies",
    },
    {
      id: "887",
      nom: "Gestion et Finance",
    },
    {
      id: "888",
      nom: "Systèmes d'information et de Gestion",
    },
  ];

  private BACS_SPECIALITÉS = [
    {
      bacId: "Générale",
      spécialitéId: "1061",
    },
    {
      bacId: "Générale",
      spécialitéId: "1062",
    },
    {
      bacId: "Générale",
      spécialitéId: "1063",
    },
    {
      bacId: "Générale",
      spécialitéId: "1065",
    },
    {
      bacId: "Générale",
      spécialitéId: "1066",
    },
    {
      bacId: "Générale",
      spécialitéId: "1067",
    },
    {
      bacId: "Générale",
      spécialitéId: "1076",
    },
    {
      bacId: "Générale",
      spécialitéId: "1095",
    },
    {
      bacId: "Générale",
      spécialitéId: "5",
    },
    {
      bacId: "Générale",
      spécialitéId: "700",
    },
    {
      bacId: "Générale",
      spécialitéId: "701",
    },
    {
      bacId: "Générale",
      spécialitéId: "702",
    },
    {
      bacId: "Générale",
      spécialitéId: "703",
    },
    {
      bacId: "Générale",
      spécialitéId: "704",
    },
    {
      bacId: "Générale",
      spécialitéId: "705",
    },
    {
      bacId: "Générale",
      spécialitéId: "706",
    },
    {
      bacId: "Générale",
      spécialitéId: "707",
    },
    {
      bacId: "Générale",
      spécialitéId: "708",
    },
    {
      bacId: "Générale",
      spécialitéId: "709",
    },
    {
      bacId: "Générale",
      spécialitéId: "710",
    },
    {
      bacId: "NC",
      spécialitéId: "1006",
    },
    {
      bacId: "NC",
      spécialitéId: "1009",
    },
    {
      bacId: "NC",
      spécialitéId: "1038",
    },
    {
      bacId: "NC",
      spécialitéId: "1039",
    },
    {
      bacId: "NC",
      spécialitéId: "1040",
    },
    {
      bacId: "NC",
      spécialitéId: "1041",
    },
    {
      bacId: "NC",
      spécialitéId: "1042",
    },
    {
      bacId: "NC",
      spécialitéId: "1043",
    },
    {
      bacId: "NC",
      spécialitéId: "1046",
    },
    {
      bacId: "NC",
      spécialitéId: "1047",
    },
    {
      bacId: "NC",
      spécialitéId: "1048",
    },
    {
      bacId: "NC",
      spécialitéId: "1049",
    },
    {
      bacId: "NC",
      spécialitéId: "1051",
    },
    {
      bacId: "NC",
      spécialitéId: "1061",
    },
    {
      bacId: "NC",
      spécialitéId: "1062",
    },
    {
      bacId: "NC",
      spécialitéId: "1063",
    },
    {
      bacId: "NC",
      spécialitéId: "1065",
    },
    {
      bacId: "NC",
      spécialitéId: "1066",
    },
    {
      bacId: "NC",
      spécialitéId: "1067",
    },
    {
      bacId: "NC",
      spécialitéId: "1076",
    },
    {
      bacId: "NC",
      spécialitéId: "1077",
    },
    {
      bacId: "NC",
      spécialitéId: "1078",
    },
    {
      bacId: "NC",
      spécialitéId: "1095",
    },
    {
      bacId: "NC",
      spécialitéId: "1096",
    },
    {
      bacId: "NC",
      spécialitéId: "282",
    },
    {
      bacId: "NC",
      spécialitéId: "320",
    },
    {
      bacId: "NC",
      spécialitéId: "5",
    },
    {
      bacId: "NC",
      spécialitéId: "700",
    },
    {
      bacId: "NC",
      spécialitéId: "701",
    },
    {
      bacId: "NC",
      spécialitéId: "702",
    },
    {
      bacId: "NC",
      spécialitéId: "703",
    },
    {
      bacId: "NC",
      spécialitéId: "704",
    },
    {
      bacId: "NC",
      spécialitéId: "705",
    },
    {
      bacId: "NC",
      spécialitéId: "706",
    },
    {
      bacId: "NC",
      spécialitéId: "707",
    },
    {
      bacId: "NC",
      spécialitéId: "708",
    },
    {
      bacId: "NC",
      spécialitéId: "709",
    },
    {
      bacId: "NC",
      spécialitéId: "710",
    },
    {
      bacId: "NC",
      spécialitéId: "719",
    },
    {
      bacId: "NC",
      spécialitéId: "887",
    },
    {
      bacId: "NC",
      spécialitéId: "888",
    },
    {
      bacId: "PA",
      spécialitéId: "5",
    },
    {
      bacId: "S2TMD",
      spécialitéId: "1046",
    },
    {
      bacId: "S2TMD",
      spécialitéId: "1047",
    },
    {
      bacId: "S2TMD",
      spécialitéId: "1048",
    },
    {
      bacId: "S2TMD",
      spécialitéId: "1049",
    },
    {
      bacId: "ST2S",
      spécialitéId: "1039",
    },
    {
      bacId: "ST2S",
      spécialitéId: "320",
    },
    {
      bacId: "STAV",
      spécialitéId: "1077",
    },
    {
      bacId: "STAV",
      spécialitéId: "1078",
    },
    {
      bacId: "STD2A",
      spécialitéId: "1042",
    },
    {
      bacId: "STD2A",
      spécialitéId: "1043",
    },
    {
      bacId: "STHR",
      spécialitéId: "1006",
    },
    {
      bacId: "STHR",
      spécialitéId: "1051",
    },
    {
      bacId: "STI2D",
      spécialitéId: "1040",
    },
    {
      bacId: "STI2D",
      spécialitéId: "1096",
    },
    {
      bacId: "STL",
      spécialitéId: "1040",
    },
    {
      bacId: "STL",
      spécialitéId: "1041",
    },
    {
      bacId: "STL",
      spécialitéId: "719",
    },
    {
      bacId: "STMG",
      spécialitéId: "1009",
    },
    {
      bacId: "STMG",
      spécialitéId: "1038",
    },
    {
      bacId: "STMG",
      spécialitéId: "282",
    },
    {
      bacId: "STMG",
      spécialitéId: "887",
    },
    {
      bacId: "STMG",
      spécialitéId: "888",
    },
  ];

  public async récupérerTous(): Promise<Bac[] | undefined> {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(this.BACS);
      }, 500);
    });
  }

  public async récupérerSpécialités(bacId: Bac["id"]): Promise<Spécialité[] | undefined> {
    const spécialitéIdsDuBac = this.BACS_SPECIALITÉS.filter((bacSpécialité) => bacSpécialité.bacId === bacId).map(
      (bacSpécialité) => bacSpécialité.spécialitéId,
    );

    const spécialitésDuBac = spécialitéIdsDuBac.map((spécialitéId) =>
      this.SPÉCIALITÉS.find((spécialité) => spécialité.id === spécialitéId),
    );

    return [...new Set(spécialitésDuBac.filter((spécialité): spécialité is Spécialité => spécialité !== undefined))];
  }
}
