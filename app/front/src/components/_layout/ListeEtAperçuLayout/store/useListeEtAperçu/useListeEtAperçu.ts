/* eslint-disable react-hooks/rules-of-hooks */
import { type ÉlémentAffichéListeEtAperçuStore, type ListeEtAperçuStore } from "./useListeEtAperçu.interface";
import { create } from "zustand";

const useListeEtAperçuStore = create<ListeEtAperçuStore>((set) => ({
  élémentAffiché: undefined,
  afficherBarreLatéraleEnMobile: false,
  actions: {
    changerÉlémentAffiché: (élément: ÉlémentAffichéListeEtAperçuStore) => {
      set({
        élémentAffiché: élément,
      });
    },
    réinitialiserÉlémentAffiché: () => {
      set({
        élémentAffiché: undefined,
      });
    },
    changerAfficherBarreLatéraleEnMobile: (afficher: boolean) => {
      set({
        afficherBarreLatéraleEnMobile: afficher,
      });
    },
  },
}));

export const actionsListeEtAperçuStore = () => useListeEtAperçuStore((étatActuel) => étatActuel.actions);
export const élémentAffichéListeEtAperçuStore = () => useListeEtAperçuStore((étatActuel) => étatActuel.élémentAffiché);
export const afficherBarreLatéraleEnMobileListeEtAperçuStore = () =>
  useListeEtAperçuStore((étatActuel) => étatActuel.afficherBarreLatéraleEnMobile);
