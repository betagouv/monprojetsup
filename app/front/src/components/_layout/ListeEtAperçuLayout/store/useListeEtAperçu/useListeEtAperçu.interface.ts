import { type Formation } from "@/features/formation/domain/formation.interface";
import { type Métier } from "@/features/métier/domain/métier.interface";

export type ListeEtAperçuStore = {
  élémentAffiché?: ÉlémentAffichéListeEtAperçuStore;
  afficherBarreLatéraleEnMobile: boolean;
  actions: {
    réinitialiserÉlémentAffiché: () => void;
    changerÉlémentAffiché: (élément: ÉlémentAffichéListeEtAperçuStore) => void;
    changerAfficherBarreLatéraleEnMobile: (afficher: boolean) => void;
  };
};

export type ÉlémentAffichéListeEtAperçuStore =
  | {
      type: "métier";
      id: Métier["id"];
    }
  | {
      type: "formation";
      id: Formation["id"];
    };
