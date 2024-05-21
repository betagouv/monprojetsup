import { type CatégorieDomainesProfessionnels } from "@/features/domaineProfessionnel/domain/domaineProfessionnel.interface";
import { type DomaineProfessionnelRepository } from "@/features/domaineProfessionnel/infrastructure/domaineProfessionnelRepository.interface";

export class domaineProfessionnelInMemoryRepository implements DomaineProfessionnelRepository {
  private CATÉGORIES_DOMAINES_PROFESSIONNELS: CatégorieDomainesProfessionnels[] = [
    {
      emoji: "🧑‍🔬",
      nom: "Sciences et Technologie",
      domainesProfessionnels: [
        { id: "T_ITM_1534", nom: "Mécanique" },
        { id: "T_ITM_611", nom: "Physique" },
        { id: "T_ITM_636", nom: "Informatique" },
        { id: "T_ITM_PERSO6", nom: "Chimie et matériaux" },
        { id: "T_ITM_PERSO4", nom: "Sciences du vivant et de la terre" },
        { id: "T_ITM_1112", nom: "Mathématiques" },
        { id: "T_ITM_1067", nom: "Électronique" },
      ],
    },
    {
      emoji: "🎨",
      nom: "Arts et Culture",
      domainesProfessionnels: [
        { id: "T_ITM_PERSO1", nom: "Art" },
        { id: "T_ITM_1420", nom: "Audiovisuel" },
        { id: "T_ITM_1021", nom: "Histoire de l'art" },
        { id: "T_ITM_723", nom: "Arts du spectacle" },
      ],
    },
    { emoji: "🏢", nom: "Commerce", domainesProfessionnels: [{ id: "T_ITM_PERSO3", nom: "Management et business" }] },
    {
      emoji: "🏥",
      nom: "Santé et Social",
      domainesProfessionnels: [
        { id: "T_ITM_1180", nom: "Santé" },
        { id: "T_ITM_1491", nom: "Travail social" },
      ],
    },
    {
      emoji: "🤵",
      nom: "Sciences Humaines et Sociales",
      domainesProfessionnels: [
        { id: "T_ITM_1039", nom: "Anthropologie" },
        { id: "T_ITM_1238", nom: "Sciences économiques" },
        { id: "T_ITM_1020", nom: "Sciences humaines et sociales" },
        { id: "T_ITM_1030", nom: "Histoire" },
        { id: "T_ITM_1054", nom: "Philosophie" },
        { id: "T_ITM_1043", nom: "Sciences des religions" },
      ],
    },
    {
      emoji: "🔣",
      nom: "Education et Formation",
      domainesProfessionnels: [
        { id: "T_ITM_1055", nom: "Enseignement - formation" },
        { id: "T_ITM_933", nom: "Science du langage" },
      ],
    },
    {
      emoji: "🚧",
      nom: "Bâtiment et construction",
      domainesProfessionnels: [{ id: "T_ITM_1248", nom: "Bâtiment - construction" }],
    },
    {
      emoji: "🏭",
      nom: "Ingénierie et Industrie",
      domainesProfessionnels: [
        { id: "T_ITM_PERSO7", nom: "Industries" },
        { id: "T_ITM_794", nom: "Ingénierie" },
        { id: "T_ITM_671", nom: "Transport" },
        { id: "T_ITM_796", nom: "Qualité" },
        { id: "T_ITM_807", nom: "Logistique" },
      ],
    },
    {
      emoji: "🌎",
      nom: "Environnement et Géographie",
      domainesProfessionnels: [
        { id: "T_ITM_PERSO9", nom: "Géographie et aménagement du territoire" },
        { id: "T_ITM_762", nom: "Environnement" },
      ],
    },
    {
      emoji: "🎓",
      nom: "Droit et politique",
      domainesProfessionnels: [
        { id: "T_ITM_1284", nom: "Droit" },
        { id: "T_ITM_950", nom: "Sciences politiques" },
      ],
    },
    {
      emoji: "🗣",
      nom: "Langues et Communication",
      domainesProfessionnels: [
        { id: "T_ITM_918", nom: "Langue étrangère" },
        { id: "T_ITM_957", nom: "Information communication" },
        { id: "T_ITM_917", nom: "Lettres - langues" },
      ],
    },
    {
      emoji: "🛡",
      nom: "Sécurité et Défense",
      domainesProfessionnels: [
        { id: "T_ITM_1094", nom: "Sécurité prévention" },
        { id: "T_ITM_1169", nom: "Défense nationale" },
      ],
    },
    {
      emoji: "🏖",
      nom: "Loisirs et tourisme",
      domainesProfessionnels: [
        { id: "T_ITM_821", nom: "Tourisme" },
        { id: "T_ITM_PERSO8", nom: "Hôtellerie-restauration" },
      ],
    },
    {
      emoji: "🧠",
      nom: "Psychologie et sociologie",
      domainesProfessionnels: [
        { id: "T_ITM_1044", nom: "Psychologie" },
        { id: "T_ITM_1025", nom: "Sociologie" },
      ],
    },
    {
      emoji: "🥕",
      nom: "Agriculture et alimentation",
      domainesProfessionnels: [{ id: "T_ITM_1351", nom: "Agriculture" }],
    },
    { emoji: "🏅", nom: "Sport", domainesProfessionnels: [{ id: "T_ITM_936", nom: "Sport" }] },
  ];

  public async récupérerTousGroupésParCatégorie(): Promise<CatégorieDomainesProfessionnels[] | undefined> {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(this.CATÉGORIES_DOMAINES_PROFESSIONNELS.sort((a, b) => a.nom.localeCompare(b.nom, "fr")));
      }, 500);
    });
  }
}
