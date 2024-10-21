import { type Élève } from "@/features/élève/domain/élève.interface";

export type ÉlèveRepository = {
  récupérerProfil: () => Promise<Élève | undefined>;
  mettreÀJourProfil: (élève: Élève) => Promise<Élève | undefined>;
  associerCompteParcourSup: (codeVerifier: string, code: string, redirectUri: string) => Promise<boolean | undefined>;
};
