import { type CatégorieCentresIntêrets } from "@/features/centreIntêret/domain/centreIntêret.interface";
import { type CentreIntêretRepository } from "@/features/centreIntêret/infrastructure/centreIntêretRepository.interface";

export class centreIntêretInMemoryRepository implements CentreIntêretRepository {
  private CATÉGORIES_CENTRES_INTÊRETS: CatégorieCentresIntêrets[] = [
    {
      emoji: "🧑‍💻",
      nom: "Transmettre et m'occuper des plus jeunes",
      centresIntêrets: [
        { nom: "Je veux travailler avec des enfants", id: "T_IDEO2_4815" },
        { nom: "Je veux enseigner", id: "T_ROME_1046112128" },
      ],
    },
    {
      emoji: "🌱",
      nom: "Protéger la nature et les animaux",
      centresIntêrets: [
        { nom: "Je veux travailler au contact de la nature", id: "T_IDEO2_4824" },
        { nom: "Je veux défendre l'écologie", id: "T_ROME_84652368" },
        { nom: "Je veux travailler avec les animaux", id: "T_IDEO2_4828" },
      ],
    },
    {
      emoji: "🗣",
      nom: "Communiquer et informer",
      centresIntêrets: [
        { nom: "J'aimerais informer, communiquer", id: "T_IDEO2_4821" },
        { nom: "J'ai le sens du contact", id: "T_IDEO2_4817" },
        { nom: "J'aime communiquer, convaincre", id: "T_ROME_1814691478" },
      ],
    },
    {
      emoji: "🔨",
      nom: "Travailler de mes mains",
      centresIntêrets: [
        { nom: "Je veux bricoler", id: "T_IDEO2_4806" },
        { nom: "Je veux créer quelque chose de mes mains", id: "T_ROME_1573349427" },
        { nom: "Je veux cuisiner", id: "T_ROME_1665443017" },
      ],
    },
    {
      emoji: "🌎",
      nom: "Découvrir le monde",
      centresIntêrets: [
        { nom: "Je veux voyager", id: "T_IDEO2_4810" },
        { nom: "Je veux apprendre de nouvelles langues", id: "T_IDEO2_4818" },
        { nom: "Je veux travailler dans un milieu multiculturel", id: "T_ROME_762517279" },
      ],
    },
    {
      emoji: "🧡",
      nom: "Prendre soin des autres",
      centresIntêrets: [
        { nom: "Je veux soigner", id: "T_IDEO2_4807" },
        { nom: "Je veux aider les autres", id: "T_ROME_731379930" },
        { nom: "Je veux aller vers les gens", id: "T_ROME_860291826" },
      ],
    },
    {
      emoji: "🤸",
      nom: "Avoir une activité physique",
      centresIntêrets: [
        { nom: "Je veux pratiquer une activité sportive", id: "T_IDEO2_4809" },
        { nom: "Je veux des sensations fortes", id: "T_IDEO2_4826" },
        { nom: "Je veux conduire", id: "T_ROME_326548351" },
      ],
    },
    {
      emoji: "🧐",
      nom: "Découvrir, enquêter et rechercher",
      centresIntêrets: [
        { nom: "Je veux faire des expériences", id: "T_ROME_2027610093" },
        { nom: "Je veux prêter attention au détail", id: "T_ROME_58088585" },
      ],
    },
    {
      emoji: "🧑‍⚖",
      nom: "Faire respecter la loi",
      centresIntêrets: [
        { nom: "Je veux faire respecter la loi", id: "T_IDEO2_4808" },
        { nom: "J'aime faire respecter la loi, les régles", id: "T_ROME_313545038" },
      ],
    },
    {
      emoji: "🎥",
      nom: "Travailler dans le monde de l'art",
      centresIntêrets: [
        { nom: "Je veux être artiste", id: "T_IDEO2_4829" },
        { nom: "Je veux découvrir l'envers du décor", id: "T_ROME_1391567938" },
        { nom: "Je veux écrire ou lire", id: "T_ROME_1825212206" },
      ],
    },
    {
      emoji: "🚀",
      nom: "Mener une équipe",
      centresIntêrets: [
        { nom: "Je veux diriger une équipe", id: "T_IDEO2_4814" },
        { nom: "Je veux organiser les choses", id: "T_IDEO2_4820" },
      ],
    },

    {
      emoji: "💻",
      nom: "Développer les nouvelles technologies",
      centresIntêrets: [
        { nom: "Je suis branché high tech", id: "T_IDEO2_4825" },
        { nom: "Je suis passionné / passionnée par les nouvelles technologies", id: "T_ROME_637471645" },
      ],
    },
    {
      emoji: "🤝",
      nom: "Vendre, développer un commerce",
      centresIntêrets: [
        { nom: "J'ai la bosse du commerce", id: "T_IDEO2_4811" },
        { nom: "J'ai le sens des affaires", id: "T_ROME_749075906" },
      ],
    },
    {
      emoji: "💯",
      nom: "Jongler avec les chiffres",
      centresIntêrets: [
        { nom: "J'aime jongler avec les chiffres", id: "T_IDEO2_4816" },
        { nom: "J'aime manier les chiffres", id: "T_ROME_2092381917" },
      ],
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
