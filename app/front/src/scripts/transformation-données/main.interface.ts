export type Données = {
  labels: Labels;
  statsAdmis: StatsAdmis;
  matieres: Matières;
  specialites: Spécialités;
  sources: MotsClefs;
  descriptifs: Descriptifs;
  interets: Intérêts;
  thematiques: Thématiques;
  urls: Urls;
  groups: GroupeDeFormation;
  labelsTypes: LabelsTypes;
  constants: Constants;
  liensMetiersFormations: LienMétierFormations;
  liensSecteursMetiers: LienSecteurMétiers;
  grillesAnalyseCandidatures: GrillesAnalyseCandidatures;
  grillesAnalyseCandidaturesLabels: GrillesAnalyseCandidaturesLabels;
  eds: Eds;
  psupData: PSupData;
  bacs: Bacs;
  domaines: Domaines;
  intêrets: Intêrets;
  nombreAdmisParBac: NombreAdmisParBac;
  repartitionAdmisParBacEtMatiere: RepartitionAdmisParBacEtMatiere;
};

export type FormationId = string;

export type MétierId = string;
export type BacId = string;
export type GroupeDeFormationId = string;
export type SpécialitéId = string;
export type SpécialitéIdAsNumber = number;
export type IntérêtId = string;
export type ThématiqueId = string;
export type Matièreid = string;
export type SecteurId = string;
export type TripleAffectationId = string;
export type CritèreAnalyseId = string;

export type Labels = Record<FormationId | MétierId | SecteurId, string>;

export type StatsAdmis = {
  parGroupe: {
    "": {
      parBac: {
        [key: BacId]: {
          parMatiere: {
            [key: Matièreid]: {
              frequencesCumulees: number[];
              middle50: {
                rangEch25: number;
                rangEch50: number;
                rangEch75: number;
                rangEch10: number;
                rangEch90: number;
              };
            };
          };
        };
      };
    };
  };
};

export type Matières = {
  [key: Matièreid]: string;
};

export type Spécialités = {
  specialites: { [key: SpécialitéId]: string };
  specialitesParBac: { [key: BacId]: SpécialitéIdAsNumber[] };
};

export type MotsClefs = {
  sources: { [key: string]: Array<MétierId | FormationId> };
};

export type Descriptifs = {
  keyToDescriptifs: {
    [key: FormationId | MétierId]: {
      presentation?: string;
      summary?: string;
      summaryFormation?: string;
    };
  };
};

export type Intérêts = {
  interets: { [key: IntérêtId]: string };
};

export type Thématiques = {
  thematiques: { [key: ThématiqueId]: string };
};

export type Urls = {
  [key: FormationId | MétierId]: Array<{ label: string; uri: string }>;
};

export type GroupeDeFormation = {
  [key: FormationId]: GroupeDeFormationId;
};

export type LabelsTypes = {
  theme: string[];
  metier: string[];
  secteur: string[];
  filiere: string[];
  interest: string[];
};

export type Constants = {
  MOYENNE_BAC_CODE: string;
  MOYENNE_GENERALE_CODE: string;
  PRECISION_PERCENTILES: string;
  TOUS_GROUPES_CODE: string;
  TOUS_BACS_CODE: string;
  BACS_GENERAL_CODE: string;
};

export type LienMétierFormations = {
  [key: MétierId]: FormationId[];
};

export type LienSecteurMétiers = {
  [key: SecteurId]: MétierId[];
};

export type GrillesAnalyseCandidatures = {
  [key: FormationId]: {
    pcts: {
      [key: CritèreAnalyseId]: string;
    };
  };
};

export type GrillesAnalyseCandidaturesLabels = {
  [key: CritèreAnalyseId]: string;
};

export type Eds = {
  [key: FormationId]: {
    attendus?: string;
    recoEDS?: string;
  };
};

export type PSupData = {
  formations: {
    formations: {
      [key: TripleAffectationId]: {
        gTaCod: number;
        gTiCod: number;
        gFlCod: number;
        libelle: string;
        academie: string;
        academieCode: number;
        capacite: number;
        etablissement: string;
        groupes: number[];
        lat: number;
        lng: number;
        commune: string;
        codeCommune: string;
      };
    };
  };
};

export type Bacs = Array<{
  id: BacId;
  nom: string;
  idExterne: string;
}>;

export type Domaines = Array<{
  emoji: string;
  nom: string;
  id: string;
  enfants: Array<{
    id: string;
    nom: string;
    emoji: string;
  }>;
}>;

export type Intêrets = Array<{
  emoji: string;
  nom: string;
  id: string;
  sousCatégories: Array<{
    id: string;
    nom: string;
    emoji: string;
    enfants: Array<{
      id: string;
      nom: string;
    }>;
  }>;
}>;

export type NombreAdmisParBac = {
  [key: FormationId]: {
    [key: BacId]: number;
  };
};

export type RepartitionAdmisParBacEtMatiere = {
  [key: FormationId]: {
    parBac: {
      [key: BacId]: {
        parMatiere: {
          [key: Matièreid]: {
            frequencesCumulees: number[];
            middle50: Record<string, number>;
          };
        };
      };
    };
  };
};
