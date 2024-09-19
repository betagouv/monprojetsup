import { type Formation } from "@/features/formation/domain/formation.interface";
import { type Métier } from "@/features/métier/domain/métier.interface";

export type ÉlémentAffichéListeEtAperçuStore =
  | {
      type: "métier";
      id: Métier["id"];
    }
  | {
      type: "formation";
      id: Formation["id"];
    };

export type ListeEtAperçuStoreState = {
  élémentAffiché?: ÉlémentAffichéListeEtAperçuStore;
  afficherBarreLatéraleEnMobile: boolean;
};

export type ListeEtAperçuStoreActions = {
  actions: {
    réinitialiserÉlémentAffiché: () => void;
    changerÉlémentAffiché: (élément: ÉlémentAffichéListeEtAperçuStore) => void;
    changerAfficherBarreLatéraleEnMobile: (afficher: boolean) => void;
    réinitialiserStore: () => void;
  };
};

export type ListeEtAperçuStore = ListeEtAperçuStoreState & ListeEtAperçuStoreActions;
