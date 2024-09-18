import {
  type AlternanceÉlève,
  type DuréeÉtudesPrévueÉlève,
} from "@/features/référentielDonnées/domain/référentielDonnées.interface";

export type Formation = {
  id: string;
  nom: string;
  descriptifs: {
    formation: string | null;
    détails: string | null;
    attendus: string | null;
    conseils: string | null;
  };
  lienParcoursSup: string | null;
  liens: Array<{
    intitulé: string;
    url: string;
  }>;
  admis: {
    moyenneGénérale: {
      idBac: string | null;
      nomBac: string | null;
      centiles: Array<{
        centile: number;
        note: number;
      }>;
    };
    répartition: {
      parBac: Array<{ idBac: string; nomBac: string; nombre: number; pourcentage: number }>;
    };
    total: number | null;
  };
  formationsAssociées: string[];
  critèresAnalyse: Array<{ nom: string; pourcentage: number }>;
  tripletsAffectations: Array<{
    id: string;
    nom: string;
    commune: {
      nom: string;
      code: string;
    };
  }>;
  communes: string[];
  métiersAccessibles: Array<{
    id: string;
    nom: string;
    descriptif: string | null;
    liens: Array<{
      intitulé: string;
      url: string;
    }>;
  }>;
  affinité: number;
  explications: {
    communes: Array<{
      nom: string;
      distanceKm: number;
    }>;
    formationsSimilaires: Array<{
      id: string;
      nom: string;
    }>;
    duréeÉtudesPrévue: DuréeÉtudesPrévueÉlève | null;
    alternance: AlternanceÉlève | null;
    intêretsEtDomainesChoisis: {
      intêrets: Array<{
        id: string;
        nom: string;
      }>;
      domaines: Array<{
        id: string;
        nom: string;
      }>;
    };
    spécialitésChoisies: Array<{
      nom: string;
      pourcentageAdmisAnnéePrécédente: number;
    }>;
    typeBaccalaureat: {
      id: string;
      nom: string;
      pourcentageAdmisAnnéePrécédente: number;
    } | null;
    autoEvaluationMoyenne: {
      moyenne: number;
      intervalBas: number;
      intervalHaut: number;
      idBacUtilisé: string;
      nomBacUtilisé: string;
    } | null;
  } | null;
};
