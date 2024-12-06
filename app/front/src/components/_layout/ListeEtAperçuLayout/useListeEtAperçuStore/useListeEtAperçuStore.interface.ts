import { FicheFormation } from "@/features/formation/domain/formation.interface";
import { Métier } from "@/features/métier/domain/métier.interface";

export type ListeEtAperçuStoreState = {
  élémentAffiché: { id: FicheFormation["id"] | null; type: "formation" } | { id: Métier["id"] | null; type: "métier" };
  afficherBarreLatéraleEnMobile: boolean;
  recherche?: string;
};

export type ListeEtAperçuStoreActions = {
  actions: {
    rechercher: (recherche: string) => void;
    réinitialiserRecherche: () => void;
    changerÉlémentAffiché: (élémentAffiché: NonNullable<ListeEtAperçuStoreState["élémentAffiché"]>) => void;
    changerAfficherBarreLatéraleEnMobile: (afficher: boolean) => void;
    réinitialiserStore: () => void;
  };
};

export type ListeEtAperçuStore = ListeEtAperçuStoreState & ListeEtAperçuStoreActions;
