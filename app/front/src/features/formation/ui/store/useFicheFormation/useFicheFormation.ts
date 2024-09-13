/* eslint-disable react-hooks/rules-of-hooks */
import { type FicheFormationStore } from "./useFicheFormation.interface";
import { type Formation } from "@/features/formation/domain/formation.interface";
import { create } from "zustand";

const useFicheFormationStore = create<FicheFormationStore>((set) => ({
  formationAffichéeId: undefined,
  actions: {
    changerFormationAffichéeId: (formationId?: Formation["id"]) => {
      set({
        formationAffichéeId: formationId,
      });
    },
  },
}));

export const actionsFicheFormationStore = () => useFicheFormationStore((étatActuel) => étatActuel.actions);
export const formationAffichéeIdFicheFormationStore = () =>
  useFicheFormationStore((étatActuel) => étatActuel.formationAffichéeId);
