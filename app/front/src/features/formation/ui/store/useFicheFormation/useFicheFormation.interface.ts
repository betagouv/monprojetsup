import { type Formation } from "@/features/formation/domain/formation.interface";

export type FicheFormationStore = {
  formationAffichéeId: Formation["id"] | undefined;
  actions: {
    changerFormationAffichéeId: (formationId?: Formation["id"]) => void;
  };
};
