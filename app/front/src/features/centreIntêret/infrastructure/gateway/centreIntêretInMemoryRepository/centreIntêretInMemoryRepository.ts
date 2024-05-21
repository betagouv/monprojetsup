import { type CatégorieCentresIntêrets } from "@/features/centreIntêret/domain/centreIntêret.interface";
import { type CentreIntêretRepository } from "@/features/centreIntêret/infrastructure/centreIntêretRepository.interface";

export class centreIntêretInMemoryRepository implements CentreIntêretRepository {
  private CATÉGORIES_CENTRES_INTÊRETS: CatégorieCentresIntêrets[] = [
    {
      emoji: "🧑‍💻",
      nom: "Transmettre et m'occuper des plus jeunes",
      centresIntêrets: [
        { nom: "Travailler avec des enfants", id: "T_IDEO2_4815" },
        { nom: "Enseigner", id: "T_ROME_1046112128" },
      ],
    },
    {
      emoji: "🌱",
      nom: "Protéger la nature et les animaux",
      centresIntêrets: [
        { nom: "Travailler au contact de la nature", id: "T_IDEO2_4824" },
        { nom: "Défendre l'écologie", id: "T_ROME_84652368" },
        { nom: "Travailler avec les animaux", id: "T_IDEO2_4828" },
      ],
    },
    {
      emoji: "🗣",
      nom: "Communiquer et informer",
      centresIntêrets: [
        { nom: "Informer, communiquer", id: "T_IDEO2_4821" },
        { nom: "J'ai le sens du contact", id: "T_IDEO2_4817" },
        { nom: "Communiquer, convaincre", id: "T_ROME_1814691478" },
      ],
    },
    {
      emoji: "🔨",
      nom: "Travailler de mes mains",
      centresIntêrets: [
        { nom: "Bricoler", id: "T_IDEO2_4806" },
        { nom: "Créer quelque chose de mes mains", id: "T_ROME_1573349427" },
        { nom: "Cuisiner", id: "T_ROME_1665443017" },
      ],
    },
    {
      emoji: "🌎",
      nom: "Découvrir le monde",
      centresIntêrets: [
        { nom: "Voyager", id: "T_IDEO2_4810" },
        { nom: "Apprendre de nouvelles langues", id: "T_IDEO2_4818" },
        { nom: "Travailler dans un milieu multiculturel", id: "T_ROME_762517279" },
      ],
    },
    {
      emoji: "🧡",
      nom: "Prendre soin des autres",
      centresIntêrets: [
        { nom: "Soigner", id: "T_IDEO2_4807" },
        { nom: "Aider les autres", id: "T_ROME_731379930" },
        { nom: "Aller vers les gens", id: "T_ROME_860291826" },
      ],
    },
    {
      emoji: "🤸",
      nom: "Avoir une activité physique",
      centresIntêrets: [
        { nom: "Pratiquer une activité sportive", id: "T_IDEO2_4809" },
        { nom: "Des sensations fortes", id: "T_IDEO2_4826" },
        { nom: "Conduire", id: "T_ROME_326548351" },
      ],
    },
    {
      emoji: "🧐",
      nom: "Découvrir, enquêter et rechercher",
      centresIntêrets: [
        { nom: "Faire des expériences", id: "T_ROME_2027610093" },
        { nom: "Prêter attention au détail", id: "T_ROME_58088585" },
      ],
    },
    {
      emoji: "🧑‍⚖",
      nom: "Faire respecter la loi",
      centresIntêrets: [{ nom: "Faire respecter la loi", id: "T_IDEO2_4808" }],
    },
    {
      emoji: "🎥",
      nom: "Travailler dans le monde de l'art",
      centresIntêrets: [
        { nom: "Être artiste", id: "T_IDEO2_4829" },
        { nom: "Découvrir l'envers du décor", id: "T_ROME_1391567938" },
        { nom: "Écrire ou lire", id: "T_ROME_1825212206" },
      ],
    },
    {
      emoji: "🚀",
      nom: "Mener une équipe",
      centresIntêrets: [
        { nom: "Diriger une équipe", id: "T_IDEO2_4814" },
        { nom: "Organiser les choses", id: "T_IDEO2_4820" },
      ],
    },

    {
      emoji: "💻",
      nom: "Développer les nouvelles technologies",
      centresIntêrets: [{ nom: "Je suis branché high tech", id: "T_IDEO2_4825" }],
    },
    {
      emoji: "🤝",
      nom: "Vendre, développer un commerce",
      centresIntêrets: [{ nom: "J'ai la bosse du commerce", id: "T_IDEO2_4811" }],
    },
    {
      emoji: "💯",
      nom: "Jongler avec les chiffres",
      centresIntêrets: [{ nom: "Jongler avec les chiffres", id: "T_IDEO2_4816" }],
    },
  ];

  public async récupérerTousGroupésParCatégorie(): Promise<CatégorieCentresIntêrets[] | undefined> {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(this.CATÉGORIES_CENTRES_INTÊRETS.sort((a, b) => a.nom.localeCompare(b.nom, "fr")));
      }, 500);
    });
  }
}
