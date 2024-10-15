/* eslint-disable @typescript-eslint/require-await */
import { type Métier } from "@/features/métier/domain/métier.interface";
import { type MétierRepository } from "@/features/métier/infrastructure/métierRepository.interface";

export class métierInMemoryRepository implements MétierRepository {
  private MÉTIERS: Métier[] = [
    {
      id: "MET_1",
      nom: "contrôleur aérien / contrôleuse aérienne",
      descriptif:
        "Garant de la sécurité et de la fluidité du trafic aérien, le contrôleur aérien guide les pilotes du décollage jusqu'à l'atterrissage de leur avion. Un métier scientifique à haute responsabilité, sans droit à l'erreur !",
      liens: [
        {
          intitulé: "Lien 1",
          url: "http://www.example.com/1",
        },
        {
          intitulé: "Lien 2",
          url: "http://www.example.com/2",
        },
      ],
      formations: [],
    },
    {
      id: "MET_140",
      nom: "gestionnaire du sport",
      descriptif:
        "<p>Le ou la gestionnaire du sport est responsable d'une structure sportive, dont il ou elle dirige les équipes. A la tête d'une association, d'un club privé ou d'un équipement sportif municipal, il ou elle en assure le fonctionnement et le développement.</p>",
      liens: [],
      formations: [],
    },
    {
      id: "MET_15",
      nom: "gestionnaire de parc micro-informatique",
      descriptif:
        "<p>Le gestionnaire de parc micro-informatique est chargé d'organiser, d'installer, de remplacer et de transformer l'ensemble du parc informatique d'une entreprise. Pour cela, il a plusieurs casquettes et une disponibilité à toute épreuve...</p>",
      liens: [],
      formations: [],
    },
    {
      id: "MET_213",
      nom: "gestionnaire de patrimoine",
      descriptif:
        "<p>Constamment à la recherche des solutions les mieux adaptées aux attentes de ses clients, le gestionnaire de patrimoine, est un spécialiste de l'investissement sur mesure et un expert en ingénierie patrimoniale qui possède de solides connaissances en économie, finance, fiscalité et droit</p>",
      liens: [],
      formations: [],
    },
    {
      id: "MET_619",
      nom: "gestionnaire de contrats informatiques",
      descriptif:
        "<p>Le gestionnaire de contrats informatiques est devenu indispensable pour garantir la qualité de prestations, souvent complexes, effectuées avec de plus en plus de fournisseurs et de prestataires de services informatiques dans l'entreprise.</p>",
      liens: [],
      formations: [],
    },
    {
      id: "MET_700",
      nom: "gestionnaire de données cliniques",
      descriptif:
        "<p>Les essais cliniques sont la phase ultime avant l'autorisation de mise sur le marché d'un médicament. Le gestionnaire de données cliniques élabore les bases de données destinées à recevoir les informations obtenues, et contrôle leur validité.</p>",
      liens: [],
      formations: [],
    },
    {
      id: "MET_815",
      nom: "gestionnaire actif / passif",
      descriptif:
        "<p>Relativement nouveau dans le domaine de l'assurance, le gestionnaire actif/passif met à disposition de sa direction toutes les informations permettant l'évaluation des risques et des opportunités financières permettant d'améliorer les performances.</p>",
      liens: [],
      formations: [],
    },
    {
      id: "MET_867",
      nom: "gestionnaire de contrats d'assurance",
      descriptif:
        "<p>Vol, incendie, accidents... Le gestionnaire de contrats d'assurances est l'interlocuteur privilégié des assurés, qu'il accompagne de l'établissement du contrat jusqu'à la réparation du dommage. Il intervient aussi pour indemniser en cas de sinistre.</p>",
      liens: [],
      formations: [],
    },
    {
      id: "MET_958",
      nom: "gestionnaire de flux reverse logistic",
      descriptif:
        "<p>Le ou la gestionnaire de flux reverse logistic (logistique inversée) optimise et gère le transport, le stockage et le traitement des produits retournés par les clients. Un canapé, un vêtement, un appareil ménager... peut être réparé, reconditionné, recyclé, revendu.</p>",
      liens: [],
      formations: [],
    },
    {
      id: "MET_10",
      nom: "peintre en bâtiment",
      descriptif:
        "Dernier ouvrier à intervenir sur un chantier de construction, le peintre en bâtiment habille murs et plafonds. Passé maître dans l'art des finitions, il sait aussi conseiller les clients dans leurs choix de décoration.",
      liens: [],
      formations: [],
    },
    {
      id: "MET_100",
      nom: "développeur rural / développeuse rurale humanitaire",
      descriptif:
        "Le développeur rural humanitaire conseille les populations vulnérables dans les pays en développement. Son objectif : les conduire vers l'autosuffisance alimentaire dans une perspective de développement durable.",
      liens: [],
      formations: [],
    },
    {
      id: "MET_101",
      nom: "conseiller / conseillère en fusions-acquisitions",
      descriptif:
        "Rachat de sociétés, vente de filiales, fusions : le conseiller en fusions-acquisitions guide les entreprises dans la réalisation d'opérations financières complexes. Un métier où l'intuition et le sens des affaires sont essentiels.",
      liens: [],
      formations: [],
    },
    {
      id: "MET_1010",
      nom: "maître-chien d'avalanche",
      descriptif: null,
      liens: [],
      formations: [],
    },
  ];

  public async récupérer(métierId: string): Promise<Métier | undefined> {
    return this.MÉTIERS.find((métier) => métier.id === métierId);
  }

  public async récupérerPlusieurs(métierIds: string[]): Promise<Métier[] | undefined> {
    return this.MÉTIERS.filter((métier) => métierIds.includes(métier.id));
  }

  public async rechercher(recherche: string): Promise<Métier[] | undefined> {
    return this.MÉTIERS.filter((métier) => métier.nom.toLowerCase().includes(recherche.toLowerCase()));
  }
}
