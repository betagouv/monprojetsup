import { type AlternanceÉlève, type DuréeÉtudesPrévueÉlève } from "@/features/élève/domain/élève.interface";

export type Formation = {
  id: string;
  nom: string;
  descriptifs: {
    formation: string | null;
    détails: string | null;
    attendus: string | null;
    conseils: string | null;
  };
  liens: Array<{
    intitulé: string;
    url: string;
  }>;
  admis: {
    moyenneGénérale: {
      idBac: string | null;
      nomBac: string | null;
      centilles: Array<{
        centille: number;
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
  villes: string[];
  métiersAccessibles: Array<{
    id: string;
    nom: string;
    descriptif: string | null;
    liens: Array<{
      intitulé: string;
      url: string;
    }>;
  }>;
  affinité?: number;
  explications: {
    villes: Array<{
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
      pourcentageChoisiAnnéePrécédente: number;
    }>;
    typeBaccalaureat: {
      id: string;
      nom: string;
      pourcentageAdmisAnnéePrécédente: number;
    };
    autoEvaluationMoyenne: {
      moyenne: number;
      médiane: number;
      idBacUtilisé: string;
      nomBacUtilisé: string;
    };
  } | null;
};

export type FormationAperçu = {
  id: Formation["id"];
  nom: Formation["nom"];
};
