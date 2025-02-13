import { type Élève, ProgressionÉlève } from "@/features/élève/domain/élève.interface";

export type ÉlèveRepository = {
  récupérerProfil: () => Promise<Élève | Error>;
  récupérerProgressionÉlève: () => Promise<ProgressionÉlève>;
  mettreÀJourProfil: (élève: Élève) => Promise<Élève | Error>;
  associerCompteParcourSup: (codeVerifier: string, code: string, redirectUri: string) => Promise<boolean | Error>;
};
