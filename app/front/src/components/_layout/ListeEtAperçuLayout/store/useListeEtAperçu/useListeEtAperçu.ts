/* eslint-disable react-hooks/rules-of-hooks */
import {
  type ÉlémentAffichéListeEtAperçuStore,
  type ListeEtAperçuStore,
  type ListeEtAperçuStoreState,
} from "./useListeEtAperçu.interface";
import { create } from "zustand";

const étatInitial: ListeEtAperçuStoreState = {
  élémentAffiché: undefined,
  afficherBarreLatéraleEnMobile: true,
  catégorieAffichée: "première",
};

const useListeEtAperçuStore = create<ListeEtAperçuStore>((set) => ({
  ...étatInitial,
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
    changerCatégorieAffichée: (catégorie: ListeEtAperçuStoreState["catégorieAffichée"]) => {
      set({
        catégorieAffichée: catégorie,
      });
    },
    réinitialiserStore: () => {
      set(étatInitial);
    },
  },
}));

export const actionsListeEtAperçuStore = () => useListeEtAperçuStore((étatActuel) => étatActuel.actions);
export const élémentAffichéListeEtAperçuStore = () => useListeEtAperçuStore((étatActuel) => étatActuel.élémentAffiché);
export const afficherBarreLatéraleEnMobileListeEtAperçuStore = () =>
  useListeEtAperçuStore((étatActuel) => étatActuel.afficherBarreLatéraleEnMobile);
export const catégorieAffichéeListeEtAperçuStore = () =>
  useListeEtAperçuStore((étatActuel) => étatActuel.catégorieAffichée);
