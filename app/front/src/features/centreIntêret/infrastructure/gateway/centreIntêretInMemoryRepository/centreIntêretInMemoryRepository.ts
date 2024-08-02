import { type CatégorieCentreIntêret } from "@/features/centreIntêret/domain/centreIntêret.interface";
import { type CentreIntêretRepository } from "@/features/centreIntêret/infrastructure/centreIntêretRepository.interface";

export class centreIntêretInMemoryRepository implements CentreIntêretRepository {
  private CATÉGORIES_CENTRE_INTÊRET: CatégorieCentreIntêret[] = [
    {
      id: "transmettre",
      nom: "Transmettre et m'occuper des plus jeunes",
      emoji: "🧑‍💻",
      sousCatégoriesCentreIntêret: [
        {
          id: "transmettre_enfants",
          nom: "Travailler avec des enfants",
          emoji: "🙋",
        },
        {
          id: "transmettre_enseigner",
          nom: "Enseigner",
          emoji: "👶",
        },
      ],
    },
    {
      id: "proteger",
      nom: "Protéger la nature et les animaux",
      emoji: "🌱",
      sousCatégoriesCentreIntêret: [
        {
          id: "proteger_nature",
          nom: "Travailler au contact de la nature",
          emoji: "🌳",
        },
        {
          id: "proteger_ecologie",
          nom: "Défendre l'écologie",
          emoji: "♻",
        },
        {
          id: "proteger_animaux",
          nom: "Travailler avec les animaux",
          emoji: "😺",
        },
      ],
    },
    {
      id: "communiquer",
      nom: "Communiquer et informer",
      emoji: "🗣",
      sousCatégoriesCentreIntêret: [
        {
          id: "communiquer_informer",
          nom: "Communiquer et informer",
          emoji: "🗣",
        },
      ],
    },
    {
      id: "travail_manuel",
      nom: "Travailler de mes mains",
      emoji: "🔨",
      sousCatégoriesCentreIntêret: [
        {
          id: "travail_manuel_bricoler",
          nom: "Bricoler",
          emoji: "🙌",
        },
        {
          id: "travail_manuel_creer",
          nom: "Créer quelque chose de mes mains",
          emoji: "🪛",
        },
        {
          id: "travail_manuel_cuisiner",
          nom: "Cuisiner",
          emoji: "🧑‍🍳",
        },
      ],
    },
    {
      id: "decouvrir",
      nom: "Découvrir le monde",
      emoji: "🌎",
      sousCatégoriesCentreIntêret: [
        {
          id: "decouvrir_voyager",
          nom: "Voyager",
          emoji: "🚅",
        },
        {
          id: "decouvrir_apprendre_langues",
          nom: "Apprendre de nouvelles langues",
          emoji: "🇬🇧",
        },
        {
          id: "decouvrir_multiculturel",
          nom: "Travailler dans un milieu multiculturel",
          emoji: "🛤",
        },
      ],
    },
    {
      id: "aider",
      nom: "Prendre soin des autres",
      emoji: "🧡",
      sousCatégoriesCentreIntêret: [
        {
          id: "aider_soigner",
          nom: "Soigner",
          emoji: "🏥",
        },
        {
          id: "aider_autres",
          nom: "Aider les autres",
          emoji: "🫂",
        },
        {
          id: "aider_aller_vers",
          nom: "Aller vers les gens",
          emoji: "😄",
        },
      ],
    },
    {
      id: "activite_physique",
      nom: "Avoir une activité physique",
      emoji: "🤸",
      sousCatégoriesCentreIntêret: [
        {
          id: "activite_physique_sportive",
          nom: "Pratiquer une activité sportive",
          emoji: "⛹",
        },
        {
          id: "activite_physique_sensations",
          nom: "Des sensations fortes",
          emoji: "🔥",
        },
        {
          id: "activite_physique_conduire",
          nom: "Conduire",
          emoji: "🏎",
        },
      ],
    },
    {
      id: "rechercher",
      nom: "Découvrir, enquêter et rechercher",
      emoji: "🧐",
      sousCatégoriesCentreIntêret: [
        {
          id: "rechercher_experiences",
          nom: "Faire des expériences",
          emoji: "🧪",
        },
        {
          id: "rechercher_detail",
          nom: "Prêter attention au détail",
          emoji: "🔎",
        },
      ],
    },
    {
      id: "loi",
      nom: "Faire respecter la loi",
      emoji: "🧑‍⚖",
      sousCatégoriesCentreIntêret: [
        {
          id: "loi_faire_respecter",
          nom: "Faire respecter la loi",
          emoji: "🧑‍⚖",
        },
      ],
    },
    {
      id: "art",
      nom: "Travailler dans le monde de l'art",
      emoji: "🎥",
      sousCatégoriesCentreIntêret: [
        {
          id: "art_artiste",
          nom: "Être artiste",
          emoji: "🎨",
        },
        {
          id: "art_envers",
          nom: "Découvrir l'envers du décor",
          emoji: "🎭",
        },
        {
          id: "art_ecrire_lire",
          nom: "Écrire ou lire",
          emoji: "✍",
        },
      ],
    },
    {
      id: "diriger",
      nom: "Mener une équipe",
      emoji: "🚀",
      sousCatégoriesCentreIntêret: [
        {
          id: "diriger_equipe",
          nom: "Diriger une équipe",
          emoji: "👍",
        },
        {
          id: "dirigier_organiser",
          nom: "Organiser les choses",
          emoji: "📑",
        },
      ],
    },
    {
      id: "technologies",
      nom: "Développer les nouvelles technologies",
      emoji: "💻",
      sousCatégoriesCentreIntêret: [
        {
          id: "technologies_high_tech",
          nom: "Je suis branché high tech",
          emoji: "💻",
        },
      ],
    },
    {
      id: "commerce",
      nom: "Vendre, développer un commerce",
      emoji: "🤝",
      sousCatégoriesCentreIntêret: [
        {
          id: "commerce_bosse",
          nom: "J'ai la bosse du commerce",
          emoji: "🤝",
        },
      ],
    },
    {
      id: "chiffres",
      nom: "Jongler avec les chiffres",
      emoji: "💯",
      sousCatégoriesCentreIntêret: [
        {
          id: "chiffres_jongler",
          nom: "Jongler avec les chiffres",
          emoji: "💯",
        },
      ],
    },
  ];

  public async récupérer(): Promise<CatégorieCentreIntêret[] | undefined> {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(this.CATÉGORIES_CENTRE_INTÊRET.sort((a, b) => a.nom.localeCompare(b.nom, "fr")));
      }, 500);
    });
  }
}
